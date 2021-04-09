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

import net.minecraft.dispenser.Position;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import org.bukkit.Location;
import site.liangbai.forgeeventbridge.wrapper.WrapperTransformer;

public final class Transformer {
    public static void init() {
        WrapperTransformer.Type.ENTITY.setTransformer(obj ->
                TransformerMethod.BUKKIT_ENTITY_GETTER.invokeOrDefault(obj, obj));

        WrapperTransformer.Type.LOCATION.setTransformer(obj -> {
            if (obj instanceof BlockPos) {
                BlockPos blockPos = ((BlockPos) obj);

                return new Location(null, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }

            if (obj instanceof Vector3d) {
                Vector3d vector3d = ((Vector3d) obj);

                return new Location(null, vector3d.x(), vector3d.y(), vector3d.z());
            }

            if (obj instanceof Position) {
                Position position = ((Position) obj);

                return new Location(null, position.x(), position.y(), position.z());
            }

            if (obj instanceof Rotations) {
                Rotations rotations = ((Rotations) obj);

                return new Location(null, rotations.getX(), rotations.getY(), rotations.getZ());
            }

            if (obj instanceof ChunkPos) {
                ChunkPos chunkPos = ((ChunkPos) obj);

                return new Location(null, chunkPos.x, 0, chunkPos.z);
            }

            return null;
        });

        WrapperTransformer.Type.ITEM_STACK.setTransformer(obj ->
                TransformerMethod.CRAFT_ITEM_STACK_AS_BUKKIT_COPY
                        .invokeOrDefault(null, obj, obj));

        WrapperTransformer.Type.WORLD.setTransformer(obj ->
                TransformerMethod.WORLD_GETTER.invokeOrDefault(obj, obj));
    }
}
