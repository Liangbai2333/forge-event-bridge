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
