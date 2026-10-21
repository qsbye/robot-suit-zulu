/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import xaero.common.server.player.IServerPlayer;
import xaero.common.server.radar.tracker.SyncedTrackedPlayer;

public class ServerPlayerData {
    private final UUID playerId;
    private SyncedTrackedPlayer lastSyncedData;
    private Set<UUID> currentlySyncedPlayers;
    private long lastTrackedPlayerSync;
    private int clientModNetworkVersion;
    private Object opacData;

    public ServerPlayerData(UUID playerId) {
        this.playerId = playerId;
    }

    public SyncedTrackedPlayer getLastSyncedData() {
        return this.lastSyncedData;
    }

    public SyncedTrackedPlayer ensureLastSyncedData() {
        if (this.lastSyncedData == null) {
            this.lastSyncedData = new SyncedTrackedPlayer(this.playerId, 0.0, 0.0, 0.0, null);
        }
        return this.lastSyncedData;
    }

    public Set<UUID> getCurrentlySyncedPlayers() {
        return this.currentlySyncedPlayers;
    }

    public Set<UUID> ensureCurrentlySyncedPlayers() {
        if (this.currentlySyncedPlayers == null) {
            this.currentlySyncedPlayers = new HashSet<UUID>();
        }
        return this.currentlySyncedPlayers;
    }

    public long getLastTrackedPlayerSync() {
        return this.lastTrackedPlayerSync;
    }

    public void setLastTrackedPlayerSync(long lastTrackedPlayerSync) {
        this.lastTrackedPlayerSync = lastTrackedPlayerSync;
    }

    public static ServerPlayerData get(ServerPlayer player) {
        ServerPlayerData result = ((IServerPlayer)player).getXaeroMinimapPlayerData();
        if (result == null) {
            result = new ServerPlayerData(player.getUUID());
            ((IServerPlayer)player).setXaeroMinimapPlayerData(result);
        }
        return result;
    }

    public boolean hasMod() {
        return this.clientModNetworkVersion != 0;
    }

    public void setClientModNetworkVersion(int clientModNetworkVersion) {
        this.clientModNetworkVersion = clientModNetworkVersion;
    }

    public int getClientModNetworkVersion() {
        return this.clientModNetworkVersion;
    }

    public void setOpacData(Object opacData) {
        this.opacData = opacData;
    }

    public Object getOpacData() {
        return this.opacData;
    }
}

