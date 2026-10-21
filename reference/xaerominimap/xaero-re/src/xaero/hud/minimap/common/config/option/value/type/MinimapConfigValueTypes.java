/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config.option.value.type;

import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.common.config.option.value.type.io.serialization.MinimapValueIOCodecs;
import xaero.hud.minimap.common.config.option.value.type.sync.serialization.MinimapValueSyncCodecs;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.lib.common.config.option.value.type.ConfigValueType;
import xaero.lib.common.config.option.value.type.LargeConfigValueType;

public class MinimapConfigValueTypes {
    public static final ConfigValueType<EntityRadarCategoryData> ENTITY_RADAR_CATEGORIES = ((LargeConfigValueType.Builder)LargeConfigValueType.Builder.begin().setIoCodec(MinimapValueIOCodecs.ENTITY_RADAR_CATEGORIES).setSyncCodec(MinimapValueSyncCodecs.ENTITY_RADAR_CATEGORIES)).build();
    public static final ConfigValueType<InfoDisplayManagerConfigData> INFO_DISPLAY_CONFIG = ((LargeConfigValueType.Builder)LargeConfigValueType.Builder.begin().setIoCodec(MinimapValueIOCodecs.INFO_DISPLAY_CONFIG).setSyncCodec(MinimapValueSyncCodecs.INFO_DISPLAY_CONFIG)).build();
}

