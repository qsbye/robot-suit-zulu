/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.packet.PacketHandlerFull;
import xaero.lib.common.packet.PacketReceiver;
import xaero.lib.common.packet.type.PacketType;

public class ServerPacketReceiver
extends PacketReceiver<ServerPlayer> {
    public ServerPacketReceiver(PacketHandlerFull packetHandlerFull) {
        super(packetHandlerFull);
    }

    @Override
    protected <T> boolean isCorrectSide(PacketType<T> packetType) {
        return packetType.getServerHandler() != null;
    }

    @Override
    protected <T> Runnable getTask(PacketType<T> packetType, T packet, ServerPlayer player) {
        return () -> packetType.getServerHandler().accept(packet, player);
    }
}

