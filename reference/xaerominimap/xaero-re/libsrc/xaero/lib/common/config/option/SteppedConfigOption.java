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
import xaero.lib.common.util.MathUtils;

public final class SteppedConfigOption
extends IndexedConfigOption<Double> {
    private final int precision;

    private SteppedConfigOption(ConfigValueType<Double> valueType, String id, Double defaultValue, boolean shouldSaveDefaultValue, boolean overridable, boolean skipConfigReset, Predicate<Double> validator, Component displayName, Component tooltip, BiFunction<ConfigOption<Double>, Double, Component> labelGetter, List<Double> validValues, int precision) {
        super(valueType, id, defaultValue, shouldSaveDefaultValue, overridable, skipConfigReset, validator, displayName, tooltip, labelGetter, validValues);
        this.precision = precision;
    }

    public int getPrecision() {
        return this.precision;
    }

    public static final class Builder
    extends IndexedConfigOption.Builder<Double, Builder> {
        private double minValue;
        private double maxValue;
        private double step;
        private int precision;
        private boolean rangeValidator;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setMinValue(0.0);
            this.setMaxValue(0.0);
            this.setStep(0.0);
            this.setRangeValidator(false);
            this.setValueType(BuiltInConfigValueTypes.DOUBLE);
            this.setDisplayGetter(ConfigUtils::getDisplayForSimpleNumber);
            return (Builder)this.self;
        }

        public Builder setMinValue(double minValue) {
            this.minValue = minValue;
            return (Builder)this.self;
        }

        public Builder setMaxValue(double maxValue) {
            this.maxValue = maxValue;
            return (Builder)this.self;
        }

        public Builder setStep(double step) {
            this.step = step;
            return (Builder)this.self;
        }

        public Builder setRangeValidator(boolean rangeValidator) {
            this.rangeValidator = rangeValidator;
            return (Builder)this.self;
        }

        @Override
        public Builder setMinIndex(int minIndex) {
            if (minIndex != 0) {
                throw new IllegalArgumentException("Cannot set a custom min index for stepped config options!");
            }
            return (Builder)super.setMinIndex(minIndex);
        }

        @Override
        public Builder setMaxIndex(int maxIndex) {
            if (maxIndex != 0) {
                throw new IllegalArgumentException("Cannot set a custom max index for stepped config options!");
            }
            return (Builder)super.setMaxIndex(maxIndex);
        }

        @Override
        public Builder setIndexValueGetter(IntFunction<Double> indexValueGetter) {
            if (indexValueGetter != null) {
                throw new IllegalArgumentException("Cannot set a custom index value getter for stepped config options!");
            }
            return (Builder)super.setIndexValueGetter(indexValueGetter);
        }

        public SteppedConfigOption build(List<ConfigOption<?>> destination) {
            double finalMaxValue = this.maxValue;
            double finalMinValue = this.minValue;
            double finalStep = this.step;
            int stepPrecision = MathUtils.getDecimalCountAfterDot(finalStep, 5);
            int minValuePrecision = MathUtils.getDecimalCountAfterDot(finalMinValue, 5);
            int maxValuePrecision = MathUtils.getDecimalCountAfterDot(finalMaxValue, 5);
            int finalPrecision = this.precision = Math.max(Math.max(stepPrecision, minValuePrecision), maxValuePrecision);
            super.setMinIndex(0);
            super.setMaxIndex((int)Math.ceil((finalMaxValue - finalMinValue) / finalStep));
            super.setIndexValueGetter((int index) -> MathUtils.floor(Math.min(finalMinValue + (double)index * finalStep, finalMaxValue), finalPrecision));
            if (this.rangeValidator) {
                Predicate<Double> rangeTest = d -> d >= finalMinValue && d <= finalMaxValue;
                Predicate baseValidator = this.validator;
                if (baseValidator == null) {
                    this.setValidator(rangeTest);
                } else {
                    this.setValidator(d -> baseValidator.test(d) && rangeTest.test((Double)d));
                }
            }
            return (SteppedConfigOption)super.build((List)destination);
        }

        protected SteppedConfigOption buildInternally(List<Double> validValues) {
            return new SteppedConfigOption(this.valueType, this.id, (Double)this.defaultValue, this.shouldSaveDefaultValue, this.overridable, this.skipConfigReset, this.validator, this.displayName, this.tooltip, this.displayGetter, validValues, this.precision);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

