/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.config.util;

import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.effect.Effects;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.misc.Misc;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public class MinimapConfigClientUtils {
    public static float getUIScale(ClientConfigManager configManager, IndexedConfigOption<Integer> option) {
        List validValues = option.getValidValues();
        return MinimapConfigClientUtils.getUIScale(configManager, option, (Integer)validValues.get(0), (Integer)validValues.get(validValues.size() - 1), 1.0);
    }

    public static float getUIScale(ClientConfigManager configManager, IndexedConfigOption<Integer> option, double autoScale) {
        List validValues = option.getValidValues();
        return MinimapConfigClientUtils.getUIScale(configManager, option, (Integer)validValues.get(0), (Integer)validValues.get(validValues.size() - 1), autoScale);
    }

    public static float getUIScale(ClientConfigManager configManager, ConfigOption<Integer> option, int auto, int max, double autoScale) {
        int configValue = (Integer)configManager.getEffective(option);
        float configBasedScale = GuiUtils.getUIScale(configValue, auto, max);
        if (configValue == auto && autoScale != 1.0) {
            return (float)Math.ceil((double)configBasedScale * autoScale);
        }
        return configBasedScale;
    }

    public static double getWaypointsClampDepth(ClientConfigManager configManager, double fov, int height) {
        int baseIconScale = (int)MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.WAYPOINT_ICON_SCALE_IN_WORLD);
        double ingameCloseScale = (Double)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_CLOSE_SCALE_IN_WORLD);
        double frameSizeAtClampDepth = ingameCloseScale * 0.02133333496749401 * (double)height / (double)baseIconScale;
        double fovMultiplier = 2.0 * Math.tan(Math.toRadians(fov / 2.0));
        return frameSizeAtClampDepth / fovMultiplier;
    }

    public static void addAutoUIScaleValueToComponent(MutableComponent component, double autoScale) {
        component.getSiblings().add(Component.literal((String)(" (" + (int)Math.ceil((double)GuiUtils.getAutoUIScale() * autoScale) + ")")));
    }

    public static void addAutoMCScaleValueToComponent(MutableComponent component) {
        component.getSiblings().add(Component.literal((String)(" (" + Minecraft.getInstance().getWindow().getGuiScale() + ")")));
    }

    public static int getAutoMinimapSize() {
        int width;
        int height = Minecraft.getInstance().getWindow().getHeight();
        int size = (int)((float)(height <= (width = Minecraft.getInstance().getWindow().getWidth()) ? height : width) / GuiUtils.getMinimapScale(HudMod.INSTANCE.getHudConfigs().getClientConfigManager()));
        return Math.min(Math.max(55, 2 * size * 130 / 1080), 250);
    }

    public static int getEffectiveMinimapSize() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        int minimapSizeConfig = (Integer)configManager.getEffective(MinimapProfiledConfigOptions.SIZE);
        if (minimapSizeConfig > 0) {
            return minimapSizeConfig;
        }
        return MinimapConfigClientUtils.getAutoMinimapSize();
    }

    public static boolean getEffectiveNorthLocked(int mapSize, int shape) {
        if (mapSize > 180 && shape == 0) {
            return true;
        }
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean northLockedConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.NORTH_LOCKED);
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return northLockedConfig;
        }
        return northLockedConfig || (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.KEEP_ENLARGED_UNLOCKED) == false && minimapSession.getProcessor().isEnlargedMap();
    }

    private static void changeZoomUnchecked(ClientConfigManager configManager, int direction) {
        ConfigProfile currentProfile = configManager.getCurrentProfile();
        currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.ZOOM, (Object)(1 + Mth.positiveModulo((int)((Integer)currentProfile.get((ConfigOption)MinimapProfiledConfigOptions.ZOOM) - 1 + direction), (int)6)));
    }

    public static void changeZoom(int direction) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            MinimapConfigClientUtils.changeZoomUnchecked(configManager, direction);
            return;
        }
        double targetBefore = minimapSession.getProcessor().getTargetZoom();
        int attempts = 0;
        do {
            MinimapConfigClientUtils.changeZoomUnchecked(configManager, direction);
        } while (++attempts < 6 && targetBefore == minimapSession.getProcessor().getTargetZoom());
        if (attempts == 6) {
            MinimapConfigClientUtils.changeZoomUnchecked(configManager, direction);
        }
    }

    public static boolean getEffectiveSlimeChunks(MinimapSession session) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean slimeChunksConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS);
        if (session == null) {
            return slimeChunksConfig;
        }
        if (!slimeChunksConfig) {
            return false;
        }
        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            return true;
        }
        MinimapWorld autoWorld = session.getWorldManager().getAutoWorld();
        return autoWorld != null && autoWorld.getSlimeChunkSeed() != null;
    }

    public static Long getEffectiveSlimeChunksSeed(MinimapWorld currentWorld) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            if (Minecraft.getInstance().level.dimension() != Level.OVERWORLD) {
                return null;
            }
            return server.getLevel(Level.OVERWORLD).getSeed();
        }
        if (currentWorld == null) {
            return null;
        }
        return currentWorld.getSlimeChunkSeed();
    }

    public static boolean isFairPlayForCaveMode() {
        boolean fairplay = HudMod.INSTANCE.isFairPlay();
        if (!fairplay) {
            return false;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return true;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null) {
            return true;
        }
        MinimapProcessor processor = session.getProcessor();
        return !processor.isConsideringNetherFairPlayMessage() || level.dimension() != Level.NETHER;
    }

    public static boolean hasNoCaveModeEffect() {
        Minecraft mc = Minecraft.getInstance();
        return Misc.hasEffect((Player)mc.player, Effects.NO_CAVE_MAPS) || Misc.hasEffect((Player)mc.player, Effects.NO_CAVE_MAPS_HARMFUL);
    }

    public static boolean getEffectiveCaveModeAllowed() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        if (!((Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED)).booleanValue()) {
            return false;
        }
        Set allowedDimensionsLocal = (Set)configManager.getEffective(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        Set allowedDimensionsServer = (Set)configManager.getServerSynced().getEffective(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        boolean ignoringServer = configManager.shouldIgnoreServerEnforcement(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return allowedDimensionsLocal.isEmpty() && (ignoringServer || allowedDimensionsServer.isEmpty());
        }
        Identifier currentDimensionId = level.dimension().identifier();
        if (!allowedDimensionsLocal.isEmpty() && !allowedDimensionsLocal.contains(currentDimensionId)) {
            return false;
        }
        if (ignoringServer) {
            return true;
        }
        return allowedDimensionsServer == null || allowedDimensionsServer.isEmpty() || allowedDimensionsServer.contains(currentDimensionId);
    }

    public static double getActualZoom(ConfigOption<Integer> option) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        int v = (Integer)configManager.getEffective(option);
        return MinimapConfigClientUtils.getActualZoom(v);
    }

    public static double getActualZoom(int v) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        if (((Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.FURTHER_ZOOMOUT)).booleanValue()) {
            if (--v == 0) {
                return 0.5;
            }
            if (v < 0) {
                return 0.0;
            }
        }
        return v;
    }

    public static boolean areDiscoveredWaystonesEnabled() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISCOVERED_WAYSTONE_WAYPOINTS);
    }

    public static boolean areOtherWaystonesEnabled() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OTHER_WAYSTONE_WAYPOINTS);
    }
}

