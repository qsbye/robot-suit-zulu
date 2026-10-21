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

public class ToggleGridFunction
extends KeyMappingFunction {
    protected ToggleGridFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        ConfigProfile currentProfile = configManager.getCurrentProfile();
        currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID, (Object)(-((Integer)currentProfile.get((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID)).intValue() - 1));
        HudMod.INSTANCE.getHudConfigs().getClientConfigProfileIO().save(currentProfile);
    }

    @Override
    public void onRelease() {
    }
}

