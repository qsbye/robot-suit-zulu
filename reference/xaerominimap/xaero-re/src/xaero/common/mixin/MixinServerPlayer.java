/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.server.player.IServerPlayer;
import xaero.common.server.player.ServerPlayerData;

@Mixin(value={ServerPlayer.class})
public class MixinServerPlayer
implements IServerPlayer {
    private ServerPlayerData xaeroMinimapPlayerData;

    @Override
    public ServerPlayerData getXaeroMinimapPlayerData() {
        return this.xaeroMinimapPlayerData;
    }

    @Override
    public void setXaeroMinimapPlayerData(ServerPlayerData data) {
        this.xaeroMinimapPlayerData = data;
    }
}

