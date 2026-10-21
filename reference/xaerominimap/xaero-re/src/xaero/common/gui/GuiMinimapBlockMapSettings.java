/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.lib.client.gui.CustomSettingEntry;
import xaero.lib.client.gui.GuiConstants;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.util.ConfigConstants;
import xaero.lib.common.gui.widget.TooltipInfo;

public class GuiMinimapBlockMapSettings
extends GuiMinimapSettings {
    public GuiMinimapBlockMapSettings(IXaeroMinimap modMain, Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_minimap_block_map_settings"), backScreen, escScreen, context);
        CustomSettingEntry ignoreHeightmapsEntry = new CustomSettingEntry(() -> false, (Component)Component.translatable((String)"gui.xaero_ignore_heightmaps"), context.isClientSide() ? new TooltipInfo("gui.xaero_box_ignore_heightmaps") : new TooltipInfo(GuiConstants.SETTING_ENTRY_WRONG_CONTEXT_COMPONENT, false, true), false, () -> {
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (session == null) {
                return null;
            }
            MinimapWorldRootContainer currentRootContainer = session.getWorldManager().getAutoRootContainer();
            return currentRootContainer.getConfig().isIgnoreHeightmaps();
        }, 0, 1, i -> i == 1, v -> {
            if (modMain.getSupportMods().shouldUseWorldMapChunks()) {
                return Component.translatable((String)"gui.xaero_world_map").withStyle(ChatFormatting.YELLOW);
            }
            return v != false ? ConfigConstants.ON : ConfigConstants.OFF;
        }, (oldValue, newValue) -> {
            if (modMain.getSupportMods().shouldUseWorldMapChunks()) {
                modMain.getSupportMods().worldmapSupport.openSettings();
                return;
            }
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (session == null) {
                return;
            }
            MinimapWorldRootContainer currentRootContainer = session.getWorldManager().getAutoRootContainer();
            currentRootContainer.getConfig().setIgnoreHeightmaps((boolean)newValue);
            currentRootContainer.getSession().getWorldManagerIO().getRootConfigIO().save(currentRootContainer);
        }, () -> context.isClientSide() && BuiltInHudModules.MINIMAP.getCurrentSession() != null);
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.BLOCK_COLORS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.BIOMES_IN_VANILLA_COLORS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.BIOME_BLENDING), this.optionEntry(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.AUTO_CAVE_MODE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_DEPTH), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.LEGIBLE_CAVE_MAPS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_WORLD_MAP_CHUNKS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_DEPTH), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_REDSTONE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_SLOPES), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_FLOWERS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.BLOCK_TRANSPARENCY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_STAINED_GLASS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS), ignoreHeightmapsEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ANTI_ALIASING)};
    }
}

