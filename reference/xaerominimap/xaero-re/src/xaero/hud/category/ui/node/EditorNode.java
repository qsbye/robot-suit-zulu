/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryTextWithAction;
import xaero.hud.category.ui.entry.EditorListEntryWrapper;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.common.gui.widget.TooltipInfo;

public abstract class EditorNode {
    private final boolean movable;
    private boolean expanded;
    protected final EditorListRootEntryFactory listEntryFactory;
    protected final IEditorDataTooltipSupplier tooltipSupplier;

    public EditorNode(boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier) {
        this.movable = movable;
        this.listEntryFactory = listEntryFactory;
        this.tooltipSupplier = tooltipSupplier;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded) {
            return;
        }
        List<EditorNode> subExpandables = this.getSubNodes();
        if (subExpandables == null) {
            return;
        }
        for (EditorNode sub : subExpandables) {
            if (!sub.isExpanded()) continue;
            sub.setExpanded(false);
            break;
        }
    }

    public final EditorListRootEntryFactory getListEntryFactory() {
        return this.listEntryFactory;
    }

    public Supplier<Tooltip> getTooltipSupplier(EditorNode parent) {
        return this.tooltipSupplier == null ? null : (Supplier)this.tooltipSupplier.apply(parent, this);
    }

    public Runnable getExpandAction(GuiCategoryEditor.SettingRowList rowList) {
        return () -> {
            List<EditorNode> subExpandables = this.getSubNodes();
            if (subExpandables == null || subExpandables.isEmpty()) {
                return;
            }
            this.setExpanded(true);
            rowList.setLastExpandedData(this);
            for (EditorNode o : subExpandables) {
                EditorNode sed = o;
                if (!sed.isExpanded()) continue;
                sed.setExpanded(false);
                break;
            }
            rowList.updateEntries();
        };
    }

    public abstract Component getDisplayName();

    public abstract List<EditorNode> getSubNodes();

    public static abstract class Builder<B extends Builder<B>> {
        protected B self = this;
        protected EditorListRootEntryFactory listEntryFactory;
        protected IEditorDataTooltipSupplier tooltipSupplier;
        protected boolean movable;

        protected Builder() {
        }

        public B setDefault() {
            this.setMovable(false);
            this.setListEntryFactory(this::mainEntryFactory);
            this.setTooltipSupplier(null);
            return this.self;
        }

        protected EditorListRootEntry mainEntryFactory(EditorNode data, EditorNode parent, int index, ConnectionLineType lineType, GuiCategoryEditor.SettingRowList rowList, int screenWidth, boolean isFinalExpanded) {
            return new EditorListEntryWrapper(this.getCenteredEntryFactory(data, parent, index, rowList), screenWidth, index, rowList, lineType, data);
        }

        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> new EditorListEntryTextWithAction(x, y, width, height, index, rowList, root, data.getExpandAction(rowList), data.getTooltipSupplier(parent));
        }

        public B setMovable(boolean movable) {
            this.movable = movable;
            return this.self;
        }

        public B setListEntryFactory(EditorListRootEntryFactory listEntryFactory) {
            this.listEntryFactory = listEntryFactory;
            return this.self;
        }

        public B setTooltipSupplier(IEditorDataTooltipSupplier tooltipSupplier) {
            this.tooltipSupplier = tooltipSupplier;
            return this.self;
        }

        public B setTooltipInfoSupplier(BiFunction<EditorNode, EditorNode, TooltipInfo> tooltipInfoSupplier) {
            return this.setTooltipSupplier((parent, data) -> {
                TooltipInfo info = (TooltipInfo)tooltipInfoSupplier.apply((EditorNode)parent, (EditorNode)data);
                return info == null ? null : new Tooltip(info);
            });
        }

        public EditorNode build() {
            if (this.listEntryFactory == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return this.buildInternally();
        }

        protected abstract EditorNode buildInternally();
    }
}

