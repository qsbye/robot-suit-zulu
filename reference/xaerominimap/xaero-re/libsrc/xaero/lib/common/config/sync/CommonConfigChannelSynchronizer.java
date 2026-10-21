/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.Logger;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.packet.config.AbstractConfigPacket;

public class CommonConfigChannelSynchronizer {
    protected final ConfigOptionManager options;
    protected ConfigChannel channel;
    public final Logger logger;

    public CommonConfigChannelSynchronizer(ConfigOptionManager options, Logger logger) {
        this.options = options;
        this.logger = logger;
    }

    public void setChannel(ConfigChannel channel) {
        if (this.channel != null) {
            throw new IllegalStateException();
        }
        this.channel = channel;
    }

    protected List<AbstractConfigPacket.OptionEntry> getConfigDifferenceEntries(ConfigProfile from, ConfigProfile to) {
        ArrayList<AbstractConfigPacket.OptionEntry> packetEntries = new ArrayList<AbstractConfigPacket.OptionEntry>();
        for (ConfigOption<?> option : this.options) {
            this.addDifferenceEntryFor(option, from, to, packetEntries);
        }
        return packetEntries;
    }

    private <T> void addDifferenceEntryFor(ConfigOption<T> option, ConfigProfile from, ConfigProfile to, List<AbstractConfigPacket.OptionEntry> packetEntries) {
        T toValue = to.get(option);
        if (!Objects.equals(from.get(option), toValue)) {
            packetEntries.add(AbstractConfigPacket.OptionEntry.of(option, toValue));
        }
    }

    protected List<AbstractConfigPacket.OptionEntry> getAllConfigEntries(ConfigProfile config) {
        ArrayList<AbstractConfigPacket.OptionEntry> packetEntries = new ArrayList<AbstractConfigPacket.OptionEntry>();
        for (ConfigOption<?> option : config.usedOptions()) {
            packetEntries.add(AbstractConfigPacket.OptionEntry.of(config, option));
        }
        return packetEntries;
    }

    protected List<AbstractConfigPacket.OptionEntry> getDirtyConfigEntries(ConfigProfile config) {
        ArrayList<AbstractConfigPacket.OptionEntry> packetEntries = new ArrayList<AbstractConfigPacket.OptionEntry>();
        for (ConfigOption<?> option : config.getDirtyOptions()) {
            packetEntries.add(AbstractConfigPacket.OptionEntry.of(config, option));
        }
        return packetEntries;
    }

    protected void setConfigValues(Config config, Iterable<AbstractConfigPacket.OptionEntry> entries) {
        for (AbstractConfigPacket.OptionEntry entry : entries) {
            ConfigOption<?> option = this.options.get(entry.optionId);
            if (option == null) {
                this.logger.warn("Received invalid config option ID {} for channel {}. Skipping.", (Object)entry.optionId, (Object)this.channel.getId());
                continue;
            }
            this.setConfigOptionValue(config, option, entry.valueTag);
        }
    }

    protected <T> void setConfigOptionValue(Config config, ConfigOption<T> option, Tag valueTag) {
        CompoundTag compoundTag;
        if (valueTag instanceof CompoundTag && (compoundTag = (CompoundTag)valueTag).isEmpty()) {
            CommonConfigChannelSynchronizer.setConfigOptionValue(config, option, null);
            return;
        }
        T value = CommonConfigChannelSynchronizer.decodeOptionValue(option, valueTag);
        if (value == null) {
            this.logger.warn("Failed to decode received value for option {} of channel {}. Skipping.", (Object)option.getId(), (Object)this.channel.getId());
            return;
        }
        CommonConfigChannelSynchronizer.setConfigOptionValue(config, option, value);
    }

    protected static <T> void setConfigOptionValue(Config config, ConfigOption<T> option, T value) {
        config.set(option, value);
    }

    protected static <T> T decodeOptionValue(ConfigOption<T> option, Tag tag) {
        return CommonConfigChannelSynchronizer.decodeOptionValue(option.getValueType().getSyncCodec(), tag);
    }

    protected static <T, N extends Tag> T decodeOptionValue(ConfigValueSyncCodec<T, N> syncCodec, Tag tag) {
        try {
            Tag tagCast = tag;
            return syncCodec.decode(tagCast);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;
        protected ConfigOptionManager options;
        protected Logger logger;

        protected Builder() {
        }

        public B setDefault() {
            this.setOptions(null);
            this.setLogger(null);
            return this.self;
        }

        public B setOptions(ConfigOptionManager options) {
            this.options = options;
            return this.self;
        }

        public B setLogger(Logger logger) {
            this.logger = logger;
            return this.self;
        }

        public CommonConfigChannelSynchronizer build() {
            if (this.options == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally();
        }

        protected abstract CommonConfigChannelSynchronizer buildInternally();
    }
}

