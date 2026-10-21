/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.prometheus;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xaero.lib.common.compat.ModCompatibility;
import xaero.lib.common.compat.prometheus.ModPrometheus;
import xaero.lib.common.compat.prometheus.PrometheusOptions;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.system.IPermissionSystem;

public class PrometheusSystem
implements IPermissionSystem {
    @Override
    public boolean getBoolean(PermissionNode<Boolean> node, ServerPlayer player) {
        Boolean boxedValue = this.get(node, player);
        return boxedValue != null && boxedValue != false;
    }

    @Override
    public boolean getBooleanFast(PermissionNode<Boolean> node, ServerPlayer player) {
        return this.getBoolean(node, player);
    }

    @Override
    public <T> T get(PermissionNode<T> node, ServerPlayer player) {
        String modId = node.getModId();
        ModPrometheus modData = ModCompatibility.getInstance().getPrometheus().getModData(modId);
        if (modData == null) {
            return this.getPermission(node, player);
        }
        PrometheusOptions options = (PrometheusOptions)RoleApi.API.getOption((Player)player, modData.getSerializer());
        if (options == null) {
            return this.getPermission(node, player);
        }
        T result = options.get(node);
        if (result == null) {
            return this.getPermission(node, player);
        }
        return result;
    }

    @Override
    public <T> T getFast(PermissionNode<T> node, ServerPlayer player) {
        return this.get(node, player);
    }

    private <T> T getPermission(PermissionNode<T> node, ServerPlayer player) {
        if (node.getType() != Boolean.class) {
            return null;
        }
        TriState value = PermissionApi.API.getPermission((Player)player, node.getActualPath());
        Boolean result = value.isDefined() ? Boolean.valueOf(value.isTrue()) : null;
        return (T)result;
    }

    @Override
    public void handleModPresence(String modId) {
    }
}

