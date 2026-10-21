/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import xaero.common.HudMod;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.BuiltInInfoDisplays;
import xaero.hud.minimap.info.InfoDisplayIO;
import xaero.hud.minimap.info.InfoDisplayManager;
import xaero.hud.minimap.info.render.InfoDisplayRenderer;
import xaero.lib.client.config.ClientConfigManager;

public class InfoDisplays {
    private final InfoDisplayManager manager;
    private final InfoDisplayIO io;
    private final InfoDisplayRenderer renderer;

    public InfoDisplays(InfoDisplayIO io) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        this.manager = InfoDisplayManager.Builder.begin().setLocalConfigSupplier(() -> (InfoDisplayManagerConfigData)configManager.getCurrentProfile().get(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG)).setLocalConfigSetter(config -> configManager.getCurrentProfile().set(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG, config)).setEnforcedConfigSupplier(() -> {
            if (configManager.shouldIgnoreServerEnforcement(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG)) {
                return null;
            }
            return (InfoDisplayManagerConfigData)configManager.getServerSynced().getConfig().get(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG);
        }).build();
        BuiltInInfoDisplays.forEach(this.manager::add);
        this.io = io;
        this.renderer = InfoDisplayRenderer.Builder.begin().build();
    }

    public InfoDisplayManager getManager() {
        return this.manager;
    }

    public InfoDisplayIO getIo() {
        return this.io;
    }

    public InfoDisplayRenderer getRenderer() {
        return this.renderer;
    }

    public void clearStateCache() {
        this.manager.clearStateCache();
    }
}

