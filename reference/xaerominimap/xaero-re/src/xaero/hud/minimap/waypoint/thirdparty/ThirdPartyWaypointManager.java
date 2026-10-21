/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.thirdparty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;

public class ThirdPartyWaypointManager {
    private final MinimapSession session;
    private final Map<Identifier, ThirdPartyWaypoints> storage;
    private final List<ThirdPartyWaypoints> orderedStorage;
    private final List<ThirdPartyWaypoints> orderedStorageUnmodifiable;

    public ThirdPartyWaypointManager(MinimapSession session) {
        this.session = session;
        this.storage = new HashMap<Identifier, ThirdPartyWaypoints>();
        this.orderedStorage = new ArrayList<ThirdPartyWaypoints>();
        this.orderedStorageUnmodifiable = Collections.unmodifiableList(this.orderedStorage);
    }

    public void clear() {
        this.storage.values().forEach(ThirdPartyWaypoints::clear);
    }

    public void clearOrigin(Identifier originId) {
        ThirdPartyWaypoints waypoints = this.storage.get(originId);
        if (waypoints == null) {
            return;
        }
        waypoints.clear();
    }

    public int getOriginCount() {
        return this.storage.size();
    }

    public ThirdPartyWaypoints get(Identifier originId) {
        return this.storage.computeIfAbsent(originId, id -> {
            ThirdPartyWaypoints created = new ThirdPartyWaypoints(this.session, (Identifier)id);
            this.orderedStorage.add(created);
            return created;
        });
    }

    public ThirdPartyWaypoints getByIndex(int originIndex) {
        return this.orderedStorage.get(originIndex);
    }

    public int getCount() {
        return this.orderedStorage.stream().map(ThirdPartyWaypoints::getCount).reduce(Integer::sum).orElse(0);
    }

    public List<ThirdPartyWaypoints> getAll() {
        return this.orderedStorageUnmodifiable;
    }
}

