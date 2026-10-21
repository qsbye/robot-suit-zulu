/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.state;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class RadarList
implements Comparable<RadarList> {
    private EntityRadarCategory clientCategory;
    private EntityRadarCategory syncedCategory;
    private final List<Entity> entities;

    private RadarList(List<Entity> entities) {
        this.entities = entities;
    }

    public EntityRadarCategory getClientCategory() {
        return this.clientCategory;
    }

    public EntityRadarCategory getSyncedCategory() {
        return this.syncedCategory;
    }

    public RadarList setClientCategory(EntityRadarCategory clientCategory) {
        this.clientCategory = clientCategory;
        return this;
    }

    public RadarList setSyncedCategory(EntityRadarCategory syncedCategory) {
        this.syncedCategory = syncedCategory;
        return this;
    }

    public <T> T getEffective(ObjectCategorySetting<T> setting) {
        T syncedValue;
        T t = syncedValue = this.syncedCategory == null ? null : (T)this.syncedCategory.getSettingValue(setting);
        if (syncedValue != null) {
            return syncedValue;
        }
        return this.clientCategory.getSettingValue(setting);
    }

    public void clearEntities() {
        this.entities.clear();
    }

    public boolean add(Entity entity) {
        return this.entities.add(entity);
    }

    public Entity get(int index) {
        return this.entities.get(index);
    }

    public int size() {
        return this.entities.size();
    }

    public Iterable<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public int compareTo(RadarList o) {
        return this.clientCategory.getSettingValue(EntityRadarCategorySettings.RENDER_ORDER).compareTo(o.clientCategory.getSettingValue(EntityRadarCategorySettings.RENDER_ORDER));
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public RadarList build() {
            return new RadarList(new ArrayList<Entity>());
        }

        public static Builder getDefault() {
            return new Builder().setDefault();
        }
    }
}

