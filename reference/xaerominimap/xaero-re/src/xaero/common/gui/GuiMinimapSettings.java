/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.common.gui.GuiEntityRadarCategoryEditor;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.lib.client.controls.util.KeyMappingUtils;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public abstract class GuiMinimapSettings
extends EditConfigScreen {
    public GuiMinimapSettings(Component title, Screen par1Screen, Screen escScreen, IEditConfigScreenContext context) {
        super(title, par1Screen, escScreen, context, HudMod.INSTANCE.getHudConfigs());
        if (!(par1Screen instanceof GuiMinimapSettings) && !(par1Screen instanceof GuiEntityRadarCategoryEditor)) {
            HudMod.INSTANCE.getEntityRadarCategoryManager().forgetEditedCategory();
        }
        this.canSkipWorldRender = false;
        this.shouldRenderEscapeScreen = false;
    }

    protected void onEditedProfileSwitch() {
        super.onEditedProfileSwitch();
        EntityRadarCategoryManager categoryManager = HudMod.INSTANCE.getEntityRadarCategoryManager();
        if (!this.context.hasPermission(this.channel) || !this.context.getSyncStatus(this.channel)) {
            if (categoryManager.getEditedCategory() != null) {
                categoryManager.forgetEditedCategory();
            }
            return;
        }
        ConfigProfile newConfig = this.getProfileOnUpdate();
        if (categoryManager.getEditedCategoryConfig() == newConfig) {
            return;
        }
        categoryManager.loadEditedCategory((Config)newConfig, this.context.isClientSide());
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractRenderState(guiGraphics, par1, par2, par3);
        Minimap minimapInterface = HudMod.INSTANCE.getMinimap();
        boolean mapSafeMode = (Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE);
        if (!mapSafeMode && minimapInterface.getMinimapFBORenderer().isTriedFBO() && !minimapInterface.getMinimapFBORenderer().isLoadedFBO()) {
            guiGraphics.centeredText(this.font, "\u00a74You've been forced into safe mode! :(", this.width / 2, 11, 0xFFFFFF);
        }
    }

    protected void handleChanges() {
        this.saveEditedCategoryIfNeeded();
        super.handleChanges();
    }

    private void saveEditedCategoryIfNeeded() {
        EntityRadarCategoryManager categoryManager = HudMod.INSTANCE.getEntityRadarCategoryManager();
        if (!categoryManager.editedCategoryNeedsSaving()) {
            return;
        }
        categoryManager.storeEditedCategory(this.context.isClientSide());
        this.handleChangesOnExit();
    }

    public boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }
        if (this.context.isClientSide() && KeyMappingUtils.inputMatches((InputConstants.Type)InputConstants.Type.KEYBOARD, (int)event.key(), (KeyMapping)((KeyMapping)HudMod.INSTANCE.getSettingsKey()), (int)0) || !this.context.isClientSide() && KeyMappingUtils.inputMatches((InputConstants.Type)InputConstants.Type.KEYBOARD, (int)event.key(), (KeyMapping)((KeyMapping)HudMod.INSTANCE.getServerSettingsKey()), (int)0)) {
            this.onExit(this.escape);
            return true;
        }
        return false;
    }
}

