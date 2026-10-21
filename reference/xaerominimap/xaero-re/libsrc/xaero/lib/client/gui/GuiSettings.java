/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.lib.XaeroLib;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.util.GuiUtils;
import xaero.lib.client.gui.widget.MyTinyButton;
import xaero.lib.client.gui.widget.online.WidgetScreen;
import xaero.lib.common.util.KeySortableByOther;

public abstract class GuiSettings
extends ScreenBase
implements WidgetScreen {
    protected int entriesPerPage = 12;
    protected ISettingEntry[] entries;
    protected String entryFilter = "";
    private boolean foundSomething;
    protected Component screenTitle;
    protected int page;
    protected int maxPage;
    private MyTinyButton nextButton;
    private MyTinyButton prevButton;
    protected EditBox searchField;
    protected boolean canSearch = true;
    private boolean shouldRefocusSearch;
    protected boolean confirmButton;

    public GuiSettings(Component title, Screen backScreen, Screen escScreen) {
        this(title, backScreen, escScreen, false);
    }

    public GuiSettings(Component title, Screen backScreen, Screen escScreen, boolean confirmButton) {
        super(backScreen, escScreen, title);
        this.confirmButton = confirmButton;
    }

    @Override
    public void init() {
        super.init();
        this.screenTitle = this.title;
        if (this.confirmButton) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_confirm"), b -> this.confirm()).bounds(this.width / 2 - 105, this.height / 6 + 168, 100, 20).build());
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_back"), b -> this.goBack()).bounds(this.width / 2 + 5, this.height / 6 + 168, 100, 20).build());
        } else {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_back"), b -> this.goBack()).bounds(this.width / 2 - 100, this.height / 6 + 168, 200, 20).build());
        }
        int verticalOffset = this.getVerticalOffset();
        if (this.entries != null) {
            ArrayList<KeySortableByOther<ISettingEntry>> sortingList = new ArrayList<KeySortableByOther<ISettingEntry>>();
            String comparisonFilter = this.entryFilter.toLowerCase();
            for (int i = 0; i < this.entries.length; ++i) {
                int positionInEntryString;
                ISettingEntry entry = this.entries[i];
                String entrySearchString = entry.getStringForSearch().toLowerCase();
                if (entrySearchString == null || (positionInEntryString = entrySearchString.indexOf(comparisonFilter)) == -1) continue;
                KeySortableByOther<ISettingEntry> sortableEntry = new KeySortableByOther<ISettingEntry>(entry, Integer.valueOf(positionInEntryString));
                sortingList.add(sortableEntry);
            }
            ArrayList filteredEntries = sortingList.stream().sorted().map(KeySortableByOther::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            if (!filteredEntries.isEmpty()) {
                this.foundSomething = true;
                this.maxPage = (int)Math.ceil((double)filteredEntries.size() / (double)this.entriesPerPage) - 1;
                if (this.page > this.maxPage) {
                    this.page = this.maxPage;
                }
                int firstEntry = this.entriesPerPage * this.page;
                int entryCount = Math.min(filteredEntries.size() - firstEntry, this.entriesPerPage);
                for (int i = 0; i < entryCount; ++i) {
                    ISettingEntry entry = (ISettingEntry)filteredEntries.get(firstEntry + i);
                    AbstractWidget optionWidget = entry.createWidget(this.width / 2 - 205 + i % 2 * 210, this.height / 7 + 5 + verticalOffset + 24 * (i >> 1), 200);
                    this.addRenderableWidget((GuiEventListener)optionWidget);
                }
            } else {
                this.foundSomething = false;
                this.page = 0;
                this.maxPage = 0;
            }
        }
        this.screenTitle = this.screenTitle.plainCopy().append(" (" + (this.page + 1) + "/" + (this.maxPage + 1) + ")");
        this.nextButton = new MyTinyButton(this.width / 2 + 131, this.height / 7 + 149 + verticalOffset, (Component)Component.translatable((String)"gui.xaero_next", (Object[])new Object[0]), b -> this.onNextButton());
        this.prevButton = new MyTinyButton(this.width / 2 - 205, this.height / 7 + 149 + verticalOffset, (Component)Component.translatable((String)"gui.xaero_previous", (Object[])new Object[0]), b -> this.onPrevButton());
        if (this.maxPage > 0) {
            this.addRenderableWidget((GuiEventListener)this.nextButton);
            this.addRenderableWidget((GuiEventListener)this.prevButton);
            this.nextButton.active = this.page < this.maxPage;
            this.prevButton.active = this.page > 0;
        }
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().initialize(this, this.width, this.height);
        boolean shouldFocusSearch = this.shouldRefocusSearch;
        if (this.canSearch && this.entries != null) {
            int cursorPos = 0;
            if (shouldFocusSearch) {
                cursorPos = this.searchField.getCursorPosition();
            }
            this.searchField = new EditBox(this.font, this.width / 2 - 100, this.height / 7 + 5 + verticalOffset - 24, 200, 20, (Component)Component.translatable((String)"gui.xaero_settings_search"));
            this.searchField.setValue(this.entryFilter);
            if (shouldFocusSearch) {
                this.setFocused((GuiEventListener)this.searchField);
                this.searchField.setFocused(true);
                this.searchField.setCursorPosition(cursorPos);
                this.searchField.setHighlightPos(cursorPos);
            }
            this.searchField.setResponder(s -> {
                if (this.canSearch) {
                    this.updateSearch();
                }
            });
            this.addWidget(this.searchField);
        } else {
            this.searchField = null;
        }
    }

    protected void confirm() {
        this.onExit(this.escape);
    }

    protected int getVerticalOffset() {
        return this.canSearch ? 24 : 0;
    }

    protected void setInitialFocus() {
        if (this.shouldRefocusSearch) {
            this.shouldRefocusSearch = false;
            return;
        }
        super.setInitialFocus();
    }

    public void tick() {
        super.tick();
        if (this.canSearch) {
            // empty if block
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractBackground(guiGraphics, par1, par2, par3);
        int verticalOffset = this.getVerticalOffset();
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().render(guiGraphics, this, this.width, this.height, par1, par2, this.minecraft.getWindow().getGuiScale());
        guiGraphics.centeredText(this.minecraft.font, this.screenTitle, this.width / 2, this.height / 7 + 29 - 42, -1);
    }

    @Override
    protected void renderPreDropdown(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        if (this.searchField != null) {
            if (!this.foundSomething && this.entries != null) {
                guiGraphics.centeredText(this.minecraft.font, I18n.get((String)"gui.xaero_settings_not_found", (Object[])new Object[0]), this.width / 2, this.height / 7 + 34, -1);
            }
            if (!this.searchField.isFocused() && this.searchField.getValue().isEmpty()) {
                GuiUtils.setFieldText(this.searchField, I18n.get((String)"gui.xaero_settings_search_placeholder", (Object[])new Object[0]), -11184811);
                this.searchField.moveCursorTo(0, false);
            }
            this.searchField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
            if (!this.searchField.isFocused()) {
                GuiUtils.setFieldText(this.searchField, this.entryFilter);
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractRenderState(guiGraphics, par1, par2, par3);
        this.renderTooltips(guiGraphics, par1, par2, par3);
    }

    @Override
    public void addButtonVisible(AbstractWidget button) {
        this.addRenderableWidget((GuiEventListener)button);
    }

    @Override
    public <S extends Screen> S getScreen() {
        return (S)this;
    }

    @Override
    public void onExit(Screen screen) {
        super.onExit(screen);
    }

    public boolean keyPressed(KeyEvent event) {
        return super.keyPressed(event) || this.getFocused() instanceof EditBox && this.getFocused().isFocused();
    }

    public boolean charTyped(CharacterEvent event) {
        boolean result = super.charTyped(event);
        return result;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().handleClick(this, this.width, this.height, (int)event.x(), (int)event.y(), this.minecraft.getWindow().getGuiScale());
        return super.mouseClicked(event, doubleClick);
    }

    protected void onNextButton() {
        ++this.page;
        this.init(this.width, this.height);
    }

    protected void onPrevButton() {
        --this.page;
        this.init(this.width, this.height);
    }

    public ISettingEntry[] getEntriesCopy() {
        if (this.entries == null) {
            return null;
        }
        ISettingEntry[] result = new ISettingEntry[this.entries.length];
        System.arraycopy(this.entries, 0, result, 0, this.entries.length);
        return result;
    }

    private void updateSearch() {
        String newValue;
        if (this.searchField.isFocused() && !Objects.equals(this.entryFilter, newValue = this.searchField.getValue())) {
            this.entryFilter = this.searchField.getValue();
            this.shouldRefocusSearch = true;
            this.page = 0;
            this.init(this.width, this.height);
        }
    }

    public boolean isInGameUi() {
        return true;
    }
}

