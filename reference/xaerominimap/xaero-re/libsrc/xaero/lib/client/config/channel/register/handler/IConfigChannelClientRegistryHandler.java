/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.channel.register.handler;

import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.client.config.option.ClientConfigOptionManager;
import xaero.lib.client.config.option.ui.ConfigOptionUITypeManager;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;

public interface IConfigChannelClientRegistryHandler {
    public void registerPrimaryClientOptions(ClientConfigOptionManager var1);

    public void registerConfigOptionUITypes(ConfigOptionUITypeManager var1);

    public void registerClientOptionChangeHandlers(ClientConfigChangeListener var1);

    public void registerOptionClientRedirectors(ClientOptionValueRedirectorManager var1);
}

