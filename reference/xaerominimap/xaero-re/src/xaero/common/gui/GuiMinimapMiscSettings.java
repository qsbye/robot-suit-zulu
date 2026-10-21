/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.gui.GuiMinimapSettings;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;

public class GuiMinimapMiscSettings
extends GuiMinimapSettings {
    public GuiMinimapMiscSettings(Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_minimap_misc_settings"), backScreen, escScreen, context);
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE), this.primaryOptionEntry((ConfigOption)MinimapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.UI_SCALE), this.optionEntry(MinimapProfiledConfigOptions.MINIMAP_ITEM)};
    }
}

