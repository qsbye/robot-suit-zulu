/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.joml.Matrix3x2fStack;
import xaero.common.IXaeroMinimap;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.EditorCategoryNodeConverter;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSettingsNode;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.Tooltip;

public abstract class GuiCategoryEditor<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, SD, ED>, CB extends ObjectCategory.Builder<C, CB>, SD extends EditorSettingsNode<?>, SDB extends EditorSettingsNode.Builder<SD, SDB>, EDB extends EditorCategoryNode.Builder<C, ED, SD, SDB, EDB>>
extends ScreenBase {
    public static final Component READ_ONLY_COMPONENT = Component.translatable((String)"gui.xaero_category_editor_read_only").withStyle(ChatFormatting.YELLOW);
    private static final int FRAME_TOP_SIZE = 32;
    private static final int FRAME_BOTTOM_SIZE = 48;
    public static final int ROW_HEIGHT = 24;
    public static final int ROW_WIDTH = 220;
    private SettingRowList rowList;
    private final EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter;
    private ED editorData;
    protected ED cutCategory;
    protected ED cutCategorySuper;
    protected final IXaeroMinimap modMain;
    protected final boolean readOnly;

    protected GuiCategoryEditor(IXaeroMinimap modMain, Screen parent, Screen escape, Component title, EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter, boolean readOnly) {
        super(parent, escape, title);
        this.modMain = modMain;
        this.dataConverter = dataConverter;
        this.readOnly = readOnly;
        this.editorData = this.constructEditorData(dataConverter);
    }

    protected abstract ED constructEditorData(EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> var1);

    protected abstract ED constructDefaultData(EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> var1);

    protected abstract void onConfigConfirmed(C var1);

    public void init() {
        super.init();
        if (this.readOnly) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_back"), b -> this.cancel(this.parent)).bounds(this.width / 2 - 100, this.height - 32, 200, 20).build());
        } else {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_category_settings_cancel"), b -> this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> {
                if (result) {
                    this.cancel(this.parent);
                } else {
                    this.minecraft.gui.setScreen((Screen)this);
                }
            }, (Component)Component.translatable((String)"gui.xaero_category_settings_cancel_confirm"), (Component)Component.literal((String)"")))).bounds(this.width / 2 + 5, this.height - 32, 150, 20).build());
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_category_settings_confirm"), b -> this.confirm()).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
        }
        Button resetButton = Button.builder((Component)Component.translatable((String)"gui.xaero_category_settings_reset"), b -> {
            if (this.readOnly) {
                return;
            }
            this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> {
                if (result) {
                    this.editorData = this.constructDefaultData(this.dataConverter);
                }
                this.minecraft.gui.setScreen((Screen)this);
            }, (Component)Component.translatable((String)"gui.xaero_category_settings_reset_confirm1"), (Component)Component.translatable((String)"gui.xaero_category_settings_reset_confirm2")));
        }).bounds(6, 6, 120, 20).build();
        resetButton.active = !this.readOnly;
        this.addRenderableWidget((GuiEventListener)resetButton);
        this.rowList = new SettingRowList(this, this.dataConverter);
        this.addWidget((GuiEventListener)this.rowList);
    }

    private void confirm() {
        super.onExit(this.parent);
        if (!this.readOnly) {
            this.onConfigConfirmed(((ObjectCategory.Builder)this.dataConverter.getConfiguredBuilder(this.editorData)).build());
        }
    }

    public void onExit(Screen screen) {
        if (this.readOnly) {
            super.onExit(screen);
            return;
        }
        this.minecraft.gui.setScreen((Screen)new ConfirmScreen(this, result -> {
            if (result) {
                this.confirm();
            }
            this.cancel(screen);
        }, (Component)Component.translatable((String)"gui.xaero_category_settings_save_confirm"), (Component)Component.literal((String)"")){
            {
                Objects.requireNonNull(this$0);
                super(arg0, arg1, arg2);
            }

            public boolean keyPressed(KeyEvent event) {
                if (event.key() == 256) {
                    return true;
                }
                return super.keyPressed(event);
            }
        });
    }

    protected void cancel(Screen screen) {
        super.onExit(screen);
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractBackground(guiGraphics, i, j, f);
        this.rowList.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.centeredText(this.minecraft.font, this.title, this.width / 2, 5, -1);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
        if (this.readOnly) {
            guiGraphics.text(this.font, READ_ONLY_COMPONENT, this.width - 5 - this.font.width((FormattedText)READ_ONLY_COMPONENT), 5, -1);
        }
        if (this.rowList.hovered == null) {
            return;
        }
        if (this.openDropdown != null && this.openDropdown.isHovered()) {
            return;
        }
        Supplier<Tooltip> tooltipSupplier = this.rowList.hovered.getTooltipSupplier();
        if (tooltipSupplier == null) {
            return;
        }
        Tooltip tooltip = tooltipSupplier.get();
        if (tooltip == null) {
            return;
        }
        tooltip.drawBox(guiGraphics, i, j, this.width, this.height);
    }

    public boolean keyPressed(KeyEvent event) {
        if (this.rowList.isFocused() && event.key() == 40 && this.rowList.confirmSelection()) {
            return true;
        }
        return super.keyPressed(event);
    }

    public void tick() {
        this.rowList.tick();
        super.tick();
    }

    public SettingRowList getRowList() {
        return this.rowList;
    }

    public class SettingRowList
    extends ObjectSelectionList<xaero.hud.category.ui.GuiCategoryEditor$SettingRowList.Entry> {
        private EditorNode lastExpandedData;
        private boolean restoreScrollAfterUpdate;
        private EditorListEntry hovered;
        private final EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter;
        public final boolean readOnly;
        private static final Component USAGE_NARRATION = Component.translatable((String)"narration.selection.usage");
        private static final Component LEFT_RIGHT_USAGE = Component.translatable((String)"narration.xaero_ui_list_left_right_usage");
        final /* synthetic */ GuiCategoryEditor this$0;

        public SettingRowList(GuiCategoryEditor this$0, EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter) {
            GuiCategoryEditor guiCategoryEditor = this$0;
            Objects.requireNonNull(guiCategoryEditor);
            this.this$0 = guiCategoryEditor;
            super(this$0.minecraft, this$0.width, Math.max(4, this$0.height - 48 - 32), 32, 24);
            this.dataConverter = dataConverter;
            this.readOnly = this$0.readOnly;
            this.updateEntries();
        }

        protected boolean entriesCanBeSelected() {
            return false;
        }

        public boolean hasCut() {
            if (this.this$0.cutCategory == null) {
                return false;
            }
            if (((EditorCategoryNode)this.this$0.cutCategorySuper).getSubCategories().contains(this.this$0.cutCategory)) {
                return true;
            }
            this.setCutCategory(null, null);
            return false;
        }

        public ED getCut() {
            return this.this$0.cutCategory;
        }

        public boolean isCut(ED category) {
            if (this.this$0.cutCategory == category) {
                return this.hasCut();
            }
            return false;
        }

        public void setCutCategory(ED cutCategory, ED cutCategorySuper) {
            this.this$0.cutCategory = cutCategory;
            this.this$0.cutCategorySuper = cutCategorySuper;
        }

        public void pasteTo(ED destination) {
            if (this.this$0.cutCategory == null) {
                return;
            }
            if (destination == this.this$0.cutCategory || destination == this.this$0.cutCategorySuper) {
                this.setCutCategory(null, null);
                this.updateEntries();
                return;
            }
            ((EditorNode)destination).getExpandAction(this).run();
            this.setLastExpandedData((EditorNode)this.this$0.cutCategory);
            ((EditorCategoryNode)this.this$0.cutCategorySuper).getSubCategories().remove(this.this$0.cutCategory);
            ((EditorCategoryNode)destination).getSubCategories().add(0, this.this$0.cutCategory);
            this.setCutCategory(null, null);
        }

        public boolean isFocused() {
            return this.this$0.getFocused() == this;
        }

        public void setLastExpandedData(EditorNode lastExpandedData) {
            this.lastExpandedData = lastExpandedData;
        }

        public void restoreScrollAfterUpdate() {
            this.restoreScrollAfterUpdate = true;
        }

        public void updateEntries() {
            double scrollBackup = this.scrollAmount();
            this.clearEntries();
            ((EditorNode)this.this$0.editorData).setExpanded(true);
            this.addEntriesForExpanded((EditorNode)this.this$0.editorData, null);
            if (this.getSelected() != null) {
                this.centerScrollOn((AbstractSelectionList.Entry)((Entry)this.getSelected()));
            }
            if (this.restoreScrollAfterUpdate) {
                this.setScrollAmount(scrollBackup);
                this.restoreScrollAfterUpdate = false;
            }
        }

        private void addEntriesForExpanded(EditorNode data, EditorNode parent) {
            int nextIndex = this.children().size();
            List<EditorNode> subExpandables = data.getSubNodes();
            if (subExpandables == null) {
                return;
            }
            EditorNode expandedData = null;
            for (EditorNode sed : subExpandables) {
                if (!sed.isExpanded()) continue;
                expandedData = sed;
                break;
            }
            EditorListRootEntry wrappedEntry = data.getListEntryFactory().get(data, parent, nextIndex, nextIndex == 0 ? ConnectionLineType.NONE : ConnectionLineType.PATH, this, this.width, expandedData == null);
            Entry currentEntry = new Entry(this, wrappedEntry, nextIndex++);
            this.addEntry((AbstractSelectionList.Entry)currentEntry);
            if (data == this.lastExpandedData) {
                this.setFocused((GuiEventListener)currentEntry);
            }
            if (expandedData != null) {
                this.addEntriesForExpanded(expandedData, data);
                return;
            }
            if (this.lastExpandedData == null && data.isExpanded()) {
                this.setFocused((GuiEventListener)currentEntry);
            }
            boolean first = true;
            for (EditorNode sed : subExpandables) {
                wrappedEntry = sed.getListEntryFactory().get(sed, data, nextIndex, first ? ConnectionLineType.HEAD_LEAF : ConnectionLineType.TAIL_LEAF, this, this.width, false);
                Entry leafEntry = new Entry(this, wrappedEntry, nextIndex++);
                this.addEntry((AbstractSelectionList.Entry)leafEntry);
                if (sed == this.lastExpandedData) {
                    this.setFocused((GuiEventListener)leafEntry);
                }
                first = false;
            }
        }

        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.isMouseOver(event.x(), event.y())) {
                this.setFocused(null);
            }
            return super.mouseClicked(event, doubleClick);
        }

        public void mouseMoved(double d, double e) {
            if (this.getSelected() != null) {
                ((Entry)this.getSelected()).mouseMoved(d, e);
            }
            super.mouseMoved(d, e);
        }

        public boolean keyReleased(KeyEvent event) {
            if (this.getSelected() != null && ((Entry)this.getSelected()).keyReleased(event)) {
                return true;
            }
            return super.keyReleased(event);
        }

        public boolean charTyped(CharacterEvent event) {
            boolean result;
            if (this.getSelected() != null && (result = ((Entry)this.getSelected()).charTyped(event))) {
                return true;
            }
            return super.charTyped(event);
        }

        public void tick() {
            if (this.getSelected() != null) {
                ((Entry)this.getSelected()).tick();
            }
        }

        public boolean confirmSelection() {
            Entry entry = (Entry)this.getSelected();
            if (entry == null) {
                return false;
            }
            EditorListEntry selectedSubEntry = entry.wrappedEntry.confirmSelection();
            return selectedSubEntry != null;
        }

        public void setFocused(GuiEventListener guiEventListener) {
            Entry entry;
            if (guiEventListener != null && !this.children().contains(guiEventListener) || this.getFocused() == guiEventListener) {
                return;
            }
            if (this.getSelected() != null) {
                ((Entry)this.getSelected()).wrappedEntry.unfocusRecursively();
            }
            if (this.getFocused() != null) {
                ((Entry)this.getFocused()).setFocused(false);
            }
            if ((entry = (Entry)guiEventListener) != null) {
                entry.wrappedEntry.focusFirstRecursively();
            }
            super.setFocused(guiEventListener);
            if (guiEventListener == null) {
                this.setSelected((Entry)null);
            }
            this.narrateSelection();
        }

        /*
         * Ignored method signature, as it can't be verified against descriptor
         */
        public void setSelected(Entry entry) {
            super.setSelected((AbstractSelectionList.Entry)entry);
        }

        public int getRowWidth() {
            return this.width;
        }

        protected int scrollBarX() {
            return this.width / 2 + 164;
        }

        public void narrateSelection() {
            this.this$0.afterKeyboardAction();
        }

        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            super.updateWidgetNarration(narrationElementOutput);
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, new Component[]{USAGE_NARRATION, LEFT_RIGHT_USAGE});
            }
        }

        public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
            this.hovered = null;
            super.extractWidgetRenderState(guiGraphics, i, j, f);
        }

        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            FocusNavigationEvent.ArrowNavigation arrowNavigation;
            if (focusNavigationEvent instanceof FocusNavigationEvent.ArrowNavigation && ((arrowNavigation = (FocusNavigationEvent.ArrowNavigation)focusNavigationEvent).direction() == ScreenDirection.LEFT || arrowNavigation.direction() == ScreenDirection.RIGHT)) {
                return null;
            }
            return super.nextFocusPath(focusNavigationEvent);
        }

        public EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> getDataConverter() {
            return this.dataConverter;
        }

        public class Entry
        extends ObjectSelectionList.Entry<xaero.hud.category.ui.GuiCategoryEditor$SettingRowList.Entry> {
            private EditorListRootEntry wrappedEntry;
            private int index;
            private int lastX;
            private int lastY;
            final /* synthetic */ SettingRowList this$1;

            public Entry(SettingRowList this$1, EditorListRootEntry entryInfo, int index) {
                SettingRowList settingRowList = this$1;
                Objects.requireNonNull(settingRowList);
                this.this$1 = settingRowList;
                this.wrappedEntry = entryInfo;
                this.index = index;
            }

            public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
                Matrix3x2fStack poseStack = guiGraphics.pose();
                int x = this.getX();
                int y = this.getY() + 2;
                this.lastX = x;
                this.lastY = y;
                poseStack.pushMatrix();
                poseStack.translate((float)x, (float)y);
                boolean includesSelected = this.this$1.getSelected() == this;
                this.wrappedEntry.preRender(guiGraphics, includesSelected, true);
                EditorListEntry hoveredInRow = this.wrappedEntry.render(guiGraphics, this.index, this.this$1.getRowWidth(), this.getHeight(), mouseX - x - this.wrappedEntry.getEntryRelativeX(), mouseY - y - this.wrappedEntry.getEntryRelativeY(), isMouseOver, partialTicks, this.this$1.this$0.font, mouseX, mouseY, includesSelected, true);
                this.wrappedEntry.postRender(guiGraphics);
                poseStack.popMatrix();
                if (hoveredInRow != null) {
                    this.this$1.hovered = hoveredInRow;
                }
            }

            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                this.this$1.setFocused((GuiEventListener)this);
                double relativeMouseX = event.x() - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                double relativeMouseY = event.y() - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY();
                this.wrappedEntry.mouseClicked(this, relativeMouseX, relativeMouseY, this.index, event.buttonInfo(), doubleClick);
                return true;
            }

            public boolean mouseReleased(MouseButtonEvent event) {
                double relativeMouseX = event.x() - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                double relativeMouseY = event.y() - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY();
                this.wrappedEntry.mouseReleased(relativeMouseX, relativeMouseY, this.index, event.buttonInfo());
                return super.mouseReleased(event);
            }

            public boolean mouseScrolled(double mouseX, double mouseY, double f, double g) {
                double relativeMouseY;
                double relativeMouseX = mouseX - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                if (this.wrappedEntry.mouseScrolled(relativeMouseX, relativeMouseY = mouseY - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY(), f, g)) {
                    return true;
                }
                return super.mouseScrolled(mouseX, mouseY, f, g);
            }

            public boolean mouseDragged(MouseButtonEvent event, double f, double g) {
                double relativeMouseY;
                double relativeMouseX = event.x() - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                if (this.wrappedEntry.mouseDragged(relativeMouseX, relativeMouseY = event.y() - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY(), this.index, f, g, event.buttonInfo())) {
                    return true;
                }
                return super.mouseDragged(event, f, g);
            }

            public boolean keyPressed(KeyEvent event) {
                if (this.wrappedEntry.keyPressed(event, true)) {
                    return true;
                }
                return super.keyPressed(event);
            }

            public boolean keyReleased(KeyEvent event) {
                if (this.wrappedEntry.keyReleased(event)) {
                    return true;
                }
                return super.keyReleased(event);
            }

            public boolean charTyped(CharacterEvent event) {
                if (this.wrappedEntry.charTyped(event)) {
                    return true;
                }
                return super.charTyped(event);
            }

            public void setFocused(boolean bl) {
                this.wrappedEntry.setFocused(bl);
                super.setFocused(bl);
            }

            public void tick() {
                this.wrappedEntry.tick();
            }

            public Component getNarration() {
                String selectedNarrationString = this.wrappedEntry.getSubNarration();
                if (selectedNarrationString == null) {
                    return Component.literal((String)"");
                }
                return Component.translatable((String)"narrator.select", (Object[])new Object[]{selectedNarrationString});
            }
        }
    }
}

