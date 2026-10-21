/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import java.util.List;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public class WaypointCollector {
    private final MinimapSession session;

    public WaypointCollector(MinimapSession session) {
        this.session = session;
    }

    public void collect(List<Waypoint> destination) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager manager = session.getWorldManager();
        if (manager.getCurrentWorld() != null) {
            ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
            boolean allSets = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS);
            if (allSets) {
                for (WaypointSet set : manager.getCurrentWorld().getIterableWaypointSets()) {
                    set.addTo(destination);
                }
            } else {
                manager.getCurrentWorld().getCurrentWaypointSet().addTo(destination);
            }
            if (allSets || "gui.xaero_default".equals(manager.getCurrentWorld().getCurrentWaypointSetId())) {
                for (ThirdPartyWaypoints thirdPartyWaypoints : manager.getCurrentWorld().getContainer().getThirdPartyWaypointManager().getAll()) {
                    if (!thirdPartyWaypoints.isEnabled()) continue;
                    for (Waypoint waypoint : thirdPartyWaypoints.getWaypoints().values()) {
                        if (waypoint.isThirdPartyDeleted()) continue;
                        destination.add(waypoint);
                    }
                }
            }
        }
        if (!manager.hasCustomWaypoints()) {
            return;
        }
        for (Waypoint waypoint : manager.getCustomWaypoints()) {
            destination.add(waypoint);
        }
    }
}

