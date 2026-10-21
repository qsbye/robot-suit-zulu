/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.config.option.value.redirect;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import xaero.common.HudMod;
import xaero.common.minimap.MinimapProcessor;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapConfigOptionClientRedirectors {
    public static final Component LEGACY_PLUGIN_TOOLTIP = Component.translatable((String)"gui.xaero_minimap_redirect_legacy");
    public static final Component FAIRPLAY_TOOLTIP = Component.translatable((String)"gui.xaero_config_redirect_fairplay");
    public static final Component SAFE_MODE_TOOLTIP = Component.translatable((String)"gui.xaero_config_redirect_safe_mode");
    public static final Component EFFECT_TOOLTIP = Component.translatable((String)"gui.xaero_config_redirect_effect");
    public static final Component FROM_WORLDMAP_TOOLTIP = Component.translatable((String)"gui.xaero_uses_worldmap_value").withStyle(ChatFormatting.YELLOW);
    public static final Component HARD_WORLDMAP_TOOLTIP = Component.translatable((String)"gui.xaero_uses_worldmap_hard_value").withStyle(ChatFormatting.YELLOW);
    public static final Component FROM_WORLDMAP_SCREEN_TOOLTIP = Component.translatable((String)"gui.xaero_uses_worldmap_screen_value").withStyle(ChatFormatting.YELLOW);
    public static final Component WORLD_MAP_COMPONENT = Component.translatable((String)"gui.xaero_world_map").withStyle(ChatFormatting.YELLOW);

    public static void registerAll(ClientOptionValueRedirectorManager manager) {
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_MINIMAP, () -> false, channel -> HudMod.INSTANCE.getSettings().minimapDisabled() || Minecraft.getInstance().player != null && !MinimapProcessor.hasMinimapItem((Player)Minecraft.getInstance().player), null, null, () -> {
            if (HudMod.INSTANCE.getSettings().minimapDisabled()) {
                return LEGACY_PLUGIN_TOOLTIP;
            }
            if (Minecraft.getInstance().player != null && !MinimapProcessor.hasMinimapItem((Player)Minecraft.getInstance().player)) {
                String minimapItemId = (String)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective(MinimapProfiledConfigOptions.MINIMAP_ITEM);
                return Component.translatable((String)"gui.xaero_minimap_redirect_item", (Object[])new Object[]{minimapItemId});
            }
            return null;
        });
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR, () -> false, channel -> HudMod.INSTANCE.isFairPlay(), null, null, () -> FAIRPLAY_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.RADAR_DOTS_STYLE, () -> 1, channel -> !HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().assumeUsingFBO(), null, null, () -> SAFE_MODE_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.RADAR_SMOOTH_DOTS, () -> false, channel -> !HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().assumeUsingFBO(), null, null, () -> SAFE_MODE_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_DOT_SIZE, () -> 2, channel -> !HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().assumeUsingFBO(), null, null, () -> SAFE_MODE_TOOLTIP);
        Predicate<ConfigChannel> waypointDisableCondition = channel -> HudMod.INSTANCE.getSettings().showWaypointsDisabled() || Minecraft.getInstance().player != null && !MinimapProcessor.hasMinimapItem((Player)Minecraft.getInstance().player);
        Supplier<Component> waypointDisableTooltipSupplier = () -> {
            if (HudMod.INSTANCE.getSettings().showWaypointsDisabled()) {
                return LEGACY_PLUGIN_TOOLTIP;
            }
            if (Minecraft.getInstance().player != null && !MinimapProcessor.hasMinimapItem((Player)Minecraft.getInstance().player)) {
                String minimapItemId = (String)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective(MinimapProfiledConfigOptions.MINIMAP_ITEM);
                return Component.translatable((String)"gui.xaero_minimap_redirect_item", (Object[])new Object[]{minimapItemId});
            }
            return null;
        };
        manager.register((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ON_MINIMAP, () -> false, waypointDisableCondition, null, null, waypointDisableTooltipSupplier);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_IN_WORLD, () -> false, waypointDisableCondition, null, null, waypointDisableTooltipSupplier);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DEATHPOINTS, () -> false, channel -> HudMod.INSTANCE.getSettings().deathpointsDisabled(), null, null, () -> LEGACY_PLUGIN_TOOLTIP);
        Function<Screen, Screen> worldMapSettingsScreenFactory = current -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getSettingsScreen((Screen)current);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_PARTIAL_Y_TELEPORT, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getPartialYTeleport(), channel -> HudMod.INSTANCE.getSupportMods().worldmap(), worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        Predicate<ConfigChannel> shouldUseWorldMapChunksPredicate = channel -> HudMod.INSTANCE.getSupportMods().shouldUseWorldMapChunks();
        manager.register((ConfigOption)MinimapProfiledConfigOptions.BLOCK_COLORS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapColours(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.BIOMES_IN_VANILLA_COLORS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapBiomeColorsVanillaMode(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED, () -> false, channel -> MinimapConfigClientUtils.isFairPlayForCaveMode() || HudMod.INSTANCE.getSettings().caveMapsDisabled() || MinimapConfigClientUtils.hasNoCaveModeEffect(), null, null, () -> MinimapConfigClientUtils.isFairPlayForCaveMode() ? FAIRPLAY_TOOLTIP : (HudMod.INSTANCE.getSettings().caveMapsDisabled() ? LEGACY_PLUGIN_TOOLTIP : EFFECT_TOOLTIP));
        manager.register(MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getManualCaveStart(), channel -> HudMod.INSTANCE.getSupportMods().shouldUseWorldMapCaveChunks(), currentScreen -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapScreenForOption((ConfigOption<?>)MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START, (Screen)currentScreen), WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_SCREEN_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.BIOME_BLENDING, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getBiomeBlending(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_DEPTH, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getCaveModeDepth(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.isLegibleCaveMaps(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_WORLD_MAP_CHUNKS, () -> false, channel -> !HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().assumeUsingFBO(), null, null, () -> SAFE_MODE_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_DEPTH, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapTerrainDepth(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_SLOPES, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapTerrainSlopes(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_REDSTONE, () -> false, shouldUseWorldMapChunksPredicate, null, WORLD_MAP_COMPONENT, () -> HARD_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_FLOWERS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getWorldMapFlowers(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_STAINED_GLASS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.isStainedGlassDisplayed(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getAdjustHeightForCarpetLikeBlocks(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.ANTI_ALIASING, () -> false, channel -> !HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().assumeUsingFBO(), null, null, () -> SAFE_MODE_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getDisplayClaims(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getClaimsFillOpacity(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
        manager.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, () -> HudMod.INSTANCE.getSupportMods().worldmapSupport.getClaimsBorderOpacity(), shouldUseWorldMapChunksPredicate, worldMapSettingsScreenFactory, WORLD_MAP_COMPONENT, () -> FROM_WORLDMAP_TOOLTIP);
    }
}

