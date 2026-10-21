/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.node.EditorNode;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryWrapper
extends EditorListRootEntry {
    public EditorListEntryWrapper(EditorListRootEntry.CenteredEntryFactory wrappedFactory, int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorNode node) {
        this(wrappedFactory, screenWidth, index, rowList, lineType, node, null);
    }

    public EditorListEntryWrapper(EditorListRootEntry.CenteredEntryFactory wrappedFactory, int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorNode node, Supplier<Tooltip> helpTooltipSupplier) {
        super(screenWidth, index, rowList, lineType, node);
        this.withSubEntry(wrappedFactory);
        this.addHelpElement(helpTooltipSupplier);
    }

    @Override
    public Component getMessage() {
        return Component.literal((String)"");
    }
}

