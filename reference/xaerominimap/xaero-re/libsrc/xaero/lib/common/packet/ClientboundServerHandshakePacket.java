/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import xaero.lib.XaeroLib;
import xaero.lib.platform.Services;

public class ClientboundServerHandshakePacket {
    public static ClientboundServerHandshakePacket read(FriendlyByteBuf buf) {
        try {
            buf.readByte();
            return new ClientboundServerHandshakePacket();
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment() || !Services.PLATFORM.isDedicatedServer()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(1);
    }

    public static final class ClientHandler
    implements Consumer<ClientboundServerHandshakePacket> {
        @Override
        public void accept(ClientboundServerHandshakePacket packet) {
            try {
                XaeroLib.INSTANCE.getClient().getConfigSynchronizer().reset();
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }
}

