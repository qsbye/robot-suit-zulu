/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.category.EntityRadarBackwardsCompatibilityConfig;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.hud.path.XaeroPathReader;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;

public class ModSettings {
    public static int defaultSettings;
    public static final String format = "\u00a7";
    protected IXaeroMinimap modMain;
    private EntityRadarBackwardsCompatibilityConfig entityRadarBackwardsCompatibilityConfig;
    private boolean foundOldRadarSettings;
    public boolean needsLegacySlimeSeedResave;
    private String loadedWaypointLines;
    public static int serverSettings;
    private static HashMap<XaeroPath, Long> legacyServerSlimeSeeds;
    private static final String[] SHOW_LIGHT_LEVEL_NAMES;
    private static int[] OLD_MINIMAP_SIZES;
    public Boolean showCoordsLegacy;
    public Boolean showBiomeLegacy;
    public Integer showLightLevelLegacy;
    public Integer showTimeLegacy;
    public Boolean showAnglesLegacy;
    public Boolean showDimensionNameLegacy;
    public Boolean displayWeatherInfoLegacy;

    public ModSettings(IXaeroMinimap modMain) {
        this.modMain = modMain;
        this.entityRadarBackwardsCompatibilityConfig = new EntityRadarBackwardsCompatibilityConfig();
        int n = defaultSettings = modMain.getVersionID().endsWith("fair") ? 16188159 : Integer.MAX_VALUE;
        if (serverSettings == 0) {
            serverSettings = defaultSettings;
        }
    }

    @Deprecated
    public boolean isKeyRepeat(KeyMapping kb) {
        return true;
    }

    @Deprecated
    public boolean getMinimap() {
        return (Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_MINIMAP);
    }

