/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.player;

import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.common.player.config.permission.PlayerConfigChannelPermissionUpdater;

public class ServerPlayerTickHandler {
    public void handle(ServerPlayer player) {
        ServerPlayerData playerData = ServerPlayerData.get(player);
        if (playerData.shouldUpdateConfigPermissions()) {
            new PlayerConfigChannelPermissionUpdater().handle(player, true);
            playerData.setShouldUpdateConfigPermissions(false);
        }
    }
}

