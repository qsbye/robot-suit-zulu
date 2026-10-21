/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryTextWithAction;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListTextButtonEntry;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSimpleDeletableWrapperNode;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryDeletableListElement
extends EditorListRootEntry {
    private final EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback;
    private final EditorNode parent;
    private static final Tooltip DELETE_TOOLTIP = new Tooltip("gui.xaero_category_delete_list_element", Style.EMPTY, true);

    public EditorListEntryDeletableListElement(int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorSimpleDeletableWrapperNode<?> node, EditorNode parent, EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback, Supplier<Tooltip> tooltipSupplier, boolean readOnly) {
        super(screenWidth, index, rowList, lineType, node);
        this.deletionCallback = deletionCallback;
        this.parent = parent;
        this.withSubEntry((int x, int y, int w, int h, EditorListRootEntry root) -> new EditorListEntryTextWithAction(x, y, w, h, index, rowList, this, node.getExpandAction(rowList), tooltipSupplier));
        if (readOnly) {
            return;
        }
        this.withSubEntry((int x, int y, int w, int h, EditorListRootEntry root) -> new EditorListTextButtonEntry(x - 24, y + 2, index, rowList, (Component)Component.literal((String)"x"), -5636096, -43691, 5, () -> deletionCallback.delete(parent, node, rowList), this, (Supplier<Tooltip>)DELETE_TOOLTIP));
    }

    @Override
    public boolean keyPressed(KeyEvent event, boolean isRoot) {
        if (event.key() == 76) {
            if (!this.rowList.readOnly && this.deletionCallback.delete(this.parent, (EditorSimpleDeletableWrapperNode)this.node, this.rowList)) {
                this.rowList.restoreScrollAfterUpdate();
                this.rowList.updateEntries();
            }
            return false;
        }
        return super.keyPressed(event, isRoot);
    }

    @Override
    public Component getMessage() {
        return this.node.getDisplayName();
    }
}

