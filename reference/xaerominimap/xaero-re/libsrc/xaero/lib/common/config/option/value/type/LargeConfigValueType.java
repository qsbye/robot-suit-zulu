/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.type;

import net.minecraft.nbt.Tag;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;
import xaero.lib.common.config.option.value.io.serialization.LargeConfigValueIOCodec;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;
import xaero.lib.common.config.option.value.type.ConfigValueType;

public class LargeConfigValueType<T>
extends ConfigValueType<T> {
    protected LargeConfigValueType(ConfigValueIOCodec<T> ioCodec, ConfigValueSyncCodec<T, ? extends Tag> syncCodec) {
        super(ioCodec, syncCodec);
    }

    @Override
    public LargeConfigValueIOCodec<T> getIoCodec() {
        return (LargeConfigValueIOCodec)super.getIoCodec();
    }

    public static final class Builder<T>
    extends ConfigValueType.Builder<T, Builder<T>> {
        private Builder() {
        }

        @Override
        public Builder<T> setDefault() {
            super.setDefault();
            return (Builder)this.self;
        }

        @Override
        public Builder<T> setIoCodec(LargeConfigValueIOCodec<T> ioCodec) {
            this.ioCodec = ioCodec;
            return (Builder)this.self;
        }

        @Override
        public ConfigValueType<T> build() {
            return super.build();
        }

        @Override
        protected LargeConfigValueType<T> buildInternally() {
            return new LargeConfigValueType(this.ioCodec, this.syncCodec);
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

