/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import net.minecraft.world.entity.Entity;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.render.WaypointDeleter;
import xaero.hud.minimap.world.MinimapWorld;

public class DestinationHandler {
    private final MinimapSession session;
    private final WaypointDeleter waypointReachDeleter;
    private Entity renderEntity;
    private MinimapWorld world;
    private boolean allSets;
    private boolean deathpoints;
    private double dimDiv;

    public DestinationHandler(MinimapSession session, WaypointDeleter waypointReachDeleter) {
        this.session = session;
        this.waypointReachDeleter = waypointReachDeleter;
    }

    public void begin(Entity renderEntity, MinimapWorld world, boolean allSets, boolean deathpoints) {
        this.waypointReachDeleter.begin();
        this.renderEntity = renderEntity;
        this.world = world;
        this.allSets = allSets;
        this.deathpoints = deathpoints;
        this.dimDiv = this.session.getDimensionHelper().getDimensionDivision(world);
    }

    public void handle(Waypoint waypoint) {
        double correctOffZ;
        double correctDistance;
        if (!waypoint.isDestination()) {
            return;
        }
        if (!this.deathpoints && waypoint.getPurpose().isDeath()) {
            return;
        }
        if (System.currentTimeMillis() - waypoint.getCreatedAt() <= 5000L) {
            return;
        }
        double correctOffX = this.renderEntity.getX() - (double)waypoint.getX(this.dimDiv);
        double correctOffY = this.renderEntity.getY() - (double)waypoint.getY();
        if (!waypoint.isYIncluded()) {
            correctOffY = 0.0;
        }
        if ((correctDistance = Math.sqrt(correctOffX * correctOffX + correctOffY * correctOffY + (correctOffZ = this.renderEntity.getZ() - (double)waypoint.getZ(this.dimDiv)) * correctOffZ)) < 4.0) {
            this.waypointReachDeleter.add(waypoint);
        }
    }

    public void end() {
        this.waypointReachDeleter.deleteCollected(this.session, this.world, this.allSets);
    }
}

