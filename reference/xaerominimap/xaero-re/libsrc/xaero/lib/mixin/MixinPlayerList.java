/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.common.packet.ClientboundDimensionHandshakePacket;

@Mixin(value={PlayerList.class})
public class MixinPlayerList {
    @Inject(at={@At(value="HEAD")}, method={"sendLevelInfo"})
    public void onSendLevelInfo(ServerPlayer player, ServerLevel world, CallbackInfo info) {
        if (!XaeroLib.isLoaded()) {
            return;
        }
        XaeroLib.INSTANCE.getPacketHandler().sendToPlayer(player, new ClientboundDimensionHandshakePacket());
    }
}

