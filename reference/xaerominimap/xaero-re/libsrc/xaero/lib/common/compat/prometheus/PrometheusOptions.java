/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.prometheus;

import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import java.util.HashMap;
import java.util.Map;
import xaero.lib.common.compat.ModCompatibility;
import xaero.lib.common.permission.PermissionNode;

public class PrometheusOptions
implements RoleOption<PrometheusOptions> {
    private final String modId;
    private final Map<PermissionNode<?>, Object> values;

    private PrometheusOptions(String modId, Map<PermissionNode<?>, Object> values) {
        this.modId = modId;
        this.values = values;
    }

    public RoleOptionSerializer<PrometheusOptions> serializer() {
        return ModCompatibility.getInstance().getPrometheus().getModData(this.modId).getSerializer();
    }

    public <T> T get(PermissionNode<T> permission) {
        Object valueCast = this.values.get(permission);
        return (T)valueCast;
    }

    public <T> void put(PermissionNode<T> permission, T value) {
        if (value == null) {
            this.values.remove(permission);
            return;
        }
        this.values.put(permission, value);
    }

    public Iterable<PermissionNode<?>> getKeys() {
        return this.values.keySet();
    }

    public static final class Builder {
        private String modId;

        private Builder() {
        }

        public Builder setDefault() {
            this.setModId(null);
            return this;
        }

        public Builder setModId(String modId) {
            this.modId = modId;
            return this;
        }

        public PrometheusOptions build() {
            if (this.modId == null) {
                throw new IllegalStateException();
            }
            HashMap values = new HashMap();
            return new PrometheusOptions(this.modId, values);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

