/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.Entity;
import xaero.lib.common.packet.PacketHandlerFabric;
import xaero.lib.common.packet.ServerPacketReceiver;
import xaero.lib.common.packet.payload.PacketPayload;
import xaero.lib.common.util.EntityServerUtils;

public class ServerPacketReceiverFabric
extends ServerPacketReceiver
implements ServerPlayNetworking.PlayPayloadHandler<PacketPayload<?>> {
    public ServerPacketReceiverFabric(PacketHandlerFabric packetHandlerFabric) {
        super(packetHandlerFabric);
    }

    public void receive(PacketPayload<?> payload, ServerPlayNetworking.Context context) {
        this.receive((ReentrantBlockableEventLoop<?>)EntityServerUtils.getServer((Entity)context.player()), payload, context.player());
    }
}

