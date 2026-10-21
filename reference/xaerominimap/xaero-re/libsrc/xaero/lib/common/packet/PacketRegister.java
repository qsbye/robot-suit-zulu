/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import xaero.lib.XaeroLib;
import xaero.lib.common.packet.ClientboundDimensionHandshakePacket;
import xaero.lib.common.packet.ClientboundServerHandshakePacket;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.common.packet.config.ConfigPacketRegister;

public class PacketRegister {
    public void register() {
        IPacketHandler packetHandler = XaeroLib.INSTANCE.getPacketHandler();
        int nextIndex = 0;
        packetHandler.register(nextIndex++, ClientboundServerHandshakePacket.class, ClientboundServerHandshakePacket::write, ClientboundServerHandshakePacket::read, null, new ClientboundServerHandshakePacket.ClientHandler());
        packetHandler.register(nextIndex++, ClientboundDimensionHandshakePacket.class, ClientboundDimensionHandshakePacket::write, ClientboundDimensionHandshakePacket::read, null, new ClientboundDimensionHandshakePacket.ClientHandler());
        nextIndex = new ConfigPacketRegister().register(packetHandler, nextIndex);
    }
}

