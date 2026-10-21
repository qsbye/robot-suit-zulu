/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.rule;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import xaero.common.misc.ListFactory;
import xaero.hud.category.FilterObjectCategory;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.rule.ObjectCategoryListRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;

public final class ObjectCategoryExcludeList<E, P, S>
extends ObjectCategoryListRule<E, P, S> {
    private ExcludeListMode excludeMode;

    private ObjectCategoryExcludeList(@Nonnull ObjectCategoryListRuleType<E, P, S> type, @Nonnull List<String> stringList, @Nonnull Set<S> set, @Nonnull ExcludeListMode excludeMode) {
        super(type, "exclude list", stringList, set);
        this.excludeMode = excludeMode;
    }

    @Override
    public boolean isFollowedBy(E object, P context) {
        boolean inList = this.inList(object, context);
        return this.excludeMode == ExcludeListMode.ALL_BUT && inList || this.excludeMode == ExcludeListMode.ONLY && !inList;
    }

    public ExcludeListMode getExcludeMode() {
        return this.excludeMode;
    }

    public static final class Builder<E, P, S>
    extends ObjectCategoryListRule.Builder<E, P, S, Builder<E, P, S>> {
        private ExcludeListMode excludeMode;

        private Builder(ListFactory listFactory, ObjectCategoryListRuleType<E, P, S> type) {
            super(listFactory, type);
        }

        @Override
        public Builder<E, P, S> setDefault() {
            super.setDefault();
            this.setExcludeMode(ExcludeListMode.ONLY);
            return (Builder)this.self;
        }

        public Builder<E, P, S> setExcludeMode(ExcludeListMode excludeMode) {
            this.excludeMode = excludeMode;
            return (Builder)this.self;
        }

        @Override
        protected <C extends FilterObjectCategory<E, P, ?, C>> ObjectCategoryExcludeList<E, P, S> build(List<C> subCategories, Function<C, ObjectCategoryListRule<E, P, S>> subListGetter, Function<C, ObjectCategoryListRule<E, P, S>> subListExceptionsGetter) {
            return (ObjectCategoryExcludeList)super.build(subCategories, subListGetter, subListExceptionsGetter);
        }

        public <C extends FilterObjectCategory<E, P, ?, C>> ObjectCategoryExcludeList<E, P, S> build(List<C> subCategories) {
            return this.build((List)subCategories, (Function)null, (Function)null);
        }

        @Override
        protected ObjectCategoryExcludeList<E, P, S> buildInternally(Set<S> effectiveSet) {
            return new ObjectCategoryExcludeList(this.type, this.stringList, effectiveSet, this.excludeMode);
        }

        public static <E, P, S> Builder<E, P, S> begin(ListFactory listFactory, ObjectCategoryListRuleType<E, P, S> type) {
            return new Builder<E, P, S>(listFactory, type).setDefault();
        }
    }
}

