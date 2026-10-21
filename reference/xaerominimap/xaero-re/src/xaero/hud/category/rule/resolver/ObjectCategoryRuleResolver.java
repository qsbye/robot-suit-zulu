/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.rule.resolver;

import java.util.Iterator;
import java.util.List;
import xaero.hud.category.FilterObjectCategory;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.rule.ObjectCategoryExcludeList;
import xaero.hud.category.rule.ObjectCategoryIncludeList;

public final class ObjectCategoryRuleResolver {
    private ObjectCategoryRuleResolver() {
    }

    public <E, P, C extends FilterObjectCategory<E, P, ?, C>> C resolve(C category, E element, P context) {
        if (!this.followsRules(category, element, context)) {
            return null;
        }
        Iterator subCategoryIterator = category.getDirectSubCategoryIterator();
        while (subCategoryIterator.hasNext()) {
            FilterObjectCategory subCategory = (FilterObjectCategory)subCategoryIterator.next();
            FilterObjectCategory subResolve = this.resolve(subCategory, element, context);
            if (subResolve == null) continue;
            return (C)subResolve;
        }
        return category;
    }

    private <E, P, C extends FilterObjectCategory<E, P, ?, C>> boolean followsRules(C category, E element, P context) {
        boolean result = category.getBaseRule().isFollowedBy(element, context);
        if (!result) {
            List<ObjectCategoryIncludeList<E, P, ?>> includeLists = category.getIncludeLists();
            for (ObjectCategoryIncludeList<E, P, ?> objectCategoryIncludeList : includeLists) {
                if (!objectCategoryIncludeList.isFollowedBy(element, context)) continue;
                result = true;
                break;
            }
        }
        if (result) {
            List<ObjectCategoryExcludeList<E, P, ?>> excludeLists = category.getExcludeLists();
            if (category.getExcludeMode() == ExcludeListMode.ALL_BUT) {
                result = false;
            }
            for (ObjectCategoryExcludeList objectCategoryExcludeList : excludeLists) {
                if (result == objectCategoryExcludeList.isFollowedBy(element, context)) continue;
                result = !result;
                break;
            }
        }
        return result;
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public ObjectCategoryRuleResolver build() {
            return new ObjectCategoryRuleResolver();
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

