/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.color.RadarColorHelper;
import xaero.hud.minimap.radar.state.RadarState;
import xaero.hud.minimap.radar.state.RadarStateUpdater;

public final class RadarSession {
    private final EntityRadarCategoryManager categoryManager;
    private final RadarState state;
    private final RadarStateUpdater stateUpdater;
    private final RadarColorHelper colorHelper;

    private RadarSession(EntityRadarCategoryManager categoryManager, RadarState state, RadarStateUpdater stateUpdater, RadarColorHelper colorHelper) {
        this.categoryManager = categoryManager;
        this.state = state;
        this.stateUpdater = stateUpdater;
        this.colorHelper = colorHelper;
    }

    public void update(ClientLevel world, Entity renderEntity, Player player) {
        this.stateUpdater.update(world, renderEntity, player);
    }

    public EntityRadarCategoryManager getCategoryManager() {
        return this.categoryManager;
    }

    public RadarState getState() {
        return this.state;
    }

    public RadarStateUpdater getStateUpdater() {
        return this.stateUpdater;
    }

    public RadarColorHelper getColorHelper() {
        return this.colorHelper;
    }

    public static class Builder {
        private EntityRadarCategoryManager categoryManager;

        private Builder() {
        }

        public Builder setDefault() {
            this.setCategoryManager(null);
            return this;
        }

        public Builder setCategoryManager(EntityRadarCategoryManager categoryManager) {
            this.categoryManager = categoryManager;
            return this;
        }

        public RadarSession build() {
            if (this.categoryManager == null) {
                throw new IllegalStateException();
            }
            RadarState state = new RadarState();
            RadarStateUpdater stateUpdater = new RadarStateUpdater(this.categoryManager, state);
            RadarColorHelper colorHelper = new RadarColorHelper();
            return new RadarSession(this.categoryManager, state, stateUpdater, colorHelper);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

