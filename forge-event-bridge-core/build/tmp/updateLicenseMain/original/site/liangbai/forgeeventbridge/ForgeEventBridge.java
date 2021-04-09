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
