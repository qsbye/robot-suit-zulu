/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xaero.common.server.MinecraftServerData;
import xaero.common.server.player.ServerPlayerData;

public class ServerPlayerTickHandler {
    public void tick(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        MinecraftServerData serverData = MinecraftServerData.get(server);
        ServerPlayerData playerData = ServerPlayerData.get(player);
        serverData.getSyncedPlayerTracker().onTick(server, player, serverData, playerData);
    }
}

