/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.prometheus;

import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import xaero.lib.common.compat.prometheus.PrometheusOptions;

public class ModPrometheus {
    private final String modId;
    private final RoleOptionSerializer<PrometheusOptions> serializer;

    private ModPrometheus(String modId, RoleOptionSerializer<PrometheusOptions> serializer) {
        this.modId = modId;
        this.serializer = serializer;
    }

    public String getModId() {
        return this.modId;
    }

    public RoleOptionSerializer<PrometheusOptions> getSerializer() {
        return this.serializer;
    }
}

