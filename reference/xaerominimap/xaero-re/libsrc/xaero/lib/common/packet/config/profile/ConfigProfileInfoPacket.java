/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.sync.ClientConfigSynchronizer;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.platform.Services;

public final class ConfigProfileInfoPacket {
    private final List<Entry> entries;
    private final String defaultEnforcedProfileId;
    private final boolean reset;

    public ConfigProfileInfoPacket(List<Entry> entries, String defaultEnforcedProfileId, boolean reset) {
        this.entries = entries;
        this.defaultEnforcedProfileId = defaultEnforcedProfileId;
        this.reset = reset;
    }

    public ConfigProfileInfoPacket(String id, String name, String defaultEnforcedProfileId) {
        this(List.of((Object)new Entry(id, name)), defaultEnforcedProfileId, false);
    }

    public ConfigProfileInfoPacket(List<Entry> entries) {
        this(entries, null, false);
    }

    public ConfigProfileInfoPacket(String id, String name) {
        this(List.of((Object)new Entry(id, name)), null, false);
    }

    public void write(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        ListTag entriesTag = new ListTag();
        for (Entry entry : this.entries) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("i", entry.id);
            entryTag.putString("n", entry.name);
            entriesTag.add((Object)entryTag);
        }
        tag.put("e", (Tag)entriesTag);
        if (this.defaultEnforcedProfileId != null) {
            tag.putString("d", this.defaultEnforcedProfileId);
        }
        tag.putBoolean("r", this.reset);
        buffer.writeNbt((Tag)tag);
    }

    public static ConfigProfileInfoPacket read(FriendlyByteBuf buffer) {
        try {
            CompoundTag tag = (CompoundTag)buffer.readNbt(NbtAccounter.unlimitedHeap());
            if (tag == null) {
                return null;
            }
            ArrayList<Entry> entries = new ArrayList<Entry>();
            ListTag entriesTag = tag.getListOrEmpty("e");
            for (Tag entryTag : entriesTag) {
                CompoundTag entryTagCast = (CompoundTag)entryTag;
                String id = entryTagCast.getStringOr("i", "");
                String name = entryTagCast.getStringOr("n", "");
                entries.add(new Entry(id, name));
            }
            String defaultEnforcedProfileId = tag.getStringOr("d", null);
            boolean reset = tag.getBooleanOr("r", false);
            return new ConfigProfileInfoPacket(entries, defaultEnforcedProfileId, reset);
        }
        catch (Throwable t) {
            if (Services.PLATFORM.isDevelopmentEnvironment() || !Services.PLATFORM.isDedicatedServer()) {
                XaeroLib.LOGGER.error("packet error", t);
            }
            return null;
        }
    }

    public static final class Entry {
        public final String id;
        public final String name;

        public Entry(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static final class ClientHandler
    implements Consumer<ConfigProfileInfoPacket> {
        @Override
        public void accept(ConfigProfileInfoPacket packet) {
            try {
                ClientConfigSynchronizer synchronizer = XaeroLib.INSTANCE.getClient().getConfigSynchronizer();
                synchronizer.onConfigProfileInfoPacket(packet.entries, packet.defaultEnforcedProfileId, packet.reset);
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }

    public static final class ServerHandler
    implements BiConsumer<ConfigProfileInfoPacket, ServerPlayer> {
        @Override
        public void accept(ConfigProfileInfoPacket packet, ServerPlayer player) {
            block2: {
                try {
                    ServerPlayerData.get(player).getConfigSynchronizer().onConfigProfileInfoPacket(packet.entries, packet.defaultEnforcedProfileId);
                }
                catch (Throwable t) {
                    if (!Services.PLATFORM.isDevelopmentEnvironment()) break block2;
                    XaeroLib.LOGGER.error("packet error", t);
                }
            }
        }
    }
}

