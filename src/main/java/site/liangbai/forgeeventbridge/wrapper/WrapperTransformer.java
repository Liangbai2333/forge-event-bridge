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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class WrapperTransformer {
    private static final Map<Type, Function<Object, Object>> wrapperTransformer = new HashMap<>();

    static {
        wrapperTransformer.put(Type.ENTITY, obj -> {
            if (obj instanceof net.minecraft.entity.Entity) {
                Method method = Reflection.findMethodOrNull(net.minecraft.entity.Entity.class, "getBukkitEntity");

                return Reflection.invokeMethodOrNull(method, obj);
            }

            return null;
        });

        wrapperTransformer.put(Type.LOCATION, obj -> {
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
    }

    public static Object require(Class<?> requireType, Object otherObj) {
        if (isAssignableFrom(Entity.class, requireType)) {
            return wrapperTransformer.get(Type.ENTITY).apply(otherObj);
        }

        if (isAssignableFrom(Location.class, requireType)) {
            return wrapperTransformer.get(Type.LOCATION).apply(otherObj);
        }

        return otherObj;
    }

    private static boolean isAssignableFrom(Class<?> first, Class<?> second) {
        return first.equals(second) || first.isAssignableFrom(second);
    }

    private enum Type {
        ENTITY,
        BLOCK,
        WORLD,
        ITEM_STACK,
        LOCATION,
        MINECRAFT_SERVER
    }
}
