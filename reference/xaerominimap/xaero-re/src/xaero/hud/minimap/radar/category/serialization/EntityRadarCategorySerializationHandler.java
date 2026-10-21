/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category.serialization;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.hud.category.rule.ObjectCategoryHardRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.serialization.FilterObjectCategorySerializationHandler;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.rule.EntityRadarCategoryHardRules;
import xaero.hud.minimap.radar.category.rule.EntityRadarListRuleTypes;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class EntityRadarCategorySerializationHandler
extends FilterObjectCategorySerializationHandler<Entity, Player, EntityRadarCategoryData, EntityRadarCategory, EntityRadarCategory.Builder, EntityRadarCategoryData.Builder> {
    private EntityRadarCategorySerializationHandler(ObjectCategoryDataSerializer<EntityRadarCategoryData, String> serializer, Supplier<EntityRadarCategoryData.Builder> dataBuilderFactory, Supplier<EntityRadarCategory.Builder> objectCategoryBuilderFactory, Function<String, ObjectCategorySetting<?>> settingTypeGetter, Function<String, ObjectCategoryHardRule<Entity, Player>> hardRuleGetter, ObjectCategoryListRuleType<Entity, Player, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<Entity, Player, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator) {
        super(serializer, dataBuilderFactory, objectCategoryBuilderFactory, settingTypeGetter, hardRuleGetter, defaultListRuleType, listRuleTypeGetter, listRuleTypePrefixSeparator);
    }

    public static final class Builder
    extends FilterObjectCategorySerializationHandler.Builder<Entity, Player, EntityRadarCategoryData, EntityRadarCategory, EntityRadarCategory.Builder, EntityRadarCategoryData.Builder, EntityRadarCategorySerializationHandler, Builder> {
        private Builder(ObjectCategoryDataSerializer<EntityRadarCategoryData, String> serializer) {
            super(serializer);
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setHardRuleGetter(EntityRadarCategoryHardRules.HARD_RULES::get);
            this.setDataBuilderFactory(EntityRadarCategoryConstants.DATA_BUILDER_FACTORY);
            this.setObjectCategoryBuilderFactory(EntityRadarCategoryConstants.CATEGORY_BUILDER_FACTORY);
            this.setSettingTypeGetter(EntityRadarCategorySettings.SETTINGS::get);
            this.setDefaultListRuleType(EntityRadarListRuleTypes.ENTITY_TYPE);
            this.setListRuleTypeGetter(EntityRadarListRuleTypes.TYPE_MAP::get);
            return this;
        }

        @Override
        protected EntityRadarCategorySerializationHandler buildInternally() {
            return new EntityRadarCategorySerializationHandler(this.serializer, this.dataBuilderFactory, this.objectCategoryBuilderFactory, (Function<String, ObjectCategorySetting<?>>)this.settingTypeGetter, this.hardRuleGetter, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator);
        }

        public static Builder begin(ObjectCategoryDataSerializer<EntityRadarCategoryData, String> serializer) {
            return new Builder(serializer).setDefault();
        }
    }
}

