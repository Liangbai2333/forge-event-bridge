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

package site.liangbai.forgeeventbridge.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import site.liangbai.forgeeventbridge.util.NMSVersion;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class WrapperTransformer {
    private static final Map<Type, Function<Object, Object>> WRAPPER_TRANSFORMER = new HashMap<>();

    static {
        WRAPPER_TRANSFORMER.put(Type.ENTITY, obj ->
                TransformerMethods.BUKKIT_ENTITY_GETTER.invokeOrDefault(obj, obj));

        WRAPPER_TRANSFORMER.put(Type.LOCATION, obj -> {
            if (obj instanceof BlockPos) {
                BlockPos blockPos = ((BlockPos) obj);

                return new Location(null, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }

            if (obj instanceof Vector3d) {
                Vector3d vector3d = ((Vector3d) obj);

                return new Location(null, vector3d.x(), vector3d.y(), vector3d.z());
            }

            return obj;
        });

        WRAPPER_TRANSFORMER.put(Type.ITEM_STACK, obj ->
                TransformerMethods.CRAFT_ITEM_STACK_AS_BUKKIT_COPY
                .invokeOrDefault(null, obj, obj));

        WRAPPER_TRANSFORMER.put(Type.WORLD, obj ->
                TransformerMethods.WORLD_GETTER.invokeOrDefault(obj, obj));
    }

    public static Object require(Class<?> requireType, Object otherObj) {
        return Type.matchType(requireType)
                .orElse(Type.NONE)
                .apply(otherObj);
    }

    private enum Type {
        ENTITY(Entity.class),
        WORLD(World.class),
        ITEM_STACK(org.bukkit.inventory.ItemStack.class),
        LOCATION(Location.class),
        NONE(None.class);
        
        private final Class<?> baseAcceptedClass;
        
        Type(Class<?> baseAcceptedClass) {
            this.baseAcceptedClass = baseAcceptedClass;
        }
        
        public boolean isAcceptedClass(Class<?> clazz) {
            return baseAcceptedClass.isAssignableFrom(clazz);
        }
        
        public Object apply(Object otherObj) {
            return this == Type.NONE
                    ? otherObj
                    : WRAPPER_TRANSFORMER.getOrDefault(this, obj -> obj)
                    .apply(otherObj);
        }
        
        public static Optional<Type> matchType(Class<?> clazz) {
            return Arrays.stream(values())
                    .filter(type -> type.isAcceptedClass(clazz))
                    .findFirst();
        }
    }

    private static final class None { }

    public enum TransformerMethods {
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

        TransformerMethods(Method method) {
            this.method = method;
        }

        public Object invokeOrDefault(Object obj, Object defaultValue, Object... args) {
            Object returnValue = Reflection.invokeMethodOrNull(method, obj, args);

            return returnValue == null ? defaultValue : returnValue;
        }
    }
}
