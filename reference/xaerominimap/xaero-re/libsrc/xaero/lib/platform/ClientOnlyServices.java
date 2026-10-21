/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform;

import java.util.ServiceLoader;
import xaero.lib.XaeroLib;
import xaero.lib.platform.services.IPlatformClientOnlyHelper;

public class ClientOnlyServices {
    public static final IPlatformClientOnlyHelper PLATFORM = ClientOnlyServices.load(IPlatformClientOnlyHelper.class);

    public static <T> T load(Class<T> clazz) {
        Object loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        XaeroLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}

