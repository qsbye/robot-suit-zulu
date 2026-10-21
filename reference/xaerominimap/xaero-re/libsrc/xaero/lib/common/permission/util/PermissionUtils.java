/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission.util;

import net.minecraft.network.chat.Component;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.util.JsonUtils;

public class PermissionUtils {
    public static <T> T parseString(PermissionNode<T> node, String valueString) {
        if (node.getType() == Boolean.class) {
            return (T)Boolean.valueOf(valueString.equals("true"));
        }
        if (node.getType() == String.class) {
            return (T)valueString;
        }
        if (node.getType() == Component.class) {
            return (T)JsonUtils.fromJson(valueString);
        }
        if (node.getType() == Integer.class) {
            return (T)Integer.valueOf(valueString);
        }
        if (node.getType() == Double.class) {
            return (T)Double.valueOf(valueString);
        }
        if (node.getType() == Long.class) {
            return (T)Long.valueOf(valueString);
        }
        if (node.getType() == Byte.class) {
            return (T)Byte.valueOf(valueString);
        }
        throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(node.getType()));
    }
}

