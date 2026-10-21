/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import xaero.lib.common.config.listener.IConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.type.LargeConfigValueType;

public class Config {
    private final Map<ConfigOption<?>, Object> values;
    private final boolean allowNullValues;
    private IConfigChangeListener changeListener;
    private final Set<ConfigOption<?>> dirtyOptions;
    private final Set<ConfigOption<?>> removedLargeOptions;
    private final Map<ConfigOption<?>, String> failedSerializedValues;
    protected final ConfigOptionManager options;

    protected Config(Map<ConfigOption<?>, Object> values, boolean allowNullValues, Set<ConfigOption<?>> dirtyOptions, Set<ConfigOption<?>> removedLargeOptions, Map<ConfigOption<?>, String> failedSerializedValues, ConfigOptionManager options) {
        this.values = values;
        this.allowNullValues = allowNullValues;
        this.dirtyOptions = dirtyOptions;
        this.removedLargeOptions = removedLargeOptions;
        this.failedSerializedValues = failedSerializedValues;
        this.options = options;
    }

    public <T> T get(ConfigOption<T> option) {
        String failedSerializedValue;
        if (this.allowNullValues && (failedSerializedValue = this.getFailedSerializedValue(option)) != null) {
            return null;
        }
        Object value = this.values.get(option);
        return (T)value;
    }

    public <T> T set(ConfigOption<T> option, T value) {
        if (!this.allowNullValues && value == null) {
            throw new IllegalArgumentException("Null values not allowed in this config!");
        }
        if (value != null && !option.isValidValue(value)) {
            throw new IllegalArgumentException("The provided value is not valid for option " + option.getId());
        }
        this.failedSerializedValues.remove(option);
        Object previousValue = value != null ? this.values.put(option, value) : this.values.remove(option);
        if (Objects.equals(previousValue, value)) {
            return (T)previousValue;
        }
        if (value == null) {
            this.handleValueRemoval(option);
        }
        if (previousValue == null) {
            this.handleValueAddition(option);
        }
        if (this.dirtyOptions != null) {
            this.dirtyOptions.add(option);
        }
        if (this.changeListener != null) {
            this.changeListener.onChange(this, option);
        }
        return (T)previousValue;
    }

    public void reset() {
        this.clearDirtyOptions();
        this.failedSerializedValues.clear();
        if (!this.allowNullValues) {
            for (ConfigOption<?> option : this.values.keySet()) {
                this.setOptionDefault(option);
            }
            return;
        }
        for (ConfigOption<?> option : new HashSet(this.values.keySet())) {
            this.set(option, null);
        }
    }

    public Iterable<ConfigOption<?>> usedOptions() {
        return this.values.keySet();
    }

    public Iterable<ConfigOption<?>> getDirtyOptions() {
        return this.dirtyOptions;
    }

    public int getChangeCount() {
        return this.dirtyOptions == null ? 0 : this.dirtyOptions.size();
    }

    public void clearDirtyOptions() {
        if (this.dirtyOptions != null) {
            this.dirtyOptions.clear();
        }
    }

    public void setChangeListener(IConfigChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public IConfigChangeListener getChangeListener() {
        return this.changeListener;
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    private <T> void copyOptionValue(ConfigOption<T> option, Object value, Config config) {
        Object valueCast = value;
        config.set(option, valueCast);
    }

    public <T> void addFailedSerializedValue(ConfigOption<T> option, String serializedValue) {
        this.set(option, option.getDefaultValue());
        this.failedSerializedValues.put(option, serializedValue);
    }

    public String getFailedSerializedValue(ConfigOption<?> option) {
        return this.failedSerializedValues.get(option);
    }

    private <T> void handleValueRemoval(ConfigOption<T> option) {
        if (!(option.getValueType() instanceof LargeConfigValueType)) {
            return;
        }
        this.removedLargeOptions.add(option);
    }

    private <T> void handleValueAddition(ConfigOption<T> option) {
        if (!(option.getValueType() instanceof LargeConfigValueType)) {
            return;
        }
        this.removedLargeOptions.remove(option);
    }

    public Iterable<ConfigOption<?>> getRemovedLargeOptions() {
        return this.removedLargeOptions;
    }

    public void postSave() {
        this.removedLargeOptions.clear();
    }

    public void setDefaults() {
        if (this.options == null) {
            throw new UnsupportedOperationException();
        }
        for (ConfigOption<?> option : this.options) {
            if (option.skipConfigReset()) continue;
            this.setOptionDefault(option);
        }
    }

    public <T> void setOptionDefault(ConfigOption<T> option) {
        this.set(option, this.allowNullValues ? null : (T)option.getDefaultValue());
    }

    public void copyOptionsFrom(Config other) {
        if (this.options == null) {
            throw new UnsupportedOperationException();
        }
        for (ConfigOption<?> option : this.options) {
            this.copyOptionFrom(option, other);
        }
    }

    public <T> void copyOptionFrom(ConfigOption<T> option, Config other) {
        this.set(option, other.get(option));
    }

    public static final class FinalBuilder
    extends Builder<Config, FinalBuilder> {
        private FinalBuilder() {
        }

        @Override
        protected Config buildInternally(Map<ConfigOption<?>, Object> values, boolean allowNullValues, Set<ConfigOption<?>> dirtyOptions, Set<ConfigOption<?>> removedLargeOptions, Map<ConfigOption<?>, String> failedSerializedValues) {
            return new Config(values, allowNullValues, dirtyOptions, removedLargeOptions, failedSerializedValues, this.options);
        }

        public static FinalBuilder begin() {
            return (FinalBuilder)new FinalBuilder().setDefault();
        }
    }

    public static abstract class Builder<C extends Config, B extends Builder<C, B>> {
        protected final B self = this;
        private boolean allowNullValues;
        private boolean trackDirtyOptions;
        protected ConfigOptionManager options;

        protected Builder() {
        }

        public B setDefault() {
            this.setAllowNullValues(false);
            this.setOptions(null);
            this.setTrackDirtyOptions(false);
            return this.self;
        }

        public B setAllowNullValues(boolean allowNullValues) {
            this.allowNullValues = allowNullValues;
            return this.self;
        }

        public B setOptions(ConfigOptionManager options) {
            this.options = options;
            return this.self;
        }

        public B setTrackDirtyOptions(boolean trackDirtyOptions) {
            this.trackDirtyOptions = trackDirtyOptions;
            return this.self;
        }

        public C build() {
            if (!this.allowNullValues && this.options == null) {
                throw new IllegalStateException();
            }
            LinkedHashMap values = new LinkedHashMap();
            if (!this.allowNullValues) {
                for (ConfigOption<?> option : this.options) {
                    values.put(option, option.getDefaultValue());
                }
            }
            HashSet dirtyOptions = this.trackDirtyOptions ? new HashSet() : null;
            HashSet removedLargeOptions = new HashSet();
            return this.buildInternally(values, this.allowNullValues, dirtyOptions, removedLargeOptions, new HashMap());
        }

        protected abstract C buildInternally(Map<ConfigOption<?>, Object> var1, boolean var2, Set<ConfigOption<?>> var3, Set<ConfigOption<?>> var4, Map<ConfigOption<?>, String> var5);
    }
}

