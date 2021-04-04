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

import org.objectweb.asm.Opcodes;
import site.liangbai.forgeeventbridge.asm.classcreator.IClassCreator;
import site.liangbai.forgeeventbridge.asm.classcreator.impl.ASMClassCreator;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;
import site.liangbai.forgeeventbridge.asm.constantsprovider.impl.ConstantsProviderImpl;
import site.liangbai.forgeeventbridge.event.EventBridge;
import site.liangbai.forgeeventbridge.util.Reflection;

public final class EventHolderProxyCreator {
    public static final IClassCreator CLASS_CREATOR = new ASMClassCreator();

    public static Class<?> createNewEventHolderProxyClass(EventBridge eventBridge) {
        String className = "EventListener$ForgeEventBridge$" + eventBridge.hashCode();

        Class<?> loadedClass = Reflection.findClassOrNull(className);

        if (loadedClass != null) {
            return loadedClass;
        }

        Generator generator = new Generator(eventBridge);

        byte[] classBuffer = generator.generate(className);

        return CLASS_CREATOR.create(className, classBuffer);
    }

    private static class Generator {
        private static final IConstantsProvider CONSTANTS_PROVIDER = new ConstantsProviderImpl();

        private final EventBridge eventBridge;

        public Generator(EventBridge eventBridge) {
            this.eventBridge = eventBridge;
        }

        public byte[] generate(String className) {
            EventHolderProxyWriter eventHolderProxyWriter =
                    new EventHolderProxyWriter(eventBridge, CONSTANTS_PROVIDER, className);

            eventHolderProxyWriter.visit(52, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, null, "java/lang/Object", null);

            eventHolderProxyWriter.writeEventHolderObjectField();

            eventHolderProxyWriter.writeConstructor();

            eventHolderProxyWriter.writeListenEventMethod("receive");

            eventHolderProxyWriter.visitEnd();

            return eventHolderProxyWriter.toByteArray();
        }
    }
}
