/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.client.level.ClientLevelData;

@Mixin(value={ClientPacketListener.class})
public class MixinClientPacketListener {
    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleInitializeBorder"})
    public void onHandleInitializeBorder(ClientboundInitializeBorderPacket clientboundInitializeBorderPacket, CallbackInfo info) {
        if (!XaeroLib.isLoaded()) {
            return;
        }
        Minecraft.getInstance().schedule(() -> {
            if (Minecraft.getInstance().level == null) {
                return;
            }
            ClientLevelData clientLevelData = ClientLevelData.get(Minecraft.getInstance().level);
            if (!clientLevelData.serverHasMod()) {
                XaeroLib.LOGGER.warn("Server side doesn't have XaeroLib installed! Resetting.");
                XaeroLib.INSTANCE.getClient().getConfigSynchronizer().reset();
            }
        });
    }
}

