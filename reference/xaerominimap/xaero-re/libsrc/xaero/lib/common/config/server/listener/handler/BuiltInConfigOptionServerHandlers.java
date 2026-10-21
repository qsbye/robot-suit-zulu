/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.server.listener.handler;

import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.listener.handler.HandlerBasedConfigChangeListener;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.primary.option.BuiltInPrimaryCommonConfigOptions;
import xaero.lib.common.config.profile.ConfigProfile;

public class BuiltInConfigOptionServerHandlers {
    public static void registerAll(HandlerBasedConfigChangeListener listener, ConfigChannel channel) {
        listener.register(BuiltInProfiledConfigOptions.PROFILE_NAME, config -> {
            if (!(config instanceof ConfigProfile)) {
                return;
            }
            if (channel.getServerConfigManager().getServer() == null) {
                return;
            }
            channel.getServerConfigSynchronizer().syncProfileInfoChange((ConfigProfile)config);
        });
        listener.register(BuiltInPrimaryCommonConfigOptions.DEFAULT_ENFORCED_PROFILE, config -> {
            if (config instanceof ConfigProfile) {
                return;
            }
            if (channel.getServerConfigManager().getServer() == null) {
                return;
            }
            ConfigProfile enforcedProfile = channel.getServerConfigManager().getDefaultEnforcedProfile();
            channel.getServerConfigSynchronizer().syncDefaultEnforcedConfigProfileSwitch(enforcedProfile);
        });
    }
}

