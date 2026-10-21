/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.profile;

import java.util.Map;
import java.util.Set;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.IConfigProfileInfo;

public class ConfigProfile
extends Config
implements IConfigProfileInfo {
    private final String id;

    private ConfigProfile(String id, Map<ConfigOption<?>, Object> values, boolean allowNullValues, Set<ConfigOption<?>> dirtyOptions, Set<ConfigOption<?>> removedLargeOptions, Map<ConfigOption<?>, String> failedEncodedValues, ConfigOptionManager options) {
        super(values, allowNullValues, dirtyOptions, removedLargeOptions, failedEncodedValues, options);
        this.id = id;
    }

    @Override
    public <T> T set(ConfigOption<T> option, T value) {
        if (option == BuiltInProfiledConfigOptions.PROFILE_NAME && value == null) {
            value = option.getDefaultValue();
        }
        return super.set(option, value);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.get(BuiltInProfiledConfigOptions.PROFILE_NAME);
    }

    public static final class Builder
    extends Config.Builder<ConfigProfile, Builder> {
        private String id;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setId(null);
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        @Override
        public ConfigProfile build() {
            if (this.id == null) {
                throw new IllegalStateException();
            }
            return (ConfigProfile)super.build();
        }

        @Override
        protected ConfigProfile buildInternally(Map<ConfigOption<?>, Object> values, boolean allowNullValues, Set<ConfigOption<?>> dirtyOptions, Set<ConfigOption<?>> removedLargeOptions, Map<ConfigOption<?>, String> failedSerializedValues) {
            if (!values.containsKey(BuiltInProfiledConfigOptions.PROFILE_NAME)) {
                values.put(BuiltInProfiledConfigOptions.PROFILE_NAME, BuiltInProfiledConfigOptions.PROFILE_NAME.getDefaultValue());
            }
            return new ConfigProfile(this.id, values, allowNullValues, dirtyOptions, removedLargeOptions, failedSerializedValues, this.options);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

