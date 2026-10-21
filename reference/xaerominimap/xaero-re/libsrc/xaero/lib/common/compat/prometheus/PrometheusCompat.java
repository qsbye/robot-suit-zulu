/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.prometheus;

import java.util.Map;
import xaero.lib.common.compat.prometheus.ModPrometheus;
import xaero.lib.common.compat.prometheus.PrometheusSystem;

public class PrometheusCompat {
    protected final Map<String, ModPrometheus> modPrometheusMap;
    private boolean frozen;

    public PrometheusCompat(Map<String, ModPrometheus> modPrometheusMap) {
        this.modPrometheusMap = modPrometheusMap;
    }

    public PrometheusSystem createPrometheusSystem() {
        return new PrometheusSystem();
    }

    public ModPrometheus getModData(String modId) {
        if (!this.frozen) {
            throw new IllegalStateException();
        }
        return this.modPrometheusMap.get(modId);
    }

    public void freeze() {
        this.frozen = true;
    }
}

