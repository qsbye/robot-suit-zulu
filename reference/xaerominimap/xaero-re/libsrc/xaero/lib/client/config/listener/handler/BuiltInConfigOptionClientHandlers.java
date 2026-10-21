/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.listener.handler;

import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.client.config.sync.SyncedConfigManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;

public class BuiltInConfigOptionClientHandlers {
    private static boolean IGNORE_SERVER_ENFORCEMENT_RECURSION;

    private static void handleIgnoreServerEnforcement(ClientConfigChangeListener listener, Config config) {
        if (IGNORE_SERVER_ENFORCEMENT_RECURSION) {
            return;
        }
        ClientConfigManager manager = listener.getManager();
        ConfigChannel channel = manager.getChannel();
        SyncedConfigManager syncedManager = manager.getServerSynced();
        for (ConfigOption<?> option : channel.getConfigOptionManager()) {
            if (option == BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR || ((Config)syncedManager.getConfig()).get(option) == null) continue;
            IGNORE_SERVER_ENFORCEMENT_RECURSION = true;
            listener.onChange((Config)syncedManager.getConfig(), option);
            IGNORE_SERVER_ENFORCEMENT_RECURSION = false;
        }
    }

    public static void registerAll(ClientConfigChangeListener listener) {
        listener.register(BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR, config -> BuiltInConfigOptionClientHandlers.handleIgnoreServerEnforcement(listener, config));
    }
}

