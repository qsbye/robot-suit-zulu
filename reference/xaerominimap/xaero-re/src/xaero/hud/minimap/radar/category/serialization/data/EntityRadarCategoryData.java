/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category.serialization.data;

import java.util.List;
import java.util.Map;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.serialization.data.FilterObjectCategoryData;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;

public final class EntityRadarCategoryData
extends FilterObjectCategoryData<EntityRadarCategoryData> {
    private EntityRadarCategoryData(String name, String hardInclude, List<String> includeList, List<String> excludeList, ExcludeListMode excludeMode, Map<String, Object> settingOverrides, List<EntityRadarCategoryData> subCategories, boolean protection, boolean includeInSuperCategory) {
        super(name, hardInclude, includeList, excludeList, excludeMode, settingOverrides, subCategories, protection, includeInSuperCategory);
    }

    public static final class Builder
    extends FilterObjectCategoryData.Builder<EntityRadarCategoryData, Builder> {
        private Builder() {
            super(EntityRadarCategoryConstants.LIST_FACTORY, EntityRadarCategoryConstants.MAP_FACTORY);
        }

        @Override
        protected EntityRadarCategoryData buildInternally(List<EntityRadarCategoryData> builtSubCategories) {
            return new EntityRadarCategoryData(this.name, this.hardInclude, (List<String>)this.includeList, (List<String>)this.excludeList, this.excludeMode, (Map<String, Object>)this.settingOverrides, builtSubCategories, this.protection, this.includeListInSuperCategory);
        }

        public static Builder begin() {
            return new Builder();
        }
    }
}

