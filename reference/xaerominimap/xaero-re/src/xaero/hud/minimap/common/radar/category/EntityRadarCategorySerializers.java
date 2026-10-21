/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.radar.category;

import com.google.gson.GsonBuilder;
import xaero.hud.category.serialization.data.FilterObjectCategoryDataNbtSerializer;
import xaero.hud.category.serialization.data.ObjectCategoryDataGsonSerializer;
import xaero.hud.category.serialization.data.ObjectCategoryDataNbtSerializer;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;

public class EntityRadarCategorySerializers {
    private final ObjectCategoryDataGsonSerializer<EntityRadarCategoryData> gson;
    private final ObjectCategoryDataNbtSerializer<EntityRadarCategoryData, EntityRadarCategoryData.Builder> nbt;

    public EntityRadarCategorySerializers(ObjectCategoryDataGsonSerializer<EntityRadarCategoryData> gson, ObjectCategoryDataNbtSerializer<EntityRadarCategoryData, EntityRadarCategoryData.Builder> nbt) {
        this.gson = gson;
        this.nbt = nbt;
    }

    public ObjectCategoryDataGsonSerializer<EntityRadarCategoryData> getGson() {
        return this.gson;
    }

    public ObjectCategoryDataNbtSerializer<EntityRadarCategoryData, EntityRadarCategoryData.Builder> getNbt() {
        return this.nbt;
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public EntityRadarCategorySerializers build() {
            ObjectCategoryDataSerializer gson = ObjectCategoryDataGsonSerializer.Builder.begin(new GsonBuilder().setPrettyPrinting().create(), EntityRadarCategoryData.class).build();
            ObjectCategoryDataNbtSerializer nbt = ((FilterObjectCategoryDataNbtSerializer.FinalBuilder)FilterObjectCategoryDataNbtSerializer.FinalBuilder.begin().setBuilderSupplier(EntityRadarCategoryData.Builder::begin)).build();
            return new EntityRadarCategorySerializers((ObjectCategoryDataGsonSerializer<EntityRadarCategoryData>)gson, nbt);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

