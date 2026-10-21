/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorSimpleButtonNode;
import xaero.hud.category.ui.node.options.text.EditorTextFieldOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public final class EditorAdderNode
extends EditorNode {
    private final Component displayName;
    private final EditorTextFieldOptionsNode nameField;
    private final EditorSimpleButtonNode confirmButton;
    private boolean confirmed;

    private EditorAdderNode(@Nonnull Component displayName, @Nonnull EditorTextFieldOptionsNode nameField, @Nonnull EditorSimpleButtonNode confirmButton, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.displayName = displayName;
        this.confirmButton = confirmButton;
        this.nameField = nameField;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            this.reset();
        }
    }

    public void reset() {
        this.confirmed = false;
        this.nameField.resetInput("");
    }

    public EditorTextFieldOptionsNode getNameField() {
        return this.nameField;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        return Lists.newArrayList((Object[])new EditorNode[]{this.nameField, this.confirmButton});
    }

    public static final class Builder
    extends EditorNode.Builder<Builder> {
        private Component displayName;
        private final EditorTextFieldOptionsNode.Builder nameFieldBuilder;
        private final EditorSimpleButtonNode.Builder confirmButtonBuilder;

        private Builder(ListFactory listFactory) {
            this.nameFieldBuilder = EditorTextFieldOptionsNode.Builder.begin(listFactory);
            this.confirmButtonBuilder = EditorSimpleButtonNode.Builder.begin();
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setDisplayName(null);
            this.nameFieldBuilder.setDefault().setDisplayName((Component)Component.translatable((String)"gui.xaero_category_name"));
            this.confirmButtonBuilder.setDefault().setDisplayName((Component)Component.translatable((String)"gui.xaero_category_confirm"));
            this.confirmButtonBuilder.setCallback((parent, bd, rl) -> {
                EditorAdderNode adder = (EditorAdderNode)parent;
                boolean bl = !adder.getNameField().getResult().isEmpty();
                adder.confirmed = bl;
                adder.setExpanded(false);
                rl.setLastExpandedData(adder);
                rl.updateEntries();
            });
            return this;
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> {
                EditorButton button = new EditorButton(parent, true, 216, 20, data, rowList);
                if (rowList.readOnly) {
                    button.active = false;
                }
                return new EditorListEntryWidget(x, y, width, height, index, rowList, root, (AbstractWidget)button, data.getTooltipSupplier(parent));
            };
        }

        public Builder setDisplayName(Component displayName) {
            this.displayName = displayName;
            return (Builder)this.self;
        }

        public EditorTextFieldOptionsNode.Builder getNameFieldBuilder() {
            return this.nameFieldBuilder;
        }

        public EditorSimpleButtonNode.Builder getConfirmButtonBuilder() {
            return this.confirmButtonBuilder;
        }

        public static Builder begin(ListFactory listFactory) {
            return new Builder(listFactory).setDefault();
        }

        @Override
        public EditorAdderNode build() {
            if (this.displayName == null) {
                throw new IllegalStateException("required fields not set!");
            }
            EditorAdderNode result = (EditorAdderNode)super.build();
            return result;
        }

        @Override
        protected EditorAdderNode buildInternally() {
            if (this.nameFieldBuilder.needsInputStringValidator()) {
                this.nameFieldBuilder.setInputStringValidator(s -> true);
            }
            return new EditorAdderNode(this.displayName, this.nameFieldBuilder.build(), this.confirmButtonBuilder.build(), this.movable, this.listEntryFactory, this.tooltipSupplier);
        }
    }
}

