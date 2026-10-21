/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.sync;

import java.util.List;
import net.minecraft.resources.Identifier;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.ConfigChannelRegistry;
import xaero.lib.common.packet.config.AbstractConfigPacket;
import xaero.lib.common.packet.config.ConfigChannelPacket;
import xaero.lib.common.packet.config.profile.ConfigProfileInfoPacket;

public final class ClientConfigSynchronizer {
    private ConfigChannel currentInputChannel;
    private ConfigChannel currentOutputChannel;

    private ClientConfigSynchronizer() {
    }

    public void receiveChannelId(Identifier channelId) {
        this.currentInputChannel = ConfigChannelRegistry.INSTANCE.get(channelId);
        if (this.currentInputChannel == null) {
            XaeroLib.LOGGER.warn("Unknown config channel ID received: {}", (Object)channelId);
        }
    }

    public ConfigChannel getCurrentInputChannel() {
        return this.currentInputChannel;
    }

    private void ensureOutputChannel(ConfigChannel channel) {
        if (channel == this.currentOutputChannel) {
            return;
        }
        XaeroLib.INSTANCE.getPacketHandler().sendToServer(new ConfigChannelPacket(channel.getId()));
        this.currentOutputChannel = channel;
    }

    public void send(ConfigChannel channel, Object packet) {
        this.ensureOutputChannel(channel);
        XaeroLib.INSTANCE.getPacketHandler().sendToServer(packet);
    }

    public void onEditProfilePacket(String profileId, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getClientConfigSynchronizer().onEditProfilePacket(profileId, entries);
    }

    public void onEnforcedConfigPacket(boolean reset, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getClientConfigSynchronizer().onEnforcedConfigPacket(reset, entries);
    }

    public void onConfigProfileInfoPacket(List<ConfigProfileInfoPacket.Entry> entries, String defaultEnforcedProfileId, boolean reset) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getClientConfigSynchronizer().onConfigProfileInfoPacket(entries, defaultEnforcedProfileId, reset);
    }

    public void onDeleteConfigProfilePacket(String profileId) {
        if (this.currentInputChannel == null) {
            return;
        }
        this.currentInputChannel.getClientConfigSynchronizer().onDeleteConfigProfilePacket(profileId);
    }

    public void reset() {
        this.currentInputChannel = null;
        this.currentOutputChannel = null;
        for (ConfigChannel configChannel : ConfigChannelRegistry.INSTANCE) {
            configChannel.getClientConfigSynchronizer().reset();
        }
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public ClientConfigSynchronizer build() {
            return new ClientConfigSynchronizer();
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

