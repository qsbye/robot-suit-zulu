/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config.profile;

import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.XaeroLib;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.platform.Services;

public final class ConfigProfileCreatePacket {
    private final String id;
    private final String name;
    private final String profileToCopy;

    public ConfigProfileCreatePacket(String id, String name, String profileToCopy) {
        this.id = id;
        this.name = name;
        this.profileToCopy = profileToCopy;
    }

    public void write(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putString("i", this.id);
        tag.putString("n", this.name);
        tag.putString("c", this.profileToCopy);
        buffer.writeNbt((Tag)tag);
    }

    public static ConfigProfileCreatePacket read(FriendlyByteBuf buffer) {
        try {
            CompoundTag tag = (CompoundTag)buffer.readNbt(NbtAccounter.unlimitedHeap());
            if (tag == null) {
                return null;
            }
            String id = tag.getStringOr("i", "");
            String name = tag.getStringOr("n", "");
            String profileToCopy = tag.getStringOr("c", "");
            return new ConfigProfileCreatePacket(id, name, profileToCopy);
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment() || !Services.PLATFORM.isDedicatedServer()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public static final class ServerHandler
    implements BiConsumer<ConfigProfileCreatePacket, ServerPlayer> {
        @Override
        public void accept(ConfigProfileCreatePacket packet, ServerPlayer player) {
            block2: {
                try {
                    ServerPlayerData.get(player).getConfigSynchronizer().onCreateConfigProfilePacket(packet.id, packet.name, packet.profileToCopy);
                }
                catch (Throwable t) {
                    if (!Services.PLATFORM.isDevelopmentEnvironment()) break block2;
                    XaeroLib.LOGGER.error("packet error", t);
                }
            }
        }
    }
}

