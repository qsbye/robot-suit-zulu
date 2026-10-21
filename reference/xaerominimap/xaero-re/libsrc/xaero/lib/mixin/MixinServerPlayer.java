/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import xaero.lib.common.player.IServerPlayer;
import xaero.lib.common.player.ServerPlayerData;

@Mixin(value={ServerPlayer.class})
public class MixinServerPlayer
implements IServerPlayer {
    private ServerPlayerData xaerolib_data;

    @Override
    public ServerPlayerData xaerolib_getData() {
        return this.xaerolib_data;
    }

    @Override
    public void xaerolib_setData(ServerPlayerData data) {
        this.xaerolib_data = data;
    }
}

