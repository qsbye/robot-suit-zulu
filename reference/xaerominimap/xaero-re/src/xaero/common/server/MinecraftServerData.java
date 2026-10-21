/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import xaero.common.IXaeroMinimap;
import xaero.common.server.IMinecraftServer;
import xaero.common.server.level.LevelMapProperties;
import xaero.common.server.level.LevelMapPropertiesIO;
import xaero.common.server.radar.tracker.SyncedPlayerTracker;
import xaero.common.server.radar.tracker.SyncedPlayerTrackerSystemManager;
import xaero.hud.minimap.MinimapLogs;

public class MinecraftServerData {
    private final SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager;
    private final SyncedPlayerTracker syncedPlayerTracker;
    private final Map<Path, LevelMapProperties> levelProperties;
    private final LevelMapPropertiesIO propertiesIO;
    private final IXaeroMinimap modMain;

    public MinecraftServerData(SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager, SyncedPlayerTracker syncedPlayerTracker, IXaeroMinimap modMain) {
        this.syncedPlayerTrackerSystemManager = syncedPlayerTrackerSystemManager;
        this.syncedPlayerTracker = syncedPlayerTracker;
        this.levelProperties = new HashMap<Path, LevelMapProperties>();
        this.propertiesIO = new LevelMapPropertiesIO();
        this.modMain = modMain;
    }

    public LevelMapProperties getLevelProperties(Path path) {
        LevelMapProperties properties = this.levelProperties.get(path);
        if (properties == null) {
            properties = new LevelMapProperties();
            try {
                this.propertiesIO.load(path, properties);
            }
            catch (FileNotFoundException fnfe) {
                try {
                    this.propertiesIO.save(path, properties);
                }
                catch (IOException e) {
                    properties.setUsable(false);
                    MinimapLogs.LOGGER.warn("Failed to initialize map properties for a world due to an IO exception. This shouldn't be a problem if it's not a \"real\" world. Message: {}", (Object)e.getMessage());
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.levelProperties.put(path, properties);
        }
        return properties;
    }

    public SyncedPlayerTrackerSystemManager getSyncedPlayerTrackerSystemManager() {
        return this.syncedPlayerTrackerSystemManager;
    }

    public SyncedPlayerTracker getSyncedPlayerTracker() {
        return this.syncedPlayerTracker;
    }

    public static MinecraftServerData get(MinecraftServer server) {
        return ((IMinecraftServer)server).getXaeroMinimapServerData();
    }

    public IXaeroMinimap getModMain() {
        return this.modMain;
    }
}

