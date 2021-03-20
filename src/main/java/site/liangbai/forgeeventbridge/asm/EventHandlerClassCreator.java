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

import org.objectweb.asm.*;
import site.liangbai.forgeeventbridge.event.EventHandler;
import site.liangbai.forgeeventbridge.event.ForgeEventHandler;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Field;
import java.util.Vector;

public final class EventHandlerClassCreator implements Opcodes {
    @SuppressWarnings("unchecked")
    public static Class<?> createNewEventHandlerClass(EventHandler<?> eventHolder) {
        String className = "EventHandler$ForgeEventBridge$" + eventHolder.hashCode();

        Class<?> loadedClass = Reflection.findClassOrNull(className);

        if (loadedClass != null) {
            return loadedClass;
        }

        Generator generator = new Generator(eventHolder);

        byte[] classBuffer = generator.generate(className);

        Class<?> eventProxy = AsmClassLoader.createNewClass(className, classBuffer);

        Field field = Reflection.findFieldOrNull(EventHandlerClassCreator.class.getClassLoader().getClass(), "classes");

        if (field != null) {
            Vector<Class<?>> classes = (Vector<Class<?>>) Reflection.getFieldObjOrNull(Reflection.setAccessible(field), EventHandlerClassCreator.class.getClassLoader());

            if (classes != null) {
                classes.addElement(eventProxy);
            }
        }

        return eventProxy;
    }

    private static String getTargetClassName(ForgeEventHandler.Target target) {
        String name = target.value().equals(ForgeEventHandler.None.class) ? target.source() : target.value().getName();

        return "L" + name.replace(".", "/") + ";";
    }

    private static class Generator implements Opcodes {
        private static final String EVENT_HANDLER_CLASS_NAME = "Lsite/liangbai/forgeeventbridge/event/EventHandler;";
        private static final String EVENT_HANDLER_CLASS_NAME_L = "site/liangbai/forgeeventbridge/event/EventHandler";
        private static final String SUBSCRIBE_EVENT_CLASS_NAME = "Lnet/minecraftforge/eventbus/api/SubscribeEvent;";
        private static final String EVENT_PRIORITY_CLASS_NAME = "Lnet/minecraftforge/eventbus/api/EventPriority;";
        private static final String EVENT_WRAPPER_CREATOR_CLASS_NAME = "Lsite/liangbai/forgeeventbridge/wrapper/creator/EventWrapperCreator;";
        private static final String WRAPPER_CREATOR_CLASS_NAME_L = "site/liangbai/forgeeventbridge/wrapper/creator/IWrapperCreator";
        private static final String WRAPPER_CREATORS_CLASS_NAME_L = "site/liangbai/forgeeventbridge/wrapper/creator/WrapperCreators";

        private final EventHandler<?> eventHolder;

        public Generator(EventHandler<?> eventHolder) {
            this.eventHolder = eventHolder;
        }

        public byte[] generate(String className) {
            ForgeEventHandler forgeEventHandler = eventHolder.getClass().getAnnotation(ForgeEventHandler.class);

            if (forgeEventHandler == null) {
                throw new UnknownEventHandlerClassError("could not find annotation: ForgeEventHandler in class: " + eventHolder.getClass().getSimpleName());
            }

            ForgeEventHandler.Target target = forgeEventHandler.value();

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;

            cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);

            {
                fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "object", EVENT_HANDLER_CLASS_NAME, null, null);
                fv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + EVENT_HANDLER_CLASS_NAME + ")V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(10, l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLineNumber(11, l1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, className, "object", EVENT_HANDLER_CLASS_NAME);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLineNumber(12, l2);
                mv.visitInsn(RETURN);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0, l3, 0);
                mv.visitLocalVariable("object", "Ljava/lang/Object;", null, l0, l3, 1);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                String targetClassName = getTargetClassName(target);

                mv = cw.visitMethod(ACC_PUBLIC, "e", "(" + targetClassName + ")V", null, null);
                {
                    av0 = mv.visitAnnotation(SUBSCRIBE_EVENT_CLASS_NAME, true);
                    av0.visitEnum("priority", EVENT_PRIORITY_CLASS_NAME, forgeEventHandler.priority().name());
                    av0.visit("receiveCanceled", forgeEventHandler.receiveCanceled());
                    av0.visitEnd();
                }
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(19, l0);

                // Write by self start.
                mv.visitFieldInsn(GETSTATIC, WRAPPER_CREATORS_CLASS_NAME_L, "EVENT", EVENT_WRAPPER_CREATOR_CLASS_NAME);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, WRAPPER_CREATOR_CLASS_NAME_L, "create", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitVarInsn(ASTORE, 2);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "object", EVENT_HANDLER_CLASS_NAME);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEINTERFACE, EVENT_HANDLER_CLASS_NAME_L, "handle", "(Ljava/lang/Object;)V", true);

                // Write by self end.
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("event", targetClassName, null, l0, l1, 1);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
            }

            cw.visitEnd();

            return cw.toByteArray();
        }
    }
}
