/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorSettingsNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNode;

public abstract class EditorCategoryNodeConverter<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, SD, ED>, CB extends ObjectCategory.Builder<C, CB>, SD extends EditorSettingsNode<?>, SDB extends EditorSettingsNode.Builder<SD, SDB>, EDB extends EditorCategoryNode.Builder<C, ED, SD, SDB, EDB>> {
    private final Supplier<CB> categoryBuilderFactory;
    private final Supplier<EDB> editorNodeBuilderFactory;

    public EditorCategoryNodeConverter(@Nonnull Supplier<CB> categoryBuilderFactory, @Nonnull Supplier<EDB> editorNodeBuilderFactory) {
        this.categoryBuilderFactory = categoryBuilderFactory;
        this.editorNodeBuilderFactory = editorNodeBuilderFactory;
    }

    public ED convert(C category, boolean canBeRoot) {
        return (ED)((EditorCategoryNode.Builder)this.getConfiguredBuilder(category, canBeRoot)).build();
    }

    protected EDB getConfiguredBuilder(C category, boolean canBeRoot) {
        EditorCategoryNode.Builder editorNodeBuilder = (EditorCategoryNode.Builder)this.editorNodeBuilderFactory.get();
        editorNodeBuilder.setName(((ObjectCategory)category).getName());
        Object settingDataBuilder = editorNodeBuilder.getSettingDataBuilder();
        ((ObjectCategory)category).getSettingOverridesIterator().forEachRemaining(e -> this.setSettingValue((CB)settingDataBuilder, (ObjectCategorySetting<T>)((ObjectCategorySetting)e.getKey()), e.getValue()));
        ((EditorSettingsNode.Builder)settingDataBuilder).setRootSettings(canBeRoot && ((ObjectCategory)category).getSuperCategory() == null);
        ((EditorSettingsNode.Builder)settingDataBuilder).setProtection(((ObjectCategory)category).getProtection());
        ((ObjectCategory)category).getDirectSubCategoryIterator().forEachRemaining(sc -> editorNodeBuilder.addSubCategoryBuilder(this.getConfiguredBuilder(sc, canBeRoot)));
        return (EDB)editorNodeBuilder;
    }

    private <T> void setSettingValue(SDB settingOverridesBuilder, ObjectCategorySetting<T> setting, Object value) {
        ((EditorSettingsNode.Builder)settingOverridesBuilder).setSettingValue(setting, (Object)value);
    }

    public C convert(ED editorData) {
        return ((ObjectCategory.Builder)this.getConfiguredBuilder(editorData)).build();
    }

    protected CB getConfiguredBuilder(ED editorNode) {
        ObjectCategory.Builder categoryBuilder = (ObjectCategory.Builder)this.categoryBuilderFactory.get();
        categoryBuilder.setName(((EditorCategoryNode)editorNode).getName());
        categoryBuilder.setProtection(((EditorSettingsNode)((EditorCategoryNode)editorNode).getSettingsNode()).getProtection());
        ((EditorSettingsNode)((EditorCategoryNode)editorNode).getSettingsNode()).getSettings().forEach((k, d) -> this.setSettingValue((CB)categoryBuilder, (ObjectCategorySetting<T>)((ObjectCategorySetting)k), ((IEditorSettingNode)((Object)d)).getSettingValue()));
        ((EditorCategoryNode)editorNode).getSubCategories().forEach(sed -> categoryBuilder.addSubCategoryBuilder(this.getConfiguredBuilder(sed)));
        return (CB)categoryBuilder;
    }

    private <T> void setSettingValue(CB categoryBuilder, ObjectCategorySetting<T> setting, Object value) {
        ((ObjectCategory.Builder)categoryBuilder).setSettingValue(setting, (Object)value);
    }

    public static abstract class Builder<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, SD, ED>, CB extends ObjectCategory.Builder<C, CB>, SD extends EditorSettingsNode<?>, SDB extends EditorSettingsNode.Builder<SD, SDB>, EDB extends EditorCategoryNode.Builder<C, ED, SD, SDB, EDB>, B extends Builder<C, ED, CB, SD, SDB, EDB, B>> {
        protected final B self = this;
        protected final Supplier<CB> categoryBuilderFactory;
        protected final Supplier<EDB> editorDataBuilderFactory;

        protected Builder(Supplier<CB> categoryBuilderFactory, Supplier<EDB> editorDataBuilderFactory) {
            this.categoryBuilderFactory = categoryBuilderFactory;
            this.editorDataBuilderFactory = editorDataBuilderFactory;
        }

        protected B setDefault() {
            return this.self;
        }

        public EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> build() {
            return this.buildInternally();
        }

        protected abstract EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> buildInternally();
    }
}

