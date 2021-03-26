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

package site.liangbai.forgeeventbridge.event;

import org.jetbrains.annotations.NotNull;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;

public interface EventHolder<T extends EventWrapper.EventObject> {
    void handle(EventWrapper<T> eventWrapper);

    default void register(@NotNull EventBridge eventBridge) {
        EventRegistry.register(this, eventBridge);
    }

    default void unregister() {
        EventRegistry.unregister(this);
    }
}
