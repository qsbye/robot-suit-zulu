/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.rule;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import xaero.hud.category.rule.ObjectCategoryRule;

public final class ObjectCategoryHardRule<E, P>
extends ObjectCategoryRule<E, P> {
    private final Predicate<E, P> predicate;
    private final boolean reversed;

    private ObjectCategoryHardRule(@Nonnull String name, boolean reversed, @Nonnull Predicate<E, P> predicate) {
        super(name);
        this.reversed = reversed;
        this.predicate = predicate;
    }

    @Override
    public boolean isFollowedBy(E object, P context) {
        if (this.reversed) {
            return !this.predicate.test(object, context);
        }
        return this.predicate.test(object, context);
    }

    public static interface Predicate<E, P> {
        public boolean test(E var1, P var2);
    }

    public static final class Builder<E, P> {
        private String name;
        private Predicate<E, P> predicate;
        private boolean reversed;

        public Builder<E, P> setDefault() {
            this.setName(null);
            this.setPredicate(null);
            this.setReversed(false);
            return this;
        }

        public Builder<E, P> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<E, P> setPredicate(Predicate<E, P> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder<E, P> setReversed(boolean reversed) {
            this.reversed = reversed;
            return this;
        }

        public ObjectCategoryHardRule<E, P> build(Map<String, ObjectCategoryHardRule<E, P>> destinationMap, List<ObjectCategoryHardRule<E, P>> destinationList) {
            if (this.name == null || this.predicate == null) {
                throw new IllegalStateException("required fields not set!");
            }
            ObjectCategoryHardRule<E, P> rule = new ObjectCategoryHardRule<E, P>(this.name, this.reversed, this.predicate);
            destinationMap.put(rule.getName(), rule);
            destinationList.add(rule);
            return rule;
        }
    }
}

