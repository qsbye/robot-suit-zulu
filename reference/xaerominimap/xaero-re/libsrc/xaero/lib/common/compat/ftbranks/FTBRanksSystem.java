/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.ftbranks;

import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.PermissionValue;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.system.IPermissionSystem;

public class FTBRanksSystem
implements IPermissionSystem {
    @Override
    public boolean getBoolean(PermissionNode<Boolean> node, ServerPlayer player) {
        PermissionValue value = FTBRanksAPI.getPermissionValue((ServerPlayer)player, (String)node.getActualPath());
        if (value == null) {
            return false;
        }
        return value.asBooleanOrFalse();
    }

    @Override
    public boolean getBooleanFast(PermissionNode<Boolean> node, ServerPlayer player) {
        return this.getBoolean(node, player);
    }

    @Override
    public <T> T get(PermissionNode<T> node, ServerPlayer player) {
        PermissionValue value = FTBRanksAPI.getPermissionValue((ServerPlayer)player, (String)node.getActualPath());
        if (value == null) {
            return null;
        }
        if (node.getType() == Boolean.class) {
            return (T)Boolean.valueOf(value.asBooleanOrFalse());
        }
        if (node.getType() == String.class) {
            return value.asString().orElse(null);
        }
        if (node.getType() == Component.class) {
            String stringResult = value.asString().orElse(null);
            return (T)(stringResult == null ? null : Component.literal((String)stringResult));
        }
        if (node.getType() == Integer.class) {
            return (T)Integer.valueOf(value.asInteger().orElse(0));
        }
        if (node.getType() == Double.class) {
            return (T)Double.valueOf(value.asDouble().orElse(0.0));
        }
        if (node.getType() == Long.class) {
            return (T)Long.valueOf(value.asLong().orElse(0L));
        }
        if (node.getType() == Byte.class) {
            return (T)Byte.valueOf((byte)value.asInteger().orElse(0));
        }
        throw new IllegalArgumentException("Unsupported permission node type!");
    }

    @Override
    public <T> T getFast(PermissionNode<T> node, ServerPlayer player) {
        return this.get(node, player);
    }

    @Override
    public void handleModPresence(String modId) {
    }
}

