/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization.data;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import xaero.hud.category.rule.ExcludeListMode;
import xaero.hud.category.serialization.data.FilterObjectCategoryData;
import xaero.hud.category.serialization.data.ObjectCategoryDataNbtSerializer;

public class FilterObjectCategoryDataNbtSerializer<D extends FilterObjectCategoryData<D>, DB extends FilterObjectCategoryData.Builder<D, DB>>
extends ObjectCategoryDataNbtSerializer<D, DB> {
    protected FilterObjectCategoryDataNbtSerializer(Supplier<DB> builderSupplier) {
        super(builderSupplier);
    }

    @Override
    public CompoundTag serialize(D data) {
        CompoundTag resultTag = super.serialize(data);
        resultTag.putString("h", ((FilterObjectCategoryData)data).getHardInclude());
        resultTag.putBoolean("i", ((FilterObjectCategoryData)data).getIncludeListInSuperCategory());
        resultTag.putString("m", ((FilterObjectCategoryData)data).getExcludeMode().name());
        ListTag includeListTag = new ListTag();
        ((FilterObjectCategoryData)data).getIncludeListIterator().forEachRemaining(includeEntry -> includeListTag.add((Object)StringTag.valueOf((String)includeEntry)));
        resultTag.put("l", (Tag)includeListTag);
        ListTag excludeListTag = new ListTag();
        ((FilterObjectCategoryData)data).getExcludeListIterator().forEachRemaining(excludeEntry -> excludeListTag.add((Object)StringTag.valueOf((String)excludeEntry)));
        resultTag.put("e", (Tag)excludeListTag);
        return resultTag;
    }

    @Override
    protected DB getConfiguredBuilder(CompoundTag serializedData) {
        FilterObjectCategoryData.Builder builder = (FilterObjectCategoryData.Builder)super.getConfiguredBuilder(serializedData);
        builder.setHardInclude(serializedData.getStringOr("h", ""));
        builder.setIncludeListInSuperCategory(serializedData.getBooleanOr("i", false));
        builder.setExcludeMode(ExcludeListMode.valueOf(serializedData.getStringOr("m", "")));
        ListTag includeListTag = serializedData.getListOrEmpty("l");
        for (Tag includeEntryTag : includeListTag) {
            builder.addToIncludeList((String)includeEntryTag.asString().get());
        }
        ListTag excludeListTag = serializedData.getListOrEmpty("e");
        for (Tag excludeEntryTag : excludeListTag) {
            builder.addToExcludeList((String)excludeEntryTag.asString().get());
        }
        return (DB)builder;
    }

    public static final class FinalBuilder<D extends FilterObjectCategoryData<D>, DB extends FilterObjectCategoryData.Builder<D, DB>>
    extends Builder<D, DB, FinalBuilder<D, DB>> {
        private FinalBuilder() {
        }

        @Override
        protected FilterObjectCategoryDataNbtSerializer<D, DB> buildInternally() {
            return new FilterObjectCategoryDataNbtSerializer(this.builderSupplier);
        }

        public static <D extends FilterObjectCategoryData<D>, DB extends FilterObjectCategoryData.Builder<D, DB>> FinalBuilder<D, DB> begin() {
            return (FinalBuilder)new FinalBuilder<D, DB>().setDefault();
        }
    }

    public static abstract class Builder<D extends FilterObjectCategoryData<D>, DB extends FilterObjectCategoryData.Builder<D, DB>, B extends Builder<D, DB, B>>
    extends ObjectCategoryDataNbtSerializer.Builder<D, DB, B> {
        protected Builder() {
        }

        @Override
        public FilterObjectCategoryDataNbtSerializer<D, DB> build() {
            return (FilterObjectCategoryDataNbtSerializer)super.build();
        }
    }
}

