/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat;

import xaero.lib.common.compat.PlatformModCompatibility;
import xaero.lib.common.compat.permissionapi.FabricPermissionAPICompat;
import xaero.lib.common.permission.system.PermissionSystemRegistry;

public class FabricModCompatibility
extends PlatformModCompatibility {
    private FabricPermissionAPICompat permissionAPI;

    public FabricModCompatibility() {
        try {
            Class.forName("me.lucko.fabric.api.permissions.v0.Options");
            this.permissionAPI = new FabricPermissionAPICompat();
        }
        catch (ClassNotFoundException | NoClassDefFoundError throwable) {
            // empty catch block
        }
    }

    public boolean hasPermissionAPI() {
        return this.permissionAPI != null;
    }

    public FabricPermissionAPICompat getPermissionAPI() {
        return this.permissionAPI;
    }

    @Override
    public void registerPermissionSystems() {
        super.registerPermissionSystems();
        if (this.hasPermissionAPI()) {
            PermissionSystemRegistry.INSTANCE.register(this.permissionAPI.createSystem());
        }
    }
}

