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

package site.liangbai.forgeeventbridge.v1_15_2_1_16_5.serviceprovider;

import site.liangbai.forgeeventbridge.asm.classcreator.IClassCreator;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;
import site.liangbai.forgeeventbridge.event.EventBridge;
import site.liangbai.forgeeventbridge.event.IEventBusProxy;
import site.liangbai.forgeeventbridge.serviceprovider.IServiceProvider;
import site.liangbai.forgeeventbridge.v1_15_2_1_16_5.classcreator.ASMClassCreator;
import site.liangbai.forgeeventbridge.v1_15_2_1_16_5.constantsprovider.ConstantsProviderImpl;
import site.liangbai.forgeeventbridge.v1_15_2_1_16_5.event.EventBusProxyImpl;

public final class ServiceProviderImpl implements IServiceProvider {
    private static final IClassCreator CLASS_CREATOR = new ASMClassCreator();

    private static final IConstantsProvider CONSTANTS_PROVIDER = new ConstantsProviderImpl();

    @Override
    public IClassCreator getClassCreator() {
        return CLASS_CREATOR;
    }

    @Override
    public IConstantsProvider getConstantsProvider() {
        return CONSTANTS_PROVIDER;
    }

    @Override
    public IEventBusProxy getEventBusProxy(EventBridge.Bus bus) {
        return new EventBusProxyImpl(bus);
    }
}
