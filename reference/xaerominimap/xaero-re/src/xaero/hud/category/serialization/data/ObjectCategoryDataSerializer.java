/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization.data;

import xaero.hud.category.serialization.data.ObjectCategoryData;

public abstract class ObjectCategoryDataSerializer<D extends ObjectCategoryData<D>, S> {
    protected ObjectCategoryDataSerializer() {
    }

    public abstract S serialize(D var1);

    public abstract D deserialize(S var1);

    public static abstract class Builder<D extends ObjectCategoryData<D>, S> {
        public Builder<D, S> setDefault() {
            return this;
        }

        public abstract ObjectCategoryDataSerializer<D, S> build();
    }
}

