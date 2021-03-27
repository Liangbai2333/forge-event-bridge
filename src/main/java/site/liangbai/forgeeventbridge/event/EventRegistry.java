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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import site.liangbai.forgeeventbridge.asm.EventHolderProxyCreator;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EventRegistry {
    private static final Map<EventHolder<?>, Object> eventHolderToListenObj = new HashMap<>();

    public static synchronized void register(@NotNull EventHolder<?> eventHolder, @NotNull EventBridge eventBridge) {
        Objects.requireNonNull(eventBridge);
        Objects.requireNonNull(eventHolder);

        Class<?> eventProxyClass = EventHolderProxyCreator.createNewEventHolderProxyClass(eventBridge);

        Constructor<?> eventProxyConstructor = Reflection.findConstructor(eventProxyClass, EventHolder.class);

        Object eventProxy = Reflection.newInstance(eventProxyConstructor, eventHolder);

        switch (eventBridge.getBus()) {
            case FORGE:
                MinecraftForge.EVENT_BUS.register(eventProxy);
                break;
            case MOD:
                FMLJavaModLoadingContext.get().getModEventBus().register(eventProxy);
                break;
        }

        eventHolderToListenObj.put(eventHolder, eventProxy);
    }

    public static synchronized void unregister(@NotNull EventHolder<?> eventHolder) {
        Objects.requireNonNull(eventHolder);

        Object listenObj = eventHolderToListenObj.get(eventHolder);

        if (listenObj == null) {
            return;
        }

        MinecraftForge.EVENT_BUS.unregister(listenObj);

        FMLJavaModLoadingContext.get().getModEventBus().unregister(listenObj);

        eventHolderToListenObj.remove(eventHolder);
    }
}
