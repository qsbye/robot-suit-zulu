/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import xaero.hud.minimap.radar.icon.cache.RadarIconEntityCache;

public class RadarIconCache {
    private final Map<EntityType<?>, RadarIconEntityCache> iconCacheMap = new HashMap();

    public RadarIconEntityCache getEntityCache(EntityType<?> entityType) {
        RadarIconEntityCache result = this.iconCacheMap.get(entityType);
        if (result == null) {
            result = new RadarIconEntityCache(entityType);
            this.iconCacheMap.put(entityType, result);
        }
        return result;
    }

    public void clear() {
        this.iconCacheMap.clear();
    }
}

