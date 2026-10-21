/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker.system;

import java.util.HashMap;
import java.util.Map;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.player.tracker.system.IRenderedPlayerTracker;

public final class RenderedPlayerTrackerManager {
    private final Map<String, IRenderedPlayerTracker<?>> systems;

    private RenderedPlayerTrackerManager(Map<String, IRenderedPlayerTracker<?>> systems) {
        this.systems = systems;
    }

    public void register(String name, IRenderedPlayerTracker<?> system) {
        if (this.systems.containsKey(name)) {
            MinimapLogs.LOGGER.error("Player tracker system with the name " + name + " has already been registered!");
            return;
        }
        this.systems.put(name, system);
        MinimapLogs.LOGGER.info("Registered player tracker system: " + name);
    }

    public Iterable<IRenderedPlayerTracker<?>> getAllSystems() {
        return this.systems.values();
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public RenderedPlayerTrackerManager build() {
            return new RenderedPlayerTrackerManager(new HashMap());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

