/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config;

import net.minecraft.ChatFormatting;
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
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;

public class EditStringConfigOptionScreen<T>
extends ScreenBase {
    private EditBox editBox;
    private Button confirmButton;
    private final Config config;
    private final Config enforcedConfig;
    private final ConfigOption<T> configOption;
    private final boolean allowEmpty;
    private final boolean emptyMeansNull;
    private final Runnable postConfirmAction;
    private boolean valid;
    private String currentValue;
    private Component enforcedValueLabel;
    protected boolean shouldRenderEscapeScreen;

    public EditStringConfigOptionScreen(Screen parent, Screen escape, Config config, Config enforcedConfig, ConfigOption<T> configOption, boolean allowEmpty, boolean emptyMeansNull, Runnable postConfirmAction) {
        super(parent, escape, configOption.getDisplayName());
        this.config = config;
        this.enforcedConfig = enforcedConfig;
        this.configOption = configOption;
        this.allowEmpty = allowEmpty;
        this.emptyMeansNull = emptyMeansNull;
        this.postConfirmAction = postConfirmAction;
        T value = config.get(configOption);
        this.currentValue = value == null ? "" : configOption.getValueType().getIoCodec().encode(value, null, configOption);
        this.shouldRenderEscapeScreen = false;
        this.canSkipWorldRender = false;
        if (parent instanceof EditConfigScreen) {
            EditConfigScreen configScreen = (EditConfigScreen)parent;
            this.shouldRenderEscapeScreen = configScreen.shouldRenderEscapeScreen;
            this.canSkipWorldRender = configScreen.canSkipWorldRender();
        }
    }

    @Override
    public void init() {
        super.init();
        Object enforcedValue = this.enforcedConfig == null ? null : (Object)this.enforcedConfig.get(this.configOption);
        this.enforcedValueLabel = enforcedValue == null ? null : Component.translatable((String)"gui.xaero_config_value_enforced_string_edit", (Object[])new Object[]{this.configOption.getValueType().getIoCodec().encode(enforcedValue, null, this.configOption)}).withStyle(ChatFormatting.YELLOW);
        this.editBox = new EditBox(this.font, this.width / 2 - 100, this.height / 7 + 29 - 2, 200, 20, this.configOption.getDisplayName());
        this.editBox.setMaxLength(this.configOption.getValueType().getIoCodec().getMaxStringLength());
        this.setFocused((GuiEventListener)this.editBox);
        this.editBox.setFocused(true);
        this.editBox.setValue(this.currentValue);
        this.editBox.setResponder(this::onChange);
        this.addRenderableWidget((GuiEventListener)this.editBox);
        this.confirmButton = Button.builder((Component)Component.translatable((String)"gui.xaero_confirm"), b -> this.confirm()).bounds(this.width / 2 - 105, this.height / 7 + 29 + 48, 100, 20).build();
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_cancel"), b -> this.goBack()).bounds(this.width / 2 + 5, this.height / 7 + 29 + 48, 100, 20).build());
        this.valid = true;
        this.updateConfirmButton();
    }

    private void onChange(String s) {
        this.currentValue = s;
        this.valid = true;
        if (this.editBox.getValue().isEmpty()) {
            this.valid = this.allowEmpty;
            this.updateConfirmButton();
            return;
        }
        try {
            T value = this.configOption.getValueType().getIoCodec().decode(s, null, this.configOption);
            this.valid = this.configOption.isValidValue(value);
        }
        catch (Throwable t) {
            this.valid = false;
        }
        this.updateConfirmButton();
    }

    private boolean canConfirm() {
        return this.valid;
    }

    private void updateConfirmButton() {
        this.confirmButton.active = this.canConfirm();
    }

    public void confirm() {
        Object value;
        if (!this.canConfirm()) {
            return;
        }
        Object t = value = this.editBox.getValue().isEmpty() && this.emptyMeansNull ? null : (Object)this.configOption.getValueType().getIoCodec().decode(this.editBox.getValue(), null, this.configOption);
        if (value != null && !this.configOption.isValidValue(value)) {
            return;
        }
        this.config.set(this.configOption, value);
        this.postConfirmAction.run();
        this.goBack();
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
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (this.shouldRenderEscapeScreen) {
            this.renderEscapeScreen(guiGraphics, 0, 0, f);
        }
        super.extractBackground(guiGraphics, i, j, f);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        if (this.enforcedValueLabel != null) {
            guiGraphics.centeredText(this.font, this.enforcedValueLabel, this.width / 2, this.height / 7 + 29 - 22, -1);
        }
        guiGraphics.centeredText(this.font, this.title, this.width / 2, this.height / 7 + 29 - 42, -1);
    }

    @Override
    public void onClose() {
        this.onExit(this.parent);
    }

    public boolean isInGameUi() {
        return true;
    }
}

