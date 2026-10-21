/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.sync;

import java.util.Objects;
import org.apache.logging.log4j.Logger;
import xaero.lib.client.config.sync.ClientConfigChannelSynchronizer;
import xaero.lib.client.config.sync.profile.SyncedConfigProfileInfoManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.single.SingleConfigManager;

public class SyncedConfigManager
extends SingleConfigManager<Config> {
    private final SyncedConfigProfileInfoManager profileInfoManager;
    private ClientConfigChannelSynchronizer synchronizer;
    private ConfigProfile editedProfile;
    private String desiredEditedProfileId;
    private boolean syncingEditedProfile;
    private boolean channelPresentOnServer;

    private SyncedConfigManager(Logger logger, String configId, SyncedConfigProfileInfoManager profileInfoManager) {
        super(logger, configId);
        this.profileInfoManager = profileInfoManager;
    }

    public void setSynchronizer(ClientConfigChannelSynchronizer synchronizer) {
        if (this.synchronizer != null) {
            throw new IllegalStateException();
        }
        this.synchronizer = synchronizer;
    }

    @Override
    public void setChannel(ConfigChannel channel) {
        super.setChannel(channel);
        this.synchronizer.setChannel(channel);
    }

    public ConfigProfile getEditedProfile() {
        return this.editedProfile;
    }

    public void receiveEditedProfile(ConfigProfile receivedProfile) {
        if (!Objects.equals(this.desiredEditedProfileId, receivedProfile.getId())) {
            return;
        }
        if (!this.syncingEditedProfile) {
            return;
        }
        this.editedProfile = receivedProfile;
        this.setSyncingEditedProfile(false);
    }

    public void confirmEdit(ConfigProfile profile) {
        this.resetEdit();
        this.synchronizer.syncDirtyOptions(profile);
    }

    public void resetEdit() {
        this.editedProfile = null;
        this.desiredEditedProfileId = null;
        this.syncingEditedProfile = false;
    }

    public String getDesiredEditedProfileId() {
        return this.desiredEditedProfileId;
    }

    public void setDesiredEditedProfileId(String desiredEditedProfileId) {
        if (Objects.equals(this.desiredEditedProfileId, desiredEditedProfileId)) {
            return;
        }
        this.desiredEditedProfileId = desiredEditedProfileId;
        this.syncingEditedProfile = true;
        this.synchronizer.requestProfile(desiredEditedProfileId);
    }

    public boolean isSyncingEditedProfile() {
        return this.syncingEditedProfile;
    }

    public void setSyncingEditedProfile(boolean syncingEditedProfile) {
        this.syncingEditedProfile = syncingEditedProfile;
    }

    public void reset() {
        ((Config)this.getConfig()).reset();
        this.profileInfoManager.reset();
        this.editedProfile = null;
        this.desiredEditedProfileId = null;
        this.syncingEditedProfile = false;
        this.channelPresentOnServer = false;
    }

    public void confirmChannelPresenceOnServer() {
        this.channelPresentOnServer = true;
    }

    public boolean isChannelPresentOnServer() {
        return this.channelPresentOnServer;
    }

    public SyncedConfigProfileInfoManager getProfileInfoManager() {
        return this.profileInfoManager;
    }

    public static final class Builder
    extends SingleConfigManager.Builder<Config, Builder> {
        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setConfigId("server_synced");
            return (Builder)this.self;
        }

        @Override
        protected SingleConfigManager<Config> buildInternally() {
            Object syncedServerConfig = ((Config.FinalBuilder)Config.FinalBuilder.begin().setAllowNullValues(true)).build();
            SyncedConfigProfileInfoManager profileInfoManager = SyncedConfigProfileInfoManager.Builder.begin().build();
            SyncedConfigManager result = new SyncedConfigManager(this.logger, this.configId, profileInfoManager);
            result.setConfig(syncedServerConfig);
            return result;
        }

        public SyncedConfigManager build() {
            return (SyncedConfigManager)super.build();
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

