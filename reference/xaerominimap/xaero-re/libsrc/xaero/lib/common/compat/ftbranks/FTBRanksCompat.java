/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.ftbranks;

import xaero.lib.common.compat.ftbranks.FTBRanksSystem;
import xaero.lib.common.permission.system.IPermissionSystem;

public class FTBRanksCompat {
    public IPermissionSystem createPermissionSystem() {
        return new FTBRanksSystem();
    }
}

