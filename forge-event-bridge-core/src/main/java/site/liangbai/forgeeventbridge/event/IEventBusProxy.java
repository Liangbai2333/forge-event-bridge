package site.liangbai.forgeeventbridge.event;

public interface IEventBusProxy {
    void register(Object object);

    void unregister(Object object);
}
