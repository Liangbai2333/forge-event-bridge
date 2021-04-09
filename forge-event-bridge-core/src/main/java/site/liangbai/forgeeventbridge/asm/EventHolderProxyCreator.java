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
import site.liangbai.forgeeventbridge.ForgeEventBridge;
import site.liangbai.forgeeventbridge.event.EventBridge;
import site.liangbai.forgeeventbridge.serviceprovider.IServiceProvider;
import site.liangbai.forgeeventbridge.util.Reflection;

public final class EventHolderProxyCreator {
    public static Class<?> createNewEventHolderProxyClass(EventBridge eventBridge) {
        String className = "EventListener$ForgeEventBridge$" + eventBridge.hashCode();

        Class<?> loadedClass = Reflection.findClassOrNull(className);

        if (loadedClass != null) {
            return loadedClass;
        }

        Generator generator = new Generator(eventBridge);

        byte[] classBuffer = generator.generate(className);

        IServiceProvider serviceProvider = ForgeEventBridge.getServiceProvider();

        return serviceProvider.getClassCreator().create(className, classBuffer);
    }

    private static class Generator {
        private final EventBridge eventBridge;

        public Generator(EventBridge eventBridge) {
            this.eventBridge = eventBridge;
        }

        public byte[] generate(String className) {
            IServiceProvider serviceProvider = ForgeEventBridge.getServiceProvider();

            EventHolderProxyWriter eventHolderProxyWriter =
                    new EventHolderProxyWriter(eventBridge, serviceProvider.getConstantsProvider(), className);

            eventHolderProxyWriter.visit(52, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, null, "java/lang/Object", null);

            eventHolderProxyWriter.writeEventHolderObjectField();

            eventHolderProxyWriter.writeConstructor();

            eventHolderProxyWriter.writeListenEventMethod("receive");

            eventHolderProxyWriter.visitEnd();

            return eventHolderProxyWriter.toByteArray();
        }
    }
}
