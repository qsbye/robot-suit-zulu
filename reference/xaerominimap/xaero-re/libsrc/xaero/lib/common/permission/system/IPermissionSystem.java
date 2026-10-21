/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission.system;

import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.permission.PermissionNode;

public interface IPermissionSystem {
    public boolean getBoolean(PermissionNode<Boolean> var1, ServerPlayer var2);

    public boolean getBooleanFast(PermissionNode<Boolean> var1, ServerPlayer var2);

    public <T> T get(PermissionNode<T> var1, ServerPlayer var2);

    public <T> T getFast(PermissionNode<T> var1, ServerPlayer var2);

    public void handleModPresence(String var1);
}

