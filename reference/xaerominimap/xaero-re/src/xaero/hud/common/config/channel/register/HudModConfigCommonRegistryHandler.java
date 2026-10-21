/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.common.config.channel.register;

import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.common.config.option.value.redirect.MinimapConfigOptionServerRedirectors;
import xaero.hud.minimap.common.config.primary.option.MinimapPrimaryCommonConfigOptions;
import xaero.hud.minimap.common.config.server.listener.handler.MinimapConfigOptionServerHandlers;
import xaero.lib.common.config.channel.register.handler.IConfigChannelCommonRegistryHandler;
import xaero.lib.common.config.listener.handler.HandlerBasedConfigChangeListener;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;
import xaero.lib.common.config.server.listener.ServerConfigChangeListener;

public class HudModConfigCommonRegistryHandler
implements IConfigChannelCommonRegistryHandler {
    public void registerPrimaryCommonOptions(ConfigOptionManager manager) {
        MinimapPrimaryCommonConfigOptions.registerAll(manager);
    }

    public void registerProfiledOptions(ConfigOptionManager manager) {
        MinimapProfiledConfigOptions.registerAll(manager);
    }

    public void registerServerOptionChangeHandlers(ServerConfigChangeListener registry) {
        MinimapConfigOptionServerHandlers.registerAll((HandlerBasedConfigChangeListener)registry);
    }

    public void registerOptionServerRedirectors(OptionValueRedirectorManager manager) {
        MinimapConfigOptionServerRedirectors.registerAll(manager);
    }
}

