/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryTextWithAction;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListTextButtonEntry;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorSettingsNode;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryCategory<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, ?, ED>>
extends EditorListRootEntry {
    private static final Tooltip HELP_TOOLTIP = new Tooltip("gui.xaero_category_help2", Style.EMPTY, true);
    private static final Tooltip PROTECTED_TOOLTIP = new Tooltip("gui.xaero_category_protected_category", Style.EMPTY, true);
    private static final Tooltip UP_TOOLTIP = new Tooltip("gui.xaero_category_category_move_up", Style.EMPTY, true);
    private static final Tooltip DOWN_TOOLTIP = new Tooltip("gui.xaero_category_category_move_down", Style.EMPTY, true);

    public EditorListEntryCategory(int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorCategoryNode<?, ?, ?> node, EditorCategoryNode<?, ?, ?> parent, Supplier<Tooltip> tooltipSupplier, boolean isFinalExpanded) {
        super(screenWidth, index, rowList, lineType, node);
        int subIndex = parent == null ? -1 : parent.getSubCategories().indexOf(node);
        EditorCategoryNode<?, ?, ?> dataCast = node;
        EditorCategoryNode<?, ?, ?> parentCast = parent;
        GuiCategoryEditor.SettingRowList rowListCast = rowList;
        boolean isCut = rowListCast.isCut(dataCast);
        Object currentCut = rowListCast.getCut();
        this.withSubEntry(this.getCategoryNameEntryFactory(dataCast, rowListCast, isCut, tooltipSupplier));
        EditorListRootEntry.CenteredEntryFactory pasteEntryFactory = this.getPasteEntryFactory(currentCut, isCut, dataCast, rowListCast);
        if (dataCast.isExpanded() || !dataCast.isMovable()) {
            if (rowListCast.hasCut()) {
                this.withSubEntry(pasteEntryFactory);
            }
            if (isFinalExpanded) {
                this.addHelpElement((Supplier<Tooltip>)HELP_TOOLTIP);
            }
            return;
        }
        if (!rowListCast.readOnly && !((EditorSettingsNode)dataCast.getSettingsNode()).getProtection()) {
            this.withSubEntry(this.getDuplicateEntryFactory(subIndex, parentCast, rowListCast));
        }
        if (rowListCast.hasCut()) {
            this.withSubEntry(pasteEntryFactory);
        }
        if (((EditorSettingsNode)dataCast.getSettingsNode()).getProtection()) {
            this.withSubEntry(this.getProtectedEntryFactory());
            return;
        }
        if (!rowListCast.readOnly && !rowListCast.hasCut()) {
            this.withSubEntry(this.getCutEntryFactory(dataCast, parentCast, rowListCast));
        }
        if (rowListCast.readOnly || parent.getSubCategories().size() <= 1) {
            return;
        }
        this.withSubEntry(this.getPriorityEntryFactory(-1, parentCast, subIndex));
        this.withSubEntry(this.getPriorityEntryFactory(1, parentCast, subIndex));
    }

    private EditorListRootEntry.CenteredEntryFactory getCategoryNameEntryFactory(ED dataCast, GuiCategoryEditor.SettingRowList rowListCast, boolean isCut, Supplier<Tooltip> tooltipSupplier) {
        return (x, y, w, h, root) -> {
            Runnable action = isCut ? () -> rowListCast.pasteTo(dataCast) : dataCast.getExpandAction(rowListCast);
            EditorListEntryTextWithAction result = new EditorListEntryTextWithAction(x, y, w, h, this.index, this.rowList, this, action, tooltipSupplier);
            if (isCut) {
                result.setColor(-5636096);
                result.setHoverColor(-43691);
            }
            return result;
        };
    }

    private EditorListRootEntry.CenteredEntryFactory getPasteEntryFactory(ED currentCut, boolean isCut, ED dataCast, GuiCategoryEditor.SettingRowList rowListCast) {
        Tooltip pasteTooltip = this.getPasteTooltip(currentCut, isCut);
        if (pasteTooltip != null) {
            pasteTooltip.setAutoLinebreak(false);
        }
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 248, y + 2, this.index, this.rowList, (Component)Component.literal((String)"\u2190"), -5592406, -1, 5, dataCast.getPasteAction(rowListCast), this, (Supplier<Tooltip>)pasteTooltip);
    }

    private Tooltip getPasteTooltip(ED currentCut, boolean isCut) {
        if (currentCut == null) {
            return null;
        }
        if (isCut) {
            return new Tooltip("gui.xaero_category_paste_cancel", Style.EMPTY, true);
        }
        MutableComponent component = Component.translatable((String)"gui.xaero_category_paste", (Object[])new Object[]{((EditorCategoryNode)currentCut).getDisplayName(), this.node.getDisplayName()});
        return new Tooltip((Component)component, true);
    }

    private EditorListRootEntry.CenteredEntryFactory getDuplicateEntryFactory(int subIndex, ED parentCast, GuiCategoryEditor.SettingRowList rowListCast) {
        MutableComponent duplicateTooltipComponent = Component.translatable((String)"gui.xaero_category_duplicate", (Object[])new Object[]{this.node.getDisplayName()});
        Tooltip duplicateTooltip = new Tooltip((Component)duplicateTooltipComponent, true);
        duplicateTooltip.setAutoLinebreak(false);
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 230, y + 2, this.index, this.rowList, (Component)Component.literal((String)"+"), -5592406, -1, 5, parentCast.getDuplicateAction(subIndex, rowListCast), this, (Supplier<Tooltip>)duplicateTooltip);
    }

    private EditorListRootEntry.CenteredEntryFactory getProtectedEntryFactory() {
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x - 24, y + 2, this.index, this.rowList, (Component)Component.literal((String)"!"), -1644980, -171, 5, () -> false, this, (Supplier<Tooltip>)PROTECTED_TOOLTIP);
    }

    private EditorListRootEntry.CenteredEntryFactory getCutEntryFactory(ED dataCast, ED parentCast, GuiCategoryEditor.SettingRowList rowListCast) {
        MutableComponent cutTooltipComponent = Component.translatable((String)"gui.xaero_category_cut", (Object[])new Object[]{this.node.getDisplayName()});
        Tooltip cutTooltip = new Tooltip((Component)cutTooltipComponent, true);
        cutTooltip.setAutoLinebreak(false);
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 248, y + 2, this.index, this.rowList, (Component)Component.literal((String)"\u2194"), -5592406, -1, 5, dataCast.getCutAction(parentCast, rowListCast), this, (Supplier<Tooltip>)cutTooltip);
    }

    private EditorListRootEntry.CenteredEntryFactory getPriorityEntryFactory(int direction, ED parentCast, int subIndex) {
        String label = direction < 0 ? "\u2191" : "\u2193";
        Tooltip tooltip = direction < 0 ? UP_TOOLTIP : DOWN_TOOLTIP;
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x - 32 + 8 * direction, y + 2, this.index, this.rowList, (Component)Component.literal((String)label), -5592406, -1, 5, parentCast.getMoveAction(subIndex, direction, this.rowList), this, (Supplier<Tooltip>)tooltip);
    }

    @Override
    public Component getMessage() {
        return Component.literal((String)"");
    }
}

