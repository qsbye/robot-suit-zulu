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

public class GuiLightOverlay
extends GuiMinimapSettings {
    public GuiLightOverlay(IXaeroMinimap modMain, Screen par1Screen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_light_overlay"), par1Screen, escScreen, context);
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MAX_LIGHT), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_COLOR), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MIN_LIGHT)};
    }
}

