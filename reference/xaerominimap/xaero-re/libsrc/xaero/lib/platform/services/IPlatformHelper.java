/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform.services;

import java.nio.file.Path;
import net.minecraft.resources.Identifier;
import xaero.lib.client.controls.util.IKeyMappingHelper;
import xaero.lib.client.graphics.util.IPlatformRenderDeviceHelper;
import xaero.lib.client.graphics.util.IPlatformRenderHelper;
import xaero.lib.common.compat.PlatformModCompatibility;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.common.reflection.util.IObfuscatedReflection;

public interface IPlatformHelper {
    public String getPlatformName();

    public boolean isModLoaded(String var1);

    default public boolean checkModForMixin(String modId) {
        return this.isModLoaded(modId);
    }

    public boolean isDevelopmentEnvironment();

    default public String getEnvironmentName() {
        return this.isDevelopmentEnvironment() ? "development" : "production";
    }

    public boolean isDedicatedServer();

    public IPacketHandler createPacketHandler(Identifier var1, int var2, String var3);

    public PlatformModCompatibility createPlatformModCompatibility();

    public IKeyMappingHelper getKeyMappingHelper();

    public IPlatformRenderHelper getPlatformRenderHelper();

    public IPlatformRenderDeviceHelper getRenderDeviceHelper();

    public IObfuscatedReflection getObfuscatedReflection();

    public Path getGameDir();

    public Path getConfigDir();
}

