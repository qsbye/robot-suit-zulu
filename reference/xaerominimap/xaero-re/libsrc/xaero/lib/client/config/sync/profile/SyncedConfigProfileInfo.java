/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.sync.profile;

import xaero.lib.common.config.profile.IConfigProfileInfo;

public class SyncedConfigProfileInfo
implements IConfigProfileInfo {
    private final String id;
    private String name;

    public SyncedConfigProfileInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

