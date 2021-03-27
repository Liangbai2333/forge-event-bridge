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

        public static Class<?> mapClass(Class<?> clazz) {
            if (clazz.equals(int.class)) {
                return Integer.class;
            } else if (clazz.equals(boolean.class)) {
                return Boolean.class;
            } else if (clazz.equals(long.class)) {
                return Long.class;
            } else if (clazz.equals(double.class)) {
                return Double.class;
            } else if (clazz.equals(float.class)) {
                return Float.class;
            } else if (clazz.equals(byte.class)) {
                return Byte.class;
            } else if (clazz.equals(char.class)) {
                return Character.class;
            } else if (clazz.equals(short.class)) {
                return Short.class;
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
            if (Modifier.isAbstract(method.getModifiers())) {
                Method eventMethod = event.getClass().getMethod(method.getName(), method.getParameterTypes());

                Object returnValue = Reflection.invokeMethodOrNull(Reflection.setAccessible(eventMethod), event, args);

                if (returnValue == null) {
                    return null;
                }

                Class<?> returnType = mapClass(method.getReturnType());

                if (!returnType.isInstance(returnValue)) {
                    returnValue = WrapperTransformer.require(returnType, returnValue);

                    if (!returnType.isInstance(returnValue)) {
                        throw new UnknownTransformerTypeError("can not transfer the type: " + returnValue.getClass().getSimpleName() + " to " + returnType.getSimpleName());
                    }
                }

                return returnValue;
            }

            return proxy.invokeSuper(obj, args);
        }
    }
}
