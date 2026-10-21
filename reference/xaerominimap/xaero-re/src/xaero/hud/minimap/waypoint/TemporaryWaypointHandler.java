/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import net.minecraft.client.Minecraft;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public class TemporaryWaypointHandler {
    private final HudMod modMain;

    public TemporaryWaypointHandler(HudMod modMain) {
        this.modMain = modMain;
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z) {
        this.createTemporaryWaypoint(minimapWorld, x, y, z, true);
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z, boolean yIncluded) {
        this.createTemporaryWaypoint(minimapWorld, x, y, z, yIncluded, Minecraft.getInstance().level.dimensionType().coordinateScale());
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z, boolean yIncluded, double dimScale) {
        if (minimapWorld == null) {
            return;
        }
        MinimapSession session = minimapWorld.getContainer().getSession();
        if (!this.modMain.getSettings().waypointsGUI(session)) {
            return;
        }
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        double waypointDestDimScale = session.getDimensionHelper().getDimCoordinateScale(minimapWorld);
        double dimDiv = dimScale / waypointDestDimScale;
        x = OptimizedMath.myFloor((double)x * dimDiv);
        z = OptimizedMath.myFloor((double)z * dimDiv);
        Waypoint instant = new Waypoint(x, y, z, "Waypoint", "X", WaypointColor.getRandom(), WaypointPurpose.NORMAL, true, yIncluded);
        boolean waypointsBottomConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.NEW_WAYPOINTS_TO_BOTTOM);
        minimapWorld.getCurrentWaypointSet().add(instant, !waypointsBottomConfig);
    }
}

