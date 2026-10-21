/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.compat;

import java.util.HashMap;
import xaero.lib.client.compat.prometheus.PrometheusClientCompat;
import xaero.lib.common.compat.ModCompatibility;
import xaero.lib.common.compat.prometheus.ModPrometheus;
import xaero.lib.common.compat.prometheus.PrometheusCompat;

public class ClientModCompatibility
extends ModCompatibility {
    @Override
    protected PrometheusCompat createPrometheus() {
        return new PrometheusClientCompat(new HashMap<String, ModPrometheus>());
    }

    public static ClientModCompatibility getInstance() {
        return (ClientModCompatibility)ModCompatibility.getInstance();
    }
}

