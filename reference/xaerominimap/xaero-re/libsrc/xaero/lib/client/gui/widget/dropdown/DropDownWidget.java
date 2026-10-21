/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.dropdown;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.sounds.SoundEvents;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.client.gui.widget.dropdown.IDropDownWidgetCallback;
import xaero.lib.common.util.ComponentUtils;

public class DropDownWidget
extends AbstractWidget {
    private static final Component EMPTY_OPTION = Component.literal((String)"-");
    private static final Component SHORTENING = Component.literal((String)"...");
    public static final int DEFAULT_BACKGROUND = -939524096;
    public static final int SELECTED_DEFAULT_BACKGROUND = -922757376;
    public static final int SELECTED_DEFAULT_HOVERED_BACKGROUND = -10496;
    public static final int TRIM = -6250336;
    public static final int TRIM_OPEN = -1;
    public static final int TRIM_INSIDE = -13487566;
    public static final int LINE_HEIGHT = 11;
    private int xOffset = 0;
    private int yOffset = 0;
    private Component[] realOptions;
    private Component[] options;
    private int selected = 0;
    private boolean closed = true;
    private int scroll;
    private long scrollTime;
    private int autoScrolling;
    protected boolean openingUp;
    private final IDropDownWidgetCallback callback;
    private final IDropDownContainer container;
    private final boolean hasEmptyOption;
    protected int selectedBackground;
    protected int selectedHoveredBackground;
    private boolean wasHovered;

    protected DropDownWidget(Component[] options, int x, int y, int w, Integer selected, boolean openingUp, IDropDownWidgetCallback callback, IDropDownContainer container, boolean hasEmptyOption, Component narrationTitle) {
        super(x, y, w, 11, narrationTitle);
        this.realOptions = options;
        this.callback = callback;
        this.container = container;
        int emptyOptionCount = hasEmptyOption ? 1 : 0;
        this.options = new Component[this.realOptions.length + emptyOptionCount];
        System.arraycopy(this.realOptions, 0, this.options, emptyOptionCount, this.realOptions.length);
        this.shortenOptions();
        this.selectId(selected, false);
        this.openingUp = openingUp;
        this.hasEmptyOption = hasEmptyOption;
        this.selectedBackground = -922757376;
        this.selectedHoveredBackground = -10496;
        this.active = true;
    }

    protected boolean shouldAddShorteningIndicator() {
        return true;
    }

    private void shortenOptions() {
        for (int i = 0; i < this.options.length; ++i) {
            if (this.options[i] == null || Minecraft.getInstance().font.width((FormattedText)this.options[i]) <= this.width - 2) continue;
            this.options[i] = ComponentUtils.shortenComponent(Minecraft.getInstance().font, this.options[i], this.width - 2);
            if (!this.shouldAddShorteningIndicator()) continue;
            this.options[i].getSiblings().add(SHORTENING);
        }
    }

    public int size() {
        return this.realOptions.length;
    }

    public int getXWithOffset() {
        return this.getX() + this.xOffset;
    }

    public int getRenderY() {
        return this.getY() + (this.openingUp ? 11 : 0);
    }

    public int getRenderYWithOffset() {
        return this.getRenderY() + this.yOffset;
    }

    private void drawSlot(GuiGraphicsExtractor guiGraphics, Component text, int slotIndex, int pos, int mouseX, int mouseY, boolean scrolling, int optionLimit, int xWithOffset, int yWithOffset) {
        int slotBackground;
        int emptyOptionCount;
        int n = emptyOptionCount = this.hasEmptyOption ? 1 : 0;
        if (this.closed && this.isHoveredOrFocused() || !this.closed && this.onDropDownSlot(mouseX, mouseY, slotIndex, scrolling, optionLimit)) {
            slotBackground = slotIndex - emptyOptionCount == this.selected ? this.selectedHoveredBackground : -13487566;
        } else {
            int n2 = slotBackground = slotIndex - emptyOptionCount == this.selected ? this.selectedBackground : -939524096;
        }
        if (this.openingUp) {
            pos = -pos - 1;
        }
        guiGraphics.fill(xWithOffset, yWithOffset + 11 * pos, xWithOffset + this.width, yWithOffset + 11 + 11 * pos, slotBackground);
        guiGraphics.horizontalLine(xWithOffset + 1, xWithOffset + this.width - 1, yWithOffset + 11 * pos, -13487566);
        int textColor = -1;
        guiGraphics.centeredText(Minecraft.getInstance().font, text, xWithOffset + 1 + this.width / 2, yWithOffset + 2 + 11 * pos, textColor);
    }

    private void drawMenu(GuiGraphicsExtractor guiGraphics, int amount, int mouseX, int mouseY, int scaledHeight, int optionLimit) {
        int first;
        boolean scrolling = this.scrolling(optionLimit);
        int totalH = 11 * (amount + (scrolling ? 2 : 0));
        int height = scaledHeight;
        int renderY = this.getRenderY();
        this.yOffset = !this.openingUp && renderY + totalH + 1 > height ? height - renderY - totalH - 1 : (this.openingUp && renderY - totalH < 0 ? totalH - renderY : 0);
        int xWithOffset = this.getXWithOffset();
        int yWithOffset = this.getRenderYWithOffset();
        int n = first = this.closed ? 0 : this.scroll;
        if (scrolling) {
            this.drawSlot(guiGraphics, (Component)Component.translatable((String)(this.openingUp ? "gui.xaero_down" : "gui.xaero_up")).withStyle(this.scroll == 0 ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY), -1, 0, mouseX, mouseY, scrolling, optionLimit, xWithOffset, yWithOffset);
            this.drawSlot(guiGraphics, (Component)Component.translatable((String)(this.openingUp ? "gui.xaero_up" : "gui.xaero_down")).withStyle(this.scroll + optionLimit >= this.options.length ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY), -2, amount + 1, mouseX, mouseY, scrolling, optionLimit, xWithOffset, yWithOffset);
        }
        for (int i = first; i < first + amount; ++i) {
            PlainTextContents.LiteralContents literalContents;
            Component slotText = this.hasEmptyOption && i == 0 ? (!this.closed ? EMPTY_OPTION : this.realOptions[this.selected]) : this.options[i];
            ComponentContents componentContents = slotText.getContents();
            if (componentContents instanceof PlainTextContents.LiteralContents && (literalContents = (PlainTextContents.LiteralContents)componentContents).text().contains("\u00a7\u00a7")) {
                slotText = Component.literal((String)literalContents.text().replace("\u00a7\u00a7", ":")).withStyle(slotText.getStyle());
            }
            this.drawSlot(guiGraphics, slotText, i, i - first + (scrolling ? 1 : 0), mouseX, mouseY, scrolling, optionLimit, xWithOffset, yWithOffset);
        }
        int trimPosY = yWithOffset - (this.openingUp ? totalH : 0);
        int trim = this.closed ? -6250336 : -1;
        guiGraphics.verticalLine(xWithOffset, trimPosY, trimPosY + totalH, trim);
        guiGraphics.verticalLine(xWithOffset + this.width, trimPosY, trimPosY + totalH, trim);
        guiGraphics.horizontalLine(xWithOffset, xWithOffset + this.width, trimPosY, trim);
        guiGraphics.horizontalLine(xWithOffset, xWithOffset + this.width, trimPosY + totalH, trim);
    }

    private boolean scrolling(int optionLimit) {
        return this.options.length > optionLimit && !this.closed;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, int scaledHeight) {
        if (mouseButton != 1) {
            return false;
        }
        if (!this.closed) {
            int optionLimit = this.optionLimit(scaledHeight);
            int clickedId = this.getHoveredId(mouseX, mouseY, this.scrolling(optionLimit), optionLimit);
            if (clickedId >= 0) {
                this.selectId(clickedId - (this.hasEmptyOption ? 1 : 0), true);
            } else {
                this.autoScrolling = clickedId == -1 ? 1 : -1;
                this.scrollTime = System.currentTimeMillis();
                this.mouseScrolledInternal(this.autoScrolling, mouseX, mouseY, optionLimit);
            }
            Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        } else if (this.options.length > 1 && this.active) {
            this.setClosed(false);
            this.scroll = 0;
        }
        return true;
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton, int scaledHeight) {
        this.autoScrolling = 0;
    }

    private int getHoveredId(int mouseX, int mouseY, boolean scrolling, int optionLimit) {
        int yOnMenu = mouseY - this.getRenderYWithOffset();
        int visibleSlotIndex = (this.openingUp ? -yOnMenu - 1 : yOnMenu) / 11;
        if (scrolling && visibleSlotIndex == 0) {
            return -1;
        }
        if (visibleSlotIndex >= optionLimit + (scrolling ? 1 : 0)) {
            return -2;
        }
        int slot = this.scroll + visibleSlotIndex - (scrolling ? 1 : 0);
        if (slot >= this.options.length) {
            slot = this.options.length - 1;
        }
        return slot;
    }

    public boolean onDropDown(int mouseX, int mouseY, int scaledHeight) {
        int optionLimit = this.optionLimit(scaledHeight);
        return this.onDropDown(mouseX, mouseY, this.scrolling(optionLimit), optionLimit);
    }

    public boolean onDropDown(int mouseX, int mouseY, boolean scrolling, int optionLimit) {
        int menuHeight;
        int menuTop = this.getRenderYWithOffset();
        int n = this.closed ? 11 : (menuHeight = (Math.min(this.options.length, optionLimit) + (scrolling ? 2 : 0)) * 11);
        if (this.openingUp) {
            menuTop -= menuHeight;
        }
        int xOnMenu = mouseX - this.getXWithOffset();
        int yOnMenu = mouseY - menuTop;
        return xOnMenu >= 0 && yOnMenu >= 0 && xOnMenu <= this.width && yOnMenu < menuHeight;
    }

    private boolean onDropDownSlot(int mouseX, int mouseY, int id, boolean scrolling, int optionLimit) {
        if (!this.onDropDown(mouseX, mouseY, scrolling, optionLimit)) {
            return false;
        }
        int hoveredSlot = this.getHoveredId(mouseX, mouseY, scrolling, optionLimit);
        return hoveredSlot == id;
    }

    public void selectId(int id, boolean callCallback) {
        boolean newId;
        if (id == -1) {
            this.setClosed(true);
            return;
        }
        if (id < 0 || id >= this.realOptions.length) {
            id = 0;
        }
        boolean bl = newId = id != this.selected;
        if (newId && (!callCallback || this.callback.onSelected(this, id))) {
            this.selected = id;
        }
        this.setClosed(true);
    }

    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        int scaledHeight = Minecraft.getInstance().gui.screen().height;
        boolean bl = this.isHovered = this.visible && this.onDropDown(mouseX, mouseY, scaledHeight);
        if (!this.visible) {
            return;
        }
        this.render(guiGraphics, mouseX, mouseY, Minecraft.getInstance().gui.screen().height, true);
        if (this.isHovered) {
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int scaledHeight, boolean closedOnly) {
        if (!this.closed && closedOnly) {
            return;
        }
        int optionLimit = this.optionLimit(scaledHeight);
        if (this.autoScrolling != 0 && System.currentTimeMillis() - this.scrollTime > 100L) {
            this.scrollTime = System.currentTimeMillis();
            this.mouseScrolledInternal(this.autoScrolling, mouseX, mouseY, optionLimit);
        }
        this.drawMenu(guiGraphics, this.closed ? 1 : Math.min(optionLimit, this.options.length), mouseX, mouseY, scaledHeight, optionLimit);
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        if (this.closed != closed) {
            if (!closed) {
                Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
                this.container.onDropdownOpen(this);
            } else {
                this.container.onDropdownClosed(this);
            }
        }
        this.closed = closed;
    }

    public void mouseScrolled(int wheel, int mouseXScaled, int mouseYScaled, int scaledHeight) {
        this.mouseScrolledInternal(wheel * (this.openingUp ? -1 : 1), mouseXScaled, mouseYScaled, this.optionLimit(scaledHeight));
    }

    private void mouseScrolledInternal(int wheel, int mouseXScaled, int mouseYScaled, int optionLimit) {
        int newScroll = this.scroll - wheel;
        if (newScroll + optionLimit > this.options.length) {
            newScroll = this.options.length - optionLimit;
        }
        if (newScroll < 0) {
            newScroll = 0;
        }
        this.scroll = newScroll;
    }

    private int optionLimit(int scaledHeight) {
        return Math.max(1, scaledHeight / 11 - 2);
    }

    public int getSelected() {
        return this.selected;
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        if (this.getMessage() != null) {
            narrationElementOutput.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        }
    }

    protected MutableComponent createNarrationMessage() {
        MutableComponent narrationMessage = Component.translatable((String)"");
        narrationMessage.getSiblings().add(this.getMessage());
        narrationMessage.getSiblings().add(Component.literal((String)". "));
        narrationMessage.getSiblings().add(Component.translatable((String)"gui.xaero_dropdown_selected_narration", (Object[])new Object[]{this.realOptions[this.selected]}));
        return narrationMessage;
    }

    public void mouseMoved(double $$0, double $$1) {
        super.mouseMoved($$0, $$1);
    }

    public boolean mouseScrolled(double mouseXScaled, double mouseYScaled, double g, double wheel) {
        if (!this.isClosed()) {
            this.mouseScrolled((int)wheel, (int)mouseXScaled, (int)mouseYScaled, Minecraft.getInstance().gui.screen().height);
            return true;
        }
        return super.mouseScrolled(mouseXScaled, mouseYScaled, g, wheel);
    }

    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 40 || event.key() == 44) {
            int nextSelection;
            if (ScreenBase.hasShiftDown()) {
                nextSelection = this.getSelected() - 1;
                if (nextSelection < 0) {
                    nextSelection = this.realOptions.length - 1;
                }
            } else {
                nextSelection = (this.getSelected() + 1) % this.realOptions.length;
            }
            this.selectId(nextSelection, true);
            return true;
        }
        return super.keyPressed(event);
    }

    public boolean keyReleased(KeyEvent event) {
        return super.keyReleased(event);
    }

    public boolean charTyped(CharacterEvent event) {
        return super.charTyped(event);
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isHovered) {
            int scaledHeight = Minecraft.getInstance().gui.screen().height;
            return this.mouseClicked((int)event.x(), (int)event.y(), event.button(), scaledHeight);
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        int scaledHeight = Minecraft.getInstance().gui.screen().height;
        this.mouseReleased((int)event.x(), (int)event.y(), event.button(), scaledHeight);
        return false;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    public static final class Builder {
        private Component[] options;
        private int x;
        private int y;
        private int w;
        private Integer selected;
        private boolean openingUp;
        private IDropDownWidgetCallback callback;
        private IDropDownContainer container;
        private boolean hasEmptyOption;
        private Component narrationTitle;

        private Builder() {
        }

        public Builder setDefault() {
            this.setOptions((Component[])null);
            this.setX(0);
            this.setY(0);
            this.setW(0);
            this.setSelected(null);
            this.setOpeningUp(false);
            this.setCallback(null);
            this.setHasEmptyOption(true);
            this.setNarrationTitle(null);
            return this;
        }

        public Builder setOptions(String[] options) {
            this.options = ComponentUtils.convertFromStrings(options);
            return this;
        }

        public Builder setOptions(Component[] options) {
            this.options = options;
            return this;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setW(int w) {
            this.w = w;
            return this;
        }

        public Builder setSelected(Integer selected) {
            this.selected = selected;
            return this;
        }

        public Builder setOpeningUp(boolean openingUp) {
            this.openingUp = openingUp;
            return this;
        }

        public Builder setCallback(IDropDownWidgetCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setContainer(IDropDownContainer container) {
            this.container = container;
            return this;
        }

        public Builder setHasEmptyOption(boolean hasEmptyOption) {
            this.hasEmptyOption = hasEmptyOption;
            return this;
        }

        public Builder setNarrationTitle(Component narrationTitle) {
            this.narrationTitle = narrationTitle;
            return this;
        }

        public DropDownWidget build() {
            if (this.options == null || this.w == 0 || this.selected == null || this.callback == null || this.narrationTitle == null || this.container == null) {
                throw new IllegalStateException();
            }
            return new DropDownWidget(this.options, this.x, this.y, this.w, this.selected, this.openingUp, this.callback, this.container, this.hasEmptyOption, this.narrationTitle);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

