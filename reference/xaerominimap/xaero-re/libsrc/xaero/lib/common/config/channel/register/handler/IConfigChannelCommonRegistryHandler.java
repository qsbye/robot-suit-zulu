/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.channel.register.handler;

import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;
import xaero.lib.common.config.server.listener.ServerConfigChangeListener;

public interface IConfigChannelCommonRegistryHandler {
    public void registerPrimaryCommonOptions(ConfigOptionManager var1);

    public void registerProfiledOptions(ConfigOptionManager var1);

    public void registerServerOptionChangeHandlers(ServerConfigChangeListener var1);

    public void registerOptionServerRedirectors(OptionValueRedirectorManager var1);
}

