/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.packet.basic;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import xaero.common.XaeroMinimapSession;
import xaero.common.server.player.ServerPlayerData;

public class HandshakePacket {
    public static final int NETWORK_COMPATIBILITY = 3;
    private final int networkVersion;

    public HandshakePacket(int networkVersion) {
        this.networkVersion = networkVersion;
    }

    public HandshakePacket() {
        this(3);
    }

    public void write(FriendlyByteBuf u) {
        u.writeInt(this.networkVersion);
    }

    public static HandshakePacket read(FriendlyByteBuf buffer) {
        return new HandshakePacket(buffer.readInt());
    }

    public static class ServerHandler
    implements BiConsumer<HandshakePacket, ServerPlayer> {
        @Override
        public void accept(HandshakePacket message, ServerPlayer player) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            playerData.setClientModNetworkVersion(message.networkVersion);
        }
    }

    public static class ClientHandler
    implements Consumer<HandshakePacket> {
        @Override
        public void accept(HandshakePacket message) {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            session.getMinimapProcessor().setServerModNetworkVersion(message.networkVersion);
            session.getModMain().getMessageHandler().sendToServer((Object)new HandshakePacket());
        }
    }
}

