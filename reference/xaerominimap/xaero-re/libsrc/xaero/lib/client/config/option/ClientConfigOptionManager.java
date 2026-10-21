/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option;

import java.util.Map;
import org.apache.logging.log4j.Logger;
import xaero.lib.client.config.option.ui.ConfigOptionUITypeManager;
import xaero.lib.client.config.option.ui.type.ConfigOptionUIType;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

public final class ClientConfigOptionManager
extends ConfigOptionManager {
    private final ConfigOptionUITypeManager uiTypeManager;

    private ClientConfigOptionManager(Logger logger, Map<String, ConfigOption<?>> options, ConfigOptionUITypeManager uiTypeManager) {
        super(logger, options);
        this.uiTypeManager = uiTypeManager;
    }

    public <CT extends ConfigOption<?>> void registerUIType(CT option, ConfigOptionUIType<? super CT> uiType) {
        this.uiTypeManager.registerUIType(option, uiType);
    }

    public <CT extends ConfigOption<?>> ConfigOptionUIType<? super CT> getUIType(CT option) {
        return this.uiTypeManager.getUIType(option);
    }

    public ConfigOptionUITypeManager getUiTypeManager() {
        return this.uiTypeManager;
    }

    public static final class Builder
    extends ConfigOptionManager.Builder<Builder> {
        private ConfigOptionUITypeManager uiTypeManager;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setUiTypeManager(null);
            return this;
        }

        public Builder setUiTypeManager(ConfigOptionUITypeManager uiTypeManager) {
            this.uiTypeManager = uiTypeManager;
            return this;
        }

        @Override
        public ClientConfigOptionManager build() {
            if (this.uiTypeManager == null) {
                throw new IllegalStateException();
            }
            return (ClientConfigOptionManager)super.build();
        }

        @Override
        protected ClientConfigOptionManager buildInternally(Map<String, ConfigOption<?>> options) {
            return new ClientConfigOptionManager(this.logger, options, this.uiTypeManager);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

