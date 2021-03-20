package site.liangbai.forgeeventbridge.util;

import org.jetbrains.annotations.NotNull;

public final class ClassLoaderUtil {

    @NotNull
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            classLoader = ClassLoaderUtil.getClassLoader();
        }

        return classLoader;
    }
}
