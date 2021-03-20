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
import site.liangbai.forgeeventbridge.asm.constantsprovider.Constant;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.event.ForgeEventHandler;

public class EventHolderProxyWriter extends ClassWriter implements Opcodes {
    private final IConstantsProvider constantsProvider;

    private final String className;

    private final ForgeEventHandler forgeEventHandler;

    public EventHolderProxyWriter(
            EventHolder<?> eventHolder,
            IConstantsProvider constantsProvider,
            String className
    ) {
        super(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        forgeEventHandler = eventHolder.getClass().getAnnotation(ForgeEventHandler.class);

        if (forgeEventHandler == null) {
            throw new UnknownEventHandlerClassError("could not find annotation: ForgeEventHandler in class: " + eventHolder.getClass().getSimpleName());
        }

        this.className = className;
        this.constantsProvider = constantsProvider;

        visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
    }

    public void writeEventHolderObjectField() {
        FieldVisitor fv = visitField(ACC_PRIVATE + ACC_FINAL, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME), null, null);
        fv.visitEnd();
    }

    public void writeConstructor() {
        MethodVisitor mv = visitMethod(ACC_PUBLIC, "<init>", "(" + constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME) + ")V", null, null);
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
        mv.visitFieldInsn(PUTFIELD, className, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME));
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

    public void writeListenEventMethod(String methodName) {
        ForgeEventHandler.Target target = forgeEventHandler.value();

        String targetClassName = getTargetClassName(target);

        MethodVisitor mv = visitMethod(ACC_PUBLIC, methodName, "(" + targetClassName + ")V", null, null);

        writeListenAnnotationForMethod(mv);

        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(19, l0);

        // Write by self start.
        mv.visitFieldInsn(GETSTATIC, constantsProvider.get(Constant.WRAPPER_CREATORS_CLASS_NAME_L), "EVENT", constantsProvider.get(Constant.EVENT_WRAPPER_CREATOR_CLASS_NAME));
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, constantsProvider.get(Constant.WRAPPER_CREATOR_CLASS_NAME_L), "create", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitVarInsn(ASTORE, 2);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME));
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME_L), "handle", "(Ljava/lang/Object;)V", true);

        // Write by self end.
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("event", targetClassName, null, l0, l1, 1);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private void writeListenAnnotationForMethod(MethodVisitor mv) {
        AnnotationVisitor av = mv.visitAnnotation(constantsProvider.get(Constant.SUBSCRIBE_EVENT_CLASS_NAME), true);
        av.visitEnum("priority", constantsProvider.get(Constant.EVENT_PRIORITY_CLASS_NAME), forgeEventHandler.priority().name());
        av.visit("receiveCanceled", forgeEventHandler.receiveCanceled());
        av.visitEnd();
    }

    private static String getTargetClassName(ForgeEventHandler.Target target) {
        String name = target.value().equals(ForgeEventHandler.None.class) ? target.source() : target.value().getName();

        return "L" + name.replace(".", "/") + ";";
    }
}
