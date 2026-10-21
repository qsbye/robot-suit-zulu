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
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.common.config.option.ConfigOption;

public class GuiMinimapInfoSettings
extends GuiMinimapSettings {
    public GuiMinimapInfoSettings(IXaeroMinimap modMain, Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_minimap_info_settings"), backScreen, escScreen, context);
        Tooltip tooltip = new Tooltip("gui.xaero_box_minimap_info_display_manager");
        this.entries = new ISettingEntry[]{this.optionEntry(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_BG_OPACITY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_ALIGNMENT), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPAC_CURRENT_CLAIM)};
    }
}

