/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;
import xaero.lib.common.config.option.value.type.BuiltInConfigValueTypes;
import xaero.lib.common.config.option.value.type.ConfigValueType;
import xaero.lib.common.config.util.ConfigUtils;

public class BooleanConfigOption
extends IndexedConfigOption<Boolean> {
    private BooleanConfigOption(ConfigValueType<Boolean> valueType, String id, Boolean defaultValue, boolean shouldSaveDefaultValue, boolean overridable, boolean skipConfigReset, Predicate<Boolean> validator, Component displayName, Component tooltip, BiFunction<ConfigOption<Boolean>, Boolean, Component> labelGetter, List<Boolean> validValues) {
        super(valueType, id, defaultValue, shouldSaveDefaultValue, overridable, skipConfigReset, validator, displayName, tooltip, labelGetter, validValues);
    }

    public static final class Builder
    extends IndexedConfigOption.Builder<Boolean, Builder> {
        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setDisplayGetter(ConfigUtils::getDisplayForBoolean);
            this.setValueType(BuiltInConfigValueTypes.BOOLEAN);
            return (Builder)this.self;
        }

        @Override
        public Builder setMinIndex(int minIndex) {
            if (minIndex != 0) {
                throw new IllegalArgumentException("Cannot set a custom min index for boolean config options!");
            }
            return (Builder)super.setMinIndex(minIndex);
        }

        @Override
        public Builder setMaxIndex(int maxIndex) {
            if (maxIndex != 0) {
                throw new IllegalArgumentException("Cannot set a custom max index for boolean config options!");
            }
            return (Builder)super.setMaxIndex(maxIndex);
        }

        @Override
        public Builder setIndexValueGetter(IntFunction<Boolean> indexValueGetter) {
            if (indexValueGetter != null) {
                throw new IllegalArgumentException("Cannot set a custom index value getter for boolean config options!");
            }
            return (Builder)super.setIndexValueGetter(indexValueGetter);
        }

        public BooleanConfigOption build(List<ConfigOption<?>> destination) {
            super.setMinIndex(0);
            super.setMaxIndex(1);
            super.setIndexValueGetter((int index) -> index > 0);
            return (BooleanConfigOption)super.build((List)destination);
        }

        protected BooleanConfigOption buildInternally(List<Boolean> validValues) {
            return new BooleanConfigOption(this.valueType, this.id, (Boolean)this.defaultValue, this.shouldSaveDefaultValue, this.overridable, this.skipConfigReset, this.validator, this.displayName, this.tooltip, this.displayGetter, validValues);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

