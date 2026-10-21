/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config.context;

import java.util.function.Function;
import java.util.stream.StreamSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import xaero.lib.client.config.primary.option.BuiltInPrimaryClientConfigOptions;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.IConfigProfileInfo;

public class EditConfigScreenClientContext
implements IEditConfigScreenContext {
    private static final Component DROPDOWN_NARRATION = Component.translatable((String)"gui.xaero_client_config_profile");

    @Override
    public ConfigProfile getCurrentProfile(ConfigChannel channel) {
        return channel.getClientConfigManager().getCurrentProfile();
    }

    @Override
    public Config getEnforcedConfig(ConfigChannel channel) {
        return Minecraft.getInstance().level == null ? null : (Config)channel.getClientConfigManager().getServerSynced().getConfig();
    }

    @Override
    public String getSelectedProfileId(ConfigChannel channel) {
        return channel.getClientConfigManager().getCurrentProfile().getId();
    }

    @Override
    public String getDefaultProfileId(ConfigChannel channel) {
        return this.getSelectedProfileId(channel);
    }

    @Override
    public void setDefaultProfileId(String profileId, ConfigChannel channel) {
        this.setCurrentProfile(profileId, channel);
    }

    @Override
    public void setCurrentProfile(String profileId, ConfigChannel channel) {
        channel.getPrimaryClientConfigManager().getConfig().set(BuiltInPrimaryClientConfigOptions.CURRENT_PROFILE, profileId);
        channel.getPrimaryClientConfigManagerIO().save();
        channel.getClientConfigManager().getCurrentProfile();
    }

    @Override
    public void confirmProfile(ConfigProfile profile, ConfigChannel channel) {
        channel.getClientConfigProfileIO().save(profile);
    }

    @Override
    public void reset(ConfigChannel channel) {
    }

    @Override
    public Iterable<IConfigProfileInfo> getProfiles(ConfigChannel channel) {
        return StreamSupport.stream(channel.getClientConfigManager().getProfileManager().spliterator(), false).map(Function.identity())::iterator;
    }

    @Override
    public boolean profileExists(String profileId, ConfigChannel channel) {
        return channel.getClientConfigManager().getProfileManager().get(profileId) != null;
    }

    @Override
    public void createProfile(String profileId, String name, ConfigChannel channel, String profileToCopy) {
        ConfigProfile profile = ((ConfigProfile.Builder)ConfigProfile.Builder.begin().setOptions(channel.getConfigOptionManager())).setId(profileId).build();
        if (channel.getConfigOptionManager().get(BuiltInProfiledConfigOptions.PROFILE_NAME.getId()) == null) {
            return;
        }
        ConfigProfile baseProfile = channel.getClientConfigManager().getProfileManager().get(profileToCopy);
        if (baseProfile != null) {
            profile.copyOptionsFrom(baseProfile);
        }
        profile.set(BuiltInProfiledConfigOptions.PROFILE_NAME, name);
        channel.getClientConfigManager().getProfileManager().add(profile);
        channel.getClientConfigProfileIO().save(profile);
    }

    @Override
    public void deleteProfile(ConfigProfile editedProfile, ConfigChannel channel) {
        channel.getClientConfigManager().getProfileManager().remove(editedProfile.getId());
        channel.getClientConfigProfileIO().delete(editedProfile);
    }

    @Override
    public Component getDropdownNarration() {
        return DROPDOWN_NARRATION;
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public boolean isAutoConfirm() {
        return true;
    }

    @Override
    public boolean isAutoDefaultProfile() {
        return true;
    }

    @Override
    public boolean getSyncStatus(ConfigChannel channel) {
        return true;
    }

    @Override
    public Component getSyncMessage() {
        return null;
    }

    @Override
    public boolean hasPermission(ConfigChannel channel) {
        return true;
    }

    @Override
    public String getScreenTitleFormat() {
        return "gui.xaero_client_config_title_format";
    }
}

