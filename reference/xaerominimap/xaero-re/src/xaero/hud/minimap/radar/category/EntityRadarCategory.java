/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category;

import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.hud.category.FilterObjectCategory;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.rule.ObjectCategoryExcludeList;
import xaero.hud.category.rule.ObjectCategoryIncludeList;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.rule.ObjectCategoryRule;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.rule.EntityRadarCategoryHardRules;
import xaero.hud.minimap.radar.category.rule.EntityRadarListRuleTypes;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;

public final class EntityRadarCategory
extends FilterObjectCategory<Entity, Player, EntityRadarCategoryData, EntityRadarCategory> {
    private EntityRadarCategory(String name, EntityRadarCategory parent, ObjectCategoryRule<Entity, Player> baseRule, Map<ObjectCategoryListRuleType<Entity, Player, ?>, ObjectCategoryIncludeList<Entity, Player, ?>> includeLists, Map<ObjectCategoryListRuleType<Entity, Player, ?>, ObjectCategoryExcludeList<Entity, Player, ?>> excludeLists, List<ObjectCategoryIncludeList<Entity, Player, ?>> includeListsIndexed, List<ObjectCategoryExcludeList<Entity, Player, ?>> excludeListsIndexed, Map<ObjectCategorySetting<?>, Object> settingOverrides, List<EntityRadarCategory> subCategories, boolean protection, ExcludeListMode excludeMode, boolean includeInSuperCategory) {
        super(name, parent, baseRule, includeLists, excludeLists, includeListsIndexed, excludeListsIndexed, settingOverrides, subCategories, protection, excludeMode, includeInSuperCategory);
    }

    public static final class Builder
    extends FilterObjectCategory.Builder<Entity, Player, EntityRadarCategory, Builder> {
        private Builder() {
            super(EntityRadarCategoryConstants.LIST_FACTORY, EntityRadarCategoryConstants.MAP_FACTORY, EntityRadarListRuleTypes.TYPE_LIST);
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setBaseRule(EntityRadarCategoryHardRules.IS_NOTHING);
            return this;
        }

        @Override
        protected EntityRadarCategory buildUncheckedFilter(List<EntityRadarCategory> subCategories, Map<ObjectCategoryListRuleType<Entity, Player, ?>, ObjectCategoryIncludeList<Entity, Player, ?>> includeLists, Map<ObjectCategoryListRuleType<Entity, Player, ?>, ObjectCategoryExcludeList<Entity, Player, ?>> excludeLists, List<ObjectCategoryIncludeList<Entity, Player, ?>> includeListsIndexed, List<ObjectCategoryExcludeList<Entity, Player, ?>> excludeListsIndexed) {
            return new EntityRadarCategory(this.name, (EntityRadarCategory)this.superCategory, this.baseRule, includeLists, excludeLists, includeListsIndexed, excludeListsIndexed, this.settingOverrides, subCategories, this.protection, this.excludeMode, this.includeInSuperCategory);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

