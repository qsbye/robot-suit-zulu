/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import xaero.common.misc.ListFactory;
import xaero.common.misc.MapFactory;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.rule.ObjectCategoryExcludeList;
import xaero.hud.category.rule.ObjectCategoryIncludeList;
import xaero.hud.category.rule.ObjectCategoryListRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.rule.ObjectCategoryRule;
import xaero.hud.category.serialization.data.FilterObjectCategoryData;
import xaero.hud.category.setting.ObjectCategorySetting;

public abstract class FilterObjectCategory<E, P, D extends FilterObjectCategoryData<D>, C extends FilterObjectCategory<E, P, D, C>>
extends ObjectCategory<D, C> {
    private final C self = this;
    private ObjectCategoryRule<E, P> baseRule;
    private final Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList<E, P, ?>> includeLists;
    private final Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryExcludeList<E, P, ?>> excludeLists;
    private final List<ObjectCategoryIncludeList<E, P, ?>> includeListsIndexed;
    private final List<ObjectCategoryExcludeList<E, P, ?>> excludeListsIndexed;
    private final ExcludeListMode excludeMode;
    private final boolean includeInSuperCategory;

    protected FilterObjectCategory(@Nonnull String name, @Nonnull C superCategory, @Nonnull ObjectCategoryRule<E, P> baseRule, @Nonnull Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList<E, P, ?>> includeLists, @Nonnull Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryExcludeList<E, P, ?>> excludeLists, @Nonnull List<ObjectCategoryIncludeList<E, P, ?>> includeListsIndexed, @Nonnull List<ObjectCategoryExcludeList<E, P, ?>> excludeListsIndexed, @Nonnull Map<ObjectCategorySetting<?>, Object> settingOverrides, @Nonnull List<C> subCategories, boolean protection, ExcludeListMode excludeMode, boolean includeInSuperCategory) {
        super(name, superCategory, settingOverrides, subCategories, protection);
        this.baseRule = baseRule;
        this.includeLists = includeLists;
        this.excludeLists = excludeLists;
        this.includeListsIndexed = includeListsIndexed;
        this.excludeListsIndexed = excludeListsIndexed;
        this.excludeMode = excludeMode;
        this.includeInSuperCategory = includeInSuperCategory;
    }

    public ObjectCategoryRule<E, P> getBaseRule() {
        return this.baseRule;
    }

    public <S> ObjectCategoryIncludeList<E, P, S> getIncludeList(ObjectCategoryListRuleType<E, P, S> type) {
        return this.includeLists.get(type);
    }

    public <S> ObjectCategoryExcludeList<E, P, S> getExcludeList(ObjectCategoryListRuleType<E, P, S> type) {
        return this.excludeLists.get(type);
    }

    public List<ObjectCategoryIncludeList<E, P, ?>> getIncludeLists() {
        return this.includeListsIndexed;
    }

    public List<ObjectCategoryExcludeList<E, P, ?>> getExcludeLists() {
        return this.excludeListsIndexed;
    }

    public ExcludeListMode getExcludeMode() {
        return this.excludeMode;
    }

    public boolean getIncludeInSuperCategory() {
        return this.includeInSuperCategory;
    }

    public static abstract class Builder<E, P, C extends FilterObjectCategory<E, P, ?, C>, B extends Builder<E, P, C, B>>
    extends ObjectCategory.Builder<C, B> {
        protected ObjectCategoryRule<E, P> baseRule;
        protected final Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList.Builder<E, P, ?>> includeListBuilders;
        protected final Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryExcludeList.Builder<E, P, ?>> excludeListBuilders;
        protected ExcludeListMode excludeMode;
        protected boolean includeInSuperCategory;

        protected Builder(ListFactory listFactory, MapFactory mapFactory, Iterable<ObjectCategoryListRuleType<E, P, ?>> listRuleTypes) {
            super(listFactory, mapFactory);
            this.includeListBuilders = mapFactory.get();
            this.excludeListBuilders = mapFactory.get();
            for (ObjectCategoryListRuleType<E, P, ?> type : listRuleTypes) {
                this.includeListBuilders.put(type, ObjectCategoryIncludeList.Builder.begin(listFactory, type));
                this.excludeListBuilders.put(type, ObjectCategoryExcludeList.Builder.begin(listFactory, type));
            }
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.includeListBuilders.forEach((k, v) -> v.setDefault());
            this.excludeListBuilders.forEach((k, v) -> v.setDefault());
            this.setBaseRule(null);
            this.setExcludeMode(ExcludeListMode.ONLY);
            this.setIncludeInSuperCategory(true);
            return (B)((Builder)this.self);
        }

        public <S> ObjectCategoryIncludeList.Builder<E, P, S> getIncludeListBuilder(ObjectCategoryListRuleType<E, P, S> type) {
            return this.includeListBuilders.get(type);
        }

        public <S> ObjectCategoryExcludeList.Builder<E, P, S> getExcludeListBuilder(ObjectCategoryListRuleType<E, P, S> type) {
            return this.excludeListBuilders.get(type);
        }

        public B setBaseRule(ObjectCategoryRule<E, P> baseRule) {
            this.baseRule = baseRule;
            return (B)((Builder)this.self);
        }

        public B setExcludeMode(ExcludeListMode excludeMode) {
            this.excludeMode = excludeMode;
            return (B)((Builder)this.self);
        }

        public B setIncludeInSuperCategory(boolean includeInSuperCategory) {
            this.includeInSuperCategory = includeInSuperCategory;
            return (B)((Builder)this.self);
        }

        @Override
        public C build() {
            if (this.baseRule == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (C)((FilterObjectCategory)super.build());
        }

        @Override
        protected final C buildUnchecked(List<C> subCategories) {
            ObjectCategoryListRule builtList;
            Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList<E, P, ?>> includeLists = this.mapFactory.get();
            Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryExcludeList<E, P, ?>> excludeLists = this.mapFactory.get();
            List<ObjectCategoryIncludeList<ObjectCategoryIncludeList<E, P, ?>, P, ?>> includeListsIndexed = this.listFactory.get();
            List<ObjectCategoryExcludeList<ObjectCategoryIncludeList<E, P, ?>, P, ?>> excludeListsIndexed = this.listFactory.get();
            for (Map.Entry<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList.Builder<E, P, ?>> entry : this.includeListBuilders.entrySet()) {
                builtList = entry.getValue().build(subCategories);
                includeLists.put(entry.getKey(), (ObjectCategoryIncludeList<E, P, ?>)builtList);
                includeListsIndexed.add((ObjectCategoryIncludeList<ObjectCategoryIncludeList<E, P, ?>, P, ?>)builtList);
            }
            for (Map.Entry<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryListRule.Builder> entry : this.excludeListBuilders.entrySet()) {
                builtList = ((ObjectCategoryExcludeList.Builder)entry.getValue()).setExcludeMode(this.excludeMode).build(subCategories);
                excludeLists.put(entry.getKey(), (ObjectCategoryExcludeList<E, P, ?>)builtList);
                excludeListsIndexed.add((ObjectCategoryExcludeList<ObjectCategoryIncludeList<E, P, ?>, P, ?>)builtList);
            }
            return this.buildUncheckedFilter(subCategories, includeLists, excludeLists, includeListsIndexed, excludeListsIndexed);
        }

        protected abstract C buildUncheckedFilter(List<C> var1, Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryIncludeList<E, P, ?>> var2, Map<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryExcludeList<E, P, ?>> var3, List<ObjectCategoryIncludeList<E, P, ?>> var4, List<ObjectCategoryExcludeList<E, P, ?>> var5);
    }
}

