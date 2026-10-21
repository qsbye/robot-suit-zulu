/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config.profile;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.XaeroLib;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.platform.Services;

public final class ConfigProfileEditPacket
extends AbstractConfigPacket<ConfigProfileEditPacket> {
    private final String profileId;

    public ConfigProfileEditPacket(String profileId, List<AbstractConfigPacket.OptionEntry> entries) {
        super(entries);
        this.profileId = profileId;
    }

    @Override
    protected void writeExtra(CompoundTag tag) {
        tag.putString("i", this.profileId);
    }

    public static ConfigProfileEditPacket read(FriendlyByteBuf buffer) {
        try {
            CompoundTag tag = (CompoundTag)buffer.readNbt(NbtAccounter.create((long)0x800000L));
            if (tag == null) {
                return null;
            }
            List<AbstractConfigPacket.OptionEntry> entries = ConfigProfileEditPacket.readOptions(tag);
            String profileId = tag.getStringOr("i", "");
            return new ConfigProfileEditPacket(profileId, entries);
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment() || !Services.PLATFORM.isDedicatedServer()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public static final class ServerHandler
    implements BiConsumer<ConfigProfileEditPacket, ServerPlayer> {
        @Override
        public void accept(ConfigProfileEditPacket message, ServerPlayer player) {
            block2: {
                try {
                    ServerPlayerData.get(player).getConfigSynchronizer().onEditProfilePacket(message.profileId, message.getEntries());
                }
                catch (Throwable t) {
                    if (!Services.PLATFORM.isDevelopmentEnvironment()) break block2;
                    XaeroLib.LOGGER.error("packet error", t);
                }
            }
        }
    }

    public static final class ClientHandler
    implements Consumer<ConfigProfileEditPacket> {
        @Override
        public void accept(ConfigProfileEditPacket message) {
            try {
                XaeroLib.INSTANCE.getClient().getConfigSynchronizer().onEditProfilePacket(message.profileId, message.getEntries());
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }
}

