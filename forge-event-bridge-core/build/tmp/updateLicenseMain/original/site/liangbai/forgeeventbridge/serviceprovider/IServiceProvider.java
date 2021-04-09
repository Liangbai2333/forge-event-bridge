package site.liangbai.forgeeventbridge.serviceprovider;

import site.liangbai.forgeeventbridge.asm.classcreator.IClassCreator;
import site.liangbai.forgeeventbridge.asm.constantsprovider.IConstantsProvider;

public interface IServiceProvider {
    IClassCreator getClassCreator();

    IConstantsProvider getConstantsProvider();
}
