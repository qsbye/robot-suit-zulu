/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform.services;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import xaero.lib.platform.services.IPlatformMixinHelper;

public class PlatformMixinHelperFabric
implements IPlatformMixinHelper<ModContainer, Version> {
    @Override
    public ModContainer getLoadingModInfo(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElse(null);
    }

    @Override
    public Version getModVersion(ModContainer modContainer) {
        return modContainer.getMetadata().getVersion();
    }

    @Override
    public Version getVersionFromString(String versionString) {
        try {
            return Version.parse((String)versionString);
        }
        catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }
}

