/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiLightOverlay;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.GuiSlimeSeed;
import xaero.common.gui.ScreenSwitchSettingEntry;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;

public class GuiMinimapOverlaysSettings
extends GuiMinimapSettings {
    public GuiMinimapOverlaysSettings(IXaeroMinimap modMain, Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_overlay_settings"), backScreen, escScreen, context);
        MinimapWorld minimapWorld;
        ScreenSwitchSettingEntry lightOverlayEntry = new ScreenSwitchSettingEntry("gui.xaero_light_overlay", (current, escape) -> new GuiLightOverlay(modMain, (Screen)current, (Screen)escape, context), null, true);
        ScreenSwitchSettingEntry slimeChunksMultiplayerEntry = null;
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null && context.isClientSide() && Minecraft.getInstance().getSingleplayerServer() == null && (minimapWorld = minimapSession.getWorldManager().getAutoWorld()) != null) {
            slimeChunksMultiplayerEntry = new ScreenSwitchSettingEntry("gui.xaero_slime_chunks", (current, escape) -> new GuiSlimeSeed(modMain, minimapSession, (Screen)current, (Screen)escape, context), null, true);
        }
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID_LINE_WIDTH), lightOverlayEntry, slimeChunksMultiplayerEntry == null ? this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS) : slimeChunksMultiplayerEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY)};
    }
}

