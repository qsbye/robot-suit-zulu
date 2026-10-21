/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config;

import net.minecraft.client.Minecraft;
import xaero.lib.client.config.ClientConfigProfileSwitchHandler;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;
import xaero.lib.client.config.primary.option.BuiltInPrimaryClientConfigOptions;
import xaero.lib.client.config.sync.SyncedConfigManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.ConfigProfileManager;
import xaero.lib.common.config.single.SingleConfigManager;

public final class ClientConfigManager {
    private final ConfigProfileManager profileManager;
    private final SingleConfigManager<Config> primaryConfigManager;
    private final SyncedConfigManager serverSynced;
    private final ClientConfigProfileSwitchHandler profileSwitchHandler;
    private final ClientOptionValueRedirectorManager redirectorManager;
    private ConfigProfile lastFetchedProfile;
    private ClientConfigChangeListener changeListener;
    private ConfigChannel channel;

    private ClientConfigManager(ConfigProfileManager profileManager, SingleConfigManager<Config> primaryConfigManager, SyncedConfigManager serverSynced, ClientConfigProfileSwitchHandler profileSwitchHandler, ClientOptionValueRedirectorManager redirectorManager) {
        this.profileManager = profileManager;
        this.primaryConfigManager = primaryConfigManager;
        this.serverSynced = serverSynced;
        this.profileSwitchHandler = profileSwitchHandler;
        this.redirectorManager = redirectorManager;
    }

    public ConfigProfileManager getProfileManager() {
        return this.profileManager;
    }

    public ConfigProfile getCurrentProfile() {
        String currentProfileId = this.primaryConfigManager.getEffective(BuiltInPrimaryClientConfigOptions.CURRENT_PROFILE);
        if (currentProfileId == null) {
            return null;
        }
        ConfigProfile result = this.profileManager.get(currentProfileId);
        if (result == null) {
            return null;
        }
        if (result != this.lastFetchedProfile) {
            ConfigProfile changingFrom = this.lastFetchedProfile;
            this.lastFetchedProfile = result;
            if (changingFrom == null) {
                return result;
            }
            this.profileSwitchHandler.handleSwitch(changingFrom, result, this);
        }
        return result;
    }

    public SyncedConfigManager getServerSynced() {
        return this.serverSynced;
    }

    private void setChangeListener(ClientConfigChangeListener changeListener) {
        if (this.changeListener != null) {
            throw new IllegalStateException();
        }
        this.changeListener = changeListener;
        this.profileManager.setChangeListener(changeListener);
        this.serverSynced.setChangeListener(changeListener);
        this.primaryConfigManager.setChangeListener(changeListener);
        this.redirectorManager.setChangeListener(changeListener);
    }

    public ClientConfigChangeListener getChangeListener() {
        return this.changeListener;
    }

    public void setChannel(ConfigChannel channel) {
        if (this.channel != null) {
            throw new IllegalStateException();
        }
        this.channel = channel;
        this.serverSynced.setChannel(channel);
        this.primaryConfigManager.setChannel(channel);
        this.redirectorManager.setChannel(channel);
    }

    public ConfigChannel getChannel() {
        return this.channel;
    }

    public <T> T getEffective(ConfigOption<T> option) {
        return this.getEffective(this.getCurrentProfile(), option);
    }

    public <T> T getEffective(ConfigProfile config, ConfigOption<T> option) {
        T serverValue;
        if (this.redirectorManager.shouldRedirect(option)) {
            return this.redirectorManager.getValue(option);
        }
        T t = serverValue = Minecraft.getInstance().level == null || this.shouldIgnoreServerEnforcement(config, option) || !option.isOverridable() ? null : (T)this.serverSynced.getEffective(option);
        if (serverValue != null) {
            return serverValue;
        }
        return this.getRaw(config, option);
    }

    public <T> boolean shouldIgnoreServerEnforcement(ConfigProfile config, ConfigOption<T> option) {
        return option != BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR && Minecraft.getInstance().player != null && this.getServerSynced().getProfileInfoManager().getDefaultEnforcedProfileId() != null && this.getEffective(config, BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR) != false;
    }

    public <T> boolean shouldIgnoreServerEnforcement(ConfigOption<T> option) {
        return this.shouldIgnoreServerEnforcement(this.getCurrentProfile(), option);
    }

    public <T> T getRaw(ConfigOption<T> option) {
        return this.getRaw(this.getCurrentProfile(), option);
    }

    public <T> T getRaw(ConfigProfile config, ConfigOption<T> option) {
        if (config == null) {
            return null;
        }
        return config.get(option);
    }

    public SingleConfigManager<Config> getPrimaryConfigManager() {
        return this.primaryConfigManager;
    }

    public ClientOptionValueRedirectorManager getRedirectorManager() {
        return this.redirectorManager;
    }

    public void postLoad() {
        this.getChangeListener().postLoad();
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

        public ClientConfigManager build() {
            if (this.primaryConfigManager == null || this.profiledConfigOptions == null) {
                throw new IllegalStateException();
            }
            ConfigProfileManager profileManager = ConfigProfileManager.Builder.begin().setConfigType("Client Config").setLogger(this.primaryConfigManager.logger).build();
            ClientConfigProfileSwitchHandler profileSwitchHandler = ClientConfigProfileSwitchHandler.Builder.begin().setOptions(this.profiledConfigOptions).build();
            SyncedConfigManager serverSynced = ((SyncedConfigManager.Builder)SyncedConfigManager.Builder.begin().setLogger(this.primaryConfigManager.logger)).build();
            ClientOptionValueRedirectorManager redirectorManager = ClientOptionValueRedirectorManager.Builder.begin().build();
            ClientConfigManager result = new ClientConfigManager(profileManager, this.primaryConfigManager, serverSynced, profileSwitchHandler, redirectorManager);
            ClientConfigChangeListener clientChangeListener = ((ClientConfigChangeListener.Builder)ClientConfigChangeListener.Builder.begin().setManager(result).setOptions(this.profiledConfigOptions)).build();
            result.setChangeListener(clientChangeListener);
            return result;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

