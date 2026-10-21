/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.server;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterable;
import java.util.Iterator;
import net.minecraft.resources.Identifier;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;

@Deprecated
public class ServerWaypointManager {
    public static final Identifier ORIGIN_ID = Identifier.fromNamespaceAndPath((String)HudMod.INSTANCE.getModId(), (String)"server");
    private final ThirdPartyWaypointManager thirdPartyWaypointManager;

    public ServerWaypointManager(ThirdPartyWaypointManager thirdPartyWaypointManager) {
        this.thirdPartyWaypointManager = thirdPartyWaypointManager;
    }

    public void clear() {
        this.thirdPartyWaypointManager.get(ORIGIN_ID).clear();
    }

    public void remove(int id) {
        this.thirdPartyWaypointManager.get(ORIGIN_ID).remove("" + id);
    }

    public void add(int id, Waypoint waypoint) {
        this.thirdPartyWaypointManager.get(ORIGIN_ID).add("" + id, waypoint);
    }

    public Waypoint getById(int id) {
        return this.thirdPartyWaypointManager.get(ORIGIN_ID).get("" + id);
    }

    public Waypoint getBySlot(int slot) {
        return this.thirdPartyWaypointManager.get(ORIGIN_ID).getBySlot(slot);
    }

    public IntIterable getIds() {
        Iterator integers = Streams.stream(this.thirdPartyWaypointManager.get(ORIGIN_ID).getIds().iterator()).map(Integer::parseInt).iterator();
        return new IntArrayList(integers);
    }

    public void addDisabled(int id) {
        WaypointRenderInfo renderOverride = new WaypointRenderInfo();
        renderOverride.setDisabled(true);
        this.thirdPartyWaypointManager.get(ORIGIN_ID).addRenderInfoOverride("" + id, renderOverride);
    }

    public boolean isEmpty() {
        return this.thirdPartyWaypointManager.get(ORIGIN_ID).isEmpty();
    }

    public Iterable<Waypoint> getWaypoints() {
        return this.thirdPartyWaypointManager.get(ORIGIN_ID).getWaypoints().values();
    }

    public int size() {
        return this.thirdPartyWaypointManager.get(ORIGIN_ID).getCount();
    }
}

