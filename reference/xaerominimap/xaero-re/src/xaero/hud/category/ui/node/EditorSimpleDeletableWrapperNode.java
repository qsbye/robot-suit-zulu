/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import javax.annotation.Nonnull;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryDeletableListElement;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSimpleWrapperNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorSimpleDeletableWrapperNode<S extends Comparable<S>>
extends EditorSimpleWrapperNode<S> {
    private final DeletionCallback deletionCallback;

    protected EditorSimpleDeletableWrapperNode(@Nonnull S element, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, @Nonnull DeletionCallback deletionCallback) {
        super(element, movable, listEntryFactory, tooltipSupplier);
        this.deletionCallback = deletionCallback;
    }

    public DeletionCallback getDeletionCallback() {
        return this.deletionCallback;
    }

    public static interface DeletionCallback {
        public boolean delete(EditorNode var1, EditorSimpleDeletableWrapperNode<?> var2, GuiCategoryEditor.SettingRowList var3);
    }

    public static final class Builder<S extends Comparable<S>>
    extends EditorSimpleWrapperNode.Builder<S, Builder<S>> {
        private DeletionCallback deletionCallback;

        private Builder() {
        }

        @Override
        public Builder<S> setDefault() {
            super.setDefault();
            this.setDeletionCallback(null);
            return (Builder)this.self;
        }

        @Override
        protected EditorListRootEntry mainEntryFactory(EditorNode data, EditorNode parent, int index, ConnectionLineType lineType, GuiCategoryEditor.SettingRowList rowList, int screenWidth, boolean isFinalExpanded) {
            EditorSimpleDeletableWrapperNode sdwData = (EditorSimpleDeletableWrapperNode)data;
            return new EditorListEntryDeletableListElement(screenWidth, index, rowList, lineType, sdwData, parent, sdwData.getDeletionCallback(), data.getTooltipSupplier(parent), rowList.readOnly);
        }

        public Builder<S> setDeletionCallback(DeletionCallback deletionCallback) {
            this.deletionCallback = deletionCallback;
            return (Builder)this.self;
        }

        @Override
        public EditorSimpleDeletableWrapperNode<S> build() {
            if (this.deletionCallback == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (EditorSimpleDeletableWrapperNode)super.build();
        }

        @Override
        protected EditorNode buildInternally() {
            return new EditorSimpleDeletableWrapperNode<Comparable>(this.element, this.movable, this.listEntryFactory, this.tooltipSupplier, this.deletionCallback);
        }

        public static <S extends Comparable<S>> Builder<S> begin() {
            return new Builder<S>().setDefault();
        }
    }
}

