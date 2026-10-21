/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListEntryWithRootReference;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListTextButtonEntry
extends EditorListEntryWithRootReference {
    private final Component text;
    private final int color;
    private final int hoverColor;
    private final int frameSize;
    private final Supplier<Boolean> action;

    public EditorListTextButtonEntry(int entryX, int entryY, int index, GuiCategoryEditor.SettingRowList rowList, Component text, int color, int hoverColor, int frameSize, Supplier<Boolean> action, EditorListRootEntry root, Supplier<Tooltip> tooltipSupplier) {
        super(entryX - Minecraft.getInstance().font.width((FormattedText)text) / 2 - frameSize, entryY, Minecraft.getInstance().font.width((FormattedText)text) + frameSize * 2, 9 + frameSize * 2, index, rowList, root, tooltipSupplier);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.frameSize = frameSize;
        this.action = action;
    }

    @Override
    public EditorListEntry render(GuiGraphicsExtractor guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, Font font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        int textX = this.frameSize + Minecraft.getInstance().font.width((FormattedText)this.text) / 2;
        guiGraphics.centeredText(font, this.text, textX, this.frameSize + 1, isMouseOver ? this.hoverColor : this.color);
        return result;
    }

    @Override
    protected boolean selectAction() {
        return this.action.get();
    }

    @Override
    public Component getMessage() {
        return this.text;
    }
}

