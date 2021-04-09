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
import site.liangbai.forgeeventbridge.event.EventBridge;

public final class EventHolderProxyWriter extends ClassWriter implements Opcodes {
    private final IConstantsProvider constantsProvider;

    private final String className;

    private final EventBridge eventBridge;

    public EventHolderProxyWriter(
            EventBridge eventBridge,
            IConstantsProvider constantsProvider,
            String className
    ) {
        super(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        this.eventBridge = eventBridge;
        this.className = className;
        this.constantsProvider = constantsProvider;
    }

    public void writeEventHolderObjectField() {
        FieldVisitor fv = visitField(ACC_PRIVATE + ACC_FINAL, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME), null, null);
        fv.visitEnd();
    }

    public void writeConstructor() {
        MethodVisitor mv = visitMethod(ACC_PUBLIC, "<init>", "(" + constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME) + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, className, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME));
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    public void writeListenEventMethod(String methodName) {
        final String targetClassName = eventBridge.getASMSourceName();

        MethodVisitor mv = visitMethod(ACC_PUBLIC, methodName, "(" + targetClassName + ")V", null, null);

        writeListenAnnotationForMethod(mv);

        mv.visitCode();
        // Write by self start.
        mv.visitFieldInsn(GETSTATIC, constantsProvider.get(Constant.WRAPPER_CREATORS_CLASS_NAME_L), "EVENT", constantsProvider.get(Constant.EVENT_WRAPPER_CREATOR_CLASS_NAME));
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, constantsProvider.get(Constant.WRAPPER_CREATOR_CLASS_NAME_L), "create", "(Ljava/lang/Object;)" + constantsProvider.get(Constant.OBJECT_WRAPPER_CLASS_NAME), true);
        mv.visitVarInsn(ASTORE, 2);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "object", constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME));
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, constantsProvider.get(Constant.EVENT_WRAPPER_CLASS_NAME_L));
        mv.visitMethodInsn(INVOKEINTERFACE, constantsProvider.get(Constant.EVENT_HOLDER_CLASS_NAME_L), "handle", "(" + constantsProvider.get(Constant.EVENT_WRAPPER_CLASS_NAME) + ")V", true);

        // Write by self end.
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private void writeListenAnnotationForMethod(MethodVisitor mv) {
        AnnotationVisitor av = mv.visitAnnotation(constantsProvider.get(Constant.SUBSCRIBE_EVENT_CLASS_NAME), true);
        av.visitEnum("priority", constantsProvider.get(Constant.EVENT_PRIORITY_CLASS_NAME), eventBridge.getPriority().getNameWithForgeEventPriority());
        av.visit("receiveCanceled", eventBridge.isReceiveCanceled());
        av.visitEnd();
    }
}
