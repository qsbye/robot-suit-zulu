/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.payload;

import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import xaero.lib.common.packet.PacketHandlerFull;
import xaero.lib.common.packet.payload.PacketPayload;
import xaero.lib.common.packet.type.PacketType;

public class PacketPayloadCodec
implements StreamCodec<FriendlyByteBuf, PacketPayload<?>> {
    private final PacketHandlerFull packetHandlerFull;

    public PacketPayloadCodec(PacketHandlerFull packetHandlerFull) {
        this.packetHandlerFull = packetHandlerFull;
    }

    public <T> void encodeTyped(FriendlyByteBuf buf, PacketPayload<T> payload) {
        PacketHandlerFull.encodePacket(payload.getPacketType(), payload.getPacket(), buf);
    }

    public void encode(FriendlyByteBuf buf, PacketPayload<?> packetPayload) {
        this.encodeTyped(buf, packetPayload);
    }

    @Nonnull
    public PacketPayload<?> decode(FriendlyByteBuf friendlyByteBuf) {
        if (friendlyByteBuf.readableBytes() <= 0) {
            return new PacketPayload<Object>(null, null, this.packetHandlerFull.getType());
        }
        byte index = friendlyByteBuf.readByte();
        PacketType<?> packetType = this.packetHandlerFull.getByIndex(index);
        return this.readPacketPayloadTyped(packetType, friendlyByteBuf);
    }

    private <P> PacketPayload<P> readPacketPayloadTyped(PacketType<P> packetType, FriendlyByteBuf friendlyByteBuf) {
        return new PacketPayload<P>(packetType, packetType.getDecoder().apply(friendlyByteBuf), this.packetHandlerFull.getType());
    }
}

