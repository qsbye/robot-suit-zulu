/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.config;

import java.util.ArrayList;
import java.util.HashMap;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.common.config.info.config.InfoDisplayConfigData;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.InfoDisplayManager;

public class InfoDisplayConfigClientUtils {
    public static <T> InfoDisplayConfigData createDefaultConfig(InfoDisplay<T> infoDisplay, ModSettings legacySettings, boolean clientSide) {
        Object legacyValue = clientSide ? infoDisplay.getLegacyValue(legacySettings) : null;
        return new InfoDisplayConfigData(!clientSide ? null : Integer.valueOf(-1), !clientSide ? null : Integer.valueOf(15), !clientSide ? null : infoDisplay.getCodec().encode(legacyValue == null ? infoDisplay.getDefaultState() : legacyValue, null, null));
    }

    public static InfoDisplayManagerConfigData createDefaultConfig(InfoDisplayManager manager, ModSettings legacySettings, boolean clientSide) {
        ArrayList<String> order = new ArrayList<String>(manager.getDefaultOrder());
        HashMap<String, InfoDisplayConfigData> configs = new HashMap<String, InfoDisplayConfigData>();
        manager.getStream().forEach(infoDisplay -> configs.put(infoDisplay.getId(), InfoDisplayConfigClientUtils.createDefaultConfig(infoDisplay, legacySettings, clientSide)));
        return new InfoDisplayManagerConfigData(order, configs);
    }
}

