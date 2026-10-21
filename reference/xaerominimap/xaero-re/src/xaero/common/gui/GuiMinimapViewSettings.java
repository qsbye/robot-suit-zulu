/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapSettings;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;

public class GuiMinimapViewSettings
extends GuiMinimapSettings {
    public GuiMinimapViewSettings(IXaeroMinimap modMain, Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_minimap_view_settings"), backScreen, escScreen, context);
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.SIZE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.SHAPE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.NORTH_LOCKED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LIGHTING), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ZOOM), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CAVE_ZOOM), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.FURTHER_ZOOMOUT), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ZOOM_ENLARGED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPACITY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.UNDISCOVERED_OPACITY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.FRAME), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.FRAME_COLOR), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CENTERED_ENLARGED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.KEEP_ENLARGED_UNLOCKED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TOGGLED_ENLARGED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_SCREEN), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.BOSS_HEALTH_PUSH_BOX), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_F3), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.POTION_EFFECT_PUSH_BOX)};
    }
}

