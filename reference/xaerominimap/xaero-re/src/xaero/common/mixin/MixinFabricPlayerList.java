/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.server.core.XaeroMinimapServerCore;

@Mixin(value={PlayerList.class})
public class MixinFabricPlayerList {
    @Inject(at={@At(value="TAIL")}, method={"placeNewPlayer"})
    public void onPlaceNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo info) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getCommonEvents().onPlayerLogIn((Player)serverPlayer);
    }
}

