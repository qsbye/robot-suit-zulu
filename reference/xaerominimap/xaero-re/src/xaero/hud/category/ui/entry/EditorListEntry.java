/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix3x2fStack;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.lib.client.gui.widget.Tooltip;

public abstract class EditorListEntry {
    protected final int entryRelativeX;
    protected final int entryRelativeY;
    protected final int entryW;
    protected final int entryH;
    protected final int index;
    protected final GuiCategoryEditor.SettingRowList rowList;
    protected final List<EditorListEntry> subEntries;
    protected final Supplier<Tooltip> tooltipSupplier;
    protected int focusedSubEntryIndex;
    protected EditorListEntry hoveredSubEntry;

    public EditorListEntry(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, Supplier<Tooltip> tooltipSupplier) {
        this.entryRelativeX = entryX;
        this.entryRelativeY = entryY;
        this.entryW = entryW;
        this.entryH = entryH;
        this.index = index;
        this.rowList = rowList;
        this.subEntries = new ArrayList<EditorListEntry>();
        this.focusedSubEntryIndex = -1;
        this.tooltipSupplier = tooltipSupplier;
    }

    public EditorListEntry onSelected() {
        if (!this.subEntries.isEmpty() && this.focusedSubEntryIndex >= 0) {
            EditorListEntry subEntry = this.subEntries.get(this.focusedSubEntryIndex);
            return subEntry.onSelected();
        }
        if (!this.selectAction()) {
            return this;
        }
        if (!(this instanceof EditorListEntryWidget)) {
            Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
        this.rowList.updateEntries();
        return this;
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    public boolean mouseClicked(GuiCategoryEditor.SettingRowList.Entry entry, double relativeMouseX, double relativeMouseY, int i, MouseButtonInfo buttonInfo, boolean doubleClick) {
        for (int subIndex = 0; subIndex < this.subEntries.size(); ++subIndex) {
            EditorListEntry subEntry = this.subEntries.get(subIndex);
            if (!subEntry.isHoveredOver(relativeMouseX, relativeMouseY)) continue;
            double subRelativeMouseX = relativeMouseX - (double)subEntry.entryRelativeX;
            double subRelativeMouseY = relativeMouseY - (double)subEntry.entryRelativeY;
            if (this.focusedSubEntryIndex != subIndex) {
                this.unfocusRecursively();
                this.focusedSubEntryIndex = subIndex;
            }
            if (!subEntry.mouseClicked(entry, subRelativeMouseX, subRelativeMouseY, subIndex, buttonInfo, doubleClick)) {
                subEntry.confirmSelection();
            }
            return true;
        }
        return false;
    }

    public EditorListEntry confirmSelection() {
        if (this.focusedSubEntryIndex >= 0) {
            return this.subEntries.get(this.focusedSubEntryIndex).confirmSelection();
        }
        return this.onSelected();
    }

    public boolean mouseReleased(double relativeMouseX, double relativeMouseY, int i, MouseButtonInfo buttonInfo) {
        for (EditorListEntry subEntry : this.subEntries) {
            double subRelativeMouseX = relativeMouseX - (double)subEntry.entryRelativeX;
            double subRelativeMouseY = relativeMouseY - (double)subEntry.entryRelativeY;
            subEntry.mouseReleased(subRelativeMouseX, subRelativeMouseY, i, buttonInfo);
        }
        return false;
    }

    public boolean mouseScrolled(double relativeMouseX, double relativeMouseY, double f, double g) {
        for (EditorListEntry subEntry : this.subEntries) {
            double subRelativeMouseX = relativeMouseX - (double)subEntry.entryRelativeX;
            double subRelativeMouseY = relativeMouseY - (double)subEntry.entryRelativeY;
            if (!subEntry.isHoveredOver(relativeMouseX, relativeMouseY)) continue;
            return subEntry.mouseScrolled(subRelativeMouseX, subRelativeMouseY, f, g);
        }
        return false;
    }

    public boolean mouseDragged(double relativeMouseX, double relativeMouseY, int i, double f, double g, MouseButtonInfo buttonInfo) {
        for (EditorListEntry subEntry : this.subEntries) {
            double subRelativeMouseX = relativeMouseX - (double)subEntry.entryRelativeX;
            double subRelativeMouseY = relativeMouseY - (double)subEntry.entryRelativeY;
            subEntry.mouseDragged(subRelativeMouseX, subRelativeMouseY, i, f, g, buttonInfo);
        }
        return false;
    }

    public void mouseMoved(double relativeMouseX, double relativeMouseY) {
    }

    public boolean keyPressed(KeyEvent event, boolean isRoot) {
        if (isRoot) {
            if (event.key() == 80 && this.moveFocus(-1)) {
                return false;
            }
            if (event.key() == 79 && this.moveFocus(1)) {
                return false;
            }
        }
        if (this.subEntries.isEmpty() || this.focusedSubEntryIndex < 0) {
            return false;
        }
        EditorListEntry subEntry = this.subEntries.get(this.focusedSubEntryIndex);
        return subEntry.keyPressed(event, false);
    }

    public boolean keyReleased(KeyEvent event) {
        if (this.subEntries.isEmpty()) {
            return false;
        }
        for (EditorListEntry subEntry : this.subEntries) {
            subEntry.keyReleased(event);
        }
        return false;
    }

    public boolean charTyped(CharacterEvent event) {
        if (this.subEntries.isEmpty() || this.focusedSubEntryIndex < 0) {
            return false;
        }
        EditorListEntry subEntry = this.subEntries.get(this.focusedSubEntryIndex);
        return subEntry.charTyped(event);
    }

    public void tick() {
        if (this.subEntries.isEmpty()) {
            return;
        }
        for (EditorListEntry subEntry : this.subEntries) {
            subEntry.tick();
        }
    }

    public String getSubNarration() {
        if (this.hoveredSubEntry == null) {
            return this.getSelectedNarration();
        }
        return this.getHoveredNarration();
    }

    public String getHoveredNarration() {
        if (this.hoveredSubEntry == null) {
            return this.getHoverNarration();
        }
        return this.hoveredSubEntry.getHoveredNarration();
    }

    public String getSelectedNarration() {
        if (this.focusedSubEntryIndex == -1) {
            return this.getNarration();
        }
        EditorListEntry subEntry = this.subEntries.get(this.focusedSubEntryIndex);
        return subEntry.getSelectedNarration();
    }

    public Supplier<Tooltip> getTooltipSupplier() {
        return this.tooltipSupplier;
    }

    public abstract Component getMessage();

    public Component getNarrationMessage() {
        return this.getMessage();
    }

    public String getNarration() {
        StringBuilder narrationBuilder = new StringBuilder();
        narrationBuilder.append(this.getNarrationMessage().getString());
        if (this.tooltipSupplier == null) {
            return narrationBuilder.toString();
        }
        Tooltip tooltip = this.tooltipSupplier.get();
        if (tooltip != null) {
            narrationBuilder.append(" . ").append(this.tooltipSupplier.get().getPlainText());
        }
        return narrationBuilder.toString();
    }

    public String getHoverNarration() {
        return this.getNarration();
    }

    public void preRender(GuiGraphicsExtractor guiGraphics, boolean includesSelected, boolean isRoot) {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate((float)this.entryRelativeX, (float)this.entryRelativeY);
        if (!includesSelected || this.focusedSubEntryIndex != -1) {
            return;
        }
        guiGraphics.fill(0, 0, this.entryW, this.entryH, this.rowList.isFocused() ? -1 : -8355712);
        guiGraphics.fill(1, 1, this.entryW - 1, this.entryH - 1, -16777216);
    }

    public EditorListEntry render(GuiGraphicsExtractor guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, Font font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        this.hoveredSubEntry = null;
        EditorListEntry result = isMouseOver ? this : null;
        for (int i = 0; i < this.subEntries.size(); ++i) {
            EditorListEntry subEntry = this.subEntries.get(i);
            boolean subIsHovered = subEntry.isHoveredOver(relativeMouseX, relativeMouseY);
            boolean subIncludesSelected = includesSelected && this.focusedSubEntryIndex == i;
            subEntry.preRender(guiGraphics, subIncludesSelected, false);
            EditorListEntry subResult = subEntry.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX - subEntry.entryRelativeX, relativeMouseY - subEntry.entryRelativeY, subIsHovered, partialTicks, font, globalMouseX, globalMouseY, subIncludesSelected, false);
            subEntry.postRender(guiGraphics);
            if (!subIsHovered) continue;
            this.hoveredSubEntry = subEntry;
            result = subResult;
        }
        return result;
    }

    public void postRender(GuiGraphicsExtractor guiGraphics) {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.popMatrix();
    }

    public boolean isHoveredOver(double relativeMouseX, double relativeMouseY) {
        return relativeMouseX >= (double)this.entryRelativeX && relativeMouseX < (double)(this.entryRelativeX + this.entryW) && relativeMouseY >= (double)this.entryRelativeY && relativeMouseY < (double)(this.entryRelativeY + this.entryH);
    }

    protected abstract boolean selectAction();

    public void setFocused(boolean bl) {
    }

    public void unhoverRecursively() {
        if (this.hoveredSubEntry == null) {
            return;
        }
        this.hoveredSubEntry.unhoverRecursively();
        this.hoveredSubEntry = null;
    }

    public boolean moveFocus(int direction) {
        this.unhoverRecursively();
        if (!this.moveFocus(direction, true)) {
            return false;
        }
        this.rowList.narrateSelection();
        return true;
    }

    public boolean moveFocus(int direction, boolean isRoot) {
        EditorListEntry focusedSub;
        if (this.subEntries.isEmpty()) {
            return false;
        }
        if (this.focusedSubEntryIndex >= 0 && (focusedSub = this.subEntries.get(this.focusedSubEntryIndex)).moveFocus(direction, false)) {
            return true;
        }
        int potentialValue = this.focusedSubEntryIndex + direction;
        if (potentialValue < 0 || potentialValue >= this.subEntries.size()) {
            if (!isRoot) {
                return false;
            }
            int n = potentialValue = potentialValue < 0 ? this.subEntries.size() - 1 : 0;
        }
        if (this.focusedSubEntryIndex == potentialValue) {
            return false;
        }
        this.focusedSubEntryIndex = potentialValue;
        focusedSub = this.subEntries.get(this.focusedSubEntryIndex);
        if (direction < 0) {
            focusedSub.focusLastRecursively();
            return true;
        }
        focusedSub.focusFirstRecursively();
        return true;
    }

    public void unfocusRecursively() {
        this.setFocused(false);
        if (this.subEntries.isEmpty()) {
            return;
        }
        if (this.focusedSubEntryIndex >= 0) {
            this.subEntries.get(this.focusedSubEntryIndex).unfocusRecursively();
        }
        this.focusedSubEntryIndex = -1;
    }

    public void focusFirstRecursively() {
        this.setFocused(true);
        if (this.subEntries.isEmpty()) {
            return;
        }
        this.focusedSubEntryIndex = 0;
        this.subEntries.get(this.focusedSubEntryIndex).focusFirstRecursively();
    }

    public void focusLastRecursively() {
        this.setFocused(true);
        if (this.subEntries.isEmpty()) {
            return;
        }
        this.focusedSubEntryIndex = this.subEntries.size() - 1;
        this.subEntries.get(this.focusedSubEntryIndex).focusLastRecursively();
    }

    public EditorListEntry withSubEntry(EditorListEntry entry) {
        this.subEntries.add(entry);
        return this;
    }

    public int getEntryRelativeX() {
        return this.entryRelativeX;
    }

    public int getEntryRelativeY() {
        return this.entryRelativeY;
    }
}

