/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.sync;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.sync.SyncedConfigManager;
import xaero.lib.client.config.sync.profile.SyncedConfigProfileInfo;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.sync.CommonConfigChannelSynchronizer;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileCreatePacket;
import xaero.lib.common.packet.config.profile.ConfigProfileDeletePacket;
import xaero.lib.common.packet.config.profile.ConfigProfileEditPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileInfoPacket;
import xaero.lib.common.packet.config.profile.ServerboundConfigProfileRequestPacket;

public class ClientConfigChannelSynchronizer
extends CommonConfigChannelSynchronizer {
    private final SyncedConfigManager manager;

    public ClientConfigChannelSynchronizer(ConfigOptionManager options, SyncedConfigManager manager, Logger logger) {
        super(options, logger);
        this.manager = manager;
    }

    public void requestProfile(String desiredEditedProfileId) {
        XaeroLib.INSTANCE.getClient().getConfigSynchronizer().send(this.channel, new ServerboundConfigProfileRequestPacket(desiredEditedProfileId));
    }

    public void createProfile(String id, String name, String profileToCopy) {
        XaeroLib.INSTANCE.getClient().getConfigSynchronizer().send(this.channel, new ConfigProfileCreatePacket(id, name, profileToCopy));
    }

    public void deleteProfile(String id) {
        XaeroLib.INSTANCE.getClient().getConfigSynchronizer().send(this.channel, new ConfigProfileDeletePacket(id));
    }

    public void changeDefaultEnforcedProfileId(String profileId) {
        XaeroLib.INSTANCE.getClient().getConfigSynchronizer().send(this.channel, new ConfigProfileInfoPacket(Lists.newArrayList(), profileId, false));
    }

    public void syncDirtyOptions(ConfigProfile profile) {
        XaeroLib.INSTANCE.getClient().getConfigSynchronizer().send(this.channel, new ConfigProfileEditPacket(profile.getId(), this.getDirtyConfigEntries(profile)));
    }

    public void onEditProfilePacket(String profileId, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        SyncedConfigProfileInfo configProfileInfo = this.manager.getProfileInfoManager().get(profileId);
        if (configProfileInfo == null) {
            this.logger.error("Received config profile with an invalid id!");
            return;
        }
        ConfigProfile profile = ((ConfigProfile.Builder)((ConfigProfile.Builder)((ConfigProfile.Builder)ConfigProfile.Builder.begin().setId(profileId).setAllowNullValues(true)).setTrackDirtyOptions(true)).setOptions(this.options)).build();
        profile.set(BuiltInProfiledConfigOptions.PROFILE_NAME, configProfileInfo.getName());
        this.setConfigValues(profile, entries);
        profile.clearDirtyOptions();
        this.manager.receiveEditedProfile(profile);
    }

    public void onEnforcedConfigPacket(boolean reset, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        if (reset) {
            ((Config)this.manager.getConfig()).reset();
        }
        this.manager.confirmChannelPresenceOnServer();
        this.setConfigValues((Config)this.manager.getConfig(), entries);
    }

    public void onConfigProfileInfoPacket(List<ConfigProfileInfoPacket.Entry> entries, String defaultEnforcedProfileId, boolean reset) {
        if (reset) {
            this.manager.getProfileInfoManager().reset();
        }
        for (ConfigProfileInfoPacket.Entry entry : entries) {
            if (!BuiltInProfiledConfigOptions.PROFILE_NAME.isValidValue(entry.id) || !BuiltInProfiledConfigOptions.PROFILE_NAME.isValidValue(entry.name)) continue;
            SyncedConfigProfileInfo profileInfo = this.manager.getProfileInfoManager().get(entry.id);
            if (profileInfo == null) {
                this.manager.getProfileInfoManager().add(entry.id, entry.name);
                continue;
            }
            profileInfo.setName(entry.name);
        }
        if (defaultEnforcedProfileId != null) {
            this.manager.getProfileInfoManager().setDefaultEnforcedProfileId(defaultEnforcedProfileId);
        }
    }

    public void reset() {
        this.manager.reset();
    }

    public void onDeleteConfigProfilePacket(String profileId) {
        this.manager.getProfileInfoManager().remove(profileId);
    }

    public static final class Builder
    extends CommonConfigChannelSynchronizer.Builder<Builder> {
        private SyncedConfigManager manager;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setManager(null);
            return this;
        }

        public Builder setManager(SyncedConfigManager manager) {
            this.manager = manager;
            return this;
        }

        @Override
        public ClientConfigChannelSynchronizer build() {
            if (this.manager == null) {
                throw new IllegalStateException();
            }
            return (ClientConfigChannelSynchronizer)super.build();
        }

        @Override
        protected CommonConfigChannelSynchronizer buildInternally() {
            return new ClientConfigChannelSynchronizer(this.options, this.manager, this.manager.logger);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

