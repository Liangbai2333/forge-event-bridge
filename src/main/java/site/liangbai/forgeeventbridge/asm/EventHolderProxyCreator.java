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

import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;
import site.liangbai.forgeeventbridge.asm.constantsprovider.impl.ConstantsProvider;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.util.Reflection;

public final class EventHolderProxyCreator {
    public static Class<?> createNewEventHolderProxyClass(EventHolder<?> eventHolder) {
        String className = "EventListener$ForgeEventBridge$" + eventHolder.hashCode();

        Class<?> loadedClass = Reflection.findClassOrNull(className);

        if (loadedClass != null) {
            return loadedClass;
        }

        Generator generator = new Generator(eventHolder);

        byte[] classBuffer = generator.generate(className);

        return AsmClassLoader.createNewClass(className, classBuffer);
    }

    private static class Generator {
        private static final IConstantsProvider CONSTANTS_PROVIDER = new ConstantsProvider();

        private final EventHolder<?> eventHolder;

        public Generator(EventHolder<?> eventHolder) {
            this.eventHolder = eventHolder;
        }

        public byte[] generate(String className) {
            EventHolderProxyWriter eventHolderProxyWriter =
                    new EventHolderProxyWriter(eventHolder, CONSTANTS_PROVIDER, className);

            eventHolderProxyWriter.writeEventHolderObjectField();

            eventHolderProxyWriter.writeConstructor();

            eventHolderProxyWriter.writeListenEventMethod("event");

            eventHolderProxyWriter.visitEnd();

            return eventHolderProxyWriter.toByteArray();
        }
    }
}
