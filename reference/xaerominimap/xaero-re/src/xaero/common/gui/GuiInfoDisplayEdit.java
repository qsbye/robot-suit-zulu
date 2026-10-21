/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.common.config.info.config.InfoDisplayConfigData;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.InfoDisplayManager;
import xaero.hud.minimap.info.config.InfoDisplayConfigClientUtils;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.common.config.Config;

public class GuiInfoDisplayEdit
extends ScreenBase {
    private static final int FRAME_TOP_SIZE = 30;
    private static final int FRAME_BOTTOM_SIZE = 61;
    private static final int SELECTION_ITEM_HEIGHT = 24;
    private static final Component HELP_COMPONENT = Component.translatable((String)"gui.xaero_minimap_info_display_manager_help");
    private static final Component SERVER_ENFORCED_COMPONENT = Component.translatable((String)"gui.xaero_info_display_editor_server_enforced").withStyle(ChatFormatting.YELLOW);
    private SelectionList selectionList;
    private final InfoDisplayManager manager;
    private List<String> currentOrder;
    private int selected;
    private int subSelected;
    private final Map<String, MoveableEntry<?>> moveableEntries;
    private final boolean clientSide;
    private InfoDisplayManagerConfigData inputConfig;
    private final Config config;
    private final Runnable onChange;
    private final boolean viewingEnforced;
    private boolean madeChanges;

    public GuiInfoDisplayEdit(EditConfigScreen parent, Screen escape, Config config, Runnable onChange, boolean viewingEnforced) {
        super((Screen)parent, escape, (Component)Component.translatable((String)"gui.xaero_minimap_info_display_manager"));
        this.config = config;
        this.onChange = onChange;
        this.clientSide = parent.getContext().isClientSide();
        this.viewingEnforced = viewingEnforced;
        this.manager = HudMod.INSTANCE.getMinimap().getInfoDisplays().getManager();
        this.inputConfig = (InfoDisplayManagerConfigData)config.get(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG);
        if (this.inputConfig == null || this.inputConfig == InfoDisplayManagerConfigData.EMPTY) {
            this.inputConfig = InfoDisplayConfigClientUtils.createDefaultConfig(this.manager, null, this.clientSide);
        }
        this.currentOrder = this.manager.adaptOrder(this.inputConfig.getOrderStream());
        this.moveableEntries = new HashMap();
        this.selected = -1;
        this.subSelected = -1;
    }

    protected void init() {
        super.init();
        this.selectionList = new SelectionList(this);
        this.addWidget((GuiEventListener)this.selectionList);
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.done", (Object[])new Object[0]), b -> this.goBack()).bounds(this.width / 2 - 100, this.height - 34, 200, 20).build());
        if (this.moveableEntries.isEmpty()) {
            for (String id : this.currentOrder) {
                InfoDisplay<?> infoDisplay = this.manager.get(id);
                MoveableEntry<?> moveable = this.createEntryFor(infoDisplay);
                this.moveableEntries.put(id, moveable);
            }
        }
        this.moveableEntries.values().forEach(this::refreshEntry);
    }

    private <T> MoveableEntry<T> createEntryFor(InfoDisplay<T> infoDisplay) {
        MoveableEntry<T> moveable = new MoveableEntry<T>(this, infoDisplay);
        InfoDisplayConfigData infoDisplayConfig = this.inputConfig.get(infoDisplay.getId());
        if (infoDisplayConfig == null) {
            infoDisplayConfig = InfoDisplayConfigClientUtils.createDefaultConfig(infoDisplay, null, this.clientSide);
        }
        String configStateString = infoDisplayConfig.getState();
        moveable.textColor = infoDisplayConfig.getTextColor();
        moveable.backgroundColor = infoDisplayConfig.getBackgroundColor();
        moveable.state = configStateString == null ? null : infoDisplay.getCodec().decode(configStateString, null, null);
        return moveable;
    }

    private <T> void refreshEntry(MoveableEntry<T> moveable) {
        moveable.clearSubElements();
        this.addSubElements(moveable);
    }

    public void onExit(Screen screen) {
        super.onExit(screen);
        if (this.madeChanges) {
            this.onChange.run();
        }
    }

    private void saveToConfigProfile() {
        InfoDisplayManagerConfigData.Builder builder = InfoDisplayManagerConfigData.Builder.begin();
        for (String id : this.currentOrder) {
            MoveableEntry<?> editorEntry = this.moveableEntries.get(id);
            String stateString = this.encodeState(editorEntry);
            builder.add(id, new InfoDisplayConfigData(editorEntry.backgroundColor, editorEntry.textColor, stateString));
        }
        InfoDisplayManagerConfigData outputConfig = builder.build();
        this.config.set(MinimapProfiledConfigOptions.INFO_DISPLAY_CONFIG, (Object)outputConfig);
        this.madeChanges = true;
    }

    private <T> String encodeState(MoveableEntry<T> editorEntry) {
        return editorEntry.state == null ? null : editorEntry.infoDisplay.getCodec().encode(editorEntry.state, null, null);
    }

    public String[] createColorOptions(String symbol, boolean noneOption, boolean nullOption) {
        int firstColorIndex = (noneOption ? 1 : 0) + (nullOption ? 1 : 0);
        String[] options = new String[MinimapConfigConstants.COLOR_NAMES.length + firstColorIndex];
        if (nullOption) {
            options[0] = "~";
        }
        if (noneOption) {
            options[firstColorIndex - 1] = "\u25a1\u25a1";
        }
        for (int i = 0; i < MinimapConfigConstants.COLOR_NAMES.length; ++i) {
            options[i + firstColorIndex] = "\u00a7" + MinimapConfigConstants.COLOR_CODES[i] + symbol;
        }
        return options;
    }

    private <T> void addSubElements(MoveableEntry<T> moveable) {
        int currentSelectedBackground;
        int currentSelectedTextColor;
        boolean includeNull;
        boolean bl = includeNull = !this.clientSide || this.viewingEnforced;
        if (!(includeNull || moveable.state != null && moveable.textColor != null && moveable.backgroundColor != null)) {
            throw new IllegalArgumentException();
        }
        AbstractWidget stateWidget = moveable.infoDisplay.createWidget(this.width / 2 + 150 - 102, 0, 100, 20, moveable, this::saveToConfigProfile, includeNull);
        if (stateWidget != null) {
            moveable.addSubElement(stateWidget);
        }
        int n = currentSelectedTextColor = moveable.textColor == null ? -1 : moveable.textColor % MinimapConfigConstants.COLOR_NAMES.length;
        if (includeNull) {
            ++currentSelectedTextColor;
        }
        if (currentSelectedTextColor < 0) {
            currentSelectedTextColor = 0;
            moveable.textColor = includeNull ? null : Integer.valueOf(0);
        }
        DropDownWidget textColorWidget = DropDownWidget.Builder.begin().setOptions(this.createColorOptions("Aa", false, includeNull)).setX(this.width / 2 - 147).setW(20).setSelected(Integer.valueOf(currentSelectedTextColor)).setContainer((IDropDownContainer)this).setCallback((menu, index) -> {
            moveable.textColor = includeNull && index == 0 ? null : Integer.valueOf(index - (includeNull ? 1 : 0));
            this.saveToConfigProfile();
            return true;
        }).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_info_display_text_color")).build();
        moveable.addSubElement((AbstractWidget)textColorWidget);
        int n2 = moveable.backgroundColor == null ? 0 : (currentSelectedBackground = (moveable.backgroundColor < 0 ? moveable.backgroundColor : moveable.backgroundColor % MinimapConfigConstants.COLOR_NAMES.length) + 1 + (includeNull ? 1 : 0));
        if (currentSelectedBackground < 0) {
            currentSelectedBackground = 0;
            moveable.backgroundColor = includeNull ? null : Integer.valueOf(-1);
        }
        DropDownWidget backgroundColorWidget = DropDownWidget.Builder.begin().setOptions(this.createColorOptions("\u25a0\u25a0", true, includeNull)).setX(this.width / 2 - 124).setW(20).setSelected(Integer.valueOf(currentSelectedBackground)).setContainer((IDropDownContainer)this).setCallback((menu, index) -> {
            moveable.backgroundColor = includeNull && index == 0 ? null : Integer.valueOf(index - 1 - (includeNull ? 1 : 0));
            this.saveToConfigProfile();
            return true;
        }).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_info_display_background_color")).build();
        moveable.addSubElement((AbstractWidget)backgroundColorWidget);
        if (this.viewingEnforced) {
            if (stateWidget != null) {
                stateWidget.active = false;
            }
            textColorWidget.active = false;
            backgroundColorWidget.active = false;
        }
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.selectionList != null) {
            this.selectionList.releaseDrag();
        }
        return super.mouseReleased(event);
    }

    public void setFocused(GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        this.selectionList.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 5, -1);
        if (this.clientSide) {
            guiGraphics.centeredText(this.font, HELP_COMPONENT, this.width / 2, this.height - 52, -1);
        }
        if (this.viewingEnforced) {
            guiGraphics.centeredText(this.font, SERVER_ENFORCED_COMPONENT, this.width / 2, 15, -1);
        }
    }

    class SelectionList
    extends ObjectSelectionList<Entry> {
        private static final Component USAGE_NARRATION = Component.translatable((String)"narration.selection.usage");
        private static final Component LEFT_RIGHT_USAGE = Component.translatable((String)"narration.xaero_ui_list_left_right_usage");
        private boolean dragging;
        private int dragStartX;
        private int dragStartY;
        private int dragged;
        private int draggedOffsetX;
        private int draggedOffsetY;
        final /* synthetic */ GuiInfoDisplayEdit this$0;

        public SelectionList(GuiInfoDisplayEdit this$0) {
            GuiInfoDisplayEdit guiInfoDisplayEdit = this$0;
            Objects.requireNonNull(guiInfoDisplayEdit);
            this.this$0 = guiInfoDisplayEdit;
            super(this$0.minecraft, this$0.width, this$0.height - 61 - 30, 30, 24);
            this.createEntries();
            if (this$0.selected != -1) {
                this.setFocused((GuiEventListener)this.children().get(this$0.selected));
            }
            this.dragged = -1;
        }

        public boolean isFocused() {
            return this.this$0.getFocused() == this;
        }

        public void setFocused(GuiEventListener guiEventListener) {
            if (guiEventListener instanceof Entry || guiEventListener == null) {
                Entry oldSelected;
                Entry entry = (Entry)guiEventListener;
                if (this.this$0.subSelected != -1 && (oldSelected = (Entry)this.getSelected()) != null) {
                    MoveableEntry<?> moveable = oldSelected.getMoveable();
                    moveable.subElements.get(this.this$0.subSelected).setFocused(false);
                }
                this.this$0.selected = entry == null ? -1 : entry.index;
                this.this$0.subSelected = -1;
            }
            super.setFocused(guiEventListener);
            if (this.getFocused() == null) {
                this.setSelected((Entry)null);
            }
        }

        public void setSelected(Entry entry) {
            super.setSelected((AbstractSelectionList.Entry)entry);
        }

        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            super.updateWidgetNarration(narrationElementOutput);
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, new Component[]{USAGE_NARRATION, LEFT_RIGHT_USAGE});
            }
        }

        private void createEntries() {
            for (int i = 0; i < this.this$0.currentOrder.size(); ++i) {
                Entry entry = new Entry(this, i);
                this.addEntry((AbstractSelectionList.Entry)entry);
            }
        }

        private void releaseDrag() {
            this.dragging = false;
            this.dragged = -1;
        }

        protected int scrollBarX() {
            return this.width / 2 + 164;
        }

        public int getRowWidth() {
            return 300;
        }

        public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            if (this.dragging) {
                int hoveredIndex;
                Entry draggedEntry = (Entry)((Object)this.children().get(this.dragged));
                draggedEntry.renderNonInteractable(guiGraphics, mouseX + this.draggedOffsetX, mouseY + this.draggedOffsetY);
                Entry hoveredEntry = (Entry)this.getEntryAtPosition(mouseX, mouseY);
                int n = hoveredIndex = hoveredEntry == null ? -1 : hoveredEntry.index;
                if (hoveredIndex != -1 && hoveredIndex != this.dragged) {
                    String draggedId = this.this$0.currentOrder.get(this.dragged);
                    int slideDirection = hoveredIndex < this.dragged ? 1 : -1;
                    for (int i = this.dragged; i != hoveredIndex; i -= slideDirection) {
                        this.this$0.currentOrder.set(i, this.this$0.currentOrder.get(i - slideDirection));
                    }
                    this.this$0.currentOrder.set(hoveredIndex, draggedId);
                    this.dragged = hoveredIndex;
                    this.this$0.saveToConfigProfile();
                }
            } else if (this.dragged != -1 && (Math.abs(mouseX - this.dragStartX) > 5 || Math.abs(mouseY - this.dragStartY) > 5)) {
                this.dragging = true;
                this.setFocused(null);
            }
        }

        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            FocusNavigationEvent.ArrowNavigation arrowNavigation;
            if (focusNavigationEvent instanceof FocusNavigationEvent.ArrowNavigation && ((arrowNavigation = (FocusNavigationEvent.ArrowNavigation)focusNavigationEvent).direction() == ScreenDirection.LEFT || arrowNavigation.direction() == ScreenDirection.RIGHT)) {
                return null;
            }
            return super.nextFocusPath(focusNavigationEvent);
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final int index;
            private int lastRenderX;
            private int lastRenderY;
            private int lastMouseX;
            private int lastMouseY;
            final /* synthetic */ SelectionList this$1;

            public Entry(SelectionList this$1, int index) {
                SelectionList selectionList = this$1;
                Objects.requireNonNull(selectionList);
                this.this$1 = selectionList;
                this.index = index;
            }

            private void renderNonInteractable(GuiGraphicsExtractor guiGraphics, int x, int y) {
                String infoDisplayId = this.this$1.this$0.currentOrder.get(this.index);
                InfoDisplay<?> infoDisplay = this.this$1.this$0.manager.get(infoDisplayId);
                guiGraphics.text(this.this$1.this$0.font, infoDisplay.getName(), x + 48, y + 6, -1);
            }

            private MoveableEntry<?> getMoveable() {
                String infoDisplayId = this.this$1.this$0.currentOrder.get(this.index);
                return this.this$1.this$0.moveableEntries.get(infoDisplayId);
            }

            public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
                int x = this.getX();
                int y = this.getY() + 2;
                this.lastRenderX = x;
                this.lastRenderY = y;
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
                if (this.this$1.dragging && this.this$1.dragged == this.index) {
                    return;
                }
                this.renderNonInteractable(guiGraphics, x, y);
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    subElement.setY(y - 2 + 12 - subElement.getHeight() / 2);
                    if (subElement instanceof DropDownWidget) {
                        subElement.setY(subElement.getY() - 1);
                    }
                    subElement.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
                }
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                double d = event.x();
                double e = event.y();
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    if (!subElement.isMouseOver(d, e) || !subElement.mouseClicked(event, doubleClick)) continue;
                    return true;
                }
                if (event.button() == 1) {
                    this.this$1.dragging = false;
                    if (!this.this$1.this$0.clientSide || this.this$1.this$0.viewingEnforced) {
                        return true;
                    }
                    this.this$1.dragged = this.index;
                    this.this$1.draggedOffsetX = (int)((double)this.lastRenderX - d);
                    this.this$1.draggedOffsetY = (int)((double)this.lastRenderY - e);
                    this.this$1.dragStartX = (int)d;
                    this.this$1.dragStartY = (int)e;
                    if (this.this$1.getSelected() != this) return true;
                    this.this$1.setFocused(null);
                    return super.mouseClicked(event, doubleClick);
                } else {
                    this.this$1.setFocused(null);
                }
                return super.mouseClicked(event, doubleClick);
            }

            public boolean mouseReleased(MouseButtonEvent event) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    subElement.mouseReleased(event);
                }
                return super.mouseReleased(event);
            }

            public void mouseMoved(double d, double e) {
                this.lastMouseX = (int)d;
                this.lastMouseY = (int)e;
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    if (!subElement.isMouseOver(d, e)) continue;
                    subElement.mouseMoved(d, e);
                }
                super.mouseMoved(d, e);
            }

            public boolean mouseDragged(MouseButtonEvent event, double moveX, double moveY) {
                double d = event.x();
                double e = event.y();
                this.lastMouseX = (int)d;
                this.lastMouseY = (int)e;
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    if (!subElement.isMouseOver(d, e) || !subElement.mouseDragged(event, moveX, moveY)) continue;
                    return true;
                }
                return super.mouseDragged(event, moveX, moveY);
            }

            public boolean mouseScrolled(double d, double e, double f, double g) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                for (AbstractWidget subElement : moveableEntry.subElements) {
                    if (!subElement.isMouseOver(d, e) || !subElement.mouseScrolled(d, e, f, g)) continue;
                    return true;
                }
                return super.mouseScrolled(d, e, f, g);
            }

            public boolean keyPressed(KeyEvent event) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                int i = event.key();
                if (i == 79 || i == 80) {
                    if (this.this$1.this$0.subSelected != -1) {
                        moveableEntry.subElements.get(this.this$1.this$0.subSelected).setFocused(false);
                    }
                    if (i == 79) {
                        ++this.this$1.this$0.subSelected;
                        if (this.this$1.this$0.subSelected == moveableEntry.subElements.size()) {
                            this.this$1.this$0.subSelected = -1;
                        }
                    } else {
                        --this.this$1.this$0.subSelected;
                        if (this.this$1.this$0.subSelected < -1) {
                            this.this$1.this$0.subSelected = moveableEntry.subElements.size() - 1;
                        }
                    }
                    if (this.this$1.this$0.subSelected != -1) {
                        moveableEntry.subElements.get(this.this$1.this$0.subSelected).setFocused(true);
                    }
                } else if (this.this$1.this$0.subSelected != -1 && moveableEntry.subElements.get(this.this$1.this$0.subSelected).keyPressed(event)) {
                    return true;
                }
                return super.keyPressed(event);
            }

            public boolean keyReleased(KeyEvent event) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                if (this.this$1.this$0.subSelected != -1 && moveableEntry.subElements.get(this.this$1.this$0.subSelected).keyReleased(event)) {
                    return true;
                }
                return super.keyReleased(event);
            }

            public boolean charTyped(CharacterEvent event) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                if (this.this$1.this$0.subSelected != -1 && moveableEntry.subElements.get(this.this$1.this$0.subSelected).charTyped(event)) {
                    return true;
                }
                return super.charTyped(event);
            }

            public void updateNarration(NarrationElementOutput narrationElementOutput) {
                MoveableEntry<?> moveableEntry = this.getMoveable();
                int sub = -1;
                if (this.this$1.this$0.selected == this.index && this.this$1.this$0.subSelected >= 0) {
                    sub = this.this$1.this$0.subSelected;
                } else {
                    for (int i = 0; i < moveableEntry.subElements.size(); ++i) {
                        if (!moveableEntry.subElements.get(i).isMouseOver((double)this.lastMouseX, (double)this.lastMouseY)) continue;
                        sub = i;
                    }
                }
                if (sub >= 0) {
                    moveableEntry.subElements.get(sub).updateNarration(narrationElementOutput);
                } else {
                    super.updateNarration(narrationElementOutput);
                }
            }

            public Component getNarration() {
                String infoDisplayId = this.this$1.this$0.currentOrder.get(this.index);
                InfoDisplay<?> infoDisplay = this.this$1.this$0.manager.get(infoDisplayId);
                String narration = infoDisplay.getName().getString();
                return Component.literal((String)I18n.get((String)"narrator.select", (Object[])new Object[]{narration}));
            }
        }
    }

    public class MoveableEntry<T> {
        private final List<AbstractWidget> subElements;
        private final InfoDisplay<T> infoDisplay;
        private T state;
        private Integer textColor;
        private Integer backgroundColor;

        public MoveableEntry(GuiInfoDisplayEdit this$0, InfoDisplay<T> infoDisplay) {
            Objects.requireNonNull(this$0);
            this.infoDisplay = infoDisplay;
            this.subElements = new ArrayList<AbstractWidget>();
        }

        public void addSubElement(AbstractWidget widget) {
            this.subElements.add(widget);
        }

        public T getState() {
            return this.state;
        }

        public void setState(T state) {
            this.state = state;
        }

        private void clearSubElements() {
            this.subElements.clear();
        }
    }
}

