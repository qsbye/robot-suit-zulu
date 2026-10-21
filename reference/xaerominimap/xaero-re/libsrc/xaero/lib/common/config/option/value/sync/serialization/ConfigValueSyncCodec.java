/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.sync.serialization;

import java.util.function.Function;
import net.minecraft.nbt.Tag;

public class ConfigValueSyncCodec<T, N extends Tag> {
    private final Function<T, N> encoder;
    private final Function<N, T> decoder;

    public ConfigValueSyncCodec(Function<T, N> encoder, Function<N, T> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public N encode(T value) {
        return (N)((Tag)this.encoder.apply(value));
    }

    public T decode(N tag) {
        return this.decoder.apply(tag);
    }
}

