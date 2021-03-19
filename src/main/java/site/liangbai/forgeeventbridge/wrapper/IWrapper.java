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

import site.liangbai.forgeeventbridge.wrapper.creator.IWrapperCreator;

public interface IWrapper {
    <T> T as(String fieldName, Class<T> cast);

    <T> T invoke(String methodName, Class<T> cast, Object... args);

    <T> T get(String name, Class<T> cast);

    void set(String name, Object value);

    default <T extends ObjectWrapper> T asWrapper(String fieldName, IWrapperCreator<T> wrapperCreator) {
        return wrapperCreator.create(as(fieldName, Object.class));
    }

    default <T extends ObjectWrapper> T invokeWrapper(String methodName, IWrapperCreator<T> wrapperCreator, Object... args) {
        return wrapperCreator.create(invoke(methodName, Object.class));
    }
}
