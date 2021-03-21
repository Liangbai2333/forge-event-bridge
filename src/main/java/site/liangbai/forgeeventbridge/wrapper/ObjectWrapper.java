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

import org.jetbrains.annotations.Nullable;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectWrapper implements IWrapper {
    private final Object object;

    public ObjectWrapper(Object object) {
        this.object = object;
    }

    @Override
    public <T> T as(String fieldName, Class<T> cast) {
        Field field = Reflection.findFieldOrNull(getObject().getClass(), fieldName);

        return Reflection.getFieldObjOrNull(field, getObject(), cast);
    }

    @Override
    public <T> T invoke(String methodName, Class<T> cast, Object... args) {
        Class<?>[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            classes[i] = args.getClass();
        }

        Method method =  Reflection.findMethodOrNull(getObject().getClass(), methodName, classes);

        return Reflection.invokeMethodOrNull(method, getObject(), cast, args);
    }

    @Override
    public <T> T get(String name, Class<T> cast) {
        String methodName = generateGetterMethodName(name);

        Method method = Reflection.findMethodOrNull(getObject().getClass(), methodName);

        if (method != null) {
            return Reflection.invokeMethodOrNull(method, getObject(), cast);
        }

        method = Reflection.findMethodOrNull(getObject().getClass(), name);

        if (method != null) {
            return Reflection.invokeMethodOrNull(method, getObject(), cast);
        }

        Field field = Reflection.findFieldOrNull(getObject().getClass(), name);

        if (field == null) {
            throw new IllegalStateException("could not found the field or method: " + name + "in class: " + getObject().getClass().getSimpleName());
        }

        return Reflection.getFieldObjOrNull(field, getObject(), cast);
    }

    @Override
    public void set(String name, Object value) {
        String methodName = generateSetterMethodName(name);

        Method method = Reflection.findMethodOrNull(getObject().getClass(), methodName, value.getClass());

        if (method != null) {
            Reflection.invokeMethodOrNull(method, getObject(), value);

            return;
        }

        method = Reflection.findMethodOrNull(getObject().getClass(), name, value.getClass());

        if (method != null) {
            Reflection.invokeMethodOrNull(method, getObject(), value);

            return;
        }

        Field field = Reflection.findFieldOrNull(getObject().getClass(), name);

        if (field == null || !field.getType().isInstance(value)) {
            throw new IllegalStateException("could not found the field or method: " + name + "in class: " + getObject().getClass().getSimpleName());
        }

       Reflection.setFieldObj(field, getObject(), value);
    }

    @Nullable
    public <K> K to(Class<K> cast) {
        if (getObject() == null || !cast.isInstance(getObject())) return null;

        return cast.cast(getObject());
    }

    public Object getObject() {
        return object;
    }

    private static String generateGetterMethodName(String oldName) {
        if (oldName.startsWith("get")) return oldName;

        return "get" + makeFirstCharUppercase(oldName);
    }

    private static String generateSetterMethodName(String oldName) {
        if (oldName.startsWith("set")) return oldName;

        return "set" + makeFirstCharUppercase(oldName);
    }

    private static String makeFirstCharUppercase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
