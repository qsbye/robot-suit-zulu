/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.permissionapi;

import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.system.IPermissionSystem;
import xaero.lib.common.permission.util.PermissionUtils;

public class PermissionAPISystem
implements IPermissionSystem {
    @Override
    public boolean getBoolean(PermissionNode<Boolean> node, ServerPlayer player) {
        return Permissions.check((Entity)player, (String)node.getActualPath());
    }

    @Override
    public boolean getBooleanFast(PermissionNode<Boolean> node, ServerPlayer player) {
        return this.getBoolean(node, player);
    }

    @Override
    public <T> T get(PermissionNode<T> node, ServerPlayer player) {
        if (node.getType() == Boolean.class) {
            PermissionNode<Boolean> booleanNode = node;
            Boolean booleanValueCast = this.getBoolean(booleanNode, player);
            return (T)booleanValueCast;
        }
        return Options.get((Entity)player, (String)node.getActualPath(), s -> PermissionUtils.parseString(node, s)).orElse(null);
    }

    @Override
    public <T> T getFast(PermissionNode<T> node, ServerPlayer player) {
        return this.get(node, player);
    }

    @Override
    public void handleModPresence(String modId) {
    }
}

