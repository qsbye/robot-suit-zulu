/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.serialization.data.ObjectCategoryData;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;
import xaero.hud.category.setting.ObjectCategorySetting;

public abstract class ObjectCategorySerializationHandler<D extends ObjectCategoryData<D>, C extends ObjectCategory<D, C>, B extends ObjectCategory.Builder<C, B>, DB extends ObjectCategoryData.Builder<D, DB>> {
    private final ObjectCategoryDataSerializer<D, String> serializer;
    private final Supplier<DB> dataBuilderFactory;
    private final Supplier<B> objectCategoryBuilderFactory;
    private final Function<String, ObjectCategorySetting<?>> settingTypeGetter;

    protected ObjectCategorySerializationHandler(@Nonnull ObjectCategoryDataSerializer<D, String> serializer, @Nonnull Supplier<DB> dataBuilderFactory, @Nonnull Supplier<B> objectCategoryBuilderFactory, @Nonnull Function<String, ObjectCategorySetting<?>> settingTypeGetter) {
        this.serializer = serializer;
        this.dataBuilderFactory = dataBuilderFactory;
        this.objectCategoryBuilderFactory = objectCategoryBuilderFactory;
        this.settingTypeGetter = settingTypeGetter;
    }

    public String serialize(C category) {
        DB dataBuilder = this.getConfiguredDataBuilderForCategory(category);
        String serializedData = this.serializer.serialize(((ObjectCategoryData.Builder)dataBuilder).build());
        return serializedData;
    }

    public D convertToData(C category) {
        DB dataBuilder = this.getConfiguredDataBuilderForCategory(category);
        return ((ObjectCategoryData.Builder)dataBuilder).build();
    }

    protected DB getConfiguredDataBuilderForCategory(C category) {
        Object dataBuilder = ((ObjectCategoryData.Builder)this.dataBuilderFactory.get()).setDefault();
        ((ObjectCategoryData.Builder)dataBuilder).setName(((ObjectCategory)category).getName());
        ((ObjectCategoryData.Builder)dataBuilder).setProtection(((ObjectCategory)category).getProtection());
        ((ObjectCategory)category).getSettingOverridesIterator().forEachRemaining(e -> dataBuilder.setSettingOverride(((ObjectCategorySetting)e.getKey()).getId(), e.getValue()));
        ((ObjectCategory)category).getDirectSubCategoryIterator().forEachRemaining(c -> dataBuilder.addSubCategoryBuilder(this.getConfiguredDataBuilderForCategory(c)));
        return (DB)dataBuilder;
    }

    public C convertFromData(D data) {
        B builder = this.getConfiguredCategoryBuilderForData(data);
        return ((ObjectCategory.Builder)builder).build();
    }

    public C deserialize(String serializedData) {
        D data = this.serializer.deserialize(serializedData);
        B categoryBuilder = this.getConfiguredCategoryBuilderForData(data);
        return ((ObjectCategory.Builder)categoryBuilder).build();
    }

    protected B getConfiguredCategoryBuilderForData(D data) {
        Object objectCategoryBuilder = ((ObjectCategory.Builder)this.objectCategoryBuilderFactory.get()).setDefault();
        String dataName = ((ObjectCategoryData)data).getName();
        ((ObjectCategory.Builder)objectCategoryBuilder).setName(dataName);
        ((ObjectCategory.Builder)objectCategoryBuilder).setProtection(((ObjectCategoryData)data).getProtection());
        ((ObjectCategoryData)data).getSettingOverrideIterator().forEachRemaining(e -> {
            ObjectCategorySetting<?> setting = this.settingTypeGetter.apply((String)e.getKey());
            if (setting != null) {
                this.setSettingValue(objectCategoryBuilder, setting, e.getValue());
            }
        });
        ((ObjectCategoryData)data).getSubCategoryIterator().forEachRemaining(subCategory -> objectCategoryBuilder.addSubCategoryBuilder(this.getConfiguredCategoryBuilderForData(subCategory)));
        return objectCategoryBuilder;
    }

    private <T> void setSettingValue(B objectCategoryBuilder, ObjectCategorySetting<T> setting, Object value) {
        ((ObjectCategory.Builder)objectCategoryBuilder).setSettingValue(setting, (Object)value);
    }

    public static abstract class Builder<D extends ObjectCategoryData<D>, C extends ObjectCategory<D, C>, B extends ObjectCategory.Builder<C, B>, DB extends ObjectCategoryData.Builder<D, DB>, SH extends ObjectCategorySerializationHandler<D, C, B, DB>, SHB extends Builder<D, C, B, DB, SH, SHB>> {
        protected final SHB self = this;
        protected final ObjectCategoryDataSerializer<D, String> serializer;
        protected Supplier<DB> dataBuilderFactory;
        protected Supplier<B> objectCategoryBuilderFactory;
        protected Function<String, ObjectCategorySetting<?>> settingTypeGetter;

        public Builder(ObjectCategoryDataSerializer<D, String> serializer) {
            this.serializer = serializer;
        }

        public SHB setDefault() {
            this.setDataBuilderFactory(null);
            this.setObjectCategoryBuilderFactory(null);
            this.setSettingTypeGetter(null);
            return this.self;
        }

        public SHB setDataBuilderFactory(Supplier<DB> dataBuilderFactory) {
            this.dataBuilderFactory = dataBuilderFactory;
            return this.self;
        }

        public SHB setObjectCategoryBuilderFactory(Supplier<B> objectCategoryBuilderFactory) {
            this.objectCategoryBuilderFactory = objectCategoryBuilderFactory;
            return this.self;
        }

        public SHB setSettingTypeGetter(Function<String, ObjectCategorySetting<?>> settingTypeGetter) {
            this.settingTypeGetter = settingTypeGetter;
            return this.self;
        }

        public SH build() {
            if (this.dataBuilderFactory == null || this.objectCategoryBuilderFactory == null || this.settingTypeGetter == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return this.buildInternally();
        }

        protected abstract SH buildInternally();
    }
}

