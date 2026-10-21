/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryExpandingOptions
extends EditorListEntryWidget {
    public EditorListEntryExpandingOptions(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, AbstractWidget widget, Supplier<Component> messageSupplier, Supplier<Tooltip> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root, widget, tooltipSupplier);
        if (messageSupplier == null) {
            return;
        }
        Component optionTypeName = messageSupplier.get();
        if (!root.node.isExpanded()) {
            widget.setMessage(optionTypeName);
            return;
        }
        widget.setMessage((Component)Component.translatable((String)"gui.xaero_category_expanded_options", (Object[])new Object[]{optionTypeName}));
    }
}

