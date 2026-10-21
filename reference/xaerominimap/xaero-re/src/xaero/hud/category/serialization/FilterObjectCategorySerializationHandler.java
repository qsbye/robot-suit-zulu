/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization;

import java.util.function.Function;
import java.util.function.Supplier;
import xaero.hud.category.FilterObjectCategory;
import xaero.hud.category.rule.ObjectCategoryExcludeList;
import xaero.hud.category.rule.ObjectCategoryHardRule;
import xaero.hud.category.rule.ObjectCategoryIncludeList;
import xaero.hud.category.rule.ObjectCategoryListRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.rule.ObjectCategoryRule;
import xaero.hud.category.serialization.ObjectCategorySerializationHandler;
import xaero.hud.category.serialization.data.FilterObjectCategoryData;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;
import xaero.hud.category.setting.ObjectCategorySetting;

public abstract class FilterObjectCategorySerializationHandler<E, P, D extends FilterObjectCategoryData<D>, C extends FilterObjectCategory<E, P, D, C>, B extends FilterObjectCategory.Builder<E, P, C, B>, DB extends FilterObjectCategoryData.Builder<D, DB>>
extends ObjectCategorySerializationHandler<D, C, B, DB> {
    private final Function<String, ObjectCategoryHardRule<E, P>> hardRuleGetter;
    private final ObjectCategoryListRuleType<E, P, ?> defaultListRuleType;
    private final Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter;
    private final String listRuleTypePrefixSeparator;

    protected FilterObjectCategorySerializationHandler(ObjectCategoryDataSerializer<D, String> serializer, Supplier<DB> dataBuilderFactory, Supplier<B> objectCategoryBuilderFactory, Function<String, ObjectCategorySetting<?>> settingTypeGetter, Function<String, ObjectCategoryHardRule<E, P>> hardRuleGetter, ObjectCategoryListRuleType<E, P, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator) {
        super(serializer, dataBuilderFactory, objectCategoryBuilderFactory, settingTypeGetter);
        this.hardRuleGetter = hardRuleGetter;
        this.defaultListRuleType = defaultListRuleType;
        this.listRuleTypeGetter = listRuleTypeGetter;
        this.listRuleTypePrefixSeparator = listRuleTypePrefixSeparator;
    }

    @Override
    protected DB getConfiguredDataBuilderForCategory(C category) {
        String prefix;
        FilterObjectCategoryData.Builder dataBuilder = (FilterObjectCategoryData.Builder)super.getConfiguredDataBuilderForCategory(category);
        ObjectCategoryRule baseRule = ((FilterObjectCategory)category).getBaseRule();
        dataBuilder.setHardInclude(baseRule == null ? "nothing" : baseRule.getName());
        dataBuilder.setExcludeMode(((FilterObjectCategory)category).getExcludeMode());
        dataBuilder.setIncludeListInSuperCategory(((FilterObjectCategory)category).getIncludeInSuperCategory());
        for (ObjectCategoryIncludeList objectCategoryIncludeList : ((FilterObjectCategory)category).getIncludeLists()) {
            prefix = this.getListRulePrefix(objectCategoryIncludeList);
            objectCategoryIncludeList.forEach(el -> dataBuilder.addToIncludeList(prefix + el));
        }
        for (ObjectCategoryExcludeList objectCategoryExcludeList : ((FilterObjectCategory)category).getExcludeLists()) {
            prefix = this.getListRulePrefix(objectCategoryExcludeList);
            objectCategoryExcludeList.forEach(el -> dataBuilder.addToExcludeList(prefix + el));
        }
        return (DB)dataBuilder;
    }

    private String getListRulePrefix(ObjectCategoryListRule<E, P, ?> listRule) {
        if (listRule.getType() == this.defaultListRuleType) {
            return "";
        }
        return listRule.getType().getId() + this.listRuleTypePrefixSeparator;
    }

    @Override
    protected B getConfiguredCategoryBuilderForData(D data) {
        ObjectCategoryHardRule<E, P> serializedHardRule;
        FilterObjectCategory.Builder objectCategoryBuilder = (FilterObjectCategory.Builder)super.getConfiguredCategoryBuilderForData(data);
        String hardInclude = ((FilterObjectCategoryData)data).getHardInclude();
        ObjectCategoryHardRule<E, P> objectCategoryHardRule = serializedHardRule = this.hardRuleGetter == null ? null : this.hardRuleGetter.apply(hardInclude);
        if (serializedHardRule != null) {
            objectCategoryBuilder.setBaseRule(serializedHardRule);
        }
        objectCategoryBuilder.setExcludeMode(((FilterObjectCategoryData)data).getExcludeMode());
        objectCategoryBuilder.setIncludeInSuperCategory(((FilterObjectCategoryData)data).getIncludeListInSuperCategory());
        ((FilterObjectCategoryData)data).getIncludeListIterator().forEachRemaining(s -> this.handleListRuleSerializedElement((String)s, objectCategoryBuilder::getIncludeListBuilder));
        ((FilterObjectCategoryData)data).getExcludeListIterator().forEachRemaining(s -> this.handleListRuleSerializedElement((String)s, objectCategoryBuilder::getExcludeListBuilder));
        return (B)objectCategoryBuilder;
    }

    public void handleListRuleSerializedElement(String s, Function<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryListRule.Builder<E, P, ?, ?>> listBuilderGetter) {
        FilterObjectCategorySerializationHandler.handleListRuleSerializedElement(s, listBuilderGetter, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator);
    }

    public static <E, P> void handleListRuleSerializedElement(String s, Function<ObjectCategoryListRuleType<E, P, ?>, ObjectCategoryListRule.Builder<E, P, ?, ?>> listBuilderGetter, ObjectCategoryListRuleType<E, P, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator) {
        ObjectCategoryListRuleType<E, P, ?> entryListRuleType = defaultListRuleType;
        if (s.contains(listRuleTypePrefixSeparator)) {
            ObjectCategoryListRuleType<E, P, ?> specifiedListRuleType = listRuleTypeGetter.apply(s.substring(0, s.indexOf(listRuleTypePrefixSeparator)));
            if (specifiedListRuleType != null) {
                entryListRuleType = specifiedListRuleType;
            }
            s = s.substring(s.indexOf(listRuleTypePrefixSeparator) + 1);
        }
        listBuilderGetter.apply(entryListRuleType).addListElement(s);
    }

    public static abstract class Builder<E, P, D extends FilterObjectCategoryData<D>, C extends FilterObjectCategory<E, P, D, C>, B extends FilterObjectCategory.Builder<E, P, C, B>, DB extends FilterObjectCategoryData.Builder<D, DB>, SH extends FilterObjectCategorySerializationHandler<E, P, D, C, B, DB>, SHB extends Builder<E, P, D, C, B, DB, SH, SHB>>
    extends ObjectCategorySerializationHandler.Builder<D, C, B, DB, SH, SHB> {
        protected Function<String, ObjectCategoryHardRule<E, P>> hardRuleGetter;
        protected ObjectCategoryListRuleType<E, P, ?> defaultListRuleType;
        protected Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter;
        protected String listRuleTypePrefixSeparator;

        public Builder(ObjectCategoryDataSerializer<D, String> serializer) {
            super(serializer);
        }

        @Override
        public SHB setDefault() {
            super.setDefault();
            this.setHardRuleGetter(null);
            this.setDefaultListRuleType(null);
            this.setListRuleTypeGetter(null);
            this.setListRuleTypePrefixSeparator(";");
            return (SHB)((Builder)this.self);
        }

        public SHB setDefaultListRuleType(ObjectCategoryListRuleType<E, P, ?> defaultListRuleType) {
            this.defaultListRuleType = defaultListRuleType;
            return (SHB)((Builder)this.self);
        }

        public SHB setHardRuleGetter(Function<String, ObjectCategoryHardRule<E, P>> hardRuleGetter) {
            this.hardRuleGetter = hardRuleGetter;
            return (SHB)((Builder)this.self);
        }

        public SHB setListRuleTypeGetter(Function<String, ObjectCategoryListRuleType<E, P, ?>> listRuleTypeGetter) {
            this.listRuleTypeGetter = listRuleTypeGetter;
            return (SHB)((Builder)this.self);
        }

        public SHB setListRuleTypePrefixSeparator(String listRuleTypePrefixSeparator) {
            this.listRuleTypePrefixSeparator = listRuleTypePrefixSeparator;
            return (SHB)((Builder)this.self);
        }

        @Override
        public SH build() {
            if (this.hardRuleGetter == null || this.defaultListRuleType == null || this.listRuleTypeGetter == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (SH)((FilterObjectCategorySerializationHandler)super.build());
        }
    }
}

