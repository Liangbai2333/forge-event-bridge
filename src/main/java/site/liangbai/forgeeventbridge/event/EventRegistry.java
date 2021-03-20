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
import site.liangbai.forgeeventbridge.asm.EventHandlerClassCreator;
import site.liangbai.forgeeventbridge.asm.UnknownEventHandlerClassError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class EventRegistry {
    private static final Map<EventHandler<?>, Object> eventHandlerToListenObj = new HashMap<>();

    public static void register(EventHandler<?> eventHolder) {
        ForgeEventHandler forgeEventHandler = eventHolder.getClass().getAnnotation(ForgeEventHandler.class);

        if (forgeEventHandler == null) {
            throw new UnknownEventHandlerClassError("could not find annotation: ForgeEventHandler in class: " + eventHolder.getClass().getSimpleName());
        }

        Class<?> eventProxyClass = EventHandlerClassCreator.createNewEventHandlerClass(eventHolder);

        try {
            Constructor<?> constructor = eventProxyClass.getDeclaredConstructor(EventHandler.class);

            Object listenObj = constructor.newInstance(eventHolder);

            for (ForgeEventHandler.Bus bus : forgeEventHandler.bus()) {
                switch (bus) {
                    case FORGE:
                        MinecraftForge.EVENT_BUS.register(listenObj);
                        break;
                    case MOD:
                        FMLJavaModLoadingContext.get().getModEventBus().register(listenObj);
                        break;
                }
            }

            eventHandlerToListenObj.put(eventHolder, listenObj);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void unregister(EventHandler<?> eventHolder) {
        if (!eventHandlerToListenObj.containsKey(eventHolder)) return;

        Object listenObj = eventHandlerToListenObj.get(eventHolder);

        MinecraftForge.EVENT_BUS.unregister(listenObj);

        FMLJavaModLoadingContext.get().getModEventBus().unregister(listenObj);

        eventHandlerToListenObj.remove(eventHolder);
    }
}
