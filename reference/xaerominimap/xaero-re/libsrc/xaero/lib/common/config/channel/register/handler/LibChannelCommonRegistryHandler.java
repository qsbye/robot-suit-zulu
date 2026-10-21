/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.channel.register.handler;

import xaero.lib.common.config.channel.register.handler.IConfigChannelCommonRegistryHandler;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.LibProfiledConfigOptions;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;
import xaero.lib.common.config.server.listener.ServerConfigChangeListener;

public class LibChannelCommonRegistryHandler
implements IConfigChannelCommonRegistryHandler {
    @Override
    public void registerPrimaryCommonOptions(ConfigOptionManager manager) {
        LibPrimaryCommonConfigOptions.registerAll(manager);
    }

    @Override
    public void registerProfiledOptions(ConfigOptionManager manager) {
        LibProfiledConfigOptions.registerAll(manager);
    }

    @Override
    public void registerServerOptionChangeHandlers(ServerConfigChangeListener registry) {
    }

    @Override
    public void registerOptionServerRedirectors(OptionValueRedirectorManager manager) {
    }
}

