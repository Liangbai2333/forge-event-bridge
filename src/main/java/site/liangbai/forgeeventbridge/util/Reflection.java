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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflection {
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

    public static Object invokeMethodOrNull(Method method, Object obj, Object... args) {
        if (method == null) {
            return null;
        }

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
            field = cls.getDeclaredField(name);

            return Reflection.setAccessible(field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Method findMethodOrNull(Class<?> cls, String name, Class<?>... params) {
        Method method;

        try {
            method = cls.getDeclaredMethod(name, params);

            return Reflection.setAccessible(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
