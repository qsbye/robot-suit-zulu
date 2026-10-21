/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.lib.client.gui.widget.Tooltip;

public abstract class EditorListEntryWithRootReference
extends EditorListEntry {
    protected final EditorListRootEntry root;

    public EditorListEntryWithRootReference(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, Supplier<Tooltip> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, tooltipSupplier);
        this.root = root;
    }
}

