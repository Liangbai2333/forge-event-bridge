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
import java.util.Optional;
import java.util.function.Function;

public final class WrapperTransformer {
    static {
        Type.ENTITY.setTransformer(obj ->
                TransformerMethods.BUKKIT_ENTITY_GETTER.invokeOrDefault(obj, obj));



        Type.ENTITY.setTransformer(obj ->
                TransformerMethods.BUKKIT_ENTITY_GETTER.invokeOrDefault(obj, obj));

        Type.LOCATION.setTransformer(obj -> {
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

        Type.ITEM_STACK.setTransformer(obj ->
                TransformerMethods.CRAFT_ITEM_STACK_AS_BUKKIT_COPY
                .invokeOrDefault(null, obj, obj));

        Type.WORLD.setTransformer(obj ->
                TransformerMethods.WORLD_GETTER.invokeOrDefault(obj, obj));
    }

    public static Object require(Class<?> requireType, Object otherObj) {
        return Type.matchType(requireType)
                .orElse(Type.NONE)
                .apply(otherObj);
    }

    public enum Type {
        ENTITY(Entity.class),
        WORLD(World.class),
        ITEM_STACK(org.bukkit.inventory.ItemStack.class),
        LOCATION(Location.class),
        NONE(None.class);
        
        private final Class<?> baseAcceptedClass;

        private Function<Object, Object> transformer;

        Type(Class<?> baseAcceptedClass) {
            this(baseAcceptedClass, obj -> obj);
        }
        
        Type(Class<?> baseAcceptedClass, Function<Object, Object> transformer) {
            this.baseAcceptedClass = baseAcceptedClass;
            this.transformer = transformer;
        }
        
        public boolean isAcceptedClass(Class<?> clazz) {
            return baseAcceptedClass.isAssignableFrom(clazz);
        }

        public Function<Object, Object> getTransformer() {
            return transformer;
        }

        public void setTransformer(Function<Object, Object> transformer) {
            this.transformer = transformer;
        }

        public Object apply(Object otherObj) {
            return getTransformer().apply(otherObj);
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
