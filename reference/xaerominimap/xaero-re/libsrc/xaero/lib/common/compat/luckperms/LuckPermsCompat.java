/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.luckperms;

import xaero.lib.common.compat.luckperms.LuckPermsSystem;
import xaero.lib.common.permission.system.IPermissionSystem;

public class LuckPermsCompat {
    public IPermissionSystem createPermissionSystem() {
        return new LuckPermsSystem();
    }
}

