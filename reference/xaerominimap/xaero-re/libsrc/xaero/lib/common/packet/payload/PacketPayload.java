/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xaero.lib.common.packet.type.PacketType;

public class PacketPayload<P>
implements CustomPacketPayload {
    private final PacketType<P> packetType;
    private final P packet;
    public final CustomPacketPayload.Type<PacketPayload<?>> type;

    public PacketPayload(PacketType<P> packetType, P packet, CustomPacketPayload.Type<PacketPayload<?>> type) {
        this.packetType = packetType;
        this.packet = packet;
        this.type = type;
    }

    public PacketType<P> getPacketType() {
        return this.packetType;
    }

    public P getPacket() {
        return this.packet;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return this.type;
    }
}

