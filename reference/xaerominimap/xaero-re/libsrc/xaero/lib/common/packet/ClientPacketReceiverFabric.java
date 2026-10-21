/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import xaero.lib.common.packet.ClientPacketReceiver;
import xaero.lib.common.packet.PacketHandlerFabric;
import xaero.lib.common.packet.payload.PacketPayload;

public class ClientPacketReceiverFabric
extends ClientPacketReceiver
implements ClientPlayNetworking.PlayPayloadHandler<PacketPayload<?>> {
    public ClientPacketReceiverFabric(PacketHandlerFabric packetHandlerFabric) {
        super(packetHandlerFabric);
    }

    public void receive(PacketPayload<?> payload, ClientPlayNetworking.Context context) {
        this.receive((ReentrantBlockableEventLoop<?>)context.client(), payload, null);
    }
}

