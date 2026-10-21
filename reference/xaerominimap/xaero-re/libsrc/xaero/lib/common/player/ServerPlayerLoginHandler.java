/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.player;

import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.common.player.config.permission.PlayerConfigChannelPermissionUpdater;

public class ServerPlayerLoginHandler {
    public void handle(ServerPlayer player) {
        ServerPlayerData playerData = ServerPlayerData.get(player);
        new PlayerConfigChannelPermissionUpdater().handle(player, false);
        playerData.getConfigSynchronizer().handleLogin();
    }
}

