/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.profile.io.serialization.cfg;

import java.nio.file.Path;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.io.serialization.cfg.AConfigCfgSerializer;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;

public final class ConfigProfileCfgSerializer
extends AConfigCfgSerializer<ConfigProfile> {
    private final String configType;

    private ConfigProfileCfgSerializer(ConfigOptionManager options, boolean configsTrackDirtyOptions, String configType) {
        super(options, configsTrackDirtyOptions);
        this.configType = configType;
    }

    @Override
    public String serialize(ConfigProfile profile, Path file) {
        StringBuilder builder = new StringBuilder();
        builder.append("############").append('\n');
        builder.append("## ").append(this.configType).append(" Profile \"").append(profile.getId()).append('\"').append('\n');
        builder.append("##").append('\n');
        builder.append('\n');
        builder.append(super.serialize(profile, file));
        return builder.toString();
    }

    @Override
    protected Config.Builder<ConfigProfile, ?> createConfigBuilder(String debugId) {
        return ConfigProfile.Builder.begin().setId(debugId);
    }

    public static final class Builder
    extends AConfigCfgSerializer.Builder<ConfigProfile, Builder> {
        private String configType;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            this.setConfigType(null);
            return (Builder)super.setDefault();
        }

        public Builder setConfigType(String configType) {
            this.configType = configType;
            return this;
        }

        public ConfigProfileCfgSerializer build() {
            if (this.configType == null) {
                throw new IllegalStateException();
            }
            return (ConfigProfileCfgSerializer)super.build();
        }

        protected ConfigProfileCfgSerializer buildInternally() {
            return new ConfigProfileCfgSerializer(this.options, this.configsTrackDirtyOptions, this.configType);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

