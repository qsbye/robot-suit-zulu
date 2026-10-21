/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization.data;

import java.util.function.Supplier;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import xaero.hud.category.serialization.data.ObjectCategoryData;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;

public class ObjectCategoryDataNbtSerializer<D extends ObjectCategoryData<D>, DB extends ObjectCategoryData.Builder<D, DB>>
extends ObjectCategoryDataSerializer<D, CompoundTag> {
    private final Supplier<DB> builderSupplier;

    protected ObjectCategoryDataNbtSerializer(Supplier<DB> builderSupplier) {
        this.builderSupplier = builderSupplier;
    }

    @Override
    public CompoundTag serialize(D data) {
        CompoundTag resultTag = new CompoundTag();
        resultTag.putString("n", ((ObjectCategoryData)data).getName());
        resultTag.putBoolean("p", ((ObjectCategoryData)data).getProtection());
        CompoundTag settingOverrides = new CompoundTag();
        ((ObjectCategoryData)data).getSettingOverrideIterator().forEachRemaining(entry -> {
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                return;
            }
            if (value instanceof Boolean) {
                settingOverrides.putBoolean(key, ((Boolean)value).booleanValue());
                return;
            }
            if (value instanceof Double) {
                settingOverrides.putDouble(key, ((Double)value).doubleValue());
                return;
            }
            if (value instanceof Integer) {
                settingOverrides.putInt(key, ((Integer)value).intValue());
                return;
            }
            throw new IllegalArgumentException("Unsupported category setting type: " + String.valueOf(value.getClass()));
        });
        resultTag.put("v", (Tag)settingOverrides);
        ListTag subCategoriesTag = new ListTag();
        ((ObjectCategoryData)data).getSubCategoryIterator().forEachRemaining(sub -> {
            CompoundTag subCategoryTag = this.serialize((D)sub);
            subCategoriesTag.add((Object)subCategoryTag);
        });
        resultTag.put("s", (Tag)subCategoriesTag);
        return resultTag;
    }

    @Override
    public final D deserialize(CompoundTag serializedData) {
        return ((ObjectCategoryData.Builder)this.getConfiguredBuilder(serializedData)).build();
    }

    protected DB getConfiguredBuilder(CompoundTag serializedData) {
        ObjectCategoryData.Builder builder = (ObjectCategoryData.Builder)this.builderSupplier.get();
        builder.setName(serializedData.getStringOr("n", ""));
        builder.setProtection(serializedData.getBooleanOr("p", false));
        CompoundTag settingOverrides = serializedData.getCompoundOrEmpty("v");
        for (String key : settingOverrides.keySet()) {
            Tag valueTag = settingOverrides.get(key);
            if (valueTag instanceof ByteTag) {
                builder.setSettingOverride(key, ((ByteTag)valueTag).byteValue() == 1);
                continue;
            }
            if (valueTag instanceof DoubleTag) {
                builder.setSettingOverride(key, ((DoubleTag)valueTag).doubleValue());
                continue;
            }
            if (valueTag instanceof IntTag) {
                builder.setSettingOverride(key, ((IntTag)valueTag).intValue());
                continue;
            }
            throw new IllegalArgumentException("Unsupported category setting NBT tag type: " + String.valueOf(valueTag.getClass()));
        }
        ListTag subCategoriesTag = serializedData.getListOrEmpty("s");
        for (Tag subCategoryTag : subCategoriesTag) {
            builder.addSubCategoryBuilder(this.getConfiguredBuilder((CompoundTag)subCategoryTag));
        }
        return (DB)builder;
    }

    public static final class FinalBuilder<D extends ObjectCategoryData<D>, DB extends ObjectCategoryData.Builder<D, DB>>
    extends Builder<D, DB, FinalBuilder<D, DB>> {
        private FinalBuilder() {
        }

        @Override
        protected ObjectCategoryDataNbtSerializer<D, DB> buildInternally() {
            return new ObjectCategoryDataNbtSerializer(this.builderSupplier);
        }

        public static <D extends ObjectCategoryData<D>, DB extends ObjectCategoryData.Builder<D, DB>> FinalBuilder<D, DB> begin() {
            return (FinalBuilder)new FinalBuilder<D, DB>().setDefault();
        }
    }

    public static abstract class Builder<D extends ObjectCategoryData<D>, DB extends ObjectCategoryData.Builder<D, DB>, B extends Builder<D, DB, B>> {
        protected final B self = this;
        protected Supplier<DB> builderSupplier;

        protected Builder() {
        }

        public B setDefault() {
            this.setBuilderSupplier(null);
            return this.self;
        }

        public B setBuilderSupplier(Supplier<DB> builderSupplier) {
            this.builderSupplier = builderSupplier;
            return this.self;
        }

        public ObjectCategoryDataNbtSerializer<D, DB> build() {
            if (this.builderSupplier == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally();
        }

        protected abstract ObjectCategoryDataNbtSerializer<D, DB> buildInternally();
    }
}

