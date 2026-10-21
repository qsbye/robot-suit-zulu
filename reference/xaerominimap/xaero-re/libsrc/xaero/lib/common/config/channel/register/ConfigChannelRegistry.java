/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.channel.register;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.lib.client.config.channel.ConfigChannelClientInit;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.ConfigChannelCommonInit;
import xaero.lib.platform.Services;

public class ConfigChannelRegistry
implements Iterable<ConfigChannel> {
    public static final ConfigChannelRegistry INSTANCE = Builder.begin().build();
    private final Map<Identifier, ConfigChannel> channels;
    private final ConfigChannelCommonInit channelInit;
    private boolean frozen;

    private ConfigChannelRegistry(Map<Identifier, ConfigChannel> channels, ConfigChannelCommonInit channelInit) {
        this.channels = channels;
        this.channelInit = channelInit;
    }

    public synchronized void register(ConfigChannel channel) {
        if (this.frozen) {
            throw new IllegalStateException("Attempted to register a config channel too late: " + String.valueOf(channel.getId()));
        }
        if (this.channels.containsKey(channel.getId())) {
            throw new IllegalArgumentException("Attempted to register a duplicate config channel: " + String.valueOf(channel.getId()));
        }
        this.channels.put(channel.getId(), channel);
        this.channelInit.handle(channel);
    }

    public ConfigChannel get(Identifier id) {
        if (!this.frozen) {
            throw new IllegalStateException();
        }
        return this.channels.get(id);
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public Iterator<ConfigChannel> iterator() {
        if (!this.frozen) {
            throw new IllegalStateException("Registry is not frozen yet!");
        }
        return Collections.unmodifiableCollection(this.channels.values()).iterator();
    }

    private static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public ConfigChannelRegistry build() {
            ConfigChannelCommonInit channelInit = !Services.PLATFORM.isDedicatedServer() ? new ConfigChannelClientInit() : new ConfigChannelCommonInit();
            return new ConfigChannelRegistry(new HashMap<Identifier, ConfigChannel>(), channelInit);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

