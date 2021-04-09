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

package site.liangbai.forgeeventbridge.event;

import org.jetbrains.annotations.NotNull;
import site.liangbai.forgeeventbridge.ForgeEventBridge;
import site.liangbai.forgeeventbridge.asm.EventHolderProxyCreator;
import site.liangbai.forgeeventbridge.serviceprovider.IServiceProvider;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EventRegistry {
    private static final Map<EventHolder<?>, RegistryInfo> eventHolderRegistryInfoMap = new HashMap<>();

    public static synchronized void register(@NotNull EventHolder<?> eventHolder, EventBridge eventBridge) {
        Objects.requireNonNull(eventHolder);

        eventHolderRegistryInfoMap.computeIfAbsent(eventHolder, key -> {
            Class<?> eventProxyClass = EventHolderProxyCreator.createNewEventHolderProxyClass(eventBridge);

            Constructor<?> eventProxyConstructor = Reflection.findConstructorOrNull(eventProxyClass, EventHolder.class);

            Object eventProxy = Reflection.newInstance(eventProxyConstructor, key);

            IServiceProvider serviceProvider = ForgeEventBridge.getServiceProvider();

            serviceProvider.getEventBusProxy(eventBridge.getBus()).register(eventProxy);

            return new RegistryInfo(eventBridge, key);
        });
    }

    public static synchronized void unregister(@NotNull EventHolder<?> eventHolder) {
        Objects.requireNonNull(eventHolder);

        eventHolderRegistryInfoMap.computeIfPresent(eventHolder, (key, registryInfo) -> {
            EventBridge eventBridge = registryInfo.getEventBridge();

            Object eventProxy = registryInfo.getEventProxy();

            IServiceProvider serviceProvider = ForgeEventBridge.getServiceProvider();

            serviceProvider.getEventBusProxy(eventBridge.getBus()).unregister(eventProxy);

            return null;
        });
    }

    private static class RegistryInfo {
        private final EventBridge eventBridge;
        private final Object eventProxy;

        public RegistryInfo(EventBridge eventBridge, Object eventProxy) {
            this.eventBridge = eventBridge;
            this.eventProxy = eventProxy;
        }

        public EventBridge getEventBridge() {
            return eventBridge;
        }

        public Object getEventProxy() {
            return eventProxy;
        }
    }
}
