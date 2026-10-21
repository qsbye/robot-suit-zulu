/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import xaero.common.misc.ListFactory;
import xaero.common.misc.MapFactory;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.serialization.data.ObjectCategoryData;

public abstract class FilterObjectCategoryData<D extends FilterObjectCategoryData<D>>
extends ObjectCategoryData<D> {
    private final String hardInclude;
    private final List<String> includeList;
    private final boolean includeListInSuperCategory;
    private final ExcludeListMode excludeMode;
    private final List<String> excludeList;

    protected FilterObjectCategoryData(String name, @Nonnull String hardInclude, @Nonnull List<String> includeList, @Nonnull List<String> excludeList, ExcludeListMode excludeMode, Map<String, Object> settingOverrides, List<D> subCategories, boolean protection, boolean includeListInSuperCategory) {
        super(name, settingOverrides, subCategories, protection);
        this.hardInclude = hardInclude;
        this.includeList = includeList;
        this.excludeList = excludeList;
        this.excludeMode = excludeMode;
        this.includeListInSuperCategory = includeListInSuperCategory;
    }

    public Iterator<String> getIncludeListIterator() {
        return this.includeList.iterator();
    }

    public Iterator<String> getExcludeListIterator() {
        return this.excludeList.iterator();
    }

    public String getHardInclude() {
        return this.hardInclude;
    }

    public ExcludeListMode getExcludeMode() {
        return this.excludeMode;
    }

    public boolean getIncludeListInSuperCategory() {
        return this.includeListInSuperCategory;
    }

    public static abstract class Builder<D extends FilterObjectCategoryData<D>, B extends Builder<D, B>>
    extends ObjectCategoryData.Builder<D, B> {
        protected String hardInclude;
        protected final List<String> includeList;
        protected final List<String> excludeList;
        protected ExcludeListMode excludeMode;
        protected boolean includeListInSuperCategory;

        public Builder(ListFactory listFactory, MapFactory mapFactory) {
            super(listFactory, mapFactory);
            this.includeList = listFactory.get();
            this.excludeList = listFactory.get();
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.setHardInclude("nothing");
            this.setExcludeMode(ExcludeListMode.ONLY);
            this.setIncludeListInSuperCategory(false);
            this.includeList.clear();
            this.excludeList.clear();
            return (B)((Builder)this.self);
        }

        public void setHardInclude(String hardInclude) {
            this.hardInclude = hardInclude;
        }

        public void setExcludeMode(ExcludeListMode excludeMode) {
            this.excludeMode = excludeMode;
        }

        public B addToIncludeList(String s) {
            this.includeList.add(s);
            return (B)((Builder)this.self);
        }

        public B addToExcludeList(String s) {
            this.excludeList.add(s);
            return (B)((Builder)this.self);
        }

        public B setIncludeListInSuperCategory(boolean includeInSuperCategory) {
            this.includeListInSuperCategory = includeInSuperCategory;
            return (B)((Builder)this.self);
        }

        @Override
        public D build() {
            Collections.sort(this.includeList);
            Collections.sort(this.excludeList);
            return (D)((FilterObjectCategoryData)super.build());
        }
    }
}

