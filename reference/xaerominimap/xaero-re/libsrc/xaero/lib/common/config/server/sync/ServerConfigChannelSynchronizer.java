/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.server.sync;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.logging.log4j.Logger;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.primary.option.BuiltInPrimaryCommonConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.server.ServerConfigManager;
import xaero.lib.common.config.server.sync.ServerPlayerConfigSynchronizer;
import xaero.lib.common.config.sync.CommonConfigChannelSynchronizer;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.packet.config.ClientboundEnforcedConfigPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileDeletePacket;
import xaero.lib.common.packet.config.profile.ConfigProfileEditPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileInfoPacket;
import xaero.lib.common.player.ServerPlayerData;

public final class ServerConfigChannelSynchronizer
extends CommonConfigChannelSynchronizer {
    private final ServerConfigManager manager;
    private ConfigProfile lastSyncedDefaultEnforcedProfile;

    private ServerConfigChannelSynchronizer(ConfigOptionManager options, ServerConfigManager manager, Logger logger) {
        super(options, logger);
        this.manager = manager;
    }

    public void syncDefaultEnforcedConfigProfileSwitch(ConfigProfile to) {
        MinecraftServer server = this.manager.getServer();
        if (server == null) {
            return;
        }
        ConfigProfile from = this.lastSyncedDefaultEnforcedProfile;
        this.lastSyncedDefaultEnforcedProfile = to;
        ConfigProfileInfoPacket defaultEnforcedIdPacket = new ConfigProfileInfoPacket(Lists.newArrayList(), to.getId(), false);
        ClientboundEnforcedConfigPacket packet = null;
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            ServerPlayerConfigSynchronizer synchronizer = playerData.getConfigSynchronizer();
            if (this.manager.hasServerProfileEditPermission(playerData)) {
                synchronizer.send(this.channel, defaultEnforcedIdPacket);
            }
            if (!this.manager.usesDefaultEnforcedProfile(playerData)) continue;
            if (packet == null) {
                List<AbstractConfigPacket.OptionEntry> packetEntries = this.getConfigDifferenceEntries(from, to);
                packet = new ClientboundEnforcedConfigPacket(packetEntries, false);
            }
            synchronizer.send(this.channel, packet);
        }
    }

    public void syncEnforcedConfigProfileDeletion(ConfigProfile deleted) {
        MinecraftServer server = this.manager.getServer();
        if (server == null) {
            return;
        }
        ConfigProfile to = this.manager.getDefaultEnforcedProfile();
        ClientboundEnforcedConfigPacket packet = null;
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            ServerPlayerConfigSynchronizer synchronizer = playerData.getConfigSynchronizer();
            if (this.manager.getEnforcedProfileForPlayer(playerData) != deleted) continue;
            this.logger.info("Resynchronizing enforced config for player {} on config deletion.", (Object)player.getGameProfile().name());
            if (packet == null) {
                List<AbstractConfigPacket.OptionEntry> packetEntries = this.getConfigDifferenceEntries(deleted, to);
                packet = new ClientboundEnforcedConfigPacket(packetEntries, false);
            }
            synchronizer.send(this.channel, packet);
        }
    }

    public void syncOptionValueChange(ConfigProfile config, ConfigOption<?> option) {
        MinecraftServer server = this.manager.getServer();
        if (server == null) {
            return;
        }
        ClientboundEnforcedConfigPacket packet = null;
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            if (this.manager.getEnforcedProfileForPlayer(playerData) != config) continue;
            if (packet == null) {
                List packetEntries = List.of((Object)AbstractConfigPacket.OptionEntry.of(config, option));
                packet = new ClientboundEnforcedConfigPacket(packetEntries, false);
            }
            playerData.getConfigSynchronizer().send(this.channel, packet);
        }
    }

    public void syncProfileInfoChange(ConfigProfile profile) {
        MinecraftServer server = this.manager.getServer();
        if (server == null) {
            return;
        }
        if (this.manager.getProfileManager().get(profile.getId()) != profile) {
            return;
        }
        ConfigProfileInfoPacket packet = null;
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            if (!this.manager.hasServerProfileEditPermission(playerData)) continue;
            if (packet == null) {
                packet = new ConfigProfileInfoPacket(profile.getId(), profile.getName());
            }
            playerData.getConfigSynchronizer().send(this.channel, packet);
        }
    }

    public void syncProfileDeletion(ConfigProfile profile) {
        MinecraftServer server = this.manager.getServer();
        if (server == null) {
            return;
        }
        ConfigProfileDeletePacket packet = null;
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            if (!this.manager.hasServerProfileEditPermission(playerData)) continue;
            if (packet == null) {
                packet = new ConfigProfileDeletePacket(profile.getId());
            }
            playerData.getConfigSynchronizer().send(this.channel, packet);
        }
    }

    public void onProfileRequestPacket(ServerPlayerConfigSynchronizer synchronizer, String profileId) {
        if (!this.manager.hasServerProfileEditPermission(synchronizer.getPlayerData())) {
            return;
        }
        ConfigProfile profile = this.manager.getProfileManager().get(profileId);
        if (profile == null) {
            this.logger.error("Requested a non-existent server config profile: {}", (Object)profileId);
            return;
        }
        synchronizer.send(this.channel, new ConfigProfileEditPacket(profileId, this.getAllConfigEntries(profile)));
    }

    public void onEditProfilePacket(String profileId, Iterable<AbstractConfigPacket.OptionEntry> entries, ServerPlayerData playerData) {
        if (!this.manager.hasServerProfileEditPermission(playerData)) {
            return;
        }
        ConfigProfile editedProfile = this.manager.getProfileManager().get(profileId);
        if (editedProfile == null) {
            this.logger.error("Attempted to edit a non-existent server config profile: {}", (Object)profileId);
            return;
        }
        this.setConfigValues(editedProfile, entries);
        this.manager.getChannel().getServerConfigProfileIO().save(editedProfile);
    }

    public void onConfigProfileInfoPacket(List<ConfigProfileInfoPacket.Entry> entries, String defaultEnforcedProfileId, ServerPlayerData playerData) {
        if (!this.manager.hasServerProfileEditPermission(playerData)) {
            return;
        }
        for (ConfigProfileInfoPacket.Entry entry : entries) {
            ConfigProfile profile;
            if (!BuiltInProfiledConfigOptions.PROFILE_NAME.isValidValue(entry.id) || !BuiltInProfiledConfigOptions.PROFILE_NAME.isValidValue(entry.name) || (profile = this.manager.getProfileManager().get(entry.id)) == null) continue;
            profile.set(BuiltInProfiledConfigOptions.PROFILE_NAME, entry.name);
            this.channel.getServerConfigProfileIO().save(profile);
        }
        if (defaultEnforcedProfileId != null) {
            this.manager.setDefaultEnforcedProfileId(defaultEnforcedProfileId);
        }
    }

    public void onDeleteConfigProfilePacket(String profileId, ServerPlayerData playerData) {
        if (!this.manager.hasServerProfileEditPermission(playerData)) {
            return;
        }
        if (profileId == null) {
            return;
        }
        if (profileId.equals(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE.getDefaultValue())) {
            this.logger.error("Player {} attempted to delete the default server config profile. Very strange!", (Object)playerData.getPlayer().getGameProfile().name());
            return;
        }
        ConfigProfile profile = this.manager.getProfileManager().get(profileId);
        if (profile == null) {
            return;
        }
        ConfigProfile defaultEnforced = this.manager.getDefaultEnforcedProfile();
        if (profile == defaultEnforced) {
            this.manager.setDefaultEnforcedProfileId(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE.getDefaultValue());
        }
        this.syncEnforcedConfigProfileDeletion(profile);
        this.manager.getProfileManager().remove(profileId);
        this.channel.getServerConfigProfileIO().delete(profile);
    }

    public void onCreateConfigProfilePacket(String id, String name, String profileToCopy) {
        ConfigProfile baseProfile;
        boolean shouldAdd;
        ConfigProfile profile = this.manager.getProfileManager().get(id);
        boolean bl = shouldAdd = profile == null;
        if (shouldAdd) {
            profile = ((ConfigProfile.Builder)((ConfigProfile.Builder)ConfigProfile.Builder.begin().setAllowNullValues(true)).setId(id).setOptions(this.options)).build();
        }
        if ((baseProfile = this.manager.getProfileManager().get(profileToCopy)) != null) {
            profile.copyOptionsFrom(baseProfile);
        }
        if (shouldAdd) {
            this.manager.getProfileManager().add(profile);
        }
        profile.set(BuiltInProfiledConfigOptions.PROFILE_NAME, name);
        this.channel.getServerConfigProfileIO().save(profile);
    }

    private void syncEnforcedConfigOnLogin(ServerPlayerConfigSynchronizer synchronizer) {
        ConfigProfile enforcedConfig = this.manager.getEnforcedProfileForPlayer(synchronizer.getPlayerData());
        ArrayList<AbstractConfigPacket.OptionEntry> packetEntries = new ArrayList<AbstractConfigPacket.OptionEntry>();
        for (ConfigOption<?> option : enforcedConfig.usedOptions()) {
            packetEntries.add(AbstractConfigPacket.OptionEntry.of(enforcedConfig, option));
        }
        ClientboundEnforcedConfigPacket packet = new ClientboundEnforcedConfigPacket(packetEntries, true);
        synchronizer.send(this.channel, packet);
    }

    public void syncServerProfileEditingInfo(ServerPlayerConfigSynchronizer synchronizer) {
        if (!this.manager.hasServerProfileEditPermission(synchronizer.getPlayerData())) {
            synchronizer.send(this.channel, new ConfigProfileInfoPacket(new ArrayList<ConfigProfileInfoPacket.Entry>(), null, true));
            return;
        }
        ArrayList<ConfigProfileInfoPacket.Entry> entries = new ArrayList<ConfigProfileInfoPacket.Entry>();
        for (ConfigProfile profile : this.manager.getProfileManager()) {
            entries.add(new ConfigProfileInfoPacket.Entry(profile.getId(), profile.getName()));
        }
        String defaultEnforcedProfileId = this.channel.getPrimaryCommonConfigManager().getEffective(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE);
        synchronizer.send(this.channel, new ConfigProfileInfoPacket(entries, defaultEnforcedProfileId, true));
    }

    public void handleLogin(ServerPlayerConfigSynchronizer synchronizer) {
        this.syncServerProfileEditingInfo(synchronizer);
        this.syncEnforcedConfigOnLogin(synchronizer);
    }

    public void postLoad() {
        this.lastSyncedDefaultEnforcedProfile = this.manager.getDefaultEnforcedProfile();
    }

    public static final class Builder
    extends CommonConfigChannelSynchronizer.Builder<Builder> {
        private ServerConfigManager manager;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setManager(null);
            return this;
        }

        public Builder setManager(ServerConfigManager manager) {
            this.manager = manager;
            return this;
        }

        @Override
        public ServerConfigChannelSynchronizer build() {
            if (this.manager == null) {
                throw new IllegalStateException();
            }
            return (ServerConfigChannelSynchronizer)super.build();
        }

        @Override
        protected CommonConfigChannelSynchronizer buildInternally() {
            Logger logger = this.manager.getProfileManager().logger;
            return new ServerConfigChannelSynchronizer(this.options, this.manager, logger);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

