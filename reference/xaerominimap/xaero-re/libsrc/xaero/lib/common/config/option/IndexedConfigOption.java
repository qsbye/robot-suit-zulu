/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.type.ConfigValueType;

public class IndexedConfigOption<T>
extends ConfigOption<T> {
    private final List<T> validValues;

    protected IndexedConfigOption(ConfigValueType<T> valueType, String id, T defaultValue, boolean shouldSaveDefaultValue, boolean overridable, boolean skipConfigReset, Predicate<T> validator, Component displayName, Component tooltip, BiFunction<ConfigOption<T>, T, Component> labelGetter, List<T> validValues) {
        super(valueType, id, defaultValue, shouldSaveDefaultValue, overridable, skipConfigReset, validator, displayName, tooltip, labelGetter);
        this.validValues = validValues;
    }

    public List<T> getValidValues() {
        return this.validValues;
    }

    public static final class FinalBuilder<T>
    extends Builder<T, FinalBuilder<T>> {
        private FinalBuilder() {
        }

        public static <T> FinalBuilder<T> begin() {
            return (FinalBuilder)new FinalBuilder<T>().setDefault();
        }

        @Override
        protected IndexedConfigOption<T> buildInternally(List<T> validValues) {
            return new IndexedConfigOption<Object>(this.valueType, this.id, this.defaultValue, this.shouldSaveDefaultValue, this.overridable, this.skipConfigReset, this.validator, this.displayName, this.tooltip, this.displayGetter, validValues);
        }
    }

    public static abstract class Builder<T, B extends Builder<T, B>>
    extends ConfigOption.Builder<T, B> {
        protected int minIndex;
        protected int maxIndex;
        protected IntFunction<T> indexValueGetter;

        protected Builder() {
        }

        @Override
        public B setDefault() {
            this.setMaxIndex(0);
            this.setMinIndex(0);
            this.setIndexValueGetter(null);
            return (B)((Builder)super.setDefault());
        }

        public B setMinIndex(int minIndex) {
            this.minIndex = minIndex;
            return (B)((Builder)this.self);
        }

        public B setMaxIndex(int maxIndex) {
            this.maxIndex = maxIndex;
            return (B)((Builder)this.self);
        }

        public B setIndexValueGetter(IntFunction<T> indexValueGetter) {
            this.indexValueGetter = indexValueGetter;
            return (B)((Builder)this.self);
        }

        @Override
        public IndexedConfigOption<T> build(List<ConfigOption<?>> destination) {
            if (this.indexValueGetter == null) {
                throw new IllegalStateException();
            }
            return (IndexedConfigOption)super.build(destination);
        }

        @Override
        protected final IndexedConfigOption<T> buildInternally() {
            List validValues = IntStream.rangeClosed(this.minIndex, this.maxIndex).mapToObj(this.indexValueGetter).toList();
            if (this.validator == null) {
                HashSet validValueSet = new HashSet(validValues);
                this.setValidator(validValueSet::contains);
            }
            return this.buildInternally(validValues);
        }

        protected abstract IndexedConfigOption<T> buildInternally(List<T> var1);
    }
}

