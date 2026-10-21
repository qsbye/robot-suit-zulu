/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xaero.lib.common.packet.ClientPacketReceiverFabric;
import xaero.lib.common.packet.PacketHandlerFabric;
import xaero.lib.common.packet.payload.PacketPayload;

public class ClientPacketHandlerFabric {
    private final PacketHandlerFabric packetHandlerFabric;

    public ClientPacketHandlerFabric(PacketHandlerFabric packetHandlerFabric) {
        this.packetHandlerFabric = packetHandlerFabric;
    }

    public void registerOnClient() {
        ClientPlayNetworking.registerGlobalReceiver((CustomPacketPayload.Type)this.packetHandlerFabric.type, (ClientPlayNetworking.PlayPayloadHandler)new ClientPacketReceiverFabric(this.packetHandlerFabric));
    }

    public <T> void sendToServer(T packet) {
        ClientPlayNetworking.send(new PacketPayload<T>(this.packetHandlerFabric.packetTypeManager.getType(packet), packet, this.packetHandlerFabric.type));
    }
}

