/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.io.serialization.cfg;

import xaero.lib.common.config.Config;
import xaero.lib.common.config.io.serialization.cfg.AConfigCfgSerializer;
import xaero.lib.common.config.option.ConfigOptionManager;

public final class ConfigCfgSerializer
extends AConfigCfgSerializer<Config> {
    private ConfigCfgSerializer(ConfigOptionManager options, boolean configsTrackDirtyOptions) {
        super(options, configsTrackDirtyOptions);
    }

    @Override
    protected Config.Builder<Config, ?> createConfigBuilder(String debugId) {
        return Config.FinalBuilder.begin();
    }

    public static final class Builder
    extends AConfigCfgSerializer.Builder<Config, Builder> {
        private Builder() {
        }

        protected ConfigCfgSerializer buildInternally() {
            return new ConfigCfgSerializer(this.options, this.configsTrackDirtyOptions);
        }

        public static Builder begin() {
            return (Builder)new Builder().setDefault();
        }
    }
}

