/*
 * Forge-Event-Bridge
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package site.liangbai.forgeeventbridge.wrapper;

import net.minecraftforge.eventbus.api.Event;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class EventWrapper<T extends EventWrapper.EventObject> extends ObjectWrapper {
    public EventWrapper(Event event) {
        super(event);
    }

    public T as(@NotNull Class<T> eventObjectClass) {
        Objects.requireNonNull(eventObjectClass);

        EventObjectProxyGenerator<T> generator = new EventObjectProxyGenerator<>(getObject());

        return generator.generate(eventObjectClass);
    }

    public static abstract class EventObject {
        public abstract boolean isCancelable();

        public abstract boolean isCanceled();

        public abstract void setCanceled(boolean cancel);
    }

    private static class EventObjectProxyGenerator<T extends EventObject> implements MethodInterceptor {
        private static final Map<Class<?>, Class<?>> classMap = new HashMap<>();

        static {
            registerMapClassType(boolean.class, Boolean.class);
            registerMapClassType(char.class, Character.class);
            registerMapClassType(int.class, Integer.class);
            registerMapClassType(long.class, Long.class);
            registerMapClassType(short.class, Short.class);
            registerMapClassType(float.class, Float.class);
            registerMapClassType(double.class, Double.class);
            registerMapClassType(byte.class, Byte.class);
        }



        public static void registerMapClassType(Class<?> clazz, Class<?> changeTo) {
            classMap.put(clazz, changeTo);
        }

        public static Class<?> mapClass(Class<?> clazz) {
            if (classMap.containsKey(clazz)) {
                return classMap.get(clazz);
            }

            return clazz;
        }

        private final Object event;

        public EventObjectProxyGenerator(Object event) {
            this.event = event;
        }

        public T generate(Class<T> eventObjectClass) {
            Enhancer enhancer = new Enhancer();

            enhancer.setSuperclass(eventObjectClass);
            enhancer.setCallback(this);

            return eventObjectClass.cast(enhancer.create());
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            for (Method eventMethod : event.getClass().getMethods()) {
                boolean isAbstract = Modifier.isAbstract(method.getModifiers());

                if (!isAbstract) {
                    continue;
                }

                String methodName = method.getName();

                if (!eventMethod.getName().equals(methodName)) {
                    continue;
                }

                if (!isSameArray(method.getParameterTypes(), eventMethod.getParameterTypes())) {
                    continue;
                }

                Object returnValue = Reflection.setAccessible(eventMethod).invoke(event, args);

                Class<?> returnType = mapClass(method.getReturnType());

                if (!returnType.isInstance(returnValue)) {
                    returnValue = WrapperTransformer.require(returnType, returnValue);
                }

                if (!returnType.isInstance(returnValue)) {
                    throw new UnknownTransformerTypeError("can not transfer the type: " + returnValue.getClass().getSimpleName() + " to " + returnType.getSimpleName());
                }

                return returnValue;
            }

            return proxy.invokeSuper(obj, args);
        }
    }

    private static <T> boolean isSameArray(T[] arrayFirst, T[] arraySecond) {
        if (arrayFirst == null || arraySecond == null) {
            return false;
        }

        if (arraySecond.length != arrayFirst.length) {
            return false;
        }

        for (int i = 0; i < arrayFirst.length; i++) {
            T obj = arrayFirst[i];

            if (!obj.equals(arraySecond[i])) {
                return false;
            }
        }

        return true;
    }
}
