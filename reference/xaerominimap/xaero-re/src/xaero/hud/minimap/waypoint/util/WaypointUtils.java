/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.util;

import net.minecraft.client.Minecraft;
import xaero.common.HudMod;
import xaero.common.gui.GuiMisc;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public class WaypointUtils {
    public static String getDistanceText(double distance, int precision, double convertToKmThreshold) {
        if (convertToKmThreshold != -1.0 && distance >= convertToKmThreshold) {
            return GuiMisc.getFormat(precision).format(distance / 1000.0) + "km";
        }
        return GuiMisc.getFormat(precision).format(distance) + "m";
    }

    public static String getDistanceText(double distance) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        double convertToKmThreshold = ((Integer)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_CONVERT_DISTANCE_TO_KM_AT)).intValue();
        int precision = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_PRECISION);
        return WaypointUtils.getDistanceText(distance, precision, convertToKmThreshold);
    }

    public static String getDistanceText(Waypoint w, double fromX, double fromY, double fromZ, double contextCoordinateScale, double waypointCoordinateScale) {
        double waypointPosDivider = contextCoordinateScale / waypointCoordinateScale;
        double wX = (double)w.getX(waypointPosDivider) + 0.5;
        double wZ = (double)w.getZ(waypointPosDivider) + 0.5;
        double xFromEntity = wX - fromX;
        double yFromEntity = (double)w.getY() - fromY;
        if (!w.isYIncluded()) {
            yFromEntity = 0.0;
        }
        double zFromEntity = wZ - fromZ;
        double distance = Math.sqrt(xFromEntity * xFromEntity + yFromEntity * yFromEntity + zFromEntity * zFromEntity);
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        double convertToKmThreshold = ((Integer)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_CONVERT_DISTANCE_TO_KM_AT)).intValue();
        int precision = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_PRECISION);
        return WaypointUtils.getDistanceText(distance, precision, convertToKmThreshold);
    }

    public static String getDistanceTextForCurrentWorld(Waypoint w, double waypointCoordinateScale, double fromX, double fromY, double fromZ) {
        double contextCoordinateScale = Minecraft.getInstance().level.dimensionType().coordinateScale();
        return WaypointUtils.getDistanceText(w, fromX, fromY, fromZ, contextCoordinateScale, waypointCoordinateScale);
    }
}

