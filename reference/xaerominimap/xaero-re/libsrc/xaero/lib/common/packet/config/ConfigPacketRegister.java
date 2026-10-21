/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config;

import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.packet.config.ClientboundEnforcedConfigPacket;
import xaero.lib.common.packet.config.ConfigChannelPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileCreatePacket;
import xaero.lib.common.packet.config.profile.ConfigProfileDeletePacket;
import xaero.lib.common.packet.config.profile.ConfigProfileEditPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileInfoPacket;
import xaero.lib.common.packet.config.profile.ServerboundConfigProfileRequestPacket;

public class ConfigPacketRegister {
    public int register(IPacketHandler packetHandler, int nextIndex) {
        packetHandler.register(nextIndex++, ConfigChannelPacket.class, ConfigChannelPacket::write, ConfigChannelPacket::read, new ConfigChannelPacket.ServerHandler(), new ConfigChannelPacket.ClientHandler());
        packetHandler.register(nextIndex++, ClientboundEnforcedConfigPacket.class, AbstractConfigPacket::write, ClientboundEnforcedConfigPacket::read, null, new ClientboundEnforcedConfigPacket.ClientHandler());
        packetHandler.register(nextIndex++, ConfigProfileEditPacket.class, AbstractConfigPacket::write, ConfigProfileEditPacket::read, new ConfigProfileEditPacket.ServerHandler(), new ConfigProfileEditPacket.ClientHandler());
        packetHandler.register(nextIndex++, ConfigProfileInfoPacket.class, ConfigProfileInfoPacket::write, ConfigProfileInfoPacket::read, new ConfigProfileInfoPacket.ServerHandler(), new ConfigProfileInfoPacket.ClientHandler());
        packetHandler.register(nextIndex++, ServerboundConfigProfileRequestPacket.class, ServerboundConfigProfileRequestPacket::write, ServerboundConfigProfileRequestPacket::read, new ServerboundConfigProfileRequestPacket.ServerHandler(), null);
        packetHandler.register(nextIndex++, ConfigProfileDeletePacket.class, ConfigProfileDeletePacket::write, ConfigProfileDeletePacket::read, new ConfigProfileDeletePacket.ServerHandler(), new ConfigProfileDeletePacket.ClientHandler());
        packetHandler.register(nextIndex++, ConfigProfileCreatePacket.class, ConfigProfileCreatePacket::write, ConfigProfileCreatePacket::read, new ConfigProfileCreatePacket.ServerHandler(), null);
        return nextIndex;
    }
}

