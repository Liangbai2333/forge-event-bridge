package site.liangbai.forgeeventbridge.event;

import site.liangbai.forgeeventbridge.util.Reflection;

import java.lang.reflect.Method;

public class CustomEventBusProxy implements IEventBusProxy {
    private final Object bus;
    private final Method registerMethod;
    private final Method unregisterMethod;

    public CustomEventBusProxy(Object bus) {
        this.bus = bus;
        this.registerMethod = Reflection.findMethodOrNull(bus.getClass(), "register", Object.class);
        this.unregisterMethod = Reflection.findMethodOrNull(bus.getClass(), "unregister", Object.class);
    }

    @Override
    public void register(Object object) {
        Reflection.invokeMethodOrNull(registerMethod, bus, object);
    }

    @Override
    public void unregister(Object object) {
        Reflection.invokeMethodOrNull(unregisterMethod, bus, object);
    }
}
