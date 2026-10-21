/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import xaero.lib.XaeroLib;
import xaero.lib.client.level.ClientLevelData;
import xaero.lib.platform.Services;

public class ClientboundDimensionHandshakePacket {
    public static ClientboundDimensionHandshakePacket read(FriendlyByteBuf buf) {
        try {
            buf.readByte();
            return new ClientboundDimensionHandshakePacket();
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
    implements Consumer<ClientboundDimensionHandshakePacket> {
        @Override
        public void accept(ClientboundDimensionHandshakePacket packet) {
            try {
                ClientLevelData.get(Minecraft.getInstance().level).setServerHasMod();
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }
}

