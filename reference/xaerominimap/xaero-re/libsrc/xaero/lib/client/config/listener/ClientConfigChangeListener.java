/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.listener;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.listener.handler.HandlerBasedConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

public final class ClientConfigChangeListener
extends HandlerBasedConfigChangeListener {
    private final ClientConfigManager manager;

    private ClientConfigChangeListener(ClientConfigManager manager, Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers, ConfigOptionManager options) {
        super(changeHandlers, options);
        this.manager = manager;
    }

    public ClientConfigManager getManager() {
        return this.manager;
    }

    @Override
    public void onChange(Config config, ConfigOption<?> option) {
        if (!this.enabled) {
            return;
        }
        Config primaryClientConfig = this.manager.getPrimaryConfigManager().getConfig();
        if (config == primaryClientConfig) {
            super.onChange(config, option);
            return;
        }
        if (config == this.manager.getRedirectorManager().getCacheConfig()) {
            super.onChange(config, option);
            return;
        }
        if (this.manager.getRedirectorManager().shouldRedirect(option)) {
            return;
        }
        Object serverSyncedConfig = this.manager.getServerSynced().getConfig();
        if (config == serverSyncedConfig) {
            super.onChange(config, option);
            return;
        }
        if (option.isOverridable() && ((Config)serverSyncedConfig).get(option) != null) {
            return;
        }
        if (config != this.manager.getCurrentProfile()) {
            return;
        }
        super.onChange(config, option);
    }

    public static final class Builder
    extends HandlerBasedConfigChangeListener.Builder<Builder> {
        private ClientConfigManager manager;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setManager(null);
            return (Builder)super.setDefault();
        }

        public Builder setManager(ClientConfigManager manager) {
            this.manager = manager;
            return this;
        }

        @Override
        public ClientConfigChangeListener build() {
            if (this.manager == null) {
                throw new IllegalStateException();
            }
            return (ClientConfigChangeListener)super.build();
        }

        @Override
        protected HandlerBasedConfigChangeListener buildInternally(Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers) {
            return new ClientConfigChangeListener(this.manager, changeHandlers, this.options);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

