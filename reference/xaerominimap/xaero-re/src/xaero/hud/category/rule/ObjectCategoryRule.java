/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.rule;

import javax.annotation.Nonnull;

public abstract class ObjectCategoryRule<E, P> {
    private final String name;

    ObjectCategoryRule(@Nonnull String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean isFollowedBy(E var1, P var2);

    public String toString() {
        return String.format("include(%s)", this.name);
    }
}

