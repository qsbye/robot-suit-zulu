/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import xaero.common.minimap.waypoints.Waypoint;

public final class WaypointSet {
    private String name;
    protected List<Waypoint> list;

    private WaypointSet(String name) {
        this.name = name;
        this.list = new ArrayList<Waypoint>();
    }

    public String getName() {
        return this.name;
    }

    public Iterable<Waypoint> getWaypoints() {
        return this.list;
    }

    public void addTo(List<Waypoint> collector) {
        collector.addAll(this.list);
    }

    public void add(Waypoint waypoint, boolean front) {
        if (front) {
            this.list.add(0, waypoint);
            return;
        }
        this.list.add(waypoint);
    }

    public void add(Waypoint waypoint) {
        this.add(waypoint, false);
    }

    public void addAll(Collection<Waypoint> waypoints, boolean front) {
        if (front) {
            this.list.addAll(0, waypoints);
            return;
        }
        this.list.addAll(waypoints);
    }

    public void addAll(Collection<Waypoint> waypoints) {
        this.addAll(waypoints, false);
    }

    public void remove(Waypoint waypoint) {
        this.list.remove(waypoint);
    }

    public Waypoint remove(int slot) {
        return this.list.remove(slot);
    }

    public void removeAll(Collection<Waypoint> waypoints) {
        this.list.removeAll(waypoints);
    }

    public void clear() {
        this.list.clear();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public Waypoint get(int slot) {
        return this.list.get(slot);
    }

    public Waypoint set(int slot, Waypoint waypoint) {
        return this.list.set(slot, waypoint);
    }

    public static final class Builder {
        private String name;

        private Builder() {
        }

        public Builder setDefault() {
            this.setName(null);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public WaypointSet build() {
            if (this.name == null) {
                throw new IllegalStateException();
            }
            return new WaypointSet(this.name);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

