/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListTextButtonEntry;
import xaero.hud.category.ui.node.EditorNode;
import xaero.lib.client.gui.widget.Tooltip;

public abstract class EditorListRootEntry
extends EditorListEntry {
    private final ConnectionLineType lineType;
    protected final EditorNode node;

    public EditorListRootEntry(int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorNode node) {
        super(0, 0, screenWidth, 24, index, rowList, () -> null);
        this.lineType = lineType;
        this.node = node;
    }

    protected void addHelpElement(Supplier<Tooltip> helpTooltipSupplier) {
        if (helpTooltipSupplier == null) {
            return;
        }
        this.withSubEntry((int x, int y, int w, int h, EditorListRootEntry root) -> new EditorListTextButtonEntry(x - 24, y + 2, this.index, this.rowList, (Component)Component.literal((String)"?"), -5592406, -1, 5, () -> false, this, helpTooltipSupplier));
    }

    @Override
    public EditorListEntry render(GuiGraphicsExtractor guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, Font font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        int xOffset = rowWidth / 2 - 110;
        int yOffset = 8;
        if (this.lineType == ConnectionLineType.TAIL_LEAF || this.lineType == ConnectionLineType.HEAD_LEAF) {
            int leftX = xOffset - 14;
            int rightX = xOffset - 2;
            int bottomY = yOffset + 4;
            int topY = yOffset - 24 + 4;
            guiGraphics.horizontalLine(leftX, rightX, bottomY, -5592406);
            guiGraphics.verticalLine(leftX, topY, bottomY, -5592406);
            guiGraphics.verticalLine(rightX - 1, bottomY - 2, bottomY + 2, -5592406);
            guiGraphics.verticalLine(rightX - 2, bottomY - 3, bottomY + 3, -5592406);
            if (this.lineType == ConnectionLineType.HEAD_LEAF) {
                guiGraphics.horizontalLine(leftX, rightX, topY, -5592406);
            }
            return result;
        }
        if (this.lineType != ConnectionLineType.PATH) {
            return result;
        }
        int topY = yOffset - 24 + 9;
        int bottomY = yOffset - 2;
        int lineX = xOffset + 12;
        guiGraphics.horizontalLine(lineX - 2, lineX + 2, bottomY - 3, -5592406);
        guiGraphics.horizontalLine(lineX - 1, lineX + 1, bottomY - 2, -5592406);
        guiGraphics.verticalLine(lineX, topY, bottomY, -5592406);
        return result;
    }

    public EditorListRootEntry withSubEntry(CenteredEntryFactory entryFactory) {
        super.withSubEntry(entryFactory.get(this.rowList.getRowWidth() / 2 - 110 - 1, 0, 220, 24, this));
        return this;
    }

    @Override
    protected boolean selectAction() {
        return false;
    }

    @FunctionalInterface
    public static interface CenteredEntryFactory {
        public EditorListEntry get(int var1, int var2, int var3, int var4, EditorListRootEntry var5);
    }
}

