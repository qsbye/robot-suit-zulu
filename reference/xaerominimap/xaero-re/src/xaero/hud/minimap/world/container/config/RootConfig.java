/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.container.config;

import xaero.hud.minimap.waypoint.WaypointsSort;
import xaero.hud.minimap.world.connection.MinimapWorldConnectionManager;

public class RootConfig {
    public boolean loaded;
    private boolean usingMultiworldDetection = false;
    private boolean ignoreServerLevelId = false;
    private String defaultMultiworldId;
    private boolean teleportationEnabled = true;
    private boolean usingDefaultTeleportCommand = true;
    private String serverTeleportCommandFormat;
    private String serverTeleportCommandRotationFormat;
    private WaypointsSort sortType = WaypointsSort.NONE;
    private boolean sortReversed;
    private boolean ignoreHeightmaps;
    private MinimapWorldConnectionManager subWorldConnections;

    public RootConfig(boolean multiplayer) {
        this.resetSubWorldConnections(multiplayer);
    }

    public boolean isUsingMultiworldDetection() {
        return this.usingMultiworldDetection;
    }

    public void setUsingMultiworldDetection(boolean usingMultiworldDetection) {
        this.usingMultiworldDetection = usingMultiworldDetection;
    }

    public String getDefaultMultiworldId() {
        return this.defaultMultiworldId;
    }

    public void setDefaultMultiworldId(String defaultMultiworldId) {
        this.defaultMultiworldId = defaultMultiworldId;
    }

    public boolean isTeleportationEnabled() {
        return this.teleportationEnabled;
    }

    public void setTeleportationEnabled(boolean teleportation) {
        this.teleportationEnabled = teleportation;
    }

    public boolean isUsingDefaultTeleportCommand() {
        return this.usingDefaultTeleportCommand;
    }

    public void setUsingDefaultTeleportCommand(boolean usingDefaultTeleportCommand) {
        this.usingDefaultTeleportCommand = usingDefaultTeleportCommand;
    }

    public String getServerTeleportCommandFormat() {
        return this.serverTeleportCommandFormat;
    }

    public String getServerTeleportCommandRotationFormat() {
        return this.serverTeleportCommandRotationFormat;
    }

    public void setServerTeleportCommandFormat(String serverTeleportCommandFormat) {
        this.serverTeleportCommandFormat = serverTeleportCommandFormat;
    }

    public void setServerTeleportCommandRotationFormat(String serverTeleportCommandRotationFormat) {
        this.serverTeleportCommandRotationFormat = serverTeleportCommandRotationFormat;
    }

    public WaypointsSort getSortType() {
        return this.sortType;
    }

    public void setSortType(WaypointsSort sortType) {
        this.sortType = sortType;
    }

    public void toggleSortType() {
        this.sortType = WaypointsSort.values()[(this.sortType.ordinal() + 1) % WaypointsSort.values().length];
    }

    public boolean isSortReversed() {
        return this.sortReversed;
    }

    public void setSortReversed(boolean sortReversed) {
        this.sortReversed = sortReversed;
    }

    public void toggleSortReversed() {
        this.sortReversed = !this.sortReversed;
    }

    public boolean isIgnoreServerLevelId() {
        return this.ignoreServerLevelId;
    }

    public void setIgnoreServerLevelId(boolean ignoreServerLevelId) {
        this.ignoreServerLevelId = ignoreServerLevelId;
    }

    public boolean isIgnoreHeightmaps() {
        return this.ignoreHeightmaps;
    }

    public void setIgnoreHeightmaps(boolean ignoreHeightmaps) {
        this.ignoreHeightmaps = ignoreHeightmaps;
    }

    public void resetSubWorldConnections(boolean multiplayer) {
        this.subWorldConnections = MinimapWorldConnectionManager.Builder.begin().setMultiplayer(multiplayer).build();
    }

    public MinimapWorldConnectionManager getSubWorldConnections() {
        return this.subWorldConnections;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}

