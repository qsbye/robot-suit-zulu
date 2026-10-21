/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import xaero.lib.XaeroLib;
import xaero.lib.client.controls.util.MouseUtils;
import xaero.lib.client.gui.IScreenBase;
import xaero.lib.client.gui.widget.ITooltipHaver;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;

public class ScreenBase
extends Screen
implements IScreenBase {
    public Screen parent;
    public Screen escape;
    protected boolean canSkipWorldRender;
    protected DropDownWidget openDropdown;
    private List<DropDownWidget> dropdowns;
    protected boolean shouldRefresh;

    protected ScreenBase(Screen parent, Screen escape, Component titleIn) {
        super(titleIn);
        this.parent = parent;
        this.escape = escape;
        this.dropdowns = new ArrayList<DropDownWidget>();
    }

    public void onExit(Screen screen) {
        this.minecraft.gui.setScreen(screen);
    }

    public void goBack() {
        this.onExit(this.parent);
    }

    public void onClose() {
        this.onExit(this.escape);
    }

    public void renderEscapeScreen(GuiGraphicsExtractor guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (this.escape != null) {
            this.escape.extractRenderStateWithTooltipAndSubtitles(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
        }
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        this.handleRefresh();
        super.extractBackground(guiGraphics, mouseX, mouseY, partial);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        this.handleRefresh();
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        for (DropDownWidget dropdown : this.dropdowns) {
            dropdown.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        }
        if (this.openDropdown != null) {
            this.openDropdown.render(guiGraphics, mouseX, mouseY, this.height, false);
        }
    }

    protected void renderPreDropdown(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
    }

    protected void init() {
        super.init();
        this.dropdowns.clear();
        this.openDropdown = null;
        if (this.escape != null) {
            this.escape.init(this.width, this.height);
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.openDropdown != null) {
            if (!this.openDropdown.onDropDown((int)event.x(), (int)event.y(), this.height)) {
                this.openDropdown.setClosed(true);
                this.openDropdown = null;
            } else {
                this.openDropdown.mouseClicked(event, doubleClick);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double g, double wheel) {
        if (this.openDropdown != null) {
            if (this.openDropdown.onDropDown((int)mouseX, (int)mouseY, this.height)) {
                return this.openDropdown.mouseScrolled(mouseX, mouseY, g, wheel);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, g, wheel);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.openDropdown != null && this.openDropdown.mouseReleased(event)) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean shouldSkipWorldRender() {
        return this.canSkipWorldRender && this.escape instanceof IScreenBase && ((IScreenBase)this.escape).shouldSkipWorldRender();
    }

    protected boolean renderTooltips(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        if (this.openDropdown != null && this.openDropdown.isHovered()) {
            return false;
        }
        boolean result = XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().renderTooltips(guiGraphics, this, this.width, this.height, par1, par2, this.minecraft.getWindow().getGuiScale());
        boolean mousePressed = MouseUtils.isMouseButtonDown(1);
        for (GuiEventListener el : this.children()) {
            Tooltip tooltip;
            AbstractWidget b;
            if (!(el instanceof AbstractWidget) || !((b = (AbstractWidget)el) instanceof ITooltipHaver) || b instanceof AbstractSliderButton && mousePressed) continue;
            ITooltipHaver optionWidget = (ITooltipHaver)b;
            if (par1 < b.getX() || par2 < b.getY() || par1 >= b.getX() + b.getWidth() || par2 >= b.getY() + b.getHeight() || optionWidget.getXaero_tooltip() == null || (tooltip = optionWidget.getXaero_tooltip().get()) == null) continue;
            tooltip.drawBox(guiGraphics, par1, par2, this.width, this.height);
            result = true;
            break;
        }
        return result;
    }

    @Override
    public void onDropdownOpen(DropDownWidget menu) {
        if (this.openDropdown != null && this.openDropdown != menu) {
            this.openDropdown.setClosed(true);
        }
        this.openDropdown = menu;
    }

    @Override
    public void onDropdownClosed(DropDownWidget menu) {
        if (menu != this.openDropdown && this.openDropdown != null) {
            this.openDropdown.setClosed(true);
        }
        this.openDropdown = null;
    }

    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T guiEventListener) {
        if (guiEventListener instanceof DropDownWidget) {
            this.dropdowns.add((DropDownWidget)guiEventListener);
        }
        return (T)super.addWidget(guiEventListener);
    }

    private void handleDropdownReplacement(AbstractWidget current, AbstractWidget replacement) {
        int dropdownIndex = this.dropdowns.indexOf(current);
        if (dropdownIndex != -1) {
            this.dropdowns.set(dropdownIndex, (DropDownWidget)replacement);
        }
        if (this.getFocused() == current) {
            this.setFocused((GuiEventListener)replacement);
        }
    }

    private void replaceWidget(AbstractWidget current, AbstractWidget replacement, boolean renderable) {
        int childIndex = this.children().indexOf(current);
        if (childIndex != -1) {
            super.removeWidget((GuiEventListener)current);
            if (renderable) {
                super.addRenderableWidget((GuiEventListener)replacement);
            } else {
                super.addWidget((GuiEventListener)replacement);
            }
            this.children().remove(replacement);
            this.children().add(childIndex, replacement);
        }
        this.handleDropdownReplacement(current, replacement);
    }

    public void replaceWidget(AbstractWidget current, AbstractWidget replacement) {
        this.replaceWidget(current, replacement, false);
    }

    public void replaceRenderableWidget(AbstractWidget current, AbstractWidget replacement) {
        this.replaceWidget(current, replacement, true);
    }

    protected void removeWidget(GuiEventListener current) {
        this.dropdowns.remove(current);
        super.removeWidget(current);
    }

    @Override
    public Screen getEscape() {
        return this.escape;
    }

    public static Screen tryToGetEscape(Screen screen) {
        if (screen instanceof IScreenBase) {
            IScreenBase screenBase = (IScreenBase)screen;
            return screenBase.getEscape();
        }
        return null;
    }

    protected void rebuildWidgets() {
        super.rebuildWidgets();
        this.handleRefresh();
    }

    public void refresh() {
        this.shouldRefresh = true;
    }

    protected final void handleRefresh() {
        if (!this.shouldRefresh) {
            return;
        }
        this.shouldRefresh = false;
        int focusedIndex = this.getIndex(this.getFocused());
        this.rebuildWidgets();
        this.restoreFocus(focusedIndex);
    }

    public void restoreFocus(int index) {
        if (index == -1) {
            return;
        }
        try {
            GuiEventListener child = (GuiEventListener)this.children().get(index);
            this.setFocused(child);
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
    }

    public int getIndex(GuiEventListener child) {
        List children = this.children();
        for (int i = 0; i < children.size(); ++i) {
            if (children.get(i) != child) continue;
            return i;
        }
        return -1;
    }

    public boolean canSkipWorldRender() {
        return this.canSkipWorldRender;
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown((int)225) || InputConstants.isKeyDown((int)229);
    }

    public static boolean hasControlDown() {
        return InputConstants.isKeyDown((int)224) || InputConstants.isKeyDown((int)228);
    }
}

