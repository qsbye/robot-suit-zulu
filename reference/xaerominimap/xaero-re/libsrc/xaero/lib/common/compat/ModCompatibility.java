/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat;

import java.util.HashMap;
import xaero.lib.client.compat.ClientModCompatibility;
import xaero.lib.common.compat.PlatformModCompatibility;
import xaero.lib.common.compat.ftbranks.FTBRanksCompat;
import xaero.lib.common.compat.luckperms.LuckPermsCompat;
import xaero.lib.common.compat.prometheus.ModPrometheus;
import xaero.lib.common.compat.prometheus.PrometheusCompat;
import xaero.lib.common.permission.system.PermissionSystemRegistry;
import xaero.lib.platform.Services;

public class ModCompatibility {
    private static ModCompatibility instance;
    private FTBRanksCompat ftbRanks;
    private PrometheusCompat prometheus;
    private LuckPermsCompat luckPerms;
    private PlatformModCompatibility platform;

    public static ModCompatibility getInstance() {
        if (instance == null) {
            instance = Services.PLATFORM.isDedicatedServer() ? new ModCompatibility() : new ClientModCompatibility();
        }
        return instance;
    }

    protected ModCompatibility() {
        try {
            Class.forName("dev.ftb.mods.ftbranks.api.FTBRanksAPI");
            this.ftbRanks = new FTBRanksCompat();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("earth.terrarium.prometheus.api.permissions.PermissionApi");
            this.prometheus = this.createPrometheus();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (Services.PLATFORM.isDedicatedServer()) {
            try {
                Class.forName("net.luckperms.api.LuckPerms");
                this.luckPerms = new LuckPermsCompat();
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        this.platform = Services.PLATFORM.createPlatformModCompatibility();
    }

    public boolean hasFTBRanks() {
        return this.ftbRanks != null;
    }

    public boolean hasPrometheus() {
        return this.prometheus != null;
    }

    public boolean hasLuckPerms() {
        return this.luckPerms != null;
    }

    public FTBRanksCompat getFtbRanks() {
        return this.ftbRanks;
    }

    public PrometheusCompat getPrometheus() {
        return this.prometheus;
    }

    protected PrometheusCompat createPrometheus() {
        return new PrometheusCompat(new HashMap<String, ModPrometheus>());
    }

    public LuckPermsCompat getLuckPerms() {
        return this.luckPerms;
    }

    public void registerPermissionSystems() {
        if (this.hasFTBRanks()) {
            PermissionSystemRegistry.INSTANCE.register(this.ftbRanks.createPermissionSystem());
        }
        if (this.hasLuckPerms()) {
            PermissionSystemRegistry.INSTANCE.register(this.luckPerms.createPermissionSystem());
        }
        if (this.hasPrometheus()) {
            PermissionSystemRegistry.INSTANCE.register(this.prometheus.createPrometheusSystem());
        }
        this.platform.registerPermissionSystems();
    }

    public void freeze() {
        if (this.hasPrometheus()) {
            this.prometheus.freeze();
        }
        this.platform.freeze();
    }
}

