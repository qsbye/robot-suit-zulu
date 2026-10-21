/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiWaypointsOptions;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.MySmallButton;

public class GuiWorldTpCommand
extends ScreenBase {
    private MySmallButton confirmButton;
    private EditBox commandFormatTextField;
    private EditBox rotationCommandFormatTextField;
    private boolean usingDefault;
    private String commandFormat;
    private String rotationCommandFormat;
    private MinimapWorldRootContainer rootContainer;

    public GuiWorldTpCommand(IXaeroMinimap modMain, Screen parent, Screen escape, MinimapWorldRootContainer rootContainer) {
        super(parent, escape, (Component)Component.translatable((String)"gui.xaero_world_teleport_command"));
        this.rootContainer = rootContainer;
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        String defaultWaypointTPCommandFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT);
        String defaultWaypointTPCommandRotationFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT);
        this.commandFormat = rootContainer.getConfig().getServerTeleportCommandFormat() == null ? defaultWaypointTPCommandFormatConfig : rootContainer.getConfig().getServerTeleportCommandFormat();
        this.rotationCommandFormat = rootContainer.getConfig().getServerTeleportCommandRotationFormat() == null ? defaultWaypointTPCommandRotationFormatConfig : rootContainer.getConfig().getServerTeleportCommandRotationFormat();
        this.usingDefault = rootContainer.getConfig().isUsingDefaultTeleportCommand();
    }

    public void init() {
        super.init();
        this.parent.resize(this.width, this.height);
        this.commandFormatTextField = new EditBox(this, this.font, this.width / 2 - 100, this.height / 7 + 50, 200, 20, (Component)Component.translatable((String)"gui.xaero_world_teleport_command")){
            {
                Objects.requireNonNull(this$0);
                super(arg0, arg1, arg2, arg3, arg4, arg5);
            }

            public void insertText(String textToWrite) {
                if (this.active) {
                    super.insertText(textToWrite);
                }
            }

            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (this.active) {
                    return super.mouseClicked(event, doubleClick);
                }
                return false;
            }

            public void deleteChars(int p_146175_1_) {
                if (this.active) {
                    super.deleteChars(p_146175_1_);
                }
            }

            public void deleteWords(int p_146177_1_) {
                if (this.active) {
                    super.deleteWords(p_146177_1_);
                }
            }
        };
        this.commandFormatTextField.setMaxLength(128);
        this.rotationCommandFormatTextField = new EditBox(this, this.font, this.width / 2 - 100, this.height / 7 + 98, 200, 20, (Component)Component.translatable((String)"gui.xaero_world_teleport_command_with_rotation")){
            {
                Objects.requireNonNull(this$0);
                super(arg0, arg1, arg2, arg3, arg4, arg5);
            }

            public void insertText(String textToWrite) {
                if (this.active) {
                    super.insertText(textToWrite);
                }
            }

            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (this.active) {
                    return super.mouseClicked(event, doubleClick);
                }
                return false;
            }

            public void deleteChars(int p_146175_1_) {
                if (this.active) {
                    super.deleteChars(p_146175_1_);
                }
            }

            public void deleteWords(int p_146177_1_) {
                if (this.active) {
                    super.deleteWords(p_146177_1_);
                }
            }
        };
        this.rotationCommandFormatTextField.setMaxLength(128);
        this.commandFormatTextField.active = !this.usingDefault;
        this.rotationCommandFormatTextField.active = !this.usingDefault;
        this.commandFormatTextField.setValue(this.commandFormat);
        this.rotationCommandFormatTextField.setValue(this.rotationCommandFormat);
        this.addWidget((GuiEventListener)this.commandFormatTextField);
        this.addWidget((GuiEventListener)this.rotationCommandFormatTextField);
        this.confirmButton = new MySmallButton(200, this.width / 2 - 155, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
            String defaultWaypointTPCommandFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT);
            String defaultWaypointTPCommandRotationFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT);
            if (this.commandFormat.equals(defaultWaypointTPCommandFormatConfig) && this.rotationCommandFormat.equals(defaultWaypointTPCommandRotationFormatConfig)) {
                this.usingDefault = true;
                this.commandFormat = null;
                this.rotationCommandFormat = null;
            }
            this.rootContainer.getConfig().setUsingDefaultTeleportCommand(this.usingDefault);
            this.rootContainer.getConfig().setServerTeleportCommandFormat(this.commandFormat);
            this.rootContainer.getConfig().setServerTeleportCommandRotationFormat(this.rotationCommandFormat);
            this.rootContainer.getSession().getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
            this.goBack();
        });
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)new MySmallButton(201, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.addRenderableWidget((GuiEventListener)new MySmallButton(202, this.width / 2 - 75, this.height / 7 + 8, (Component)Component.literal((String)(I18n.get((String)"gui.xaero_use_default", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.usingDefault))), b -> {
            this.usingDefault = !this.usingDefault;
            this.commandFormatTextField.active = !this.usingDefault;
            this.rotationCommandFormatTextField.active = !this.usingDefault;
            this.init(this.width, this.height);
        }));
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (this.parent instanceof GuiWaypointsOptions) {
            ((GuiWaypointsOptions)this.parent).parent.extractRenderStateWithTooltipAndSubtitles(guiGraphics, 0, 0, f);
        }
        super.extractBackground(guiGraphics, i, j, f);
        super.extractBackground(guiGraphics, i, j, f);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        guiGraphics.centeredText(this.font, "{x} {y} {z} {name}", this.width / 2, this.height / 7 + 36, -5592406);
        guiGraphics.centeredText(this.font, "{x} {y} {z} {name} {yaw}", this.width / 2, this.height / 7 + 84, -5592406);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        if (this.usingDefault) {
            ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
            String defaultWaypointTPCommandFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT);
            String defaultWaypointTPCommandRotationFormatConfig = (String)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT);
            this.commandFormatTextField.setValue(defaultWaypointTPCommandFormatConfig);
            this.rotationCommandFormatTextField.setValue(defaultWaypointTPCommandRotationFormatConfig);
            this.commandFormatTextField.setTextColor(-11184811);
            this.rotationCommandFormatTextField.setTextColor(-11184811);
        }
        this.commandFormatTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.rotationCommandFormatTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        if (this.usingDefault) {
            this.commandFormatTextField.setValue(this.commandFormat);
            this.rotationCommandFormatTextField.setValue(this.rotationCommandFormat);
            this.commandFormatTextField.setTextColor(-1);
            this.rotationCommandFormatTextField.setTextColor(-1);
        }
    }

    public void tick() {
        this.commandFormat = this.commandFormatTextField.getValue();
        this.rotationCommandFormat = this.rotationCommandFormatTextField.getValue();
        this.confirmButton.active = this.commandFormat != null && this.commandFormat.length() > 0 && this.rotationCommandFormat != null && this.rotationCommandFormat.length() > 0 || this.usingDefault;
    }

    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 40 && (this.commandFormatTextField.isFocused() || this.rotationCommandFormatTextField.isFocused()) && this.commandFormat != null && this.commandFormat.length() > 0 && this.rotationCommandFormat != null && this.rotationCommandFormat.length() > 0) {
            this.confirmButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
        }
        return super.keyPressed(event);
    }

    public boolean isInGameUi() {
        return true;
    }
}

