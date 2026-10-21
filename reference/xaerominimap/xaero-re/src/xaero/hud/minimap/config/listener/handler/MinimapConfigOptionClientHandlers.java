/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.config.listener.handler;

import xaero.common.HudMod;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapConfigOptionClientHandlers {
    private static void resetImage() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            minimapSession.getProcessor().setToResetImage(true);
        }
    }

    private static void handleSafeMode(Config config) {
        MinimapConfigOptionClientHandlers.resetImage();
        GuiUtils.refreshScreenBase();
    }

    private static void handleMinimapItem(Config config) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            minimapSession.getProcessor().updateMinimapItem();
        }
    }

    private static void handleEntityRadarCategories(Config config) {
        HudMod.INSTANCE.getEntityRadarCategoryManager().updateFromConfigChange(config);
    }

    private static void requestClaimsRefresh() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        DimensionHighlighterHandler hh = minimapSession.getProcessor().getMinimapWriter().getDimensionHighlightHandler();
        if (hh == null) {
            return;
        }
        hh.requestRefresh();
    }

    private static void handleOpacClaims(Config config) {
        MinimapConfigOptionClientHandlers.requestClaimsRefresh();
    }

    private static void handleOpacClaimsOpacity(Config config) {
        if (!((Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS)).booleanValue()) {
            return;
        }
        MinimapConfigOptionClientHandlers.requestClaimsRefresh();
    }

    private static void handleLegibleCaveMaps(Config config) {
        MinimapConfigOptionClientHandlers.resetImage();
    }

    private static void handleDisplayWorldMapChunks(Config config) {
        GuiUtils.refreshScreenBase();
    }

    private static void handleBlockTransparency(Config config) {
        MinimapConfigOptionClientHandlers.resetImage();
    }

    private static void handleDisplayStainedGlass(Config config) {
        MinimapConfigOptionClientHandlers.resetImage();
    }

    private static void handleInfoDisplayConfig(Config config) {
        Minimap minimap = HudMod.INSTANCE.getMinimap();
        if (minimap == null) {
            return;
        }
        if (config != HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getServerSynced().getConfig()) {
            minimap.getInfoDisplays().getManager().applyLocalConfig();
        }
        minimap.getInfoDisplays().clearStateCache();
    }

    public static void registerAll(ClientConfigChangeListener listener) {
        listener.register((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE, MinimapConfigOptionClientHandlers::handleSafeMode);
        listener.register(MinimapProfiledConfigOptions.MINIMAP_ITEM, MinimapConfigOptionClientHandlers::handleMinimapItem);
        listener.register(MinimapProfiledConfigOptions.RADAR_CATEGORIES, MinimapConfigOptionClientHandlers::handleEntityRadarCategories);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, MinimapConfigOptionClientHandlers::handleLegibleCaveMaps);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_WORLD_MAP_CHUNKS, MinimapConfigOptionClientHandlers::handleDisplayWorldMapChunks);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.BLOCK_TRANSPARENCY, MinimapConfigOptionClientHandlers::handleBlockTransparency);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_STAINED_GLASS, MinimapConfigOptionClientHandlers::handleDisplayStainedGlass);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS, MinimapConfigOptionClientHandlers::handleOpacClaims);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, MinimapConfigOptionClientHandlers::handleOpacClaimsOpacity);
        listener.register((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, MinimapConfigOptionClientHandlers::handleOpacClaimsOpacity);
        listener.register(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG, MinimapConfigOptionClientHandlers::handleInfoDisplayConfig);
    }
}

