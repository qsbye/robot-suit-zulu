/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.codec;

import java.util.function.Function;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;

@Deprecated
public class InfoDisplayStateCodec<T>
extends ConfigValueIOCodec<T> {
    public InfoDisplayStateCodec(Function<String, T> decoder, Function<T, String> encoder, int maxStringLength) {
        super(encoder, decoder, maxStringLength);
    }
}

