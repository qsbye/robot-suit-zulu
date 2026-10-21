/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.prometheus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import xaero.lib.common.compat.prometheus.PrometheusOptions;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.PermissionRegistry;
import xaero.lib.common.util.JsonUtils;

public class PrometheusOptionsCodec
implements Codec<PrometheusOptions> {
    private final String modId;

    public PrometheusOptionsCodec(String modId) {
        this.modId = modId;
    }

    public <T> DataResult<T> encode(PrometheusOptions input, DynamicOps<T> ops, T prefix) {
        Object tag = ops.createMap(new LinkedHashMap());
        for (PermissionNode<?> node : input.getKeys()) {
            T valueTag = this.encodeValue(input, ops, node);
            Object mergeResult = ops.mergeToMap(tag, ops.createString(node.getDefaultPath()), valueTag).result().orElse(null);
            if (mergeResult == null) continue;
            tag = mergeResult;
        }
        return DataResult.success((Object)tag);
    }

    private <T, V> T encodeValue(PrometheusOptions input, DynamicOps<T> ops, PermissionNode<V> node) {
        V nodeValue = input.get(node);
        if (nodeValue == null) {
            return null;
        }
        if (node.getType() == Boolean.class) {
            return (T)ops.createBoolean(((Boolean)nodeValue).booleanValue());
        }
        if (node.getType() == String.class) {
            return (T)ops.createString((String)nodeValue);
        }
        if (node.getType() == Component.class) {
            String jsonString = JsonUtils.toJson((Component)nodeValue);
            return (T)(jsonString == null ? null : ops.createString(jsonString));
        }
        if (node.getType() == Integer.class) {
            return (T)ops.createInt(((Integer)nodeValue).intValue());
        }
        if (node.getType() == Byte.class) {
            return (T)ops.createByte(((Byte)nodeValue).byteValue());
        }
        if (node.getType() == Double.class) {
            return (T)ops.createDouble(((Double)nodeValue).doubleValue());
        }
        if (node.getType() == Long.class) {
            return (T)ops.createLong(((Long)nodeValue).longValue());
        }
        throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(node.getType()));
    }

    public <T> DataResult<Pair<PrometheusOptions, T>> decode(DynamicOps<T> ops, T input) {
        PrometheusOptions result = PrometheusOptions.Builder.begin().setModId(this.modId).build();
        Consumer mapEntriesResult = ops.getMapEntries(input).result().orElse(null);
        if (mapEntriesResult == null) {
            return DataResult.success((Object)Pair.of((Object)result, input));
        }
        mapEntriesResult.accept((key, value) -> {
            String keyString = ops.getStringValue(key).result().orElse(null);
            if (keyString == null) {
                return;
            }
            PermissionNode<?> node = PermissionRegistry.INSTANCE.getNode(this.modId, keyString);
            if (node == null) {
                return;
            }
            this.addValue(result, ops, value, node);
        });
        return DataResult.success((Object)Pair.of((Object)result, input));
    }

    private <T, V> void addValue(PrometheusOptions dest, DynamicOps<T> ops, T valueTag, PermissionNode<V> node) {
        dest.put(node, this.decodeValue(ops, valueTag, node));
    }

    private <T, V> V decodeValue(DynamicOps<T> ops, T valueTag, PermissionNode<V> node) {
        if (node.getType() == Boolean.class) {
            return (V)ops.getBooleanValue(valueTag).result().orElse(Boolean.FALSE);
        }
        if (node.getType() == String.class) {
            return ops.getStringValue(valueTag).result().orElse(null);
        }
        if (node.getType() == Component.class) {
            String stringValue = ops.getStringValue(valueTag).result().orElse(null);
            Component componentValue = null;
            if (stringValue != null) {
                componentValue = JsonUtils.fromJson(stringValue);
            }
            return (V)componentValue;
        }
        if (Number.class.isAssignableFrom(node.getType())) {
            return this.decodeNumber(ops, valueTag, node);
        }
        throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(node.getType()));
    }

    private <T, V> V decodeNumber(DynamicOps<T> ops, T valueTag, PermissionNode<V> node) {
        Number numberResult = ops.getNumberValue(valueTag).result().orElse(null);
        if (numberResult == null) {
            return null;
        }
        if (node.getType() == Integer.class) {
            return (V)Integer.valueOf(numberResult.intValue());
        }
        if (node.getType() == Double.class) {
            return (V)Double.valueOf(numberResult.doubleValue());
        }
        if (node.getType() == Long.class) {
            return (V)Long.valueOf(numberResult.longValue());
        }
        if (node.getType() == Byte.class) {
            return (V)Byte.valueOf(numberResult.byteValue());
        }
        return null;
    }
}

