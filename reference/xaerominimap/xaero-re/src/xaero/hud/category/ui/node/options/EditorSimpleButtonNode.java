/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryExpandingOptions;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public final class EditorSimpleButtonNode
extends EditorNode {
    protected final Component displayName;
    private ISimpleButtonCallback callback;
    private EditorButton.PressActionWithContext pressAction;
    private ISimpleButtonMessageSupplier messageSupplier;
    private final ISimpleButtonIsActiveSupplier isActiveSupplier;

    private EditorSimpleButtonNode(@Nonnull Component displayName, @Nonnull IEditorDataTooltipSupplier tooltipSupplier, boolean movable, ISimpleButtonCallback callback, @Nonnull EditorListRootEntryFactory listEntryFactory, ISimpleButtonMessageSupplier messageSupplier, ISimpleButtonIsActiveSupplier isActiveSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.displayName = displayName;
        this.callback = callback;
        this.messageSupplier = messageSupplier;
        this.isActiveSupplier = isActiveSupplier;
    }

    public Supplier<Component> getMessageSupplier(EditorNode parent, EditorSimpleButtonNode data) {
        return this.messageSupplier.get(parent, data);
    }

    public boolean getIsActiveSupplier(EditorNode parent, EditorSimpleButtonNode data, GuiCategoryEditor.SettingRowList rowList) {
        return this.isActiveSupplier.get(parent, data, rowList);
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    public EditorButton.PressActionWithContext getPressAction() {
        if (this.pressAction == null) {
            this.pressAction = new EditorButton.PressActionWithContext(this){
                final /* synthetic */ EditorSimpleButtonNode this$0;
                {
                    EditorSimpleButtonNode editorSimpleButtonNode = this$0;
                    Objects.requireNonNull(editorSimpleButtonNode);
                    this.this$0 = editorSimpleButtonNode;
                }

                @Override
                public void onPress(EditorButton button, EditorNode parent, GuiCategoryEditor.SettingRowList rowList) {
                    if (this.this$0.callback != null) {
                        this.this$0.callback.onButtonPress(parent, this.this$0, rowList);
                    }
                }
            };
        }
        return this.pressAction;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        return null;
    }

    @FunctionalInterface
    public static interface ISimpleButtonCallback {
        public void onButtonPress(EditorNode var1, EditorSimpleButtonNode var2, GuiCategoryEditor.SettingRowList var3);
    }

    @FunctionalInterface
    public static interface ISimpleButtonMessageSupplier {
        public Supplier<Component> get(EditorNode var1, EditorSimpleButtonNode var2);
    }

    @FunctionalInterface
    public static interface ISimpleButtonIsActiveSupplier {
        public boolean get(EditorNode var1, EditorSimpleButtonNode var2, GuiCategoryEditor.SettingRowList var3);
    }

    public static final class Builder
    extends EditorNode.Builder<Builder> {
        private Component displayName;
        private ISimpleButtonCallback callback;
        private ISimpleButtonMessageSupplier messageSupplier;
        private ISimpleButtonIsActiveSupplier isActiveSupplier;

        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setDisplayName(null);
            this.setCallback(null);
            this.setMessageSupplier((parent, node) -> node::getDisplayName);
            this.setIsActiveSupplier((p, d, rowList) -> true);
            return this;
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode node, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            EditorSimpleButtonNode buttonNode = (EditorSimpleButtonNode)node;
            Supplier<Component> messageSupplier = buttonNode.getMessageSupplier(parent, buttonNode);
            return (x, y, width, height, root) -> {
                boolean isActive = buttonNode.getIsActiveSupplier(parent, buttonNode, rowList);
                EditorButton widget = new EditorButton(parent, messageSupplier, isActive, 216, 20, buttonNode.getPressAction(), rowList);
                return new EditorListEntryExpandingOptions(x, y, width, height, index, rowList, root, (AbstractWidget)widget, messageSupplier, node.getTooltipSupplier(parent));
            };
        }

        public Builder setDisplayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setCallback(ISimpleButtonCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setMessageSupplier(ISimpleButtonMessageSupplier messageSupplier) {
            this.messageSupplier = messageSupplier;
            return this;
        }

        public Builder setIsActiveSupplier(ISimpleButtonIsActiveSupplier isActiveSupplier) {
            this.isActiveSupplier = isActiveSupplier;
            return this;
        }

        @Override
        public EditorSimpleButtonNode build() {
            if (this.displayName == null || this.callback == null) {
                throw new IllegalStateException("required fields not set!");
            }
            EditorSimpleButtonNode result = (EditorSimpleButtonNode)super.build();
            return result;
        }

        @Override
        protected EditorNode buildInternally() {
            return new EditorSimpleButtonNode(this.displayName, this.tooltipSupplier, this.movable, this.callback, this.listEntryFactory, this.messageSupplier, this.isActiveSupplier);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

