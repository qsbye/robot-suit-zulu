/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config.context;

import net.minecraft.network.chat.Component;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.IConfigProfileInfo;

public interface IEditConfigScreenContext {
    public ConfigProfile getCurrentProfile(ConfigChannel var1);

    public Config getEnforcedConfig(ConfigChannel var1);

    public String getSelectedProfileId(ConfigChannel var1);

    public String getDefaultProfileId(ConfigChannel var1);

    public void setDefaultProfileId(String var1, ConfigChannel var2);

    public void setCurrentProfile(String var1, ConfigChannel var2);

    public void confirmProfile(ConfigProfile var1, ConfigChannel var2);

    public void reset(ConfigChannel var1);

    public Iterable<IConfigProfileInfo> getProfiles(ConfigChannel var1);

    public boolean profileExists(String var1, ConfigChannel var2);

    public void createProfile(String var1, String var2, ConfigChannel var3, String var4);

    public void deleteProfile(ConfigProfile var1, ConfigChannel var2);

    public Component getDropdownNarration();

    public boolean isClientSide();

    public boolean isAutoConfirm();

    public boolean isAutoDefaultProfile();

    public boolean getSyncStatus(ConfigChannel var1);

    public Component getSyncMessage();

    public boolean hasPermission(ConfigChannel var1);

    public String getScreenTitleFormat();
}

