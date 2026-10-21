/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config.option.value.type.io.serialization;

import xaero.common.HudMod;
import xaero.hud.category.serialization.data.ObjectCategoryData;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.lib.common.config.option.value.io.serialization.LargeConfigValueIOCodec;

public class MinimapValueIOCodecs {
    public static final LargeConfigValueIOCodec<EntityRadarCategoryData> ENTITY_RADAR_CATEGORIES = new LargeConfigValueIOCodec(rootCategory -> HudMod.INSTANCE.getEntityRadarCategorySerializers().getGson().serialize((ObjectCategoryData)rootCategory), serializedData -> serializedData == null ? null : HudMod.INSTANCE.getEntityRadarCategorySerializers().getGson().deserialize((String)serializedData), Integer.MAX_VALUE, ".json");
    public static final LargeConfigValueIOCodec<InfoDisplayManagerConfigData> INFO_DISPLAY_CONFIG = new LargeConfigValueIOCodec(managerConfigData -> HudMod.INSTANCE.getInfoDisplaysIO().encode((InfoDisplayManagerConfigData)managerConfigData), encodedData -> encodedData == null ? null : HudMod.INSTANCE.getInfoDisplaysIO().decode((String)encodedData), Integer.MAX_VALUE, ".txt");
}

