/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client;

import xaero.lib.client.compat.ClientModCompatibilityFabric;
import xaero.lib.client.event.ClientEventsFabric;

public class XaeroLibClientFabric {
    private Throwable firstStageError;
    private ClientModCompatibilityFabric modCompatibility;
    private ClientEventsFabric events;

    public void load() {
    }

    public void loadLater() {
        if (this.firstStageError != null) {
            throw new RuntimeException(this.firstStageError);
        }
        this.modCompatibility = new ClientModCompatibilityFabric();
    }

    public void registerEvents() {
        this.events = new ClientEventsFabric();
        this.events.register();
    }

    public ClientModCompatibilityFabric getModCompatibility() {
        return this.modCompatibility;
    }

    public ClientEventsFabric getEvents() {
        return this.events;
    }
}

