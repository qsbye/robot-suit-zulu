/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.type;

import net.minecraft.nbt.Tag;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;
import xaero.lib.common.config.option.value.io.serialization.LargeConfigValueIOCodec;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;

public class ConfigValueType<T> {
    private final ConfigValueIOCodec<T> ioCodec;
    private final ConfigValueSyncCodec<T, ? extends Tag> syncCodec;

    protected ConfigValueType(ConfigValueIOCodec<T> ioCodec, ConfigValueSyncCodec<T, ? extends Tag> syncCodec) {
        this.ioCodec = ioCodec;
        this.syncCodec = syncCodec;
    }

    public ConfigValueIOCodec<T> getIoCodec() {
        return this.ioCodec;
    }

    public ConfigValueSyncCodec<T, ? extends Tag> getSyncCodec() {
        return this.syncCodec;
    }

    public static final class FinalBuilder<T>
    extends Builder<T, FinalBuilder<T>> {
        private FinalBuilder() {
        }

        @Override
        protected ConfigValueType<T> buildInternally() {
            return new ConfigValueType(this.ioCodec, this.syncCodec);
        }

        public static <T> FinalBuilder<T> begin() {
            return (FinalBuilder)new FinalBuilder<T>().setDefault();
        }
    }

    public static abstract class Builder<T, B extends Builder<T, B>> {
        protected final B self = this;
        protected ConfigValueIOCodec<T> ioCodec;
        protected ConfigValueSyncCodec<T, ? extends Tag> syncCodec;

        protected Builder() {
        }

        public B setDefault() {
            this.setIoCodec(null);
            this.setSyncCodec(null);
            return this.self;
        }

        public B setIoCodec(ConfigValueIOCodec<T> ioCodec) {
            if (ioCodec instanceof LargeConfigValueIOCodec) {
                throw new IllegalArgumentException();
            }
            this.ioCodec = ioCodec;
            return this.self;
        }

        public B setSyncCodec(ConfigValueSyncCodec<T, ? extends Tag> syncCodec) {
            this.syncCodec = syncCodec;
            return this.self;
        }

        public ConfigValueType<T> build() {
            if (this.ioCodec == null || this.syncCodec == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally();
        }

        protected abstract ConfigValueType<T> buildInternally();
    }
}

