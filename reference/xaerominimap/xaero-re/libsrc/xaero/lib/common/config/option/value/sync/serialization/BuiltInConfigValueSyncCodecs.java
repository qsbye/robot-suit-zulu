/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.sync.serialization;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;

public class BuiltInConfigValueSyncCodecs {
    public static final ConfigValueSyncCodec<Boolean, ByteTag> BOOLEAN = new ConfigValueSyncCodec<Boolean, ByteTag>(ByteTag::valueOf, tag -> tag.byteValue() == 1);
    public static final ConfigValueSyncCodec<Integer, IntTag> INTEGER = new ConfigValueSyncCodec<Integer, IntTag>(IntTag::valueOf, IntTag::intValue);
    public static final ConfigValueSyncCodec<Double, DoubleTag> DOUBLE = new ConfigValueSyncCodec<Double, DoubleTag>(DoubleTag::valueOf, DoubleTag::doubleValue);
    public static final ConfigValueSyncCodec<String, StringTag> STRING = new ConfigValueSyncCodec<String, StringTag>(StringTag::valueOf, StringTag::value);
    public static final ConfigValueSyncCodec<Identifier, StringTag> RESOURCE_LOCATION = new ConfigValueSyncCodec<Identifier, StringTag>(rl -> STRING.encode(rl.toString()), tag -> Identifier.parse((String)STRING.decode((StringTag)tag)));
}

