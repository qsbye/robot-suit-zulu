/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.range;

import com.google.common.base.Objects;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.options.EditorExpandingOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorExpandingRangeNode<V>
extends EditorExpandingOptionsNode<Integer> {
    private V currentRangeValue;
    private final IntFunction<V> numberReader;

    protected EditorExpandingRangeNode(@Nonnull Component displayName, V currentRangeValue, @Nonnull IntFunction<V> numberReader, @Nonnull EditorOptionNode<Integer> currentValue, @Nonnull List<EditorOptionNode<Integer>> options, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(displayName, currentValue, options, movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.currentRangeValue = currentRangeValue;
        this.numberReader = numberReader;
    }

    @Override
    public boolean onSelected(EditorOptionNode<Integer> option) {
        Object selectedValue;
        Object v0 = selectedValue = option.getValue() == null ? null : this.numberReader.apply(option.getValue());
        if (this.currentRangeValue != selectedValue && !Objects.equal(this.currentRangeValue, selectedValue)) {
            this.currentRangeValue = selectedValue;
        }
        return super.onSelected(option);
    }

    public V getCurrentRangeValue() {
        return this.currentRangeValue;
    }

    public static final class FinalBuilder<V>
    extends Builder<V, FinalBuilder<V>> {
        private FinalBuilder(ListFactory listFactory) {
            super(listFactory);
        }

        @Override
        protected EditorExpandingRangeNode<V> buildInternally(EditorOptionNode<Integer> currentValueData, List<EditorOptionNode<Integer>> options) {
            return new EditorExpandingRangeNode<Object>(this.displayName, this.currentRangeValue, this.numberReader, currentValueData, options, this.movable, this.listEntryFactory, this.tooltipSupplier, this.isActiveSupplier);
        }

        public static <V> FinalBuilder<V> begin(ListFactory listFactory) {
            return (FinalBuilder)new FinalBuilder<V>(listFactory).setDefault();
        }
    }

    public static abstract class Builder<V, B extends Builder<V, B>>
    extends EditorExpandingOptionsNode.Builder<Integer, B> {
        protected V currentRangeValue;
        protected int minNumber;
        protected int maxNumber;
        protected IntFunction<V> numberReader;
        protected Function<V, Integer> numberWriter;
        protected Function<V, Component> valueNamer;
        protected boolean hasNullOption;

        protected Builder(ListFactory listFactory) {
            super(listFactory);
        }

        @Override
        public B setDefault() {
            this.setCurrentRangeValue(null);
            this.setMinNumber(0);
            this.setMaxNumber(0);
            this.setNumberReader(null);
            this.setNumberWriter(null);
            this.setValueNamer(null);
            this.setHasNullOption(false);
            return (B)((Builder)super.setDefault());
        }

        public B setCurrentRangeValue(V currentRangeValue) {
            this.currentRangeValue = currentRangeValue;
            return (B)((Builder)this.self);
        }

        public B setMinNumber(int minNumber) {
            this.minNumber = minNumber;
            return (B)((Builder)this.self);
        }

        public B setMaxNumber(int maxNumber) {
            this.maxNumber = maxNumber;
            return (B)((Builder)this.self);
        }

        public B setNumberReader(IntFunction<V> numberReader) {
            this.numberReader = numberReader;
            return (B)((Builder)this.self);
        }

        public B setNumberWriter(Function<V, Integer> numberWriter) {
            this.numberWriter = numberWriter;
            return (B)((Builder)this.self);
        }

        public B setValueNamer(Function<V, Component> valueNamer) {
            this.valueNamer = valueNamer;
            return (B)((Builder)this.self);
        }

        public B setHasNullOption(boolean hasNullOption) {
            this.hasNullOption = hasNullOption;
            return (B)((Builder)this.self);
        }

        @Override
        public EditorExpandingRangeNode<V> build() {
            if (this.numberReader == null || this.valueNamer == null || this.numberWriter == null) {
                throw new IllegalStateException("required fields not set!");
            }
            this.optionBuilders.clear();
            if (this.currentRangeValue != null) {
                this.setCurrentValue(this.numberWriter.apply(this.currentRangeValue));
            }
            if (this.hasNullOption) {
                EditorOptionNode.Builder optionBuilder = EditorOptionNode.Builder.begin();
                optionBuilder.setValue(null);
                optionBuilder.setDisplayName(this.valueNamer.apply(null));
                this.addOptionBuilder(optionBuilder);
            }
            for (int index = this.minNumber; index <= this.maxNumber; ++index) {
                EditorOptionNode.Builder<Integer> optionBuilder = EditorOptionNode.Builder.begin();
                optionBuilder.setValue(index);
                optionBuilder.setDisplayName(this.valueNamer.apply(this.numberReader.apply(index)));
                this.addOptionBuilder(optionBuilder);
            }
            EditorExpandingRangeNode result = (EditorExpandingRangeNode)super.build();
            return result;
        }

        @Override
        protected abstract EditorExpandingRangeNode<V> buildInternally(EditorOptionNode<Integer> var1, List<EditorOptionNode<Integer>> var2);
    }
}

