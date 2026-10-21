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

public class OpacClaimsFunction
extends KeyMappingFunction {
    protected OpacClaimsFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        ConfigProfile currentProfile;
        HudMod modMain = HudMod.INSTANCE;
        if (modMain.getSupportMods().worldmap() && modMain.getSupportMods().shouldUseWorldMapChunks()) {
            modMain.getSupportMods().worldmapSupport.toggleChunkClaims();
            return;
        }
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS, (Object)((Boolean)(currentProfile = configManager.getCurrentProfile()).get((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS) == false ? 1 : 0));
        HudMod.INSTANCE.getHudConfigs().getClientConfigProfileIO().save(currentProfile);
    }

    @Override
    public void onRelease() {
    }
}

