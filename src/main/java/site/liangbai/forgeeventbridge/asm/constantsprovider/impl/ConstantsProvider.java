package site.liangbai.forgeeventbridge.asm.constantsprovider.impl;

import site.liangbai.forgeeventbridge.asm.constantsprovider.Constant;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;

import java.util.EnumMap;
import java.util.HashMap;

public final class ConstantsProvider implements IConstantsProvider {
    private static final EnumMap<Constant, String> constantStringEnumMap = new EnumMap<>(new HashMap<>());

    static {
        put(Constant.EVENT_HOLDER_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/event/EventHolder;");
        put(Constant.EVENT_HOLDER_CLASS_NAME_L, "site/liangbai/forgeeventbridge/event/EventHolder");
        put(Constant.SUBSCRIBE_EVENT_CLASS_NAME, "Lnet/minecraftforge/eventbus/api/SubscribeEvent;");
        put(Constant.EVENT_PRIORITY_CLASS_NAME, "Lnet/minecraftforge/eventbus/api/EventPriority;");
        put(Constant.EVENT_WRAPPER_CREATOR_CLASS_NAME, "Lsite/liangbai/forgeeventbridge/wrapper/creator/EventWrapperCreator;");
        put(Constant.WRAPPER_CREATOR_CLASS_NAME_L, "site/liangbai/forgeeventbridge/wrapper/creator/IWrapperCreator");
        put(Constant.WRAPPER_CREATORS_CLASS_NAME_L, "site/liangbai/forgeeventbridge/wrapper/creator/WrapperCreators");
    }

    private static void put(Constant constant, String value) {
        constantStringEnumMap.put(constant, value);
    }

    @Override
    public String get(Constant constant) {
        return constantStringEnumMap.get(constant);
    }
}
