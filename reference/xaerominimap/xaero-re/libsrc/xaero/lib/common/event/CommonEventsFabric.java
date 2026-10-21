/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;
import xaero.lib.common.event.CommonEvents;

public class CommonEventsFabric
extends CommonEvents {
    public void register() {
        ServerPlayerEvents.COPY_FROM.register(this::clonePlayer);
        ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
    }

    private void clonePlayer(ServerPlayer from, ServerPlayer to, boolean alive) {
        super.clonePlayer((Player)from, (Player)to);
    }

    @Override
    public void playerLogIn(Player player) {
        super.playerLogIn(player);
    }

    @Override
    public void serverStarting(MinecraftServer server) {
        if (server instanceof DedicatedServer && XaeroLib.INSTANCE != null) {
            ((XaeroLibFabric)XaeroLib.INSTANCE).tryLoadLaterServer();
        }
        super.serverStarting(server);
    }

    public void onPlayerPermissionsChanged(PlayerList playerList, NameAndId profile) {
        ServerPlayer player = playerList.getPlayer(profile.id());
        if (player == null) {
            return;
        }
        super.onPlayerPermissionChange((Player)player);
    }

    public void onPlayerTick(boolean isTickStart, Player player) {
        super.onPlayerTick(isTickStart, player instanceof ServerPlayer, player);
    }
}

