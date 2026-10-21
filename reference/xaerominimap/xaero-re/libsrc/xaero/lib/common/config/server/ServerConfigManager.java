/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.server;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;
import xaero.lib.common.config.primary.option.BuiltInPrimaryCommonConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.ConfigProfileManager;
import xaero.lib.common.config.server.listener.ServerConfigChangeListener;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.lib.common.player.ServerPlayerData;

public final class ServerConfigManager {
    private final ConfigProfileManager profileManager;
    private final SingleConfigManager<Config> primaryConfigManager;
    private final OptionValueRedirectorManager redirectorManager;
    private ServerConfigChangeListener changeListener;
    private MinecraftServer server;
    private ConfigChannel channel;
    private boolean loaded;

    private ServerConfigManager(ConfigProfileManager profileManager, SingleConfigManager<Config> primaryConfigManager, OptionValueRedirectorManager redirectorManager) {
        this.profileManager = profileManager;
        this.primaryConfigManager = primaryConfigManager;
        this.redirectorManager = redirectorManager;
    }

    public ConfigProfileManager getProfileManager() {
        return this.profileManager;
    }

    public ConfigProfile getDefaultEnforcedProfile() {
        String enforcedProfileId = this.primaryConfigManager.getEffective(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE);
        if (enforcedProfileId == null) {
            return null;
        }
        return this.profileManager.get(enforcedProfileId);
    }

    public void setDefaultEnforcedProfileId(String enforcedProfileId) {
        this.primaryConfigManager.getConfig().set(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE, enforcedProfileId);
        this.channel.getPrimaryCommonConfigManagerIO().save();
    }

    public void setChangeListener(ServerConfigChangeListener changeListener) {
        if (this.changeListener != null) {
            throw new IllegalStateException();
        }
        this.changeListener = changeListener;
        this.profileManager.setChangeListener(changeListener);
        this.primaryConfigManager.setChangeListener(changeListener);
        this.redirectorManager.setChangeListener(changeListener);
    }

    public void setServer(MinecraftServer server) {
        if (this.server != null && this.server.isRunning()) {
            throw new IllegalStateException("Multiple servers running at once is not supported!");
        }
        this.server = server;
    }

    public void setChannel(ConfigChannel channel) {
        if (this.channel != null) {
            throw new IllegalStateException();
        }
        this.channel = channel;
        this.primaryConfigManager.setChannel(channel);
        this.redirectorManager.setChannel(channel);
    }

    public ConfigChannel getChannel() {
        return this.channel;
    }

    public ConfigProfile getEnforcedProfileForPlayer(ServerPlayerData playerData) {
        if (this.usesDefaultEnforcedProfile(playerData)) {
            return this.getDefaultEnforcedProfile();
        }
        ConfigProfile enforcedConfig = this.getPermissionBasedProfile(playerData);
        if (enforcedConfig == null) {
            return this.getDefaultEnforcedProfile();
        }
        return enforcedConfig;
    }

    public boolean usesDefaultEnforcedProfile(ServerPlayerData playerData) {
        return !playerData.getConfigChannelData(this.channel).isUsingConfigProfilePermission();
    }

    public boolean usesDefaultEnforcedProfile(ServerPlayer player) {
        return this.usesDefaultEnforcedProfile(ServerPlayerData.get(player));
    }

    public String getPermissionBasedProfileId(ServerPlayerData playerData) {
        return playerData.getConfigChannelData(this.channel).getEnforcedConfigProfilePermission();
    }

    @Nullable
    public ConfigProfile getPermissionBasedProfile(ServerPlayerData playerData) {
        if (this.usesDefaultEnforcedProfile(playerData)) {
            return this.getDefaultEnforcedProfile();
        }
        String profileId = this.getPermissionBasedProfileId(playerData);
        return this.profileManager.get(profileId);
    }

    public boolean hasServerProfileEditPermission(ServerPlayerData playerData) {
        return playerData.getConfigChannelData(this.channel).hasServerProfileEditPermission();
    }

    public <T> T getEffective(ServerPlayer player, ConfigOption<T> option) {
        if (this.redirectorManager.shouldRedirect(option)) {
            return this.redirectorManager.getValue(option);
        }
        return this.getRaw(player, option);
    }

    public <T> T getEffective(ServerPlayerData playerData, ConfigOption<T> option) {
        if (this.redirectorManager.shouldRedirect(option)) {
            return this.redirectorManager.getValue(option);
        }
        return this.getRaw(playerData, option);
    }

    public <T> T getRaw(ServerPlayer player, ConfigOption<T> option) {
        return this.getRaw(ServerPlayerData.get(player), option);
    }

    public <T> T getRaw(ServerPlayerData playerData, ConfigOption<T> option) {
        ConfigProfile profile = this.getEnforcedProfileForPlayer(playerData);
        return this.getRaw(profile, option);
    }

    public <T> T getRaw(ConfigProfile profile, ConfigOption<T> option) {
        if (profile == null) {
            return null;
        }
        return profile.get(option);
    }

    public void postLoad() {
        if (this.loaded) {
            throw new IllegalStateException();
        }
        this.loaded = true;
        this.channel.getServerConfigSynchronizer().postLoad();
        this.getChangeListener().postLoad();
    }

    public MinecraftServer getServer() {
        if (this.server != null && this.server.isStopped()) {
            this.server = null;
        }
        return this.server;
    }

    public ServerConfigChangeListener getChangeListener() {
        return this.changeListener;
    }

    public OptionValueRedirectorManager getRedirectorManager() {
        return this.redirectorManager;
    }

    public static final class Builder {
        private SingleConfigManager<Config> primaryConfigManager;
        private ConfigOptionManager profiledConfigOptions;

        private Builder() {
        }

        public Builder setDefault() {
            this.setPrimaryConfigManager(null);
            this.setProfiledConfigOptions(null);
            return this;
        }

        public Builder setPrimaryConfigManager(SingleConfigManager<Config> primaryConfigManager) {
            this.primaryConfigManager = primaryConfigManager;
            return this;
        }

        public Builder setProfiledConfigOptions(ConfigOptionManager profiledConfigOptions) {
            this.profiledConfigOptions = profiledConfigOptions;
            return this;
        }

        public ServerConfigManager build() {
            if (this.primaryConfigManager == null || this.profiledConfigOptions == null) {
                throw new IllegalStateException();
            }
            ConfigProfileManager profileManager = ConfigProfileManager.Builder.begin().setConfigType("Server Config").setLogger(this.primaryConfigManager.logger).build();
            OptionValueRedirectorManager redirectorManager = OptionValueRedirectorManager.FinalBuilder.begin().build();
            ServerConfigManager result = new ServerConfigManager(profileManager, this.primaryConfigManager, redirectorManager);
            ServerConfigChangeListener changeListener = ((ServerConfigChangeListener.Builder)ServerConfigChangeListener.Builder.begin().setOptions(this.profiledConfigOptions)).setManager(result).build();
            result.setChangeListener(changeListener);
            return result;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

