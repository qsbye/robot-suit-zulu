/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.thirdparty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;

public class ThirdPartyWaypoints {
    private final MinimapSession session;
    private final Identifier originId;
    private final Map<String, Waypoint> waypoints;
    private final Map<String, Waypoint> waypointsUnmodifiable;
    private final Map<Waypoint, String> ids;
    private final Map<Waypoint, String> idsUnmodifiable;
    private final List<Waypoint> list;
    private final List<Waypoint> listUnmodifiable;
    private final Map<String, WaypointRenderInfo> renderInfoOverrides;
    private final Map<String, WaypointRenderInfo> renderInfoOverridesUnmodifiable;
    private Supplier<Boolean> enabledStateGetter;

    public ThirdPartyWaypoints(MinimapSession session, Identifier originId) {
        this.session = session;
        this.originId = originId;
        this.waypoints = new HashMap<String, Waypoint>();
        this.waypointsUnmodifiable = Collections.unmodifiableMap(this.waypoints);
        this.ids = new HashMap<Waypoint, String>();
        this.idsUnmodifiable = Collections.unmodifiableMap(this.ids);
        this.list = new ArrayList<Waypoint>();
        this.listUnmodifiable = Collections.unmodifiableList(this.list);
        this.renderInfoOverrides = new HashMap<String, WaypointRenderInfo>();
        this.renderInfoOverridesUnmodifiable = Collections.unmodifiableMap(this.renderInfoOverrides);
    }

    public void clear() {
        this.waypoints.clear();
        this.ids.clear();
        this.list.clear();
    }

    public void remove(String id) {
        Waypoint waypoint = this.waypoints.remove(id);
        if (waypoint == null) {
            return;
        }
        this.ids.remove(waypoint);
        this.list.remove(waypoint);
        this.renderInfoOverrides.remove(id);
        MinimapWorldRootContainer rootContainer = this.session.getWorldManager().getAutoRootContainer();
        this.session.getWorldManagerIO().getRootConfigIO().save(rootContainer);
    }

    public void add(String id, Waypoint waypoint) {
        Waypoint oldValue;
        String existingId = this.ids.get(waypoint);
        if (existingId != null) {
            this.remove(existingId);
        }
        if ((oldValue = this.waypoints.put(id, waypoint)) != null) {
            this.list.remove(oldValue);
            this.ids.remove(oldValue);
        }
        waypoint.setThirdPartyOrigin(this.originId);
        WaypointRenderInfo defaultRenderInfoOverride = this.renderInfoOverrides.get(id);
        if (defaultRenderInfoOverride != null) {
            waypoint.setThirdPartyRenderOverride(defaultRenderInfoOverride);
        } else {
            this.addRenderInfoOverride(id, waypoint.getThirdPartyRenderOverride());
        }
        this.list.add(waypoint);
        this.ids.put(waypoint, id);
    }

    public Waypoint get(String id) {
        return this.waypoints.get(id);
    }

    public Waypoint getBySlot(int slot) {
        return this.list.get(slot);
    }

    public Iterable<String> getIds() {
        return this.idsUnmodifiable.values();
    }

    public void addRenderInfoOverride(String id, WaypointRenderInfo renderInfo) {
        Waypoint existingWaypoint = this.get(id);
        if (existingWaypoint != null) {
            existingWaypoint.setThirdPartyRenderOverride(renderInfo);
        }
        this.renderInfoOverrides.put(id, renderInfo);
    }

    public boolean isEmpty() {
        return this.waypoints.isEmpty();
    }

    public Map<String, Waypoint> getWaypoints() {
        return this.waypointsUnmodifiable;
    }

    public int getCount() {
        return this.waypoints.size();
    }

    public Identifier getOriginId() {
        return this.originId;
    }

    public Map<String, WaypointRenderInfo> getRenderInfoOverrides() {
        return this.renderInfoOverridesUnmodifiable;
    }

    public void setEnabledStateGetter(Supplier<Boolean> enabledStateGetter) {
        this.enabledStateGetter = enabledStateGetter;
    }

    public boolean isEnabled() {
        if (this.enabledStateGetter == null) {
            return true;
        }
        return this.enabledStateGetter.get();
    }

    public boolean hasEnabledStateGetter() {
        return this.enabledStateGetter != null;
    }

    public Supplier<Boolean> getEnabledStateGetter() {
        return this.enabledStateGetter;
    }
}

