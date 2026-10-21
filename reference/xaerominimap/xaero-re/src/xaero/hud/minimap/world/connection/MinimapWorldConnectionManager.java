/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.connection;

import java.io.PrintWriter;
import java.lang.invoke.CallSite;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.path.XaeroPath;

public final class MinimapWorldConnectionManager {
    private Map<XaeroPath, Set<XaeroPath>> allConnections = new HashMap<XaeroPath, Set<XaeroPath>>();
    private final boolean multiplayer;

    private MinimapWorldConnectionManager(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    public void addConnection(MinimapWorld world1, MinimapWorld world2) {
        this.addConnection(world1.getLocalWorldKey(), world2.getLocalWorldKey());
    }

    public void addConnection(XaeroPath worldKey1, XaeroPath worldKey2) {
        this.addOneWayConnection(worldKey1, worldKey2);
        this.addOneWayConnection(worldKey2, worldKey1);
    }

    private void addOneWayConnection(XaeroPath worldKey1, XaeroPath worldKey2) {
        Set<XaeroPath> connections = this.allConnections.get(worldKey1);
        if (connections == null) {
            connections = new HashSet<XaeroPath>();
            this.allConnections.put(worldKey1, connections);
        }
        connections.add(worldKey2);
    }

    public void removeConnection(MinimapWorld world1, MinimapWorld world2) {
        this.removeConnection(world1.getLocalWorldKey(), world2.getLocalWorldKey());
    }

    protected void removeConnection(XaeroPath worldKey1, XaeroPath worldKey2) {
        this.removeOneWayConnection(worldKey1, worldKey2);
        this.removeOneWayConnection(worldKey2, worldKey1);
    }

    private void removeOneWayConnection(XaeroPath worldKey1, XaeroPath worldKey2) {
        Set<XaeroPath> connections = this.allConnections.get(worldKey1);
        if (connections == null) {
            return;
        }
        connections.remove(worldKey2);
    }

    public boolean isConnected(MinimapWorld world1, MinimapWorld world2) {
        if (!this.multiplayer) {
            return true;
        }
        if (world1 == world2) {
            return true;
        }
        if (world1 == null || world2 == null) {
            return false;
        }
        Set<XaeroPath> connections = this.allConnections.get(world1.getLocalWorldKey());
        if (connections == null) {
            return false;
        }
        return connections.contains(world2.getLocalWorldKey());
    }

    public boolean isEmpty() {
        return this.allConnections.isEmpty();
    }

    public void save(PrintWriter writer) {
        if (!this.allConnections.isEmpty()) {
            HashSet<CallSite> redundantConnections = new HashSet<CallSite>();
            for (Map.Entry<XaeroPath, Set<XaeroPath>> entry : this.allConnections.entrySet()) {
                XaeroPath worldKey = entry.getKey();
                Set<XaeroPath> connections = entry.getValue();
                for (XaeroPath c : connections) {
                    String fullConnection = String.valueOf(worldKey) + ":" + String.valueOf(c);
                    if (redundantConnections.contains(fullConnection)) continue;
                    writer.println("connection:" + fullConnection);
                    redundantConnections.add((CallSite)((Object)(String.valueOf(c) + ":" + String.valueOf(worldKey))));
                }
            }
        }
    }

    public void swapConnections(MinimapWorld world1, MinimapWorld world2) {
        this.swapConnections(world1.getLocalWorldKey(), world2.getLocalWorldKey());
    }

    private void swapConnections(XaeroPath worldKey1, XaeroPath worldKey2) {
        HashSet connections1 = new HashSet(this.allConnections.getOrDefault(worldKey1, new HashSet()));
        HashSet connections2 = new HashSet(this.allConnections.getOrDefault(worldKey2, new HashSet()));
        for (XaeroPath c : connections1) {
            if (c.equals(worldKey2)) continue;
            this.removeConnection(worldKey1, c);
        }
        for (XaeroPath c : connections2) {
            if (c.equals(worldKey1)) continue;
            this.addConnection(worldKey1, c);
        }
        for (XaeroPath c : connections2) {
            if (c.equals(worldKey1)) continue;
            this.removeConnection(worldKey2, c);
        }
        for (XaeroPath c : connections1) {
            if (c.equals(worldKey2)) continue;
            this.addConnection(worldKey2, c);
        }
    }

    public void renameDimension(String oldName, String newName) {
        HashSet<XaeroPath> keysCopy = new HashSet<XaeroPath>(this.allConnections.keySet());
        for (XaeroPath worldKey : keysCopy) {
            if (worldKey.getNodeCount() <= 1 || !worldKey.getRoot().getLastNode().equals(oldName)) continue;
            XaeroPath nonDimPart = worldKey.getSubPath(1);
            this.swapConnections(worldKey, XaeroPath.root(newName).resolve(nonDimPart));
        }
    }

    public static final class Builder {
        private boolean multiplayer;

        private Builder() {
        }

        public Builder setDefault() {
            this.setMultiplayer(true);
            return this;
        }

        public Builder setMultiplayer(boolean multiplayer) {
            this.multiplayer = multiplayer;
            return this;
        }

        public MinimapWorldConnectionManager build() {
            return new MinimapWorldConnectionManager(this.multiplayer);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

