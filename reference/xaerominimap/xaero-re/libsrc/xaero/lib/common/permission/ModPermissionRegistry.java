/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import xaero.lib.common.permission.PermissionNode;

public class ModPermissionRegistry
implements Iterable<PermissionNode<?>> {
    private final Map<String, PermissionNode<?>> permissions;
    private boolean frozen;

    private ModPermissionRegistry(Map<String, PermissionNode<?>> permissions) {
        this.permissions = permissions;
    }

    public PermissionNode<?> get(String defaultNodePath) {
        if (!this.frozen) {
            throw new IllegalStateException();
        }
        return this.permissions.get(defaultNodePath);
    }

    public synchronized void register(PermissionNode<?> node) {
        if (this.frozen) {
            throw new IllegalStateException();
        }
        this.permissions.put(node.getDefaultPath(), node);
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public Iterator<PermissionNode<?>> iterator() {
        if (!this.frozen) {
            throw new IllegalStateException();
        }
        return this.permissions.values().iterator();
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public ModPermissionRegistry build() {
            return new ModPermissionRegistry(new HashMap());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

