/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Vector3f;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListEntryWithRootReference;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.widget.EditorTextField;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorListEntryWidget
extends EditorListEntryWithRootReference {
    protected AbstractWidget widget;
    private boolean widgetPressed;

    public EditorListEntryWidget(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, AbstractWidget widget, Supplier<Tooltip> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root, tooltipSupplier);
        this.widget = widget;
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    @Override
    public boolean mouseClicked(GuiCategoryEditor.SettingRowList.Entry entry, double relativeMouseX, double relativeMouseY, int i, MouseButtonInfo buttonInfo, boolean doubleClick) {
        boolean result = super.mouseClicked(entry, relativeMouseX, relativeMouseY, i, buttonInfo, doubleClick);
        if (result) {
            return true;
        }
        if (this.widget instanceof AbstractButton) {
            return false;
        }
        if (!this.widget.isMouseOver(relativeMouseX, relativeMouseY)) {
            return false;
        }
        this.widgetPressed = true;
        return this.widget.mouseClicked(new MouseButtonEvent(relativeMouseX, relativeMouseY, buttonInfo), doubleClick);
    }

    @Override
    public boolean mouseReleased(double relativeMouseX, double relativeMouseY, int i, MouseButtonInfo buttonInfo) {
        if (this.widgetPressed) {
            this.widget.mouseReleased(new MouseButtonEvent(relativeMouseX, relativeMouseY, buttonInfo));
        }
        this.widgetPressed = false;
        super.mouseReleased(relativeMouseX, relativeMouseY, i, buttonInfo);
        return false;
    }

    @Override
    public boolean mouseDragged(double relativeMouseX, double relativeMouseY, int i, double f, double g, MouseButtonInfo buttonInfo) {
        if (this.widgetPressed && this.widget.mouseDragged(new MouseButtonEvent(relativeMouseX, relativeMouseY, buttonInfo), f, g)) {
            return true;
        }
        return super.mouseDragged(relativeMouseX, relativeMouseY, i, f, g, buttonInfo);
    }

    @Override
    public boolean mouseScrolled(double relativeMouseX, double relativeMouseY, double f, double g) {
        if (this.widget.isMouseOver(relativeMouseX, relativeMouseY) && this.widget.mouseScrolled(relativeMouseX, relativeMouseY, f, g)) {
            return true;
        }
        return super.mouseScrolled(relativeMouseX, relativeMouseY, f, g);
    }

    @Override
    public void mouseMoved(double relativeMouseX, double relativeMouseY) {
        this.widget.mouseMoved(relativeMouseX, relativeMouseY);
        super.mouseMoved(relativeMouseX, relativeMouseY);
    }

    @Override
    public boolean keyPressed(KeyEvent event, boolean isRoot) {
        if (this.widget.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event, isRoot);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (this.widget.keyReleased(event)) {
            return true;
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.widget.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public String getNarration() {
        return super.getNarration();
    }

    @Override
    public String getHoverNarration() {
        return this.getNarration();
    }

    @Override
    public Component getMessage() {
        return this.widget.getMessage();
    }

    @Override
    public Component getNarrationMessage() {
        if (this.widget instanceof EditorTextField) {
            return ((EditorTextField)this.widget).createNarrationMessage();
        }
        if (this.widget instanceof AbstractSliderButton) {
            return Component.translatable((String)"gui.narrate.slider", (Object[])new Object[]{this.getMessage()});
        }
        return Component.translatable((String)"gui.narrate.button", (Object[])new Object[]{this.getMessage()});
    }

    @Override
    public EditorListEntry render(GuiGraphicsExtractor guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, Font font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        Matrix3x2fStack poseStack = guiGraphics.pose();
        Vector3f widgetPos = new Vector3f((float)this.widget.getX(), (float)this.widget.getY(), 1.0f);
        widgetPos.mul((Matrix3x2fc)poseStack);
        int xBU = this.widget.getX();
        int yBU = this.widget.getY();
        this.widget.setX((int)widgetPos.x());
        this.widget.setY((int)widgetPos.y());
        poseStack.pushMatrix();
        poseStack.identity();
        this.widget.extractRenderState(guiGraphics, globalMouseX, globalMouseY, partialTicks);
        poseStack.popMatrix();
        this.widget.setX(xBU);
        this.widget.setY(yBU);
        return this.widgetPressed ? null : result;
    }

    @Override
    public void setFocused(boolean bl) {
        if (this.widget.active && this.widget.visible && this.widget.isFocused() != bl) {
            this.widget.setFocused(bl);
        }
        super.setFocused(bl);
    }

    @Override
    protected boolean selectAction() {
        if (!(this.widget instanceof AbstractButton) || !this.widget.active) {
            return false;
        }
        ((AbstractButton)this.widget).onPress((InputWithModifiers)new KeyEvent(40, 0, 0));
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        return false;
    }
}

