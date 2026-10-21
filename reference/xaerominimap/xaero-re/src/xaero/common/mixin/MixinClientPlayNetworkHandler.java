/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={ClientPacketListener.class})
public class MixinClientPlayNetworkHandler
implements IXaeroMinimapClientPlayNetHandler {
    XaeroMinimapSession xaero_minimapSession;

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleChunkBlocksUpdate"})
    public void onOnChunkDeltaUpdate(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo info) {
        XaeroMinimapCore.onMultiBlockChange(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleLevelChunkWithLight"})
    public void onHandleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet, CallbackInfo info) {
        XaeroMinimapCore.onHandleLevelChunkWithLight(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleLightUpdatePacket"})
    public void onHandleLightUpdatePacket(ClientboundLightUpdatePacket packet, CallbackInfo info) {
        XaeroMinimapCore.onHandleLightUpdatePacket(packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"queueLightRemoval"})
    public void onQueueLightRemoval(ClientboundForgetLevelChunkPacket packet, CallbackInfo info) {
        XaeroMinimapCore.onQueueLightRemoval(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleBlockUpdate"})
    public void onOnBlockUpdate(ClientboundBlockUpdatePacket packet, CallbackInfo info) {
        XaeroMinimapCore.onBlockChange(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleSetSpawn"})
    public void onOnPlayerSpawnPosition(ClientboundSetDefaultSpawnPositionPacket packet, CallbackInfo info) {
        XaeroMinimapCore.onSpawn(packet);
    }

    @Override
    public XaeroMinimapSession getXaero_minimapSession() {
        return this.xaero_minimapSession;
    }

    @Override
    public void setXaero_minimapSession(XaeroMinimapSession session) {
        this.xaero_minimapSession = session;
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V")}, method={"handleLogin"})
    public void onOnGameJoin(ClientboundLoginPacket packet, CallbackInfo info) {
        XaeroMinimapCore.onPlayNetHandler((ClientPacketListener)this, packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"close"})
    public void onClose(CallbackInfo info) {
        XaeroMinimapCore.onPlayNetHandlerCleanup((ClientPacketListener)this);
    }

    @Inject(at={@At(value="HEAD")}, method={"sendCommand(Ljava/lang/String;)V"}, cancellable=true)
    public void onSendCommand(String string_1, CallbackInfo info) {
        if (XaeroMinimapCore.onLocalPlayerCommand(string_1)) {
            info.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"sendUnattendedCommand(Ljava/lang/String;Lnet/minecraft/client/gui/screens/Screen;)V"}, cancellable=true)
    public void onSendUnattendedCommand(String string_1, Screen screen, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (HudMod.INSTANCE.getEvents().handleClientSendChatEvent(string_1)) {
            info.cancel();
        }
    }
}

