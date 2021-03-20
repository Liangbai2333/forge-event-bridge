package site.liangbai.forgeeventbridge.wrapper.creator;

import net.minecraft.entity.Entity;
import site.liangbai.forgeeventbridge.wrapper.EntityWrapper;

public final class EntityWrapperCreator implements IWrapperCreator<EntityWrapper> {
    @Override
    public EntityWrapper create(Object object) {
        if (!(object instanceof Entity)) return null;

        return new EntityWrapper(object);
    }
}
