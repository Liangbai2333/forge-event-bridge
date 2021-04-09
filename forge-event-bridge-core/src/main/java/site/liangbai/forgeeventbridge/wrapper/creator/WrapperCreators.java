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

package site.liangbai.forgeeventbridge.wrapper.creator;

public final class WrapperCreators {
    public static final ObjectWrapperCreator OBJECT = new ObjectWrapperCreator();

    public static final EventWrapperCreator<?> EVENT = new EventWrapperCreator<>();

    public static final EntityWrapperCreator ENTITY = new EntityWrapperCreator();

    public static final PlayerWrapperCreator PLAYER = new PlayerWrapperCreator();

    public static final LocationWrapperCreator LOCATION = new LocationWrapperCreator();

    public static final ItemStackWrapperCreator ITEM_STACK = new ItemStackWrapperCreator();

    public static final WorldWrapperCreator WORLD = new WorldWrapperCreator();
}
