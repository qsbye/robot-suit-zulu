/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.option.ui.ConfigOptionScreenEntry;
import xaero.lib.client.gui.GuiSettings;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.TextSettingEntry;
import xaero.lib.client.gui.config.ConfigProfileCreationScreen;
import xaero.lib.client.gui.config.ConfigProfileDropdownContext;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.client.gui.widget.MyTinyButton;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.BuiltInPrimaryCommonConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.IConfigProfileInfo;

public abstract class EditConfigScreen
extends GuiSettings {
    private static final Component NO_PERMISSION_0 = Component.translatable((String)"gui.xaero_no_config_edit_permission_0");
    private static final Component NO_PERMISSION_1 = Component.translatable((String)"gui.xaero_no_config_edit_permission_1");
    protected final IEditConfigScreenContext context;
    protected final ConfigChannel channel;
    private boolean shouldHandleChangesOnExit;
    private DropDownWidget dropdown;
    private boolean restoreDropdownFocus;
    private boolean syncStatusOnUpdate;
    private ConfigProfile profileOnUpdate;
    private ConfigProfileDropdownContext dropdownContext;
    private boolean hasPermissionToEdit;
    private ISettingEntry[] entriesBackup;
    private boolean shouldAskToConfirmOnExit;
    protected boolean shouldRenderEscapeScreen;

    public EditConfigScreen(Component title, Screen backScreen, Screen escScreen, IEditConfigScreenContext context, ConfigChannel channel) {
        super((Component)Component.translatable((String)context.getScreenTitleFormat(), (Object[])new Object[]{title}), backScreen, escScreen, !context.isAutoConfirm());
        this.context = context;
        this.channel = channel;
        this.shouldRenderEscapeScreen = true;
        this.canSkipWorldRender = true;
        if (!(backScreen instanceof EditConfigScreen)) {
            context.reset(channel);
            return;
        }
        EditConfigScreen backEditConfigScreen = (EditConfigScreen)backScreen;
        if (backEditConfigScreen.channel != channel || backEditConfigScreen.context != context) {
            context.reset(channel);
        }
    }

    private void updateEditedProfile() {
        this.hasPermissionToEdit = this.context.hasPermission(this.channel);
        if (!this.hasPermissionToEdit) {
            return;
        }
        this.syncStatusOnUpdate = this.context.getSyncStatus(this.channel);
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        if (editedProfile == null && this.syncStatusOnUpdate) {
            this.context.setCurrentProfile(this.context.getDefaultProfileId(this.channel), this.channel);
            this.syncStatusOnUpdate = this.context.getSyncStatus(this.channel);
            editedProfile = this.context.getCurrentProfile(this.channel);
        }
        if (this.profileOnUpdate == editedProfile) {
            return;
        }
        this.profileOnUpdate = editedProfile;
        this.onEditedProfileSwitch();
        this.shouldAskToConfirmOnExit = false;
        this.shouldHandleChangesOnExit = false;
    }

    protected void onEditedProfileSwitch() {
    }

    public boolean getSyncStatus() {
        return this.context.getSyncStatus(this.channel);
    }

    @Override
    public void init() {
        this.updateEditedProfile();
        if (!this.syncStatusOnUpdate || !this.hasPermissionToEdit) {
            if (this.entries != null) {
                this.entriesBackup = this.entries;
            }
            this.entries = null;
        } else if (this.entriesBackup != null) {
            this.entries = this.entriesBackup;
            this.entriesBackup = null;
        }
        super.init();
        this.dropdownContext = !this.hasPermissionToEdit ? null : new ConfigProfileDropdownContext(this.context, this.channel, this.profileOnUpdate, this.context.getSelectedProfileId(this.channel), true);
        String[] dropdownOptions = this.dropdownContext == null ? null : this.dropdownContext.getOptions();
        this.dropdown = null;
        if (dropdownOptions == null) {
            return;
        }
        int dropdownX = this.width / 2 - 100;
        if (this.canSearch) {
            if (this.searchField != null) {
                this.searchField.setX(this.width / 2 + 5);
            }
            dropdownX = this.width / 2 - 205;
        }
        this.dropdown = DropDownWidget.Builder.begin().setCallback(this::onDropdownSelection).setContainer(this).setX(dropdownX).setY(this.height / 7 + 9).setW(200).setNarrationTitle(this.context.getDropdownNarration()).setSelected(this.dropdownContext.getInitialSelection()).setOptions(dropdownOptions).build();
        this.addWidget(this.dropdown);
        int verticalOffset = this.getVerticalOffset();
        MyTinyButton deleteButton = new MyTinyButton(this.width / 2 + 130, this.height / 7 + 5 + verticalOffset - 48, (Component)Component.translatable((String)"gui.xaero_delete_profile"), this::onDeleteProfileButton);
        this.addRenderableWidget((GuiEventListener)deleteButton);
        if (!this.context.isAutoDefaultProfile()) {
            MyTinyButton setDefaultButton = new MyTinyButton(this.width / 2 - 205, this.height / 7 + 5 + verticalOffset - 48, (Component)Component.translatable((String)"gui.xaero_set_default_profile"), this::onSetDefaultButton);
            this.addRenderableWidget((GuiEventListener)setDefaultButton);
            ((Button)setDefaultButton).active = this.canSetDefault(this.profileOnUpdate);
        }
        String defaultProfileId = BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE.getDefaultValue();
        boolean bl = ((Button)deleteButton).active = this.profileOnUpdate != null && !this.profileOnUpdate.getId().equals(defaultProfileId);
        if (this.restoreDropdownFocus) {
            this.setFocused((GuiEventListener)this.dropdown);
        }
    }

    private boolean onDropdownSelection(DropDownWidget menu, int selected) {
        if (!this.context.getSyncStatus(this.channel)) {
            return false;
        }
        this.handleChanges();
        if (this.shouldAskToConfirmOnExit) {
            this.askForConfirmation(() -> {
                this.minecraft.gui.setScreen((Screen)this);
                this.onDropdownSelection(selected);
            });
            return false;
        }
        return this.onDropdownSelection(selected);
    }

    private boolean onDropdownSelection(int selected) {
        if (selected >= this.dropdownContext.getProfiles().length) {
            this.minecraft.gui.setScreen((Screen)new ConfigProfileCreationScreen(this, this.context, this.channel, this::onCreatedProfile));
            return true;
        }
        IConfigProfileInfo newCurrent = this.dropdownContext.getProfiles()[selected];
        String newCurrentId = newCurrent.getId();
        this.context.setCurrentProfile(newCurrentId, this.channel);
        this.restoreDropdownFocus = true;
        this.rebuildWidgets();
        this.restoreDropdownFocus = false;
        return true;
    }

    private void onCreatedProfile() {
    }

    private boolean canSetDefault(ConfigProfile editedProfile) {
        return editedProfile != null && !editedProfile.getId().equals(this.context.getDefaultProfileId(this.channel));
    }

    private void onSetDefaultButton(Button button) {
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        if (editedProfile == null) {
            return;
        }
        if (!this.canSetDefault(editedProfile)) {
            return;
        }
        this.context.setDefaultProfileId(editedProfile.getId(), this.channel);
        button.active = false;
    }

    private void onDeleteProfileButton(Button button) {
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        if (editedProfile == null) {
            return;
        }
        ConfigProfile profileToDelete = editedProfile;
        this.minecraft.gui.setScreen((Screen)new ConfirmScreen(b -> {
            if (b) {
                this.onDeleteProfile(profileToDelete);
            }
            this.minecraft.gui.setScreen((Screen)this);
        }, (Component)Component.translatable((String)"gui.xaero_delete_profile_0"), (Component)Component.translatable((String)"gui.xaero_delete_profile_1", (Object[])new Object[]{profileToDelete.getName(), profileToDelete.getId()})));
    }

    private void onDeleteProfile(ConfigProfile profile) {
        if (profile == null) {
            return;
        }
        String defaultProfileId = BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE.getDefaultValue();
        if (profile.getId().equals(defaultProfileId)) {
            return;
        }
        this.context.deleteProfile(profile, this.channel);
        this.context.reset(this.channel);
        this.context.setCurrentProfile(defaultProfileId, this.channel);
        this.updateEditedProfile();
    }

    public void handleChangesOnExit() {
        this.shouldHandleChangesOnExit = true;
    }

    @Override
    public void onExit(Screen screen) {
        this.handleChanges();
        if (this.shouldAskToConfirmOnExit) {
            this.askForConfirmation(screen);
            return;
        }
        super.onExit(screen);
    }

    private void askForConfirmation(Screen destinationScreen) {
        this.askForConfirmation(() -> this.minecraft.gui.setScreen(destinationScreen));
    }

    private void askForConfirmation(Runnable queuedAction) {
        this.shouldAskToConfirmOnExit = false;
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        int changeCount = editedProfile.getChangeCount();
        if (changeCount == 0) {
            this.context.reset(this.channel);
            queuedAction.run();
            return;
        }
        this.minecraft.gui.setScreen((Screen)new ConfirmScreen(c -> {
            if (c) {
                this.confirmProfile();
            }
            this.context.reset(this.channel);
            queuedAction.run();
        }, (Component)Component.translatable((String)"gui.xaero_confirm_save_0"), (Component)Component.translatable((String)"gui.xaero_confirm_save_1", (Object[])new Object[]{changeCount})));
    }

    @Override
    protected void confirm() {
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        if (editedProfile == null) {
            super.confirm();
            return;
        }
        this.handleChanges();
        this.confirmProfile();
        super.confirm();
    }

    private void confirmProfile() {
        this.shouldAskToConfirmOnExit = false;
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        this.context.confirmProfile(editedProfile, this.channel);
    }

    private void onWidgetChange() {
        if (!this.context.isAutoConfirm()) {
            this.shouldAskToConfirmOnExit = true;
            return;
        }
        this.confirmProfile();
    }

    protected void handleChanges() {
        if (!this.shouldHandleChangesOnExit) {
            return;
        }
        this.shouldHandleChangesOnExit = false;
        this.onWidgetChange();
        if (!this.context.isClientSide()) {
            return;
        }
        this.channel.getPrimaryClientConfigManagerIO().save();
    }

    public <T> ConfigOptionScreenEntry<T> optionEntry(ConfigOption<T> option) {
        return new ConfigOptionScreenEntry<T>(option, () -> this.context.getCurrentProfile(this.channel), () -> this.context.getEnforcedConfig(this.channel), () -> {
            if (Minecraft.getInstance().gui.screen() instanceof EditConfigScreen) {
                ((EditConfigScreen)Minecraft.getInstance().gui.screen()).onWidgetChange();
            } else {
                this.onWidgetChange();
            }
        }, this.channel, this.context.isClientSide());
    }

    public <T> ConfigOptionScreenEntry<T> primaryOptionEntry(ConfigOption<T> option) {
        return new ConfigOptionScreenEntry<T>(option, () -> this.channel.getPrimaryClientConfigManager().getConfig(), () -> null, () -> this.channel.getPrimaryClientConfigManagerIO().save(), this.channel, this.context.isClientSide(), !this.context.isClientSide());
    }

    public <T> ConfigOptionScreenEntry<T> libPrimaryOptionEntry(ConfigOption<T> option) {
        return new ConfigOptionScreenEntry<T>(option, () -> XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryClientConfigManager().getConfig(), () -> null, () -> XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryClientConfigManagerIO().save(), XaeroLib.INSTANCE.getLibConfigChannel(), this.context.isClientSide(), !this.context.isClientSide());
    }

    public IEditConfigScreenContext getContext() {
        return this.context;
    }

    public ConfigProfile getProfileOnUpdate() {
        return this.profileOnUpdate;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (this.shouldRenderEscapeScreen) {
            this.renderEscapeScreen(guiGraphics, 0, 0, f);
        }
        super.extractBackground(guiGraphics, i, j, f);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        if (this.hasPermissionToEdit && !this.syncStatusOnUpdate && this.context.getSyncStatus(this.channel)) {
            this.restoreDropdownFocus = this.dropdown != null && this.getFocused() == this.dropdown;
            this.rebuildWidgets();
            this.restoreDropdownFocus = false;
        }
        super.extractRenderState(guiGraphics, par1, par2, par3);
        if (!this.hasPermissionToEdit) {
            guiGraphics.centeredText(this.font, NO_PERMISSION_0, this.width / 2, this.height / 7 + 34, -1);
            guiGraphics.centeredText(this.font, NO_PERMISSION_1, this.width / 2, this.height / 7 + 54, -1);
            return;
        }
        if (!this.syncStatusOnUpdate) {
            Component syncMessage = this.context.getSyncMessage();
            guiGraphics.centeredText(this.font, syncMessage, this.width / 2, this.height / 7 + 34, -1);
        }
    }

    public ISettingEntry createProfileIDEntry() {
        return new TextSettingEntry(() -> Component.translatable((String)"gui.xaero_config_profile_id", (Object[])new Object[]{this.context.getSelectedProfileId(this.channel)}));
    }

    protected void resetProfileToDefaults() {
        ConfigProfile editedProfile = this.context.getCurrentProfile(this.channel);
        if (editedProfile == null) {
            return;
        }
        editedProfile.setDefaults();
        this.onWidgetChange();
    }

    protected void resetPrimaryToDefaults() {
        this.channel.getPrimaryClientConfigManager().getConfig().setDefaults();
    }

    @Override
    protected int getVerticalOffset() {
        return 24;
    }
}

