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
import xaero.hud.category.rule.ObjectCategoryListRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;

public final class ObjectCategoryIncludeList<E, P, S>
extends ObjectCategoryListRule<E, P, S> {
    private ObjectCategoryIncludeList(@Nonnull ObjectCategoryListRuleType<E, P, S> type, @Nonnull List<String> stringList, @Nonnull Set<S> set) {
        super(type, "include list", stringList, set);
    }

    @Override
    public boolean isFollowedBy(E object, P context) {
        return this.inList(object, context);
    }

    public static final class Builder<E, P, S>
    extends ObjectCategoryListRule.Builder<E, P, S, Builder<E, P, S>> {
        private Builder(ListFactory listFactory, ObjectCategoryListRuleType<E, P, S> type) {
            super(listFactory, type);
        }

        @Override
        public Builder<E, P, S> setDefault() {
            super.setDefault();
            return this;
        }

        public static <E, P, S> Builder<E, P, S> begin(ListFactory listFactory, ObjectCategoryListRuleType<E, P, S> type) {
            return new Builder<E, P, S>(listFactory, type).setDefault();
        }

        @Override
        protected <C extends FilterObjectCategory<E, P, ?, C>> ObjectCategoryIncludeList<E, P, S> build(List<C> subCategories, Function<C, ObjectCategoryListRule<E, P, S>> subListGetter, Function<C, ObjectCategoryListRule<E, P, S>> subListExceptionsGetter) {
            return (ObjectCategoryIncludeList)super.build(subCategories, subListGetter, subListExceptionsGetter);
        }

        public <C extends FilterObjectCategory<E, P, ?, C>> ObjectCategoryIncludeList<E, P, S> build(List<C> subCategories) {
            return this.build((List)subCategories, (T sub) -> sub.getIncludeList(this.type), (Function)null);
        }

        @Override
        protected ObjectCategoryIncludeList<E, P, S> buildInternally(Set<S> effectiveSet) {
            return new ObjectCategoryIncludeList(this.type, this.stringList, effectiveSet);
        }
    }
}