    @Deprecated
    public void setSlimeChunksSeed(long seed, XaeroPath fullWorldID) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null) {
            legacyServerSlimeSeeds.put(fullWorldID, seed);
            return;
        }
        MinimapWorld minimapWorld = session.getWorldManager().getWorld(fullWorldID);
        if (minimapWorld == null) {
            legacyServerSlimeSeeds.put(fullWorldID, seed);
            return;
        }
        minimapWorld.setSlimeChunkSeed(seed);
    }

    @Deprecated
    public Long getSlimeChunksSeed(XaeroPath fullWorldID) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null) {
            return this.getLegacySlimeChunksSeed(fullWorldID);
        }
        MinimapWorld minimapWorld = session.getWorldManager().getWorld(fullWorldID);
        if (minimapWorld == null) {
            return this.getLegacySlimeChunksSeed(fullWorldID);
        }
        return minimapWorld.getSlimeChunkSeed();
    }

    @Deprecated
    public Long getLegacySlimeChunksSeed(XaeroPath fullWorldID) {
        return legacyServerSlimeSeeds.get(fullWorldID);
    }

    public Long removeLegacySlimeChunksSeed(XaeroPath fullWorldID) {
        this.needsLegacySlimeSeedResave = true;
        return legacyServerSlimeSeeds.remove(fullWorldID);
    }

    @Deprecated
    public boolean getSlimeChunks(MinimapSession session) {
        return MinimapConfigClientUtils.getEffectiveSlimeChunks(session);
    }

    public boolean waypointsGUI(MinimapSession waypointSession) {
        MinimapProcessor processor = waypointSession.getProcessor();
        return Minecraft.getInstance().player != null && waypointSession.getWorldState().getAutoWorldPath() != null && (processor.getMinimapItem() == null || Minecraft.getInstance().player == null || MinimapProcessor.hasMinimapItem((Player)Minecraft.getInstance().player));
    }

    public float getMinimapScale() {
        return GuiUtils.getMinimapScale(HudMod.INSTANCE.getHudConfigs().getClientConfigManager());
    }

    public float getUIScale(int optionValue, int min, int max) {
        return GuiUtils.getUIScale(optionValue, min, max);
    }

    public int getAutoUIScale() {
        return GuiUtils.getAutoUIScale();
    }

    private int getMaxWaypointsDistance(int exp) {
        if (exp <= 0) {
            return 0;
        }
        return (int)Math.pow(2.0, 2 + exp);
    }

    private boolean assumeUsingFBO() {
        return this.modMain.getMinimap().getMinimapFBORenderer().assumeUsingFBO();
    }

    public boolean isIgnoreHeightmaps() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapIgnoreHeightmaps();
        }
        MinimapWorldRootContainer currentRootContainer = BuiltInHudModules.MINIMAP.getCurrentSession().getWorldManager().getAutoRootContainer();
        return currentRootContainer.getConfig().isIgnoreHeightmaps();
    }

    public void writeSettings(PrintWriter writer) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveSettings() throws IOException {
        try (PrintWriter writer = null;){
            writer = new PrintWriter(new FileWriter(this.modMain.getConfigFile().toFile()));
            this.writeSettings(writer);
            Object[] keys = legacyServerSlimeSeeds.keySet().toArray();
            Object[] values = legacyServerSlimeSeeds.values().toArray();
            for (int i = 0; i < keys.length; ++i) {
                writer.println("seed:" + String.valueOf(keys[i]) + ":" + String.valueOf(values[i]));
            }
            if (this.loadedWaypointLines != null && !this.loadedWaypointLines.isEmpty()) {
                writer.print(this.loadedWaypointLines);
            }
            this.modMain.getHudIO().save(writer);
        }
        this.needsLegacySlimeSeedResave = false;
    }

    public void readSetting(String[] args) {
        String valueString = args.length < 2 ? "" : args[1];
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        if (args[0].equalsIgnoreCase("ignoreUpdate")) {
            configManager.getPrimaryConfigManager().getConfig().set(MinimapPrimaryClientConfigOptions.IGNORED_UPDATE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("updateNotification")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("minimapItemId")) {
            String minimapItemId = valueString + ":" + args[2];
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.MINIMAP_ITEM, (Object)minimapItemId);
        } else if (args[0].equalsIgnoreCase("allowWrongWorldTeleportation")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.WRONG_WORLD_TELEPORT, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("differentiateByServerAddress")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.DIFFERENTIATE_BY_SERVER_ADDRESS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("debugEntityIcons")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.DEBUG_ENTITY_ICONS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("debugEntityVariantIds")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.DEBUG_ENTITY_VARIANT_IDS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("radarHideInvisibleEntities")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_HIDE_INVISIBLE, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("allowInternetAccess")) {
            boolean savedAllowInternetAccess = valueString.equals("true");
            if (!savedAllowInternetAccess) {
                XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)LibPrimaryCommonConfigOptions.ALLOW_INTERNET, (Object)false);
                XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManagerIO().save();
            }
        } else if (args[0].equalsIgnoreCase("minimap")) {
            BuiltInHudModules.MINIMAP.setActive(configManager, valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("caveMaps")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.AUTO_CAVE_MODE, (Object)(valueString.equals("true") ? 1 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString))));
        } else if (args[0].equalsIgnoreCase("caveZoom")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CAVE_ZOOM, (Object)(valueString.equals("true") ? 3 : (valueString.equals("false") ? 1 : Integer.parseInt(valueString) + 1)));
        } else if (args[0].equalsIgnoreCase("showWaypoints")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ON_MINIMAP, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("deathpoints")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DEATHPOINTS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("oldDeathpoints")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OLD_DEATHPOINTS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("showIngameWaypoints")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_IN_WORLD, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("displayRedstone")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_REDSTONE, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("distance")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_IN_WORLD, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("showCoords")) {
            this.showCoordsLegacy = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("lockNorth")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.NORTH_LOCKED, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("zoom")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ZOOM, (Object)(Integer.parseInt(valueString) + 1));
        } else if (args[0].equalsIgnoreCase("mapSize")) {
            int oldSize = Integer.parseInt(valueString);
            if (oldSize == -1) {
                configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.SIZE, (Object)0);
            } else {
                configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.SIZE, (Object)OLD_MINIMAP_SIZES[oldSize]);
            }
        } else if (args[0].equalsIgnoreCase("minimapSize")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.SIZE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("chunkGrid")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID, (Object)(valueString.equals("true") ? 0 : (valueString.equals("false") ? -1 : Integer.parseInt(valueString))));
        } else if (args[0].equalsIgnoreCase("slimeChunks")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("mapSafeMode")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("minimapOpacity")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPACITY, (Object)Double.valueOf(valueString).intValue());
        } else if (args[0].equalsIgnoreCase("waypointsIngameIconScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_ICON_SCALE_IN_WORLD, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("waypointsIngameDistanceScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_SCALE_IN_WORLD, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("waypointsIngameNameScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_NAME_SCALE_IN_WORLD, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("waypointsIngameCloseScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_CLOSE_SCALE_IN_WORLD, (Object)Double.valueOf(valueString));
        } else if (args[0].equalsIgnoreCase("antiAliasing")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ANTI_ALIASING, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("blockColours")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.BLOCK_COLORS, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lighting")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LIGHTING, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("dotsStyle")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_DOTS_STYLE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("dotNameScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_NAME_SCALE, (Object)Double.valueOf(valueString));
        } else if (args[0].equalsIgnoreCase("compassOverEverything")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_OVER_EVERYTHING, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("showBiome")) {
            this.showBiomeLegacy = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("showFlowers")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_FLOWERS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("keepWaypointNames")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_NAME_IN_WORLD, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("waypointsDistance")) {
            double oldValue = Double.valueOf(valueString);
            int exp = oldValue <= 0.0 ? 0 : (int)Math.max(3.0, Math.ceil(Math.log(oldValue) / Math.log(2.0))) - 2;
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_MAX_DISTANCE, (Object)this.getMaxWaypointsDistance(exp));
        } else if (args[0].equalsIgnoreCase("waypointsDistanceExp")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_MAX_DISTANCE, (Object)this.getMaxWaypointsDistance(Integer.parseInt(valueString)));
        } else if (args[0].equalsIgnoreCase("waypointsDistanceMin")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_MIN_DISTANCE_IN_WORLD, (Object)Double.valueOf(valueString));
        } else if (args[0].equalsIgnoreCase("waypointTp")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT, (Object)("/" + valueString + " {x} {y} {z}"));
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT, (Object)("/" + valueString + " {x} {y} {z} {yaw} ~"));
        } else if (args[0].equalsIgnoreCase("waypointTPCommand")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT, (Object)(valueString.replace("^col^", ":") + " {x} {y} {z}"));
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT, (Object)(valueString.replace("^col^", ":") + " {x} {y} {z} {yaw} ~"));
        } else if (args[0].equalsIgnoreCase("defaultWaypointTPCommandFormat")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_FORMAT, (Object)valueString.replace("^col^", ":"));
        } else if (args[0].equalsIgnoreCase("defaultWaypointTPCommandRotationFormat")) {
            configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_DEFAULT_TELEPORT_ROTATION_FORMAT, (Object)valueString.replace("^col^", ":"));
        } else if (args[0].equalsIgnoreCase("arrowScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ARROW_SCALE, (Object)Double.valueOf(valueString));
        } else if (args[0].equalsIgnoreCase("arrowColour")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ARROW_COLOR, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("seed")) {
            legacyServerSlimeSeeds.put(new XaeroPathReader().read(valueString), Long.parseLong(args[2]));
        } else if (args[0].equalsIgnoreCase("smoothDots")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_SMOOTH_DOTS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("worldMap")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_WORLD_MAP_CHUNKS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("terrainDepth")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_DEPTH, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("terrainSlopes")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_SLOPES, (Object)(valueString.equals("true") ? 2 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString))));
        } else if (args[0].equalsIgnoreCase("alwaysArrow") && valueString.equals("true")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_ENTITY, (Object)2);
        } else if (args[0].equalsIgnoreCase("mainEntityAs")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_ENTITY, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("blockTransparency")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.BLOCK_TRANSPARENCY, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("waypointOpacityIngame")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_OPACITY_IN_WORLD, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("waypointOpacityMap")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_OPACITY_ON_MINIMAP, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("hideWorldNames")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.HIDE_WORLD_NAMES, (Object)(valueString.equals("true") ? 2 : (valueString.equals("false") ? 1 : Integer.parseInt(valueString))));
        } else if (args[0].equalsIgnoreCase("openSlimeSettings")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPEN_SLIME_CHUNKS_SCREEN, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("alwaysShowDistance")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_SHORT_DISTANCE_IN_WORLD, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("showLightLevel")) {
            this.showLightLevelLegacy = valueString.equals("true") ? 1 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("renderLayerIndex")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RENDER_LAYER, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("crossDimensionalTp")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_TELEPORT_CROSS_DIMENSION, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("showTime")) {
            this.showTimeLegacy = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("biomeColorsVanillaMode")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.BIOMES_IN_VANILLA_COLORS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("lookingAtAngle")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_HORIZONTAL_POINTING_ANGLE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lookingAtAngleVertical")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_VERTICAL_POINTING_ANGLE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("centeredEnlarged")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CENTERED_ENLARGED, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("zoomedOutEnlarged")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ZOOM_ENLARGED, (Object)(valueString.equals("true") ? 1 : 0));
        } else if (args[0].equalsIgnoreCase("zoomOnEnlarged")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ZOOM_ENLARGED, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("minimapTextAlign")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_ALIGNMENT, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("showAngles")) {
            this.showAnglesLegacy = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("waypointsMutualEdit")) {
            configManager.getPrimaryConfigManager().getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.WAYPOINT_MUTUAL_EDIT, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("compass")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_LOCATION, (Object)(valueString.equals("true") ? 1 : 0));
        } else if (args[0].equalsIgnoreCase("compassLocation")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_LOCATION, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("compassDirectionScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_SCALE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("caveMapsDepth")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_DEPTH, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("hideWaypointCoordinates")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.HIDE_WAYPOINT_COORDINATES, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("renderAllSets")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("playerArrowOpacity")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ARROW_OPACITY, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("waypointsBottom")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.NEW_WAYPOINTS_TO_BOTTOM, (Object)valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("minimapShape")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.SHAPE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lightOverlayType")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lightOverlayMaxLight")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MAX_LIGHT, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lightOverlayMinLight")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MIN_LIGHT, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("lightOverlayColor")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_COLOR, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("uiScale")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.UI_SCALE, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("bossHealthPushBox")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.BOSS_HEALTH_PUSH_BOX, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("potionEffectPushBox")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.POTION_EFFECT_PUSH_BOX, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("minimapFrame")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.FRAME, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("minimapFrameColor")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.FRAME_COLOR, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("compassColor")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_SHADOW_COLOR, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("northCompassColor")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.COMPASS_NORTH_SHADOW_COLOR, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("showDimensionName")) {
            this.showDimensionNameLegacy = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("displayMultipleWaypointInfo")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.MULTIPLE_WAYPOINTS_INFO, (Object)Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("entityRadar")) {
            configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR, (Object)valueString.equals("true"));
        } else {
            if (this.entityRadarBackwardsCompatibilityConfig.readSetting(args)) {
                this.foundOldRadarSettings = true;
                return;
            }
            if (args[0].equalsIgnoreCase("adjustHeightForCarpetLikeBlocks")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("autoConvertWaypointDistanceToKmThreshold")) {
                configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.WAYPOINT_CONVERT_DISTANCE_TO_KM_AT, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("waypointDistancePrecision")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_PRECISION, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("mainDotSize")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_DOT_SIZE, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("partialYTeleportation")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_PARTIAL_Y_TELEPORT, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("deleteReachedDeathpoints")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DELETE_REACHED_DEATHPOINTS, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("hideMinimapUnderScreen")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_SCREEN, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("manualCaveModeStart")) {
                configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("manualCaveModeStartAuto")) {
                boolean savedManualCaveModeStartAuto = valueString.equals("true");
                if (savedManualCaveModeStartAuto) {
                    configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START, (Object)0);
                }
            } else if (args[0].equalsIgnoreCase("chunkGridLineWidth")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID_LINE_WIDTH, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("hideMinimapUnderF3")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_F3, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("temporaryWaypointsGlobal")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TEMPORARY_WAYPOINTS_GLOBAL, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("keepUnlockedWhenEnlarged")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.KEEP_ENLARGED_UNLOCKED, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("enlargedMinimapAToggle")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TOGGLED_ENLARGED, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayStainedGlass")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_STAINED_GLASS, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("waypointOnMapScale")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_ICON_SCALE_ON_MINIMAP, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("switchToAutoOnDeath")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.AUTO_WAYPOINTS_ON_DEATH, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayWeatherInfo")) {
                this.displayWeatherInfoLegacy = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("infoDisplayBackgroundOpacity")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_BG_OPACITY, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("caveModeToggleTimer")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("legibleCaveMaps")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("biomeBlending")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.BIOME_BLENDING, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayPacPlayers") || args[0].equalsIgnoreCase("displayTrackedPlayers")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_ON_MINIMAP, (Object)valueString.equals("true"));
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_IN_WORLD, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayTrackedPlayersOnMap")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_ON_MINIMAP, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayTrackedPlayersInWorld")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_IN_WORLD, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("dimensionScaledMaxWaypointDistance")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_MAX_DISTANCE_DIMENSION_SCALE, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("trackedPlayerWorldIconScale")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_ICON_SCALE, (Object)Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("trackedPlayerWorldNameScale")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_NAME_SCALE, (Object)Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("trackedPlayerMinimapIconScale")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE, (Object)Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("displayClaims")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("displayCurrentClaim")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CURRENT_CLAIM, (Object)valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("claimsOpacity")) {
                int borderOpacity = Integer.parseInt(valueString);
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, (Object)borderOpacity);
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, (Object)(borderOpacity * 58 / 100));
            } else if (args[0].equalsIgnoreCase("claimsBorderOpacity")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, (Object)Integer.parseInt(valueString));
            } else if (args[0].equalsIgnoreCase("claimsFillOpacity")) {
                configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, (Object)Integer.parseInt(valueString));
            }
        }
    }

    public void loadDefaultSettings(boolean shouldLoadLegacySettings) throws IOException {
        File defaultConfigFile;
        Path mainConfigFile = this.modMain.getConfigFile();
        Path legacyDefaultConfigFile = mainConfigFile.getParent().resolveSibling("defaultconfigs").resolve(HudMod.INSTANCE.getOldConfigFileName());
        if (Files.exists(legacyDefaultConfigFile, new LinkOption[0])) {
            this.loadSettingsFile(legacyDefaultConfigFile.toFile(), shouldLoadLegacySettings);
        }
        if ((defaultConfigFile = mainConfigFile.getParent().resolveSibling("defaultconfigs").resolve(mainConfigFile.getFileName()).toFile()).exists()) {
            this.loadSettingsFile(defaultConfigFile, shouldLoadLegacySettings);
        }
    }

    public void loadSettings(boolean shouldLoadLegacySettings) throws IOException {
        this.loadDefaultSettings(shouldLoadLegacySettings);
        Path mainConfigFile = this.modMain.getConfigFile();
        Path configFolderPath = mainConfigFile.getParent();
        if (!Files.exists(configFolderPath, new LinkOption[0])) {
            Files.createDirectories(configFolderPath, new FileAttribute[0]);
        }
        if (Files.exists(mainConfigFile, new LinkOption[0])) {
            this.loadSettingsFile(mainConfigFile.toFile(), shouldLoadLegacySettings);
        }
        this.saveSettings();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadSettingsFile(File file, boolean shouldLoadLegacySettings) throws IOException {
        try (BufferedReader reader = null;){
            String s;
            reader = new BufferedReader(new FileReader(file));
            StringBuilder legacyWaypointLinesBuilder = new StringBuilder();
            StringBuilder legacyEncodedInfoDisplayConfigBuilder = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                if (this.modMain.getHudIO().load(s, shouldLoadLegacySettings)) continue;
                String[] args = s.split(":");
                if (args[0].equals("waypoint") || args[0].equals("world")) {
                    legacyWaypointLinesBuilder.append(s).append("\n");
                    continue;
                }
                if (!shouldLoadLegacySettings) continue;
                try {
                    if (args[0].equalsIgnoreCase("interface") && args[1].equals("gui.xaero_minimap")) {
                        BuiltInHudModules.MINIMAP.setTransform(this.modMain.getHud().getOldSystemCompatibility().loadOldTransform(args));
                        continue;
                    }
                    if (args[0].equals("infoDisplayOrder") || args[0].equals("infoDisplay")) {
                        legacyEncodedInfoDisplayConfigBuilder.append(s).append("\n");
                        continue;
                    }
                    this.readSetting(args);
                }
                catch (Exception e) {
                    MinimapLogs.LOGGER.info("Skipping setting:" + args[0]);
                }
            }
            if (!legacyEncodedInfoDisplayConfigBuilder.isEmpty()) {
                InfoDisplayManagerConfigData legacyInfoDisplayConfig = HudMod.INSTANCE.getMinimap().getInfoDisplays().getIo().decode(legacyEncodedInfoDisplayConfigBuilder.toString());
                HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getCurrentProfile().set(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG, (Object)legacyInfoDisplayConfig);
            }
            if (!legacyWaypointLinesBuilder.isEmpty()) {
                this.loadedWaypointLines = legacyWaypointLinesBuilder.toString();
            }
        }
    }

    public static String getTranslation(boolean o) {
        return I18n.get((String)("gui.xaero_" + (o ? "on" : "off")), (Object[])new Object[0]);
    }

    @Deprecated
    private void refreshScreen() {
        GuiUtils.refreshScreenBase();
    }

    public boolean minimapDisabled() {
        return (serverSettings & 1) != 1;
    }

    public boolean caveMapsDisabled() {
        if (HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getServerSynced().isChannelPresentOnServer()) {
            return false;
        }
        return (serverSettings & 0x4000) != 16384 || Minecraft.getInstance().level != null && (!MinimapClientWorldDataHelper.getCurrentWorldData().getSyncedRules().allowCaveModeOnServer && Minecraft.getInstance().level.dimension() != Level.NETHER || !MinimapClientWorldDataHelper.getCurrentWorldData().getSyncedRules().allowNetherCaveModeOnServer && Minecraft.getInstance().level.dimension() == Level.NETHER);
    }

    public boolean showWaypointsDisabled() {
        return (serverSettings & 0x10000) != 65536;
    }

    public boolean deathpointsDisabled() {
        return (serverSettings & 0x200000) == 0;
    }

    public void resetServerSettings() {
        serverSettings = defaultSettings;
    }

    public static void setServerSettings() {
    }

    public static boolean canEditIngameSettings() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        return minimapSession != null && minimapSession.getWorldState().getAutoWorldPath() != null;
    }

    public EntityRadarBackwardsCompatibilityConfig getEntityRadarBackwardsCompatibilityConfig() {
        return this.entityRadarBackwardsCompatibilityConfig;
    }

    public void resetEntityRadarBackwardsCompatibilityConfig() {
        this.entityRadarBackwardsCompatibilityConfig = new EntityRadarBackwardsCompatibilityConfig();
        this.foundOldRadarSettings = false;
    }

    public boolean foundOldRadarSettings() {
        return this.foundOldRadarSettings;
    }

    public String getLoadedWaypointLines() {
        return this.loadedWaypointLines;
    }

    public void removeLoadedWaypointLines() {
        this.loadedWaypointLines = null;
    }

    static {
        legacyServerSlimeSeeds = new HashMap();
        SHOW_LIGHT_LEVEL_NAMES = new String[]{"gui.xaero_off", "gui.xaero_light_block", "gui.xaero_light_sky", "gui.xaero_light_all", "gui.xaero_light_both2"};
        OLD_MINIMAP_SIZES = new int[]{57, 85, 113, 169};
    }
}

