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

package site.liangbai.forgeeventbridge.asm;

import site.liangbai.forgeeventbridge.util.ClassLoaderUtil;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Field;
import java.util.Vector;

public class AsmClassLoader extends ClassLoader {
    public static final AsmClassLoader INSTANCE = new AsmClassLoader();

    public AsmClassLoader() {
        super(AsmClassLoader.class.getClassLoader());
    }

    public static Class<?> createNewClass(String name, byte[] classBuffer) {
        return INSTANCE.defineClass(
                name,
                classBuffer,
                0,
                classBuffer.length,
                AsmClassLoader.class.getProtectionDomain()
        );
    }

    @SuppressWarnings("unchecked")
    public static void addClassToClassLoader(Class<?> clazz, ClassLoader classLoader) {
        Field field = Reflection.findFieldOrNull(classLoader.getClass(), "classes");

        if (field != null) {
            Vector<Class<?>> classes = (Vector<Class<?>>) Reflection.getFieldObjOrNull(Reflection.setAccessible(field), EventHolderProxyCreator.class.getClassLoader());

            if (classes != null) {
                classes.addElement(clazz);
            }
        }
    }

    public static void addClassToClassLoader(Class<?> clazz) {
        addClassToClassLoader(clazz, ClassLoaderUtil.getClassLoader());
    }
}
