/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;

public class MinimapWorldManager {
    private final HudMod modMain;
    private final MinimapSession session;
    private final Map<String, MinimapWorldRootContainer> rootContainers;
    private final Map<Identifier, Int2ObjectMap<Waypoint>> customWaypoints;

    public MinimapWorldManager(HudMod modMain, MinimapSession session) {
        this.modMain = modMain;
        this.session = session;
        this.rootContainers = new HashMap<String, MinimapWorldRootContainer>();
        this.customWaypoints = new Hashtable<Identifier, Int2ObjectMap<Waypoint>>();
    }

    public MinimapWorldContainer getWorldContainer(XaeroPath path) {
        return this.addWorldContainer(path);
    }

    public MinimapWorldContainer getWorldContainerNullable(XaeroPath path) {
        if (this.containerExists(path)) {
            return this.addWorldContainer(path);
        }
        return null;
    }

    public MinimapWorldRootContainer getRootWorldContainer(String rootContainerId) {
        return this.getRootWorldContainer(XaeroPath.root(rootContainerId));
    }

    public MinimapWorldRootContainer getRootWorldContainer(XaeroPath rootContainerPath) {
        return this.getWorldContainer(rootContainerPath).getRoot();
    }

    public MinimapWorldContainer addWorldContainer(XaeroPath path) {
        XaeroPath rootPath = path.getRoot();
        MinimapWorldRootContainer rootContainer = this.rootContainers.get(rootPath.getLastNode());
        if (rootContainer == null) {
            rootContainer = ((MinimapWorldRootContainer.Builder)((MinimapWorldRootContainer.Builder)((MinimapWorldRootContainer.Builder)MinimapWorldRootContainer.Builder.begin().setModMain(this.modMain)).setSession(this.session)).setPath(rootPath)).build();
            this.rootContainers.put(rootPath.getLastNode(), rootContainer);
            this.session.getWorldManagerIO().onRootContainerAdded(rootContainer);
        }
        if (path.getNodeCount() > 1) {
            return rootContainer.addSubContainer(path);
        }
        return rootContainer;
    }

    public void addRootWorldContainer(MinimapWorldRootContainer container) {
        if (this.rootContainers.containsKey(container.getPath().getLastNode())) {
            throw new IllegalArgumentException();
        }
        this.rootContainers.put(container.getPath().getLastNode(), container);
    }

    public boolean removeContainer(XaeroPath path) {
        if (path.getNodeCount() == 1) {
            return this.rootContainers.remove(path.getLastNode()) != null;
        }
        MinimapWorldRootContainer rootContainer = this.rootContainers.get(path.getRoot().getLastNode());
        if (rootContainer == null) {
            return false;
        }
        return rootContainer.deleteSubContainer(path);
    }

    public boolean containerExists(XaeroPath path) {
        MinimapWorldRootContainer rootContainer = this.rootContainers.get(path.getRoot().getLastNode());
        if (rootContainer == null) {
            return false;
        }
        if (path.getNodeCount() == 1) {
            return true;
        }
        return rootContainer.containsSubContainer(path);
    }

    public MinimapWorld getWorld(XaeroPath worldPath) {
        return this.addWorld(worldPath);
    }

    public MinimapWorld addWorld(XaeroPath worldPath) {
        if (worldPath == null) {
            return null;
        }
        MinimapWorldContainer wc = this.addWorldContainer(worldPath.getParent());
        return wc.addWorld(worldPath.getLastNode());
    }

    public MinimapWorld getCurrentWorld() {
        return this.getCurrentWorld(this.session.getWorldState().getAutoWorldPath());
    }

    public MinimapWorld getCurrentWorld(XaeroPath autoWorldPath) {
        return this.getWorld(this.session.getWorldState().getCurrentWorldPath(autoWorldPath));
    }

    @Nullable
    public MinimapWorld getAutoWorld() {
        return this.getWorld(this.session.getWorldState().getAutoWorldPath());
    }

    public Iterable<MinimapWorldRootContainer> getRootContainers() {
        return this.rootContainers.values();
    }

    public MinimapWorldRootContainer getAutoRootContainer() {
        return this.getRootWorldContainer(this.session.getWorldState().getAutoRootContainerPath());
    }

    public MinimapWorldRootContainer getCurrentRootContainer() {
        MinimapWorld currentWorld = this.getCurrentWorld();
        if (currentWorld == null) {
            return null;
        }
        return currentWorld.getContainer().getRoot();
    }

    public Int2ObjectMap<Waypoint> getCustomWaypoints(Identifier modId) {
        return this.customWaypoints.computeIfAbsent(modId, s -> Int2ObjectMaps.synchronize((Int2ObjectMap)new Int2ObjectOpenHashMap()));
    }

    public boolean hasCustomWaypoints() {
        return !this.customWaypoints.isEmpty();
    }

    public Iterable<Waypoint> getCustomWaypoints() {
        return this.customWaypoints.values().stream().flatMap(modMap -> modMap.values().stream())::iterator;
    }
}

