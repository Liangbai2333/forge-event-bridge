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

package site.liangbai.forgeeventbridge.v1_18_2.classcreator;

import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ASMEventHandler;
import site.liangbai.forgeeventbridge.asm.classcreator.IClassCreator;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;

public final class ASMClassCreator implements IClassCreator {
    public static final TransformingClassLoader TRANSFORMING_CLASS_LOADER
            = getTransformingClassLoader();

    @Override
    public Class<?> create(String name, byte[] classBuffer) {
        ClassLoader classLoader = TRANSFORMING_CLASS_LOADER != null ? TRANSFORMING_CLASS_LOADER : ClassLoader.getSystemClassLoader();

        Method defineMethod = Reflection.findDeclaredMethodOrNull(ClassLoader.class, "defineClass", String.class, byte[].class, int.class, int.class);

        Class<?> clazz = Reflection.invokeMethodOrNull(
                defineMethod, classLoader,
                Class.class,
                name, classBuffer, 0, classBuffer.length
        );

        if (clazz == null || Reflection.findClassOrNull(name) == null) {
            throw new ClassFormatError("could not define class: " + name);
        }

        return clazz;
    }

    public static TransformingClassLoader getTransformingClassLoader() {
        ClassLoader classLoader = ASMClassCreator.class.getClassLoader();

        if (classLoader instanceof TransformingClassLoader) {
            return (TransformingClassLoader) classLoader;
        }

        classLoader = ASMEventHandler.class.getClassLoader();

        if (classLoader instanceof TransformingClassLoader) {
            return (TransformingClassLoader) classLoader;
        }

        classLoader = MinecraftForge.class.getClassLoader();

        if (classLoader instanceof TransformingClassLoader) {
            return (TransformingClassLoader) classLoader;
        }

        return null;
    }
}
