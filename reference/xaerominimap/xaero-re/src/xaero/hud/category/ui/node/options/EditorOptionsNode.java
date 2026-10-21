/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public abstract class EditorOptionsNode<V>
extends EditorNode {
    protected EditorOptionNode<V> currentValue;
    protected Supplier<Component> messageSupplier;
    protected final Component displayName;
    private final IOptionsNodeIsActiveSupplier isActiveSupplier;

    protected EditorOptionsNode(@Nonnull Component displayName, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.displayName = displayName;
        this.isActiveSupplier = isActiveSupplier;
    }

    public EditorOptionNode<V> getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(EditorOptionNode<V> currentValue) {
        this.currentValue = currentValue;
    }

    public final Supplier<Component> getMessageSupplier() {
        if (this.messageSupplier == null) {
            this.messageSupplier = () -> this.isExpanded() ? this.displayName : CommonComponents.optionNameValue((Component)this.displayName, (Component)this.currentValue.getDisplayName());
        }
        return this.messageSupplier;
    }

    public IOptionsNodeIsActiveSupplier getIsActiveSupplier() {
        return this.isActiveSupplier;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @FunctionalInterface
    public static interface IOptionsNodeIsActiveSupplier {
        public boolean get(EditorNode var1, EditorOptionsNode<?> var2);
    }

    public static abstract class Builder<V, B extends Builder<V, B>>
    extends EditorNode.Builder<B> {
        protected B self = this;
        protected V currentValue;
        protected Component displayName;
        protected IOptionsNodeIsActiveSupplier isActiveSupplier;

        protected Builder() {
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.setCurrentValue(null);
            this.setDisplayName(null);
            this.setIsActiveSupplier((p, d) -> true);
            return this.self;
        }

        public B setCurrentValue(V currentValue) {
            this.currentValue = currentValue;
            return this.self;
        }

        public B setDisplayName(Component displayName) {
            this.displayName = displayName;
            return this.self;
        }

        public B setIsActiveSupplier(IOptionsNodeIsActiveSupplier isActiveSupplier) {
            this.isActiveSupplier = isActiveSupplier;
            return this.self;
        }

        @Override
        public EditorOptionsNode<V> build() {
            if (this.displayName == null) {
                throw new IllegalStateException("required fields not set!");
            }
            EditorOptionsNode result = (EditorOptionsNode)super.build();
            return result;
        }

        @Override
        protected abstract EditorOptionsNode<V> buildInternally();
    }
}

