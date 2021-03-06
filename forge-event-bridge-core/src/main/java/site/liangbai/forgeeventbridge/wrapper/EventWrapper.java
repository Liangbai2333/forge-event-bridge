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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class EventWrapper<T extends EventWrapper.EventObject> extends ObjectWrapper {
    public EventWrapper(Object object) {
        super(object);
    }

    public T as(@NotNull Class<T> eventObjectClass) {
        EventObjectProxyGenerator<T> generator = new EventObjectProxyGenerator<>(getObject());

        return generator.generate(eventObjectClass);
    }

    public static abstract class EventObject {
        public abstract boolean isCancelable();

        public abstract boolean isCanceled();

        public abstract void setCanceled(boolean cancel);
    }

    private record EventObjectProxyGenerator<T extends EventObject>(Object event) implements MethodInterceptor {

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

                Object returnValue = Reflection.invokeMethodOrNull(eventMethod, event, args);

                if (returnValue == null) {
                    return null;
                }

                Class<?> returnType = method.getReturnType();

                if (!returnType.isInstance(returnValue)) {
                    returnType = mapClass(method.getReturnType());

                    if (!returnType.isInstance(returnValue)) {
                        returnValue = WrapperTransformer.require(returnType, returnValue);

                        if (!returnType.isInstance(returnValue)) {
                            throw new UnknownTransformerTypeError("can not transfer the type: " + returnValue.getClass().getSimpleName() + " to " + returnType.getSimpleName());
                        }
                    }
                }

                return returnValue;
            }

            return proxy.invokeSuper(obj, args);
        }
    }
}
