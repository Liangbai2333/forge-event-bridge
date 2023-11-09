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

package site.liangbai.forgeeventbridge.util;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;

@SuppressWarnings("unchecked")
public final class Reflection {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static MethodHandles.Lookup getLookup() {
        return lookup;
    }

    @NotNull
    public static MethodHandle unreflect(Method method) {
        try {
            return getLookup().unreflect(setAccessible(method));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("could not unreflect the method: " + method.getName(), e);
        }
    }

    public static Object invokeMHOrNull(MethodHandle mh, Object obj, Object... args) {
        try {
            return mh.invoke(obj, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Class<?> findClassOrNull(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> findClassOrNull(String name, ClassLoader classLoader) {
        return findClassOrNull(name, false, classLoader);
    }

    public static Class<?> findClassOrNull(String name, boolean initialize, ClassLoader classLoader) {
        try {
            return Class.forName(name, initialize, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }

        return accessibleObject;
    }

    public static <T> T invokeMethodOrNull(Method method, Object obj, Object... args) {
        if (method == null) {
            return null;
        }

        try {
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static Object getFieldObjOrNull(Field field, Object obj) {
        if (field == null) {
            return null;
        }

        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static <T> T getFieldObjOrNull(Field field, Object obj, Class<T> cast) {
        return cast.cast(getFieldObjOrNull(field, obj));
    }

    public static void setFieldObj(Field field, Object obj, Object value) {
        if (field == null) {
            return;
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException ignored) { }
    }

    public static Field findFieldOrNull(Class<?> cls, String name) {
        Field field;

        try {
            field = cls.getField(name);

            return Reflection.setAccessible(field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Method findMethodOrNull(Class<?> cls, String name, Class<?>... params) {
        Method method;

        try {
            method = cls.getMethod(name, params);

            return Reflection.setAccessible(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        if (constructor == null) {
            return null;
        }

        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    public static <T> Constructor<T> findConstructorOrNull(Class<T> cls, Class<?>... params) {
        try {
            Constructor<T> constructor = cls.getConstructor(params);

            return Reflection.setAccessible(constructor);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findDeclaredMethodOrNull(Class<?> cls, String name, Class<?>... params) {
        Method method;

        try {
            method = cls.getDeclaredMethod(name, params);

            return Reflection.setAccessible(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
