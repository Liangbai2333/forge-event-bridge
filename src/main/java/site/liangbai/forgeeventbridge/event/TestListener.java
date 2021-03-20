package site.liangbai.forgeeventbridge.event;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;
import site.liangbai.forgeeventbridge.wrapper.creator.WrapperCreators;

@ForgeEventHandler(
        @ForgeEventHandler.Target(
                LivingHurtEvent.class
        )
)
public class TestListener implements EventHandler<EventWrapper.EventObject> {
    public TestListener() {
        register();
    }

    @Override
    public void handle(EventWrapper<EventWrapper.EventObject> eventWrapper) {
        System.out.println(eventWrapper.invokeWrapper("getEntityLiving", WrapperCreators.ENTITY).asEntity().getEntityId());
    }
}
