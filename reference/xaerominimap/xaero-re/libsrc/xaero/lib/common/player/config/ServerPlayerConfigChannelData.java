/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.player.config;

public class ServerPlayerConfigChannelData {
    private String cachedEnforcedConfigProfilePermission;
    private boolean cachedUsingConfigProfilePermission;
    private boolean cachedHasServerProfileEditPermission;

    public String getEnforcedConfigProfilePermission() {
        return this.cachedEnforcedConfigProfilePermission;
    }

    public void setCachedEnforcedConfigProfilePermission(String cachedEnforcedConfigProfilePermission) {
        this.cachedEnforcedConfigProfilePermission = cachedEnforcedConfigProfilePermission;
    }

    public boolean isUsingConfigProfilePermission() {
        return this.cachedUsingConfigProfilePermission;
    }

    public void setCachedUsingConfigProfilePermission(boolean cachedUsingConfigProfilePermission) {
        this.cachedUsingConfigProfilePermission = cachedUsingConfigProfilePermission;
    }

    public boolean hasServerProfileEditPermission() {
        return this.cachedHasServerProfileEditPermission;
    }

    public void setCachedHasServerProfileEditPermission(boolean cachedHasServerProfileEditPermission) {
        this.cachedHasServerProfileEditPermission = cachedHasServerProfileEditPermission;
    }
}

