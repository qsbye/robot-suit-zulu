/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.server.listener;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.listener.handler.HandlerBasedConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.server.ServerConfigManager;
import xaero.lib.common.config.server.sync.ServerConfigChannelSynchronizer;

public final class ServerConfigChangeListener
extends HandlerBasedConfigChangeListener {
    private final ServerConfigManager manager;
    private ServerConfigChannelSynchronizer synchronizer;

    private ServerConfigChangeListener(Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers, ConfigOptionManager options, ServerConfigManager manager) {
        super(changeHandlers, options);
        this.manager = manager;
    }

    @Override
    public void onChange(Config config, ConfigOption<?> option) {
        if (!this.enabled) {
            return;
        }
        super.onChange(config, option);
        if (!(config instanceof ConfigProfile)) {
            return;
        }
        ConfigProfile profile = (ConfigProfile)config;
        if (this.manager.getProfileManager().get(profile.getId()) != profile) {
            return;
        }
        this.synchronizer.syncOptionValueChange(profile, option);
    }

    @Override
    public void onRemoved(Config config) {
        if (!this.enabled) {
            return;
        }
        super.onRemoved(config);
        if (!(config instanceof ConfigProfile)) {
            return;
        }
        ConfigProfile profile = (ConfigProfile)config;
        this.synchronizer.syncProfileDeletion(profile);
    }

    public void setSynchronizer(ServerConfigChannelSynchronizer synchronizer) {
        if (this.synchronizer != null) {
            throw new IllegalStateException();
        }
        this.synchronizer = synchronizer;
    }

    public static final class Builder
    extends HandlerBasedConfigChangeListener.Builder<Builder> {
        private ServerConfigManager manager;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setManager(null);
            return (Builder)super.setDefault();
        }

        public Builder setManager(ServerConfigManager manager) {
            this.manager = manager;
            return this;
        }

        @Override
        public ServerConfigChangeListener build() {
            if (this.manager == null) {
                throw new IllegalStateException();
            }
            return (ServerConfigChangeListener)super.build();
        }

        @Override
        protected HandlerBasedConfigChangeListener buildInternally(Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers) {
            return new ServerConfigChangeListener(changeHandlers, this.options, this.manager);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

