/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import xaero.lib.common.packet.PacketHandlerFull;
import xaero.lib.common.packet.PacketReceiver;
import xaero.lib.common.packet.type.PacketType;

public abstract class ClientPacketReceiver
extends PacketReceiver<Object> {
    public ClientPacketReceiver(PacketHandlerFull packetHandlerFull) {
        super(packetHandlerFull);
    }

    @Override
    protected <T> boolean isCorrectSide(PacketType<T> packetType) {
        return packetType.getClientHandler() != null;
    }

    @Override
    protected <T> Runnable getTask(PacketType<T> packetType, T packet, Object context) {
        return () -> packetType.getClientHandler().accept(packet);
    }
}

