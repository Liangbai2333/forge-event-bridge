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

package site.liangbai.forgeeventbridge;

import site.liangbai.forgeeventbridge.exception.NotFoundServiceProviderException;
import site.liangbai.forgeeventbridge.serviceprovider.IServiceProvider;

public final class ForgeEventBridge {
    private static IServiceProvider serviceProvider;

    public static IServiceProvider getServiceProvider() {
        if (serviceProvider == null) {
            throw new NotFoundServiceProviderException("could not found service provider, please initialize it.");
        }

        return serviceProvider;
    }

    public static void setServiceProvider(IServiceProvider serviceProvider) {
        ForgeEventBridge.serviceProvider = serviceProvider;
    }
}
