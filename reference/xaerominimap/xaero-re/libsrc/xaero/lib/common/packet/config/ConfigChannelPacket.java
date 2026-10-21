/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.XaeroLib;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.platform.Services;

public class ConfigChannelPacket {
    private final Identifier channelId;

    public ConfigChannelPacket(Identifier channelId) {
        this.channelId = channelId;
    }

    public void write(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        tag.putString("c", this.channelId.toString());
        buf.writeNbt((Tag)tag);
    }

    public static ConfigChannelPacket read(FriendlyByteBuf buf) {
        try {
            CompoundTag tag = (CompoundTag)buf.readNbt(NbtAccounter.create((long)65536L));
            String channelIdString = tag.getStringOr("c", "");
            return new ConfigChannelPacket(Identifier.parse((String)channelIdString));
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment() || !Services.PLATFORM.isDedicatedServer()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public static final class ServerHandler
    implements BiConsumer<ConfigChannelPacket, ServerPlayer> {
        @Override
        public void accept(ConfigChannelPacket packet, ServerPlayer player) {
            block2: {
                try {
                    ServerPlayerData.get(player).getConfigSynchronizer().receiveChannelId(packet.channelId);
                }
                catch (Throwable t) {
                    ServerPlayerData.get(player).getConfigSynchronizer().receiveChannelId(null);
                    if (!Services.PLATFORM.isDevelopmentEnvironment()) break block2;
                    XaeroLib.LOGGER.error("packet error", t);
                }
            }
        }
    }

    public static final class ClientHandler
    implements Consumer<ConfigChannelPacket> {
        @Override
        public void accept(ConfigChannelPacket packet) {
            try {
                XaeroLib.INSTANCE.getClient().getConfigSynchronizer().receiveChannelId(packet.channelId);
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }
}

