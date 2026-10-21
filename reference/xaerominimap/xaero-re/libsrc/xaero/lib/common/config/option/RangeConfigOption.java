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

public class RangeConfigOption
extends IndexedConfigOption<Integer> {
    private RangeConfigOption(ConfigValueType<Integer> valueType, String id, Integer defaultValue, boolean shouldSaveDefaultValue, boolean overridable, boolean skipConfigReset, Predicate<Integer> validator, Component displayName, Component tooltip, BiFunction<ConfigOption<Integer>, Integer, Component> labelGetter, List<Integer> validIndices) {
        super(valueType, id, defaultValue, shouldSaveDefaultValue, overridable, skipConfigReset, validator, displayName, tooltip, labelGetter, validIndices);
    }

    public static final class Builder
    extends IndexedConfigOption.Builder<Integer, Builder> {
        private boolean rangeValidator;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            Builder result = (Builder)super.setDefault();
            this.setValueType(BuiltInConfigValueTypes.INTEGER);
            this.setRangeValidator(true);
            return result;
        }

        @Override
        public Builder setIndexValueGetter(IntFunction<Integer> indexValueGetter) {
            if (indexValueGetter != null) {
                throw new IllegalArgumentException("Cannot set a custom index value getter for range config options!");
            }
            return (Builder)super.setIndexValueGetter(indexValueGetter);
        }

        public Builder setRangeValidator(boolean rangeValidator) {
            this.rangeValidator = rangeValidator;
            return (Builder)this.self;
        }

        public RangeConfigOption build(List<ConfigOption<?>> destination) {
            super.setIndexValueGetter((int index) -> index);
            if (this.rangeValidator) {
                int finalMinIndex = this.minIndex;
                int finalMaxIndex = this.maxIndex;
                Predicate<Integer> rangeTest = d -> d >= finalMinIndex && d <= finalMaxIndex;
                Predicate baseValidator = this.validator;
                if (baseValidator == null) {
                    this.setValidator(rangeTest);
                } else {
                    this.setValidator(d -> baseValidator.test(d) && rangeTest.test((Integer)d));
                }
            }
            return (RangeConfigOption)super.build((List)destination);
        }

        protected RangeConfigOption buildInternally(List<Integer> validValues) {
            return new RangeConfigOption(this.valueType, this.id, (Integer)this.defaultValue, this.shouldSaveDefaultValue, this.overridable, this.skipConfigReset, this.validator, this.displayName, this.tooltip, this.displayGetter, validValues);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

