/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.io.serialization;

import net.minecraft.resources.Identifier;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;

public class BuiltInConfigValueIOCodecs {
    public static final ConfigValueIOCodec<Boolean> BOOLEAN = new ConfigValueIOCodec<Boolean>(b -> "" + b, s -> s.equals("true"), 5);
    public static final ConfigValueIOCodec<Integer> INTEGER = new ConfigValueIOCodec<Integer>(i -> "" + i, Integer::parseInt, "-2147483648".length());
    public static final ConfigValueIOCodec<Double> DOUBLE = new ConfigValueIOCodec<Double>(d -> "" + d, Double::parseDouble, 100);
    public static final ConfigValueIOCodec<Identifier> RESOURCE_LOCATION = new ConfigValueIOCodec<Identifier>(Identifier::toString, Identifier::parse, 200);

    public static ConfigValueIOCodec<String> getString(int maxLength) {
        return new ConfigValueIOCodec<String>(s -> s, s -> s, maxLength);
    }
}

