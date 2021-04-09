package site.liangbai.forgeeventbridge.v1_15_2_1_16_5.transformer;

import net.minecraft.item.ItemStack;
import site.liangbai.forgeeventbridge.util.NMSVersion;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;

public enum TransformerMethod {
    BUKKIT_ENTITY_GETTER
            (Reflection.findMethodOrNull(net.minecraft.entity.Entity.class, "getBukkitEntity")),

    CRAFT_ITEM_STACK_AS_BUKKIT_COPY
            (Reflection.findMethodOrNull(
                    NMSVersion.getCraftBukkitClassOrNull("inventory.CraftItemStack"),
                    "asBukkitCopy",
                    ItemStack.class)
            ),

    WORLD_GETTER
            (Reflection.findMethodOrNull(net.minecraft.world.World.class, "getWorld"));

    private final Method method;

    TransformerMethod(Method method) {
        this.method = method;
    }

    public Object invokeOrDefault(Object obj, Object defaultValue, Object... args) {
        Object returnValue = Reflection.invokeMethodOrNull(method, obj, args);

        return returnValue == null ? defaultValue : returnValue;
    }
}
