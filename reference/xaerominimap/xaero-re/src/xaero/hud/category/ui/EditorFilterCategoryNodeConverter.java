/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import xaero.hud.category.FilterObjectCategory;
import xaero.hud.category.rule.ObjectCategoryExcludeList;
import xaero.hud.category.rule.ObjectCategoryIncludeList;
import xaero.hud.category.rule.ObjectCategoryListRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.serialization.FilterObjectCategorySerializationHandler;
import xaero.hud.category.ui.EditorCategoryNodeConverter;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorFilterCategoryNode;
import xaero.hud.category.ui.node.EditorFilterSettingsNode;
import xaero.hud.category.ui.node.rule.EditorExcludeListNode;
import xaero.hud.category.ui.node.rule.EditorIncludeListNode;

public abstract class EditorFilterCategoryNodeConverter<E, P, C extends FilterObjectCategory<E, P, ?, C>, ED extends EditorFilterCategoryNode<C, SD, ED>, CB extends FilterObjectCategory.Builder<E, P, C, CB>, SD extends EditorFilterSettingsNode<E, P, ?>, SDB extends EditorFilterSettingsNode.Builder<E, P, SD, SDB>, EDB extends EditorFilterCategoryNode.Builder<C, ED, SD, SDB, EDB>>
extends EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> {
    private final ObjectCategoryListRuleType<E, P, ?> defaultListRuleType;
    private final Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter;
    private final String listRuleTypePrefixSeparator;
    private final Predicate<String> inputRuleTypeStringValidator;

    public EditorFilterCategoryNodeConverter(@Nonnull Supplier<CB> categoryBuilderFactory, @Nonnull Supplier<EDB> editorDataBuilderFactory, ObjectCategoryListRuleType<E, P, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator, Predicate<String> inputRuleTypeStringValidator) {
        super(categoryBuilderFactory, editorDataBuilderFactory);
        this.defaultListRuleType = defaultListRuleType;
        this.listRuleTypeGetter = listRuleTypeGetter;
        this.listRuleTypePrefixSeparator = listRuleTypePrefixSeparator;
        this.inputRuleTypeStringValidator = inputRuleTypeStringValidator;
    }

    @Override
    protected EDB getConfiguredBuilder(C category, boolean canBeRoot) {
        String prefix;
        EditorFilterCategoryNode.Builder editorNodeBuilder = (EditorFilterCategoryNode.Builder)super.getConfiguredBuilder(category, canBeRoot);
        EditorFilterSettingsNode.Builder settingNodeBuilder = (EditorFilterSettingsNode.Builder)editorNodeBuilder.getSettingDataBuilder();
        settingNodeBuilder.setBaseRule(((FilterObjectCategory)category).getBaseRule());
        EditorIncludeListNode.Builder includeListBuilder = settingNodeBuilder.getIncludeListBuilder();
        EditorExcludeListNode.Builder excludeListBuilder = settingNodeBuilder.getExcludeListBuilder();
        for (ObjectCategoryIncludeList objectCategoryIncludeList : ((FilterObjectCategory)category).getIncludeLists()) {
            prefix = this.getListRulePrefix(objectCategoryIncludeList);
            objectCategoryIncludeList.forEach(el -> includeListBuilder.addListElement(prefix + el));
        }
        for (ObjectCategoryExcludeList objectCategoryExcludeList : ((FilterObjectCategory)category).getExcludeLists()) {
            prefix = this.getListRulePrefix(objectCategoryExcludeList);
            objectCategoryExcludeList.forEach(el -> excludeListBuilder.addListElement(prefix + el));
        }
        ((EditorFilterCategoryNode.Builder)editorNodeBuilder.setListRuleTypePrefixSeparator(this.listRuleTypePrefixSeparator)).setInputRuleTypeStringValidator(this.inputRuleTypeStringValidator);
        includeListBuilder.getIncludeInSuperToggleDataBuilder().setCurrentValue(((FilterObjectCategory)category).getIncludeInSuperCategory());
        excludeListBuilder.setExcludeMode(((FilterObjectCategory)category).getExcludeMode());
        return (EDB)editorNodeBuilder;
    }

    private String getListRulePrefix(ObjectCategoryListRule<E, P, ?> list) {
        if (list.getType() == this.defaultListRuleType) {
            return "";
        }
        return list.getType().getId() + this.listRuleTypePrefixSeparator;
    }

    @Override
    protected CB getConfiguredBuilder(ED editorNode) {
        FilterObjectCategory.Builder categoryBuilder = (FilterObjectCategory.Builder)super.getConfiguredBuilder(editorNode);
        EditorFilterSettingsNode settingsNode = (EditorFilterSettingsNode)((EditorCategoryNode)editorNode).getSettingsNode();
        categoryBuilder.setBaseRule(settingsNode.getBaseRule());
        categoryBuilder.setIncludeInSuperCategory(settingsNode.getIncludeList().getIncludeInSuper());
        categoryBuilder.setExcludeMode(settingsNode.getExcludeList().getExcludeMode());
        settingsNode.getIncludeList().getList().forEach(led -> FilterObjectCategorySerializationHandler.handleListRuleSerializedElement((String)led.getElement(), categoryBuilder::getIncludeListBuilder, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator));
        settingsNode.getExcludeList().getList().forEach(led -> FilterObjectCategorySerializationHandler.handleListRuleSerializedElement((String)led.getElement(), categoryBuilder::getExcludeListBuilder, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator));
        return (CB)categoryBuilder;
    }

    public static abstract class Builder<E, P, C extends FilterObjectCategory<E, P, ?, C>, ED extends EditorFilterCategoryNode<C, SD, ED>, CB extends FilterObjectCategory.Builder<E, P, C, CB>, SD extends EditorFilterSettingsNode<E, P, ?>, SDB extends EditorFilterSettingsNode.Builder<E, P, SD, SDB>, EDB extends EditorFilterCategoryNode.Builder<C, ED, SD, SDB, EDB>, B extends Builder<E, P, C, ED, CB, SD, SDB, EDB, B>>
    extends EditorCategoryNodeConverter.Builder<C, ED, CB, SD, SDB, EDB, B> {
        protected ObjectCategoryListRuleType<E, P, ?> defaultListRuleType;
        protected Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter;
        protected String listRuleTypePrefixSeparator;
        protected Predicate<String> inputRuleTypeStringValidator;

        protected Builder(Supplier<CB> categoryBuilderFactory, Supplier<EDB> editorDataBuilderFactory) {
            super(categoryBuilderFactory, editorDataBuilderFactory);
        }

        @Override
        protected B setDefault() {
            this.setDefaultListRuleType(null);
            this.setListRuleTypeGetter(null);
            this.setListRuleTypePrefixSeparator(";");
            this.setInputRuleTypeStringValidator(s -> s.matches("[a-z_0-9\\-]+"));
            return (B)((Builder)super.setDefault());
        }

        public B setDefaultListRuleType(ObjectCategoryListRuleType<E, P, ?> defaultListRuleType) {
            this.defaultListRuleType = defaultListRuleType;
            return (B)((Builder)this.self);
        }

        public B setListRuleTypeGetter(Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter) {
            this.listRuleTypeGetter = listRuleTypeGetter;
            return (B)((Builder)this.self);
        }

        public B setListRuleTypePrefixSeparator(String listRuleTypePrefixSeparator) {
            this.listRuleTypePrefixSeparator = listRuleTypePrefixSeparator;
            return (B)((Builder)this.self);
        }

        public B setInputRuleTypeStringValidator(Predicate<String> inputRuleTypeStringValidator) {
            this.inputRuleTypeStringValidator = inputRuleTypeStringValidator;
            return (B)((Builder)this.self);
        }

        @Override
        public EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> build() {
            if (this.defaultListRuleType == null || this.listRuleTypeGetter == null) {
                throw new IllegalStateException();
            }
            return super.build();
        }

        protected abstract EditorFilterCategoryNodeConverter<E, P, C, ED, CB, SD, SDB, EDB> buildInternally();
    }
}

