/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.mods.opac;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xaero.common.server.mods.opac.ServerPlayerOpacData;
import xaero.common.server.player.ServerPlayerData;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.player.config.api.IPlayerConfigAPI;
import xaero.pac.common.server.player.config.api.IPlayerConfigManagerAPI;
import xaero.pac.common.server.player.config.api.PlayerConfigOptions;

public class SupportOPACServer {
    public boolean isPositionSyncAllowed(int relationship, ServerPlayerData fromPlayerData, boolean receive) {
        if (!receive) {
            return false;
        }
        if (relationship <= 0) {
            return false;
        }
        ServerPlayerOpacData fromPlayerOpacData = this.getPlayerOpacData(fromPlayerData);
        if (relationship == 1 && !fromPlayerOpacData.shareLocationWithMutualAllies) {
            return false;
        }
        return relationship <= 1 || fromPlayerOpacData.shareLocationWithParty;
    }

    public boolean getReceiveLocationsFromMutualAlliesConfigValue(ServerPlayer player) {
        IPlayerConfigManagerAPI configManager = OpenPACServerAPI.get((MinecraftServer)player.level().getServer()).getPlayerConfigs();
        IPlayerConfigAPI config = configManager.getLoadedConfig(player.getUUID());
        return (Boolean)config.getEffective(PlayerConfigOptions.RECEIVE_LOCATIONS_FROM_PARTY_MUTUAL_ALLIES);
    }

    public boolean getReceiveLocationsFromPartyConfigValue(ServerPlayer player) {
        IPlayerConfigManagerAPI configManager = OpenPACServerAPI.get((MinecraftServer)player.level().getServer()).getPlayerConfigs();
        IPlayerConfigAPI config = configManager.getLoadedConfig(player.getUUID());
        return (Boolean)config.getEffective(PlayerConfigOptions.RECEIVE_LOCATIONS_FROM_PARTY);
    }

    public void updateShareLocationConfigValues(ServerPlayer player, ServerPlayerData playerData) {
        ServerPlayerOpacData opacData = this.getPlayerOpacData(playerData);
        IPlayerConfigManagerAPI configManager = OpenPACServerAPI.get((MinecraftServer)player.level().getServer()).getPlayerConfigs();
        IPlayerConfigAPI config = configManager.getLoadedConfig(player.getUUID());
        opacData.shareLocationWithParty = (Boolean)config.getEffective(PlayerConfigOptions.SHARE_LOCATION_WITH_PARTY);
        opacData.shareLocationWithMutualAllies = (Boolean)config.getEffective(PlayerConfigOptions.SHARE_LOCATION_WITH_PARTY_MUTUAL_ALLIES);
    }

    private ServerPlayerOpacData getPlayerOpacData(ServerPlayerData playerData) {
        ServerPlayerOpacData opacData = (ServerPlayerOpacData)playerData.getOpacData();
        if (opacData == null) {
            opacData = new ServerPlayerOpacData();
            playerData.setOpacData(opacData);
        }
        return opacData;
    }
}

