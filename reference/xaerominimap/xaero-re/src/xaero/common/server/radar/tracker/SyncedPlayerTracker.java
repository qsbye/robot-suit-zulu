/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.radar.tracker;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.server.MinecraftServerData;
import xaero.common.server.player.ServerPlayerData;
import xaero.common.server.radar.tracker.ISyncedPlayerTrackerSystem;
import xaero.common.server.radar.tracker.SyncedTrackedPlayer;
import xaero.hud.packet.tracker.ClientboundTrackedPlayerPacket;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;
import xaero.lib.common.config.single.SingleConfigManager;

public class SyncedPlayerTracker {
    private final IXaeroMinimap modMain;

    public SyncedPlayerTracker(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void onTick(MinecraftServer server, ServerPlayer player, MinecraftServerData serverData, ServerPlayerData playerData) {
        boolean opacReceiveMutualAllies;
        boolean shouldSyncToOthers;
        boolean playerHasMod;
        long currentTime = System.currentTimeMillis();
        if (currentTime - playerData.getLastTrackedPlayerSync() < 250L) {
            return;
        }
        playerData.setLastTrackedPlayerSync(currentTime);
        boolean shouldSyncToPlayer = playerHasMod = playerData.hasMod();
        Iterable<ISyncedPlayerTrackerSystem> playerTrackerSystems = serverData.getSyncedPlayerTrackerSystemManager().getSystems();
        Set<UUID> syncedPlayers = playerData.ensureCurrentlySyncedPlayers();
        HashSet<UUID> leftoverPlayers = new HashSet<UUID>(syncedPlayers);
        SyncedTrackedPlayer toSync = playerData.getLastSyncedData();
        boolean bl = shouldSyncToOthers = toSync == null || !toSync.matchesEnough((Player)player, 0.0);
        if (shouldSyncToOthers) {
            toSync = playerData.ensureLastSyncedData();
            toSync.update((Player)player);
        }
        SingleConfigManager primaryCommonConfig = HudMod.INSTANCE.getHudConfigs().getPrimaryCommonConfigManager();
        SingleConfigManager libPrimaryCommonConfig = XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager();
        boolean everyoneIsTracked = (Boolean)libPrimaryCommonConfig.getEffective((ConfigOption)LibPrimaryCommonConfigOptions.EVERYONE_TRACKS_EVERYONE);
        boolean opacReceiveParty = shouldSyncToPlayer && this.modMain.getSupportServerMods().hasOpac() && this.modMain.getSupportServerMods().getOpac().getReceiveLocationsFromPartyConfigValue(player);
        boolean bl2 = opacReceiveMutualAllies = shouldSyncToPlayer && this.modMain.getSupportServerMods().hasOpac() && this.modMain.getSupportServerMods().getOpac().getReceiveLocationsFromMutualAlliesConfigValue(player);
        if (this.modMain.getSupportServerMods().hasOpac()) {
            this.modMain.getSupportServerMods().getOpac().updateShareLocationConfigValues(player, playerData);
        }
        for (ServerPlayer otherPlayer : server.getPlayerList().getPlayers()) {
            Set<UUID> otherPlayerSyncedPlayers;
            if (otherPlayer == player) continue;
            leftoverPlayers.remove(otherPlayer.getUUID());
            ServerPlayerData otherPlayerData = ServerPlayerData.get(otherPlayer);
            if (shouldSyncToOthers && (otherPlayerSyncedPlayers = otherPlayerData.getCurrentlySyncedPlayers()) != null && otherPlayerSyncedPlayers.contains(player.getUUID())) {
                this.sendTrackedPlayerPacket(otherPlayer, otherPlayerData, toSync);
            }
            if (!shouldSyncToPlayer) continue;
            boolean tracked = everyoneIsTracked;
            if (!tracked) {
                boolean opacConfigsAllowPartySync = !this.modMain.getSupportServerMods().hasOpac() || this.modMain.getSupportServerMods().getOpac().isPositionSyncAllowed(2, otherPlayerData, opacReceiveParty);
                boolean opacConfigsAllowAllySync = !this.modMain.getSupportServerMods().hasOpac() || this.modMain.getSupportServerMods().getOpac().isPositionSyncAllowed(1, otherPlayerData, opacReceiveMutualAllies);
                for (ISyncedPlayerTrackerSystem system : playerTrackerSystems) {
                    int trackingLevel = system.getTrackingLevel((Player)player, (Player)otherPlayer);
                    if (trackingLevel <= 0 || system.isPartySystem() && (trackingLevel != 1 || !opacConfigsAllowAllySync) && (trackingLevel <= 1 || !opacConfigsAllowPartySync)) continue;
                    tracked = true;
                    break;
                }
            }
            boolean alreadySynced = syncedPlayers.contains(otherPlayer.getUUID());
            if (!tracked) {
                if (!alreadySynced) continue;
                syncedPlayers.remove(otherPlayer.getUUID());
                this.sendRemovePacket(player, playerData, otherPlayer.getUUID());
                continue;
            }
            if (alreadySynced || otherPlayerData.getLastSyncedData() == null) continue;
            syncedPlayers.add(otherPlayer.getUUID());
            this.sendTrackedPlayerPacket(player, playerData, otherPlayerData.getLastSyncedData());
        }
        for (UUID offlineId : leftoverPlayers) {
            syncedPlayers.remove(offlineId);
            this.sendRemovePacket(player, playerData, offlineId);
        }
    }

    private void sendRemovePacket(ServerPlayer player, ServerPlayerData playerData, UUID toRemove) {
        this.modMain.getMessageHandler().sendToPlayer(player, (Object)new ClientboundTrackedPlayerPacket(true, toRemove, 0.0, 0.0, 0.0, null, playerData.getClientModNetworkVersion()));
    }

    private void sendTrackedPlayerPacket(ServerPlayer player, ServerPlayerData playerData, SyncedTrackedPlayer tracked) {
        this.modMain.getMessageHandler().sendToPlayer(player, (Object)new ClientboundTrackedPlayerPacket(false, tracked.getId(), tracked.getX(), tracked.getY(), tracked.getZ(), tracked.getDimensionKey().identifier(), playerData.getClientModNetworkVersion()));
    }
}

