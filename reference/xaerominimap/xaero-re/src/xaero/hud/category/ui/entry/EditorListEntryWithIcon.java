/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListEntryWithRootReference;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.render.TextureLocations;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryWithIcon
extends EditorListEntryWithRootReference {
    private final int iconU;
    private final int iconV;
    private final int iconW;
    private final int iconH;

    public EditorListEntryWithIcon(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, int iconU, int iconV, int iconW, int iconH, EditorListRootEntry root, Supplier<Tooltip> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root, tooltipSupplier);
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconW = iconW;
        this.iconH = iconH;
    }

    public int getIconX() {
        return this.iconU;
    }

    public int getIconY() {
        return this.iconV;
    }

    public int getIconW() {
        return this.iconW;
    }

    public int getIconH() {
        return this.iconH;
    }

    @Override
    public EditorListEntry render(GuiGraphicsExtractor guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, Font font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocations.GUI_TEXTURES, 0, 0, (float)this.iconU, (float)this.iconV, this.iconW, this.iconH, 256, 256);
        return result;
    }

    @Override
    protected boolean selectAction() {
        return false;
    }

    @Override
    public Component getMessage() {
        return Component.literal((String)"unnamed");
    }
}

