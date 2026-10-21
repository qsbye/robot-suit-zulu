/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import xaero.common.IXaeroMinimap;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

public class WaypointDeleter {
    private final IXaeroMinimap modMain;
    private final List<Waypoint> toDeleteList;
    private boolean started;

    public WaypointDeleter(IXaeroMinimap modMain) {
        this.modMain = modMain;
        this.toDeleteList = new ArrayList<Waypoint>();
    }

    public void begin() {
        this.started = true;
    }

    public void add(Waypoint w) {
        if (!this.started) {
            throw new IllegalStateException();
        }
        this.toDeleteList.add(w);
    }

    public void deleteCollected(MinimapSession session, MinimapWorld world, boolean allSets) {
        if (!this.started) {
            throw new IllegalStateException();
        }
        this.started = false;
        if (this.toDeleteList.isEmpty()) {
            return;
        }
        if (world == null) {
            this.toDeleteList.clear();
            return;
        }
        if (allSets) {
            for (WaypointSet set : world.getIterableWaypointSets()) {
                set.removeAll(this.toDeleteList);
            }
        } else {
            world.getCurrentWaypointSet().removeAll(this.toDeleteList);
        }
        try {
            session.getWorldManagerIO().saveWorld(world);
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.toDeleteList.clear();
    }
}

