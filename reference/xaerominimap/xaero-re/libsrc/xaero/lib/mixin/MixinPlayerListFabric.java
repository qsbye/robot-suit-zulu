/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;

@Mixin(value={PlayerList.class})
public class MixinPlayerListFabric {
    @Inject(at={@At(value="TAIL")}, method={"placeNewPlayer"})
    public void onPlaceNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo info) {
        if (!XaeroLib.isLoaded()) {
            return;
        }
        ((XaeroLibFabric)XaeroLib.INSTANCE).getCommonEvents().playerLogIn((Player)serverPlayer);
    }

    @Inject(at={@At(value="HEAD")}, method={"op"})
    public void onOp(NameAndId profile, CallbackInfo info) {
        ((XaeroLibFabric)XaeroLib.INSTANCE).getCommonEvents().onPlayerPermissionsChanged((PlayerList)this, profile);
    }

    @Inject(at={@At(value="HEAD")}, method={"deop"})
    public void onDeop(NameAndId profile, CallbackInfo info) {
        ((XaeroLibFabric)XaeroLib.INSTANCE).getCommonEvents().onPlayerPermissionsChanged((PlayerList)this, profile);
    }
}

