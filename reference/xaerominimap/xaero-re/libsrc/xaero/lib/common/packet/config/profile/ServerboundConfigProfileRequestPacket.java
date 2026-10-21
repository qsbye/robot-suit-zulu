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

public final class ServerboundConfigProfileRequestPacket {
    private final String profileId;

    public ServerboundConfigProfileRequestPacket(String profileId) {
        this.profileId = profileId;
    }

    public void write(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putString("i", this.profileId);
        buffer.writeNbt((Tag)tag);
    }

    public static ServerboundConfigProfileRequestPacket read(FriendlyByteBuf buffer) {
        try {
            CompoundTag tag = (CompoundTag)buffer.readNbt(NbtAccounter.create((long)65536L));
            if (tag == null) {
                return null;
            }
            String profileId = tag.getStringOr("i", "");
            return new ServerboundConfigProfileRequestPacket(profileId);
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public static final class ServerHandler
    implements BiConsumer<ServerboundConfigProfileRequestPacket, ServerPlayer> {
        @Override
        public void accept(ServerboundConfigProfileRequestPacket packet, ServerPlayer player) {
            block2: {
                try {
                    ServerPlayerData.get(player).getConfigSynchronizer().onProfileRequestPacket(packet.profileId);
                }
                catch (Throwable t) {
                    if (!Services.PLATFORM.isDevelopmentEnvironment()) break block2;
                    XaeroLib.LOGGER.error("packet error", t);
                }
            }
        }
    }
}

