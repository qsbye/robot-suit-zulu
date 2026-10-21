/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform.services;

public interface IPlatformMixinHelper<LOADING_MOD_INFO, MOD_VERSION extends Comparable<MOD_VERSION>> {
    public LOADING_MOD_INFO getLoadingModInfo(String var1);

    public MOD_VERSION getModVersion(LOADING_MOD_INFO var1);

    public MOD_VERSION getVersionFromString(String var1);
}

