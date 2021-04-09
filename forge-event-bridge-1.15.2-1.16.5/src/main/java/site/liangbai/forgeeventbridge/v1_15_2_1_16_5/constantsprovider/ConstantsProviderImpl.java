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

package site.liangbai.forgeeventbridge.v1_15_2_1_16_5.constantsprovider;

import site.liangbai.forgeeventbridge.asm.constantsprovider.Constant;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;

import java.util.HashMap;

public final class ConstantsProviderImpl implements IConstantsProvider {
    private static final HashMap<Constant, String> constantStringEnumMap = new HashMap<>();

    static {
        put(Constant.EVENT_HOLDER_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/event/EventHolder;");
        put(Constant.EVENT_HOLDER_CLASS_NAME_L, "site/liangbai/forgeeventbridge/event/EventHolder");
        put(Constant.SUBSCRIBE_EVENT_CLASS_NAME, "Lnet/minecraftforge/eventbus/api/SubscribeEvent;");
        put(Constant.EVENT_PRIORITY_CLASS_NAME, "Lnet/minecraftforge/eventbus/api/EventPriority;");
        put(Constant.EVENT_WRAPPER_CREATOR_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/wrapper/creator/EventWrapperCreator;");
        put(Constant.WRAPPER_CREATOR_CLASS_NAME_L, "site/liangbai/forgeeventbridge/wrapper/creator/IWrapperCreator");
        put(Constant.WRAPPER_CREATORS_CLASS_NAME_L, "site/liangbai/forgeeventbridge/wrapper/creator/WrapperCreators");
        put(Constant.OBJECT_WRAPPER_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/wrapper/ObjectWrapper;");
        put(Constant.EVENT_WRAPPER_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/wrapper/EventWrapper;");
        put(Constant.EVENT_WRAPPER_CLASS_NAME_L, "site/liangbai/forgeeventbridge/wrapper/EventWrapper");
    }

    private static void put(Constant constant, String value) {
        constantStringEnumMap.put(constant, value);
    }

    @Override
    public String get(Constant constant) {
        return constantStringEnumMap.get(constant);
    }
}
