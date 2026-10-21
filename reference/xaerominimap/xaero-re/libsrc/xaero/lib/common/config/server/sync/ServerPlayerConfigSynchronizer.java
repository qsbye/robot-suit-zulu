/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.server.sync;

import java.util.List;
import net.minecraft.resources.Identifier;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.ConfigChannelRegistry;
import xaero.lib.common.packet.ClientboundServerHandshakePacket;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.packet.config.ConfigChannelPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileInfoPacket;
import xaero.lib.common.player.ServerPlayerData;

public class ServerPlayerConfigSynchronizer {
    private final ServerPlayerData playerData;
    private ConfigChannel currentOutputChannel;
    private ConfigChannel currentInputChannel;

    public ServerPlayerConfigSynchronizer(ServerPlayerData playerData) {
        this.playerData = playerData;
    }

    public void send(ConfigChannel channel, Object packet) {
        this.ensureOutputChannel(channel);
        XaeroLib.INSTANCE.getPacketHandler().sendToPlayer(this.playerData, packet);
    }

    private void ensureOutputChannel(ConfigChannel channel) {
        if (channel == this.currentOutputChannel) {
            return;
        }
        XaeroLib.INSTANCE.getPacketHandler().sendToPlayer(this.playerData, new ConfigChannelPacket(channel.getId()));
        this.currentOutputChannel = channel;
    }

    public void receiveChannelId(Identifier channelId) {
        if (channelId == null) {
            this.currentInputChannel = null;
            return;
        }
        this.currentInputChannel = ConfigChannelRegistry.INSTANCE.get(channelId);
    }

    public void onProfileRequestPacket(String profileId) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getServerConfigSynchronizer().onProfileRequestPacket(this, profileId);
    }

    public void onEditProfilePacket(String profileId, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getServerConfigSynchronizer().onEditProfilePacket(profileId, entries, this.playerData);
    }

    public void onConfigProfileInfoPacket(List<ConfigProfileInfoPacket.Entry> entries, String defaultEnforcedProfileId) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getServerConfigSynchronizer().onConfigProfileInfoPacket(entries, defaultEnforcedProfileId, this.playerData);
    }

    public void onDeleteConfigProfilePacket(String profileId) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getServerConfigSynchronizer().onDeleteConfigProfilePacket(profileId, this.playerData);
    }

    public void onCreateConfigProfilePacket(String id, String name, String profileToCopy) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getServerConfigSynchronizer().onCreateConfigProfilePacket(id, name, profileToCopy);
    }

    public void handleLogin() {
        XaeroLib.INSTANCE.getPacketHandler().sendToPlayer(this.playerData, new ClientboundServerHandshakePacket());
        for (ConfigChannel channel : ConfigChannelRegistry.INSTANCE) {
            channel.getServerConfigSynchronizer().handleLogin(this);
        }
    }

    public ConfigChannel getCurrentInputChannel() {
        return this.currentInputChannel;
    }

    public ServerPlayerData getPlayerData() {
        return this.playerData;
    }
}

