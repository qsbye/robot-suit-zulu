/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.channel;

import xaero.lib.client.config.primary.option.BuiltInPrimaryClientConfigOptions;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.handler.IConfigChannelCommonRegistryHandler;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.BuiltInPrimaryCommonConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.ConfigProfileManager;
import xaero.lib.common.config.server.listener.handler.BuiltInConfigOptionServerHandlers;
import xaero.lib.common.config.single.SingleConfigManager;

public class ConfigChannelCommonInit {
    public final void handle(ConfigChannel channel) {
        this.registerOptions(channel);
        channel.freezeOptionManagers();
        this.load(channel);
        channel.postLoad();
    }

    protected void registerOptions(ConfigChannel channel) {
        channel.logger.info("Registering common data for channel {}!", (Object)channel.getId());
        IConfigChannelCommonRegistryHandler commonRegistryHandler = channel.getCommonRegistryHandler();
        BuiltInPrimaryCommonConfigOptions.registerAll(channel.getPrimaryCommonConfigOptionManager());
        channel.getBuiltInPermissions().register();
        commonRegistryHandler.registerPrimaryCommonOptions(channel.getPrimaryCommonConfigOptionManager());
        BuiltInProfiledConfigOptions.registerAll(channel.getConfigOptionManager());
        commonRegistryHandler.registerProfiledOptions(channel.getConfigOptionManager());
        BuiltInConfigOptionServerHandlers.registerAll(channel.getServerConfigManager().getChangeListener(), channel);
        commonRegistryHandler.registerServerOptionChangeHandlers(channel.getServerConfigManager().getChangeListener());
        commonRegistryHandler.registerOptionServerRedirectors(channel.getServerConfigManager().getRedirectorManager());
        channel.getServerConfigManager().getRedirectorManager().freeze();
    }

    protected void load(ConfigChannel channel) {
        channel.logger.info("Loading primary common config for channel {}!", (Object)channel.getId());
        channel.getPrimaryCommonConfigManagerIO().load();
        SingleConfigManager<Config> primaryCommonConfigManager = channel.getPrimaryCommonConfigManager();
        if (primaryCommonConfigManager.getConfig() == null) {
            primaryCommonConfigManager.setConfig((Config)((Config.FinalBuilder)((Config.FinalBuilder)Config.FinalBuilder.begin().setOptions(channel.getPrimaryCommonConfigOptionManager())).setAllowNullValues(false)).build());
        }
        channel.getPrimaryCommonConfigManagerIO().save();
        channel.logger.info("Loading server config profiles for channel {}!", (Object)channel.getId());
        channel.getServerConfigProfileIO().load();
        ConfigProfile defaultEnforcedProfile = channel.getServerConfigManager().getDefaultEnforcedProfile();
        if (defaultEnforcedProfile == null) {
            this.setDefaultServerConfigProfile(channel);
        }
        channel.getServerConfigProfileIO().saveAll();
    }

    protected void setDefaultClientConfigProfile(ConfigChannel configs) {
        this.setDefaultConfigProfile(configs, configs.getClientConfigManager().getProfileManager(), configs.getPrimaryClientConfigManager(), BuiltInPrimaryClientConfigOptions.CURRENT_PROFILE, false, false);
    }

    private void setDefaultServerConfigProfile(ConfigChannel configs) {
        this.setDefaultConfigProfile(configs, configs.getServerConfigManager().getProfileManager(), configs.getPrimaryCommonConfigManager(), BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE, true, false);
    }

    private void setDefaultConfigProfile(ConfigChannel configs, ConfigProfileManager profileManager, SingleConfigManager<Config> primaryConfigManager, ConfigOption<String> defaultProfileOption, boolean allowNullValues, boolean trackDirtyOptions) {
        ConfigProfile defaultProfile = profileManager.get(defaultProfileOption.getDefaultValue());
        if (defaultProfile == null) {
            defaultProfile = ((ConfigProfile.Builder)((ConfigProfile.Builder)((ConfigProfile.Builder)ConfigProfile.Builder.begin().setId(defaultProfileOption.getDefaultValue()).setOptions(configs.getConfigOptionManager())).setAllowNullValues(allowNullValues)).setTrackDirtyOptions(trackDirtyOptions)).build();
            defaultProfile.set(BuiltInProfiledConfigOptions.PROFILE_NAME, "Default");
            profileManager.add(defaultProfile);
        }
        primaryConfigManager.getConfig().set(defaultProfileOption, defaultProfile.getId());
    }
}

