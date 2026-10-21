/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.ConfigProfileDropdownContext;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.util.ConfigUtils;

public final class ConfigProfileCreationScreen
extends ScreenBase {
    private static final Component PROFILE_TO_COPY_COMPONENT = Component.translatable((String)"gui.xaero_config_profile_to_copy");
    private DropDownWidget profileToCopyDropdown;
    private EditBox editBox;
    private Button confirmButton;
    private String currentValue;
    private final IEditConfigScreenContext context;
    private final ConfigChannel channel;
    private final Runnable onConfirm;
    private ConfigProfileDropdownContext dropdownContext;
    private String selectedProfileToCopy;

    protected ConfigProfileCreationScreen(Screen parent, IEditConfigScreenContext context, ConfigChannel channel, Runnable onConfirm) {
        super(parent, parent, (Component)Component.translatable((String)"gui.xaero_config_profile_creation_screen"));
        this.context = context;
        this.channel = channel;
        this.onConfirm = onConfirm;
        this.currentValue = "";
        this.selectedProfileToCopy = context.getSelectedProfileId(channel);
    }

    @Override
    public void init() {
        super.init();
        this.dropdownContext = new ConfigProfileDropdownContext(this.context, this.channel, null, this.selectedProfileToCopy, false);
        this.profileToCopyDropdown = DropDownWidget.Builder.begin().setX(this.width / 2 - 100).setY(this.height / 7 + 15).setW(200).setCallback(this::onDropdownSelection).setOptions(this.dropdownContext.getOptions()).setSelected(this.dropdownContext.getInitialSelection()).setNarrationTitle(PROFILE_TO_COPY_COMPONENT).setContainer(this).build();
        this.addWidget(this.profileToCopyDropdown);
        this.editBox = new EditBox(this.font, this.width / 2 - 100, this.height / 7 + 55, 200, 20, BuiltInProfiledConfigOptions.PROFILE_NAME.getDisplayName());
        this.editBox.setMaxLength(50);
        this.setFocused((GuiEventListener)this.editBox);
        this.editBox.setFocused(true);
        this.editBox.setValue(this.currentValue);
        this.editBox.setResponder(this::onChange);
        this.addRenderableWidget((GuiEventListener)this.editBox);
        this.confirmButton = new MySmallButton(200, this.width / 2 - 155, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_confirm"), b -> this.confirm());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)new MySmallButton(201, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel"), b -> this.goBack()));
        this.updateConfirmButton();
    }

    private boolean onDropdownSelection(DropDownWidget menu, int selected) {
        if (!this.context.getSyncStatus(this.channel)) {
            return false;
        }
        this.selectedProfileToCopy = this.dropdownContext.getProfiles()[selected].getId();
        return true;
    }

    private void onChange(String s) {
        this.currentValue = s;
        this.updateConfirmButton();
    }

    private boolean canConfirm() {
        return !this.editBox.getValue().isEmpty() && BuiltInProfiledConfigOptions.PROFILE_NAME.isValidValue(this.editBox.getValue());
    }

    private void updateConfirmButton() {
        this.confirmButton.active = this.canConfirm();
    }

    public void confirm() {
        if (!this.canConfirm()) {
            return;
        }
        String autoId = ConfigUtils.getAutoProfileIdForName(this::profileExists, this.editBox.getValue());
        this.createProfile(autoId, this.editBox.getValue(), this.selectedProfileToCopy);
        this.context.setCurrentProfile(autoId, this.channel);
        this.onConfirm.run();
        this.goBack();
    }

    private boolean profileExists(String id) {
        return this.context.profileExists(id, this.channel);
    }

    private void createProfile(String id, String name, String profileToCopy) {
        this.context.createProfile(id, name, this.channel, profileToCopy);
    }

    public boolean keyPressed(KeyEvent event) {
        boolean result = super.keyPressed(event);
        if (event.key() == 40 && this.canConfirm()) {
            this.confirmButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
            return true;
        }
        return result;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.centeredText(this.font, PROFILE_TO_COPY_COMPONENT, this.width / 2, this.height / 7, -1);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, this.height / 7 + 40, -1);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
    }
}

