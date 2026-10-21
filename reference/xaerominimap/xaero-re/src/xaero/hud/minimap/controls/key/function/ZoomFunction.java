/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;

public class ZoomFunction
extends KeyMappingFunction {
    private final boolean in;

    protected ZoomFunction(boolean in) {
        super(false);
        this.in = in;
    }

    @Override
    public void onPress() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        ConfigChannel channel = HudMod.INSTANCE.getHudConfigs();
        ClientConfigManager configManager = channel.getClientConfigManager();
        if (minimapSession.getProcessor().isEnlargedMap() && (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ZOOM_ENLARGED) != 0) {
            return;
        }
        int zoomChange = this.in ? 1 : -1;
        MinimapConfigClientUtils.changeZoom(zoomChange);
        channel.getClientConfigProfileIO().save(configManager.getCurrentProfile());
    }

    @Override
    public void onRelease() {
    }
}

