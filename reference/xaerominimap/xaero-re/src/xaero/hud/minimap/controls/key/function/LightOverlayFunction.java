/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public class LightOverlayFunction
extends KeyMappingFunction {
    protected LightOverlayFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        ConfigProfile currentProfile = configManager.getCurrentProfile();
        int lightOverlayTypeConfig = (Integer)currentProfile.get((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE);
        if (lightOverlayTypeConfig == 0) {
            currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE, (Object)1);
        } else {
            currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE, (Object)(-lightOverlayTypeConfig));
        }
        HudMod.INSTANCE.getHudConfigs().getClientConfigProfileIO().save(currentProfile);
    }

    @Override
    public void onRelease() {
    }
}

