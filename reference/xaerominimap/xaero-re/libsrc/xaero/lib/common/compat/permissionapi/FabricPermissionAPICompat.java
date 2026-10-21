/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.permissionapi;

import xaero.lib.common.compat.permissionapi.PermissionAPISystem;
import xaero.lib.common.permission.system.IPermissionSystem;

public class FabricPermissionAPICompat {
    public IPermissionSystem createSystem() {
        return new PermissionAPISystem();
    }
}

