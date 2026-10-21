/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.player;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.server.sync.ServerPlayerConfigSynchronizer;
import xaero.lib.common.player.IServerPlayer;
import xaero.lib.common.player.config.ServerPlayerConfigChannelData;

public class ServerPlayerData {
    private ServerPlayer player;
    private ServerPlayerConfigSynchronizer configSynchronizer = new ServerPlayerConfigSynchronizer(this);
    private Map<ConfigChannel, ServerPlayerConfigChannelData> configChannelDataMap = new HashMap<ConfigChannel, ServerPlayerConfigChannelData>();
    private boolean shouldUpdateConfigPermissions;

    private ServerPlayerData() {
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public ServerPlayerConfigSynchronizer getConfigSynchronizer() {
        return this.configSynchronizer;
    }

    public static ServerPlayerData get(ServerPlayer player) {
        ServerPlayerData data = ((IServerPlayer)player).xaerolib_getData();
        if (data == null) {
            data = new ServerPlayerData();
            ((IServerPlayer)player).xaerolib_setData(data);
            data.setPlayer(player);
        }
        return data;
    }

    public ServerPlayerConfigChannelData getConfigChannelData(ConfigChannel channel) {
        return this.configChannelDataMap.computeIfAbsent(channel, c -> new ServerPlayerConfigChannelData());
    }

    public void setShouldUpdateConfigPermissions(boolean value) {
        this.shouldUpdateConfigPermissions = value;
    }

    public boolean shouldUpdateConfigPermissions() {
        return this.shouldUpdateConfigPermissions;
    }
}

