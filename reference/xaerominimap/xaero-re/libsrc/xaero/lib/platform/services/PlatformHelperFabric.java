/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform.services;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import xaero.lib.client.controls.util.IKeyMappingHelper;
import xaero.lib.client.controls.util.KeyMappingHelperFabric;
import xaero.lib.client.graphics.util.IPlatformRenderHelper;
import xaero.lib.client.graphics.util.RenderDeviceHelperFabric;
import xaero.lib.client.graphics.util.RenderHelperFabric;
import xaero.lib.common.compat.FabricModCompatibility;
import xaero.lib.common.compat.PlatformModCompatibility;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.common.packet.PacketHandlerFabric;
import xaero.lib.common.reflection.util.IObfuscatedReflection;
import xaero.lib.common.reflection.util.ObfuscatedReflectionFabric;
import xaero.lib.platform.services.IPlatformHelper;

public class PlatformHelperFabric
implements IPlatformHelper {
    private final KeyMappingHelperFabric keyMappingHelper = new KeyMappingHelperFabric();
    private final RenderHelperFabric renderHelper = new RenderHelperFabric();
    private final ObfuscatedReflectionFabric obfuscatedReflection = new ObfuscatedReflectionFabric();
    private final RenderDeviceHelperFabric renderDeviceHelper = new RenderDeviceHelperFabric();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public IKeyMappingHelper getKeyMappingHelper() {
        return this.keyMappingHelper;
    }

    @Override
    public IPlatformRenderHelper getPlatformRenderHelper() {
        return this.renderHelper;
    }

    @Override
    public RenderDeviceHelperFabric getRenderDeviceHelper() {
        return this.renderDeviceHelper;
    }

    @Override
    public IObfuscatedReflection getObfuscatedReflection() {
        return this.obfuscatedReflection;
    }

    @Override
    public IPacketHandler createPacketHandler(Identifier channelId, int protocolVersion, String protocolVersionString) {
        return ((PacketHandlerFabric.Builder)PacketHandlerFabric.Builder.begin().setChannelId(channelId)).build();
    }

    @Override
    public PlatformModCompatibility createPlatformModCompatibility() {
        return new FabricModCompatibility();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir().normalize();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

