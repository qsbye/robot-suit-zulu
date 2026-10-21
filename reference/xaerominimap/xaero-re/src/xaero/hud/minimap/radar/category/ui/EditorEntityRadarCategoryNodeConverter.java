/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category.ui;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.ui.EditorFilterCategoryNodeConverter;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.rule.EntityRadarListRuleTypes;
import xaero.hud.minimap.radar.category.ui.node.EditorEntityRadarCategoryNode;
import xaero.hud.minimap.radar.category.ui.node.EditorEntityRadarCategorySettingsNode;

public final class EditorEntityRadarCategoryNodeConverter
extends EditorFilterCategoryNodeConverter<Entity, Player, EntityRadarCategory, EditorEntityRadarCategoryNode, EntityRadarCategory.Builder, EditorEntityRadarCategorySettingsNode<?>, EditorEntityRadarCategorySettingsNode.Builder, EditorEntityRadarCategoryNode.Builder> {
    private EditorEntityRadarCategoryNodeConverter(@Nonnull Supplier<EntityRadarCategory.Builder> categoryBuilderFactory, @Nonnull Supplier<EditorEntityRadarCategoryNode.Builder> editorDataBuilderFactory, ObjectCategoryListRuleType<Entity, Player, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<Entity, Player, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator, Predicate<String> inputRuleTypeStringValidator) {
        super(categoryBuilderFactory, editorDataBuilderFactory, defaultListRuleType, listRuleTypeGetter, listRuleTypePrefixSeparator, inputRuleTypeStringValidator);
    }

    public static final class Builder
    extends EditorFilterCategoryNodeConverter.Builder<Entity, Player, EntityRadarCategory, EditorEntityRadarCategoryNode, EntityRadarCategory.Builder, EditorEntityRadarCategorySettingsNode<?>, EditorEntityRadarCategorySettingsNode.Builder, EditorEntityRadarCategoryNode.Builder, Builder> {
        private Builder() {
            super(EntityRadarCategory.Builder::begin, EditorEntityRadarCategoryNode.Builder::begin);
        }

        @Override
        protected Builder setDefault() {
            super.setDefault();
            this.setDefaultListRuleType(EntityRadarListRuleTypes.ENTITY_TYPE);
            this.setListRuleTypeGetter(EntityRadarListRuleTypes.TYPE_MAP::get);
            return this;
        }

        @Override
        protected EditorEntityRadarCategoryNodeConverter buildInternally() {
            return new EditorEntityRadarCategoryNodeConverter(this.categoryBuilderFactory, this.editorDataBuilderFactory, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator, (Predicate<String>)this.inputRuleTypeStringValidator);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

