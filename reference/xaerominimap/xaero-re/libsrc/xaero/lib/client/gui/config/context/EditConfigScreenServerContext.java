/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config.context;

import net.minecraft.network.chat.Component;
import xaero.lib.client.config.sync.SyncedConfigManager;
import xaero.lib.client.config.sync.profile.SyncedConfigProfileInfoManager;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.IConfigProfileInfo;

public class EditConfigScreenServerContext
implements IEditConfigScreenContext {
    private static final Component DROPDOWN_NARRATION = Component.translatable((String)"gui.xaero_server_config_profile");
    private static final Component SYNC_MESSAGE = Component.translatable((String)"gui.xaero_server_config_profile_syncing");

    @Override
    public ConfigProfile getCurrentProfile(ConfigChannel channel) {
        return channel.getClientConfigManager().getServerSynced().getEditedProfile();
    }

    @Override
    public Config getEnforcedConfig(ConfigChannel channel) {
        return null;
    }

    @Override
    public String getSelectedProfileId(ConfigChannel channel) {
        return channel.getClientConfigManager().getServerSynced().getDesiredEditedProfileId();
    }

    @Override
    public String getDefaultProfileId(ConfigChannel channel) {
        SyncedConfigProfileInfoManager profileInfoManager = channel.getClientConfigManager().getServerSynced().getProfileInfoManager();
        return profileInfoManager.getDefaultEnforcedProfileId();
    }

    @Override
    public void setDefaultProfileId(String profileId, ConfigChannel channel) {
        SyncedConfigProfileInfoManager profileInfoManager = channel.getClientConfigManager().getServerSynced().getProfileInfoManager();
        profileInfoManager.setDefaultEnforcedProfileId(profileId);
        channel.getClientConfigSynchronizer().changeDefaultEnforcedProfileId(profileId);
    }

    @Override
    public void setCurrentProfile(String profileId, ConfigChannel channel) {
        channel.getClientConfigManager().getServerSynced().setDesiredEditedProfileId(profileId);
    }

    @Override
    public void confirmProfile(ConfigProfile profile, ConfigChannel channel) {
        channel.getClientConfigManager().getServerSynced().confirmEdit(profile);
    }

    @Override
    public void reset(ConfigChannel channel) {
        channel.getClientConfigManager().getServerSynced().resetEdit();
    }

    @Override
    public Iterable<IConfigProfileInfo> getProfiles(ConfigChannel channel) {
        return channel.getClientConfigManager().getServerSynced().getProfileInfoManager();
    }

    @Override
    public boolean profileExists(String profileId, ConfigChannel channel) {
        return channel.getClientConfigManager().getServerSynced().getProfileInfoManager().get(profileId) != null;
    }

    @Override
    public void createProfile(String profileId, String name, ConfigChannel channel, String profileToCopy) {
        SyncedConfigManager syncedConfigManager = channel.getClientConfigManager().getServerSynced();
        syncedConfigManager.getProfileInfoManager().add(profileId, name);
        channel.getClientConfigSynchronizer().createProfile(profileId, name, profileToCopy);
    }

    @Override
    public void deleteProfile(ConfigProfile editedProfile, ConfigChannel channel) {
        channel.getClientConfigSynchronizer().deleteProfile(editedProfile.getId());
    }

    @Override
    public Component getDropdownNarration() {
        return DROPDOWN_NARRATION;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public boolean isAutoConfirm() {
        return false;
    }

    @Override
    public boolean isAutoDefaultProfile() {
        return false;
    }

    @Override
    public boolean getSyncStatus(ConfigChannel channel) {
        return !channel.getClientConfigManager().getServerSynced().isSyncingEditedProfile();
    }

    @Override
    public Component getSyncMessage() {
        return SYNC_MESSAGE;
    }

    @Override
    public boolean hasPermission(ConfigChannel channel) {
        return this.getDefaultProfileId(channel) != null;
    }

    @Override
    public String getScreenTitleFormat() {
        return "gui.xaero_server_config_title_format";
    }
}

