/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWithIconAndText;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.node.options.EditorExpandingOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryExpandingOption<V>
extends EditorListEntryWithIconAndText {
    private EditorExpandingOptionsNode<V> dataParent;

    public EditorListEntryExpandingOption(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorExpandingOptionsNode<V> dataParent, EditorListRootEntry root, Supplier<Tooltip> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root.node.getDisplayName(), root, tooltipSupplier);
        this.dataParent = dataParent;
    }

    @Override
    public boolean selectAction() {
        return this.dataParent.onSelected((EditorOptionNode)this.root.node);
    }
}

