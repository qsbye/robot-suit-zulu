/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options;

import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorCompactOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorCompactBooleanOptionsNode
extends EditorCompactOptionsNode<Boolean> {
    private final EditorOptionNode<Boolean> trueOption;
    private final EditorOptionNode<Boolean> falseOption;
    private IntFunction<EditorOptionNode<Boolean>> indexReader;

    protected EditorCompactBooleanOptionsNode(Component displayName, int currentIndex, int optionCount, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier, EditorOptionNode<Boolean> trueOption, EditorOptionNode<Boolean> falseOption) {
        super(displayName, currentIndex, optionCount, movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.trueOption = trueOption;
        this.falseOption = falseOption;
        this.currentValue = this.getIndexReader().apply(currentIndex);
    }

    @Override
    protected IntFunction<EditorOptionNode<Boolean>> getIndexReader() {
        if (this.indexReader == null) {
            this.indexReader = i -> i != 0 ? this.trueOption : this.falseOption;
        }
        return this.indexReader;
    }

    public static final class Builder
    extends EditorCompactOptionsNode.Builder<Boolean, Builder> {
        private final EditorOptionNode.Builder<Boolean> trueOptionBuilder = EditorOptionNode.Builder.begin();
        private final EditorOptionNode.Builder<Boolean> falseOptionBuilder = EditorOptionNode.Builder.begin();

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            ((EditorOptionNode.Builder)this.trueOptionBuilder.setDefault()).setDisplayName((Component)Component.translatable((String)"gui.xaero_on")).setValue(true);
            ((EditorOptionNode.Builder)this.falseOptionBuilder.setDefault()).setDisplayName((Component)Component.translatable((String)"gui.xaero_off")).setValue(false);
            this.setCurrentValue(false);
            return (Builder)this.self;
        }

        public EditorOptionNode.Builder<Boolean> getTrueOptionBuilder() {
            return this.trueOptionBuilder;
        }

        public EditorOptionNode.Builder<Boolean> getFalseOptionBuilder() {
            return this.falseOptionBuilder;
        }

        @Override
        public EditorCompactBooleanOptionsNode build() {
            if (this.currentValue == null) {
                throw new IllegalStateException();
            }
            if (this.movable) {
                throw new IllegalStateException("toggles can't be movable!");
            }
            return (EditorCompactBooleanOptionsNode)super.build();
        }

        @Override
        protected EditorCompactBooleanOptionsNode buildInternally() {
            EditorNode trueOption = this.trueOptionBuilder.build();
            EditorNode falseOption = this.falseOptionBuilder.build();
            return new EditorCompactBooleanOptionsNode(this.displayName, (Boolean)this.currentValue != false ? 1 : 0, 2, this.movable, this.listEntryFactory, this.tooltipSupplier, this.isActiveSupplier, (EditorOptionNode<Boolean>)trueOption, (EditorOptionNode<Boolean>)falseOption);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

