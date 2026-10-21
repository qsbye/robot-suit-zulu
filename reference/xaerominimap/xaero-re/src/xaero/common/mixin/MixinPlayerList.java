/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.server.core.XaeroMinimapServerCore;

@Mixin(value={PlayerList.class})
public class MixinPlayerList {
    @Inject(at={@At(value="HEAD")}, method={"sendLevelInfo"})
    public void onSendWorldInfo(ServerPlayer player, ServerLevel world, CallbackInfo info) {
        XaeroMinimapServerCore.onServerWorldInfo((Player)player);
    }
}

