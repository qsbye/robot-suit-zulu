/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.channel;

import xaero.lib.client.config.channel.register.handler.IConfigChannelClientRegistryHandler;
import xaero.lib.client.config.listener.handler.BuiltInConfigOptionClientHandlers;
import xaero.lib.client.config.option.ui.BuiltInConfigOptionUIRegister;
import xaero.lib.client.config.primary.option.BuiltInPrimaryClientConfigOptions;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.ConfigChannelCommonInit;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.single.SingleConfigManager;

public class ConfigChannelClientInit
extends ConfigChannelCommonInit {
    @Override
    protected void registerOptions(ConfigChannel channel) {
        super.registerOptions(channel);
        channel.logger.info("Registering client data for channel {}!", (Object)channel.getId());
        IConfigChannelClientRegistryHandler clientRegistryHandler = channel.getClientRegistryHandler();
        BuiltInPrimaryClientConfigOptions.registerAll(channel.getPrimaryClientConfigOptionManager());
        clientRegistryHandler.registerPrimaryClientOptions(channel.getPrimaryClientConfigOptionManager());
        BuiltInConfigOptionUIRegister.registerAll(channel.getConfigOptionUITypeManager());
        clientRegistryHandler.registerConfigOptionUITypes(channel.getConfigOptionUITypeManager());
        BuiltInConfigOptionClientHandlers.registerAll(channel.getClientConfigManager().getChangeListener());
        clientRegistryHandler.registerClientOptionChangeHandlers(channel.getClientConfigManager().getChangeListener());
        clientRegistryHandler.registerOptionClientRedirectors(channel.getClientConfigManager().getRedirectorManager());
        channel.getClientConfigManager().getRedirectorManager().freeze();
    }

    @Override
    protected void load(ConfigChannel channel) {
        super.load(channel);
        channel.logger.info("Loading primary client config for channel {}!", (Object)channel.getId());
        channel.getPrimaryClientConfigManagerIO().load();
        SingleConfigManager<Config> primaryClientConfigManager = channel.getPrimaryClientConfigManager();
        if (primaryClientConfigManager.getConfig() == null) {
            primaryClientConfigManager.setConfig((Config)((Config.FinalBuilder)((Config.FinalBuilder)Config.FinalBuilder.begin().setOptions(channel.getPrimaryClientConfigOptionManager())).setAllowNullValues(false)).build());
        }
        channel.getPrimaryClientConfigManagerIO().save();
        channel.logger.info("Loading client config profiles for channel {}!", (Object)channel.getId());
        channel.getClientConfigProfileIO().load();
        ConfigProfile currentProfile = channel.getClientConfigManager().getCurrentProfile();
        if (currentProfile == null) {
            this.setDefaultClientConfigProfile(channel);
        }
        channel.getClientConfigProfileIO().saveAll();
    }
}

