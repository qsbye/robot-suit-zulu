/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform;

import java.util.ServiceLoader;
import xaero.lib.platform.services.IPlatformMixinHelper;

public class MixinServices {
    public static final IPlatformMixinHelper<?, ?> PLATFORM = MixinServices.load(IPlatformMixinHelper.class);

    public static <T> T load(Class<T> clazz) {
        Object loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load mixin service for " + clazz.getName()));
        return loadedService;
    }
}

