/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.type;

import net.minecraft.resources.Identifier;
import xaero.lib.common.config.option.value.io.serialization.BuiltInConfigValueIOCodecs;
import xaero.lib.common.config.option.value.sync.serialization.BuiltInConfigValueSyncCodecs;
import xaero.lib.common.config.option.value.type.ConfigValueType;

public class BuiltInConfigValueTypes {
    public static ConfigValueType<Boolean> BOOLEAN = ((ConfigValueType.FinalBuilder)((ConfigValueType.FinalBuilder)ConfigValueType.FinalBuilder.begin().setIoCodec(BuiltInConfigValueIOCodecs.BOOLEAN)).setSyncCodec(BuiltInConfigValueSyncCodecs.BOOLEAN)).build();
    public static ConfigValueType<Integer> INTEGER = ((ConfigValueType.FinalBuilder)((ConfigValueType.FinalBuilder)ConfigValueType.FinalBuilder.begin().setIoCodec(BuiltInConfigValueIOCodecs.INTEGER)).setSyncCodec(BuiltInConfigValueSyncCodecs.INTEGER)).build();
    public static ConfigValueType<Double> DOUBLE = ((ConfigValueType.FinalBuilder)((ConfigValueType.FinalBuilder)ConfigValueType.FinalBuilder.begin().setIoCodec(BuiltInConfigValueIOCodecs.DOUBLE)).setSyncCodec(BuiltInConfigValueSyncCodecs.DOUBLE)).build();
    public static ConfigValueType<Identifier> RESOURCE_LOCATION = ((ConfigValueType.FinalBuilder)((ConfigValueType.FinalBuilder)ConfigValueType.FinalBuilder.begin().setIoCodec(BuiltInConfigValueIOCodecs.RESOURCE_LOCATION)).setSyncCodec(BuiltInConfigValueSyncCodecs.RESOURCE_LOCATION)).build();
    public static ConfigValueType<String> UNLIMITED_STRING = BuiltInConfigValueTypes.getString(Integer.MAX_VALUE);

    public static ConfigValueType<String> getString(int maxLength) {
        return ((ConfigValueType.FinalBuilder)((ConfigValueType.FinalBuilder)ConfigValueType.FinalBuilder.begin().setIoCodec(BuiltInConfigValueIOCodecs.getString(maxLength))).setSyncCodec(BuiltInConfigValueSyncCodecs.STRING)).build();
    }
}

