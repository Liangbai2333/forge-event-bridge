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

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Reflection {
    public static final MethodHandles.Lookup TRUSTED_LOOKUP = getTrustedLookup();

    static {
        Field module;
        try {
            module = Class.class.getDeclaredField("module");
            Unsafe unsafe = UnsafeUtil.getUnsafe();
            long offset = unsafe.objectFieldOffset(module);
            unsafe.putObject(Reflection.class, offset, Object.class.getModule());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static MethodHandles.Lookup getTrustedLookup() {
        try {
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");

            Unsafe unsafe = UnsafeUtil.getUnsafe();

            long offset = unsafe.staticFieldOffset(field);

            return (MethodHandles.Lookup) unsafe.getObject(MethodHandles.Lookup.class, offset);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("could not get trusted lookup", e);
        }
    }

    @SuppressWarnings("deprecation")
    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
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

    public static Object invokeMethodOrNull(Method method, Object obj, Object... args) {
        if (method == null) {
            return null;
        }

        try {
            return setAccessible(method).invoke(obj, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static <T> T invokeMethodOrNull(Method method, Object obj, Class<T> cast, Object... args) {
        return cast.cast(invokeMethodOrNull(method, obj, args));
    }

    public static Object getFieldObjOrNull(Field field, Object obj) {
        if (field == null) {
            return null;
        }

        try {
            return setAccessible(field).get(obj);
        } catch (Throwable e) {
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
            setAccessible(field).set(obj, value);
        } catch (Throwable ignored) { }
    }

    public static Field findFieldOrNull(Class<?> cls, String name) {
        Field field;

        try {
            field = cls.getField(name);
            return setAccessible(field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Method findMethodOrNull(Class<?> cls, String name, Class<?>... params) {
        Method method;

        try {
            method = cls.getMethod(name, params);

            return setAccessible(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        if (constructor == null) {
            return null;
        }

        try {
            return setAccessible(constructor).newInstance(args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static <T> Constructor<T> findConstructorOrNull(Class<T> cls, Class<?>... params) {
        try {

            return setAccessible(cls.getConstructor(params));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findDeclaredMethodOrNull(Class<?> cls, String name, Class<?>... params) {
        Method method;

        try {
            method = cls.getDeclaredMethod(name, params);

            return setAccessible(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
