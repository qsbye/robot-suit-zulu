/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.ConfigChannelRegistry;
import xaero.lib.common.player.IServerPlayer;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.common.player.ServerPlayerLoginHandler;
import xaero.lib.common.player.ServerPlayerTickHandler;

public class CommonEvents {
    private final ServerPlayerTickHandler playerTickHandler = new ServerPlayerTickHandler();

    public void clonePlayer(Player from, Player to) {
        if (!(to instanceof IServerPlayer)) {
            return;
        }
        ((IServerPlayer)to).xaerolib_setData(((IServerPlayer)from).xaerolib_getData());
        ((IServerPlayer)to).xaerolib_getData().setPlayer((ServerPlayer)to);
    }

    public void playerLogIn(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        new ServerPlayerLoginHandler().handle(serverPlayer);
    }

    public void onPlayerTick(boolean isTickStart, boolean isServerSide, Player player) {
        if (!isTickStart || !isServerSide) {
            return;
        }
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        this.playerTickHandler.handle((ServerPlayer)player);
    }

    public void serverStarting(MinecraftServer server) {
        for (ConfigChannel configChannel : ConfigChannelRegistry.INSTANCE) {
            configChannel.getServerConfigManager().setServer(server);
        }
    }

    public void onPlayerPermissionChange(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayerData.get((ServerPlayer)player).setShouldUpdateConfigPermissions(true);
    }
}

