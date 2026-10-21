/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.io.serialization.cfg;

import java.nio.file.Path;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.io.serialization.IConfigSerializer;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

public abstract class AConfigCfgSerializer<C extends Config>
implements IConfigSerializer<C> {
    public static final String RECOMMENDED_EXTENSION = ".cfg";
    protected final ConfigOptionManager options;
    protected final boolean configsTrackDirtyOptions;

    protected AConfigCfgSerializer(ConfigOptionManager options, boolean configsTrackDirtyOptions) {
        this.options = options;
        this.configsTrackDirtyOptions = configsTrackDirtyOptions;
    }

    @Override
    public String serialize(C config, Path file) {
        StringBuilder builder = new StringBuilder();
        for (ConfigOption<?> option : ((Config)config).usedOptions()) {
            this.addOptionLine(option, config, builder, file);
        }
        return builder.toString();
    }

    private <T> void addOptionLine(ConfigOption<T> option, C config, StringBuilder builder, Path file) {
        String serializedValue = ((Config)config).getFailedSerializedValue(option);
        if (serializedValue == null) {
            T value = ((Config)config).get(option);
            if (!option.shouldSaveDefaultValue() && value == option.getDefaultValue()) {
                return;
            }
            serializedValue = option.getValueType().getIoCodec().encode(value, file, option);
        }
        builder.append(option.getId()).append(" = ").append(serializedValue).append('\n');
    }

    @Override
    public C deserialize(String serializedConfig, boolean allowNullValues, String debugId, Path file) {
        Object config = ((Config.Builder)((Config.Builder)((Config.Builder)this.createConfigBuilder(debugId).setOptions(this.options)).setAllowNullValues(allowNullValues)).setTrackDirtyOptions(this.configsTrackDirtyOptions)).build();
        serializedConfig.lines().forEach(line -> {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("#")) {
                return;
            }
            int equalsIndex = trimmedLine.indexOf("=");
            if (equalsIndex == -1) {
                return;
            }
            String optionId = trimmedLine.substring(0, equalsIndex).trim();
            ConfigOption<?> option = this.options.get(optionId);
            if (option == null) {
                this.options.logger.warn("Unknown option id {} in config {}! Skipping.", (Object)optionId, (Object)debugId);
                return;
            }
            String serializedValue = trimmedLine.substring(equalsIndex + 1).trim();
            this.setOptionValue(option, serializedValue, (Config)config, debugId, file);
        });
        return config;
    }

    protected abstract Config.Builder<C, ?> createConfigBuilder(String var1);

    private <T> void setOptionValue(ConfigOption<T> option, String serializedValue, Config config, String debugId, Path file) {
        try {
            T value = option.getValueType().getIoCodec().decode(serializedValue, file, option);
            if (value == null) {
                this.options.logger.warn("Unknown value in config {} for option {}! Skipping.", (Object)debugId, (Object)option.getId());
                config.addFailedSerializedValue(option, serializedValue);
                return;
            }
            config.set(option, value);
        }
        catch (Throwable t) {
            this.options.logger.warn("Error parsing config option value in config {}! {} = {} Message: {}", (Object)debugId, (Object)option.getId(), (Object)serializedValue, (Object)t.getMessage());
            config.addFailedSerializedValue(option, serializedValue);
        }
    }

    protected static abstract class Builder<C extends Config, B extends Builder<C, B>> {
        protected final B self = this;
        protected ConfigOptionManager options;
        protected boolean configsTrackDirtyOptions;

        protected Builder() {
        }

        public B setDefault() {
            this.setOptions(null);
            this.setConfigsTrackDirtyOptions(false);
            return this.self;
        }

        public B setOptions(ConfigOptionManager options) {
            this.options = options;
            return this.self;
        }

        public B setConfigsTrackDirtyOptions(boolean configsTrackDirtyOptions) {
            this.configsTrackDirtyOptions = configsTrackDirtyOptions;
            return this.self;
        }

        public AConfigCfgSerializer<C> build() {
            if (this.options == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally();
        }

        protected abstract AConfigCfgSerializer<C> buildInternally();
    }
}

