/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui;

import java.util.HashMap;
import java.util.Map;
import xaero.lib.client.config.option.ui.type.ConfigOptionUIType;
import xaero.lib.common.config.option.ConfigOption;

public class ConfigOptionUITypeManager {
    private final Map<ConfigOption<?>, ConfigOptionUIType<?>> uiTypes;

    private ConfigOptionUITypeManager(Map<ConfigOption<?>, ConfigOptionUIType<?>> uiTypes) {
        this.uiTypes = uiTypes;
    }

    public <CT extends ConfigOption<?>> void registerUIType(CT option, ConfigOptionUIType<? super CT> uiType) {
        if (this.uiTypes.containsKey(option)) {
            throw new IllegalArgumentException("Duplicate config option UI type register!");
        }
        this.uiTypes.put(option, uiType);
    }

    public <CT extends ConfigOption<?>> ConfigOptionUIType<? super CT> getUIType(CT option) {
        return this.uiTypes.get(option);
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public ConfigOptionUITypeManager build() {
            return new ConfigOptionUITypeManager(new HashMap());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

