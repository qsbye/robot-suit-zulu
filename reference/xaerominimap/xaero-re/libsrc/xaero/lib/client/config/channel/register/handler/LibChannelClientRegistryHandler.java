/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.channel.register.handler;

import xaero.lib.client.config.channel.register.handler.IConfigChannelClientRegistryHandler;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.client.config.option.ClientConfigOptionManager;
import xaero.lib.client.config.option.ui.ConfigOptionUITypeManager;
import xaero.lib.client.config.option.ui.LibConfigOptionUIRegister;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;
import xaero.lib.client.config.primary.option.LibPrimaryClientConfigOptions;

public class LibChannelClientRegistryHandler
implements IConfigChannelClientRegistryHandler {
    @Override
    public void registerPrimaryClientOptions(ClientConfigOptionManager manager) {
        LibPrimaryClientConfigOptions.registerAll(manager);
    }

    @Override
    public void registerConfigOptionUITypes(ConfigOptionUITypeManager manager) {
        LibConfigOptionUIRegister.registerAll(manager);
    }

    @Override
    public void registerClientOptionChangeHandlers(ClientConfigChangeListener registry) {
    }

    @Override
    public void registerOptionClientRedirectors(ClientOptionValueRedirectorManager manager) {
    }
}

