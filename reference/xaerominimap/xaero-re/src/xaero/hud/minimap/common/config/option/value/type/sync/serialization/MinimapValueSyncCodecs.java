/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config.option.value.type.sync.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import xaero.common.HudMod;
import xaero.hud.minimap.common.config.info.config.InfoDisplayConfigData;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;

public class MinimapValueSyncCodecs {
    public static final ConfigValueSyncCodec<EntityRadarCategoryData, CompoundTag> ENTITY_RADAR_CATEGORIES = new ConfigValueSyncCodec(rootCategory -> HudMod.INSTANCE.getEntityRadarCategorySerializers().getNbt().serialize((EntityRadarCategoryData)rootCategory), serializedData -> HudMod.INSTANCE.getEntityRadarCategorySerializers().getNbt().deserialize((CompoundTag)serializedData));
    public static final ConfigValueSyncCodec<InfoDisplayManagerConfigData, CompoundTag> INFO_DISPLAY_CONFIG = new ConfigValueSyncCodec(config -> {
        CompoundTag encodedData = new CompoundTag();
        ListTag orderTag = new ListTag();
        config.getOrderStream().forEach(s -> orderTag.add((Object)StringTag.valueOf((String)s)));
        encodedData.put("o", (Tag)orderTag);
        CompoundTag configsTag = new CompoundTag();
        config.getOrderStream().forEach(id -> {
            InfoDisplayConfigData infoDisplayConfig = config.get((String)id);
            if (infoDisplayConfig == null) {
                return;
            }
            CompoundTag infoDisplayTag = new CompoundTag();
            if (infoDisplayConfig.getState() != null) {
                infoDisplayTag.putString("s", infoDisplayConfig.getState());
            }
            if (infoDisplayConfig.getBackgroundColor() != null) {
                infoDisplayTag.putInt("b", infoDisplayConfig.getBackgroundColor().intValue());
            }
            if (infoDisplayConfig.getTextColor() != null) {
                infoDisplayTag.putInt("t", infoDisplayConfig.getTextColor().intValue());
            }
            if (infoDisplayTag.isEmpty()) {
                return;
            }
            configsTag.put(id, (Tag)infoDisplayTag);
        });
        encodedData.put("c", (Tag)configsTag);
        return encodedData;
    }, encodedData -> {
        ListTag orderTag = encodedData.getListOrEmpty("o");
        ArrayList<String> order = new ArrayList<String>();
        if (!orderTag.isEmpty()) {
            for (int i = 0; i < orderTag.size(); ++i) {
                order.add(orderTag.getStringOr(i, ""));
            }
        }
        CompoundTag configsTag = encodedData.getCompoundOrEmpty("c");
        HashMap<String, InfoDisplayConfigData> configs = new HashMap<String, InfoDisplayConfigData>();
        order.forEach(id -> {
            CompoundTag infoDisplayTag = configsTag.getCompoundOrEmpty(id);
            if (infoDisplayTag.isEmpty()) {
                configs.put((String)id, new InfoDisplayConfigData(null, null, null));
                return;
            }
            String state = infoDisplayTag.contains("s") ? infoDisplayTag.getStringOr("s", "") : null;
            Integer backgroundColor = infoDisplayTag.contains("b") ? Integer.valueOf(infoDisplayTag.getIntOr("b", 0)) : null;
            Integer textColor = infoDisplayTag.contains("t") ? Integer.valueOf(infoDisplayTag.getIntOr("t", 0)) : null;
            configs.put((String)id, new InfoDisplayConfigData(backgroundColor, textColor, state));
        });
        return new InfoDisplayManagerConfigData(order, configs);
    });
}

