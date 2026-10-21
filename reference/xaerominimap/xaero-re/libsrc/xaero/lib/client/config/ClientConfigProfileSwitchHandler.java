/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config;

import java.util.Objects;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;

public final class ClientConfigProfileSwitchHandler {
    private final ConfigOptionManager options;

    private ClientConfigProfileSwitchHandler(ConfigOptionManager options) {
        this.options = options;
    }

    public void handleSwitch(ConfigProfile from, ConfigProfile to, ClientConfigManager manager) {
        ClientConfigChangeListener listener = manager.getChangeListener();
        for (ConfigOption<?> option : this.options) {
            if (Objects.equals(manager.getEffective(from, option), manager.getEffective(to, option))) continue;
            listener.onChange(to, option);
        }
    }

    public static final class Builder {
        private ConfigOptionManager options;

        private Builder() {
        }

        public Builder setDefault() {
            this.setOptions(null);
            return this;
        }

        public Builder setOptions(ConfigOptionManager options) {
            this.options = options;
            return this;
        }

        public ClientConfigProfileSwitchHandler build() {
            if (this.options == null) {
                throw new IllegalStateException();
            }
            return new ClientConfigProfileSwitchHandler(this.options);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

