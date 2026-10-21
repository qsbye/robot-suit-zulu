/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.server.IMinecraftServer;
import xaero.common.server.MinecraftServerData;

@Mixin(value={MinecraftServer.class})
public class MixinMinecraftServer
implements IMinecraftServer {
    private MinecraftServerData xaeroMinimapServerData;

    @Override
    public MinecraftServerData getXaeroMinimapServerData() {
        return this.xaeroMinimapServerData;
    }

    @Override
    public void setXaeroMinimapServerData(MinecraftServerData data) {
        this.xaeroMinimapServerData = data;
    }
}

