/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.list;

import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.options.EditorCompactOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public final class EditorCompactListOptionsNode<V>
extends EditorCompactOptionsNode<V> {
    private IntFunction<EditorOptionNode<V>> indexReader;
    private List<EditorOptionNode<V>> options;

    protected EditorCompactListOptionsNode(Component displayName, @Nonnull EditorOptionNode<V> currentValue, @Nonnull List<EditorOptionNode<V>> options, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(displayName, options.indexOf(currentValue), options.size(), movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.currentValue = currentValue;
        this.options = options;
    }

    @Override
    protected IntFunction<EditorOptionNode<V>> getIndexReader() {
        if (this.indexReader == null) {
            this.indexReader = this.options::get;
        }
        return this.indexReader;
    }

    public static final class Builder<V>
    extends EditorCompactOptionsNode.Builder<V, Builder<V>> {
        protected final List<EditorOptionNode.Builder<V>> optionBuilders;
        protected final ListFactory listFactory;

        private Builder(ListFactory listFactory) {
            this.optionBuilders = listFactory.get();
            this.listFactory = listFactory;
        }

        @Override
        public Builder<V> setDefault() {
            this.optionBuilders.clear();
            return (Builder)super.setDefault();
        }

        public Builder<V> addOptionBuilder(EditorOptionNode.Builder<V> optionBuilder) {
            this.optionBuilders.add(optionBuilder);
            return this;
        }

        @Override
        public EditorCompactListOptionsNode<V> build() {
            if (this.listFactory == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (EditorCompactListOptionsNode)super.build();
        }

        @Override
        protected EditorCompactListOptionsNode<V> buildInternally() {
            List options = this.optionBuilders.stream().map(EditorOptionNode.Builder::build).collect(this.listFactory::get, List::add, List::addAll);
            EditorOptionNode currentValueNode = null;
            for (EditorOptionNode optionData : options) {
                if (optionData.getValue() != this.currentValue) continue;
                currentValueNode = optionData;
                break;
            }
            if (currentValueNode == null) {
                throw new IllegalStateException("current value is not one of the options! " + String.valueOf(this.currentValue));
            }
            return new EditorCompactListOptionsNode(this.displayName, currentValueNode, options, this.movable, this.listEntryFactory, this.tooltipSupplier, this.isActiveSupplier);
        }

        public static <V> Builder<V> begin(ListFactory listFactory) {
            return new Builder<V>(listFactory).setDefault();
        }
    }
}

