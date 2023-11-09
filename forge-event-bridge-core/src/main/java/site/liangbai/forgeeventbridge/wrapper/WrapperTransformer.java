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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public final class WrapperTransformer {

    @SuppressWarnings("unchecked")
    public static <T> T require(Class<?> requireType, Object otherObj) {
        return (T) Type.matchType(requireType)
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
}
