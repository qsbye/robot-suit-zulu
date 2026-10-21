/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.compat;

import xaero.lib.client.compat.amecs.AmecsCompatibility;

public class ClientModCompatibilityFabric {
    private AmecsCompatibility amecs;

    public ClientModCompatibilityFabric() {
        try {
            Class.forName("de.siphalor.amecs.api.KeyModifiers");
            this.amecs = new AmecsCompatibility();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public boolean hasAmecs() {
        return this.amecs != null;
    }

    public AmecsCompatibility getAmecs() {
        return this.amecs;
    }
}

