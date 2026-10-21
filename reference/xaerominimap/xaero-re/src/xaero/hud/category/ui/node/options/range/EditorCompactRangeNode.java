/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.range;

import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.options.EditorCompactOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorCompactRangeNode<V>
extends EditorCompactOptionsNode<Integer> {
    private V currentRangeValue;
    private final int minNumber;
    private final IntFunction<V> numberReader;
    private final Function<V, Component> valueNamer;
    private IntFunction<EditorOptionNode<Integer>> zeroIndexReader;
    private final boolean hasNullOption;

    protected EditorCompactRangeNode(Component displayName, V currentRangeValue, int currentIndex, int optionCount, int minNumber, boolean hasNullOption, IntFunction<V> numberReader, Function<V, Component> valueNamer, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(displayName, currentIndex, optionCount, movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.numberReader = numberReader;
        this.valueNamer = valueNamer;
        this.currentRangeValue = currentRangeValue;
        this.hasNullOption = hasNullOption;
        this.minNumber = minNumber;
        this.currentValue = this.getIndexReader().apply(currentIndex);
    }

    @Override
    public void setCurrentValue(EditorOptionNode<Integer> currentValue) {
        super.setCurrentValue(currentValue);
        Integer currentInteger = currentValue.getValue();
        this.currentRangeValue = currentInteger == null ? null : this.numberReader.apply(currentInteger);
    }

    public V getCurrentRangeValue() {
        return this.currentRangeValue;
    }

    @Override
    protected IntFunction<EditorOptionNode<Integer>> getIndexReader() {
        if (this.zeroIndexReader == null) {
            this.zeroIndexReader = i -> {
                if (this.hasNullOption) {
                    --i;
                }
                Integer actualOptionNumber = i < 0 ? null : Integer.valueOf(this.minNumber + i);
                Object correspondingSettingValue = actualOptionNumber == null ? null : (Object)this.numberReader.apply(actualOptionNumber);
                return EditorOptionNode.Builder.begin().setDisplayName(this.valueNamer.apply(correspondingSettingValue)).setValue(actualOptionNumber).build();
            };
        }
        return this.zeroIndexReader;
    }

    public static final class FinalBuilder<V>
    extends Builder<V, FinalBuilder<V>> {
        private FinalBuilder() {
        }

        @Override
        protected EditorCompactRangeNode<V> buildInternally(int currentIndex, int optionCount, EditorListRootEntryFactory listEntryFactory) {
            return new EditorCompactRangeNode<Object>(this.displayName, this.currentRangeValue, currentIndex, optionCount, this.minNumber, this.hasNullOption, this.numberReader, this.valueNamer, this.movable, listEntryFactory, this.tooltipSupplier, this.isActiveSupplier);
        }

        public static <V> FinalBuilder<V> begin() {
            return (FinalBuilder)new FinalBuilder<V>().setDefault();
        }
    }

    public static abstract class Builder<V, B extends Builder<V, B>>
    extends EditorCompactOptionsNode.Builder<Integer, B> {
        protected V currentRangeValue;
        protected int minNumber;
        protected int maxNumber;
        protected IntFunction<V> numberReader;
        protected Function<V, Integer> numberWriter;
        protected Function<V, Component> valueNamer;
        protected boolean hasNullOption;

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
        public EditorCompactRangeNode<V> build() {
            if (this.numberReader == null || this.valueNamer == null || this.numberWriter == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (EditorCompactRangeNode)super.build();
        }

        @Override
        protected EditorCompactRangeNode<V> buildInternally() {
            int currentIndex;
            int n = currentIndex = this.currentRangeValue == null ? -1 : this.numberWriter.apply(this.currentRangeValue) - this.minNumber;
            if (this.currentRangeValue != null && currentIndex < 0) {
                currentIndex = 0;
            }
            int optionCount = this.maxNumber - this.minNumber + 1;
            if (this.hasNullOption) {
                ++optionCount;
                ++currentIndex;
            }
            return this.buildInternally(currentIndex, optionCount, this.listEntryFactory);
        }

        protected abstract EditorCompactRangeNode<V> buildInternally(int var1, int var2, EditorListRootEntryFactory var3);
    }
}

