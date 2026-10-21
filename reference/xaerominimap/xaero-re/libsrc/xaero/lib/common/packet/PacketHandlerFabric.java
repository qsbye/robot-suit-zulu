/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.packet.ClientPacketHandlerFabric;
import xaero.lib.common.packet.PacketHandlerFull;
import xaero.lib.common.packet.ServerPacketReceiverFabric;
import xaero.lib.common.packet.payload.PacketPayload;
import xaero.lib.common.packet.payload.PacketPayloadCodec;
import xaero.lib.common.packet.type.PacketTypeManager;

public class PacketHandlerFabric
extends PacketHandlerFull {
    private ClientPacketHandlerFabric clientPacketHandlerFabric;

    private PacketHandlerFabric(Identifier channelId, PacketTypeManager packetTypeManager, CustomPacketPayload.Type<PacketPayload<?>> type) {
        super(channelId, packetTypeManager, type);
    }

    private void setClientPacketHandlerFabric(ClientPacketHandlerFabric clientPacketHandlerFabric) {
        this.clientPacketHandlerFabric = clientPacketHandlerFabric;
    }

    @Override
    public <T> void sendToServer(T packet) {
        this.clientPacketHandlerFabric.sendToServer(packet);
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, T packet) {
        ServerPlayNetworking.send((ServerPlayer)player, new PacketPayload<T>(this.packetTypeManager.getType(packet), packet, this.type));
    }

    public static class Builder
    extends PacketHandlerFull.Builder<Builder> {
        private Builder() {
        }

        @Override
        public Builder setDefault() {
            return this;
        }

        @Override
        public PacketHandlerFabric build() {
            return (PacketHandlerFabric)super.build();
        }

        @Override
        protected PacketHandlerFull buildInternal(PacketTypeManager packetTypeManager, CustomPacketPayload.Type<PacketPayload<?>> type) {
            PacketHandlerFabric result = new PacketHandlerFabric(this.channelId, packetTypeManager, type);
            PacketPayloadCodec payloadCodec = new PacketPayloadCodec(result);
            PayloadTypeRegistry.clientboundPlay().register(type, (StreamCodec)payloadCodec);
            PayloadTypeRegistry.serverboundPlay().register(type, (StreamCodec)payloadCodec);
            ServerPlayNetworking.registerGlobalReceiver(type, (ServerPlayNetworking.PlayPayloadHandler)new ServerPacketReceiverFabric(result));
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientPacketHandlerFabric clientHandler = new ClientPacketHandlerFabric(result);
                result.setClientPacketHandlerFabric(clientHandler);
                clientHandler.registerOnClient();
            }
            return result;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

