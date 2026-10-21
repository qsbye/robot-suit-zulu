/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.gui.GuiChoosePreset;
import xaero.common.gui.GuiInstructions;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleManager;
import xaero.hud.module.ModuleSession;
import xaero.hud.module.ModuleTransform;
import xaero.hud.preset.HudPreset;
import xaero.hud.pushbox.PushboxHandler;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.lib.client.gui.widget.Tooltip;

public class GuiEditMode
extends ScreenBase {
    public static final Component CENTERED_COMPONENT = Component.translatable((String)"gui.xaero_centered");
    public static final Component FLIPPED_COMPONENT = Component.translatable((String)"gui.xaero_flipped");
    public static final Component TRUE_COMPONENT = Component.translatable((String)"gui.yes");
    public static final Component FALSE_COMPONENT = Component.translatable((String)"gui.no");
    public static final Component PRESS_C_COMPONENT = Component.translatable((String)"gui.xaero_press_c");
    public static final Component PRESS_F_COMPONENT = Component.translatable((String)"gui.xaero_press_f");
    public static final Component NOT_INGAME = Component.translatable((String)"gui.xaero_not_ingame");
    private final int NORMAL_COLOR = 1354612157;
    private final int HOVERED_COLOR = 0x64FFFFFF;
    private final int SELECTED_COLOR = -2097152001;
    private final boolean instructions;
    private final Component message;
    private HudModule<?> draggedModule;
    private HudModule<?> selectedModule;
    private HudModule<?> lastFrameHoveredModule;
    private int dragOffsetX;
    private int dragOffsetY;
    private final IXaeroMinimap modMain;

    public GuiEditMode(IXaeroMinimap modMain, Screen parent, Screen escape, boolean instructions, Component message) {
        super(parent, escape, (Component)Component.translatable((String)"gui.xaero_edit_mode"));
        this.modMain = modMain;
        this.instructions = instructions;
        this.message = message;
    }

    protected void init() {
        super.init();
        this.draggedModule = null;
        this.selectedModule = null;
        this.addRenderableWidget((GuiEventListener)new MySmallButton(200, this.width / 2 - 155, this.height / 6 + 143, (Component)Component.translatable((String)"gui.xaero_confirm"), b -> this.confirm()));
        this.addRenderableWidget((GuiEventListener)new MySmallButton(202, this.width / 2 + 5, this.height / 6 + 143, (Component)Component.translatable((String)"gui.xaero_choose_a_preset"), b -> this.minecraft.gui.setScreen((Screen)new GuiChoosePreset(this.modMain, (Screen)this, this.escape))));
        if (this.instructions) {
            this.addRenderableWidget((GuiEventListener)new MySmallButton(201, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel"), b -> this.cancel()));
            this.addRenderableWidget((GuiEventListener)new MySmallButton(203, this.width / 2 - 155, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_instructions"), b -> this.minecraft.gui.setScreen((Screen)new GuiInstructions(this.modMain, (Screen)this, this.escape))));
            return;
        }
        this.addRenderableWidget((GuiEventListener)new MySmallButton(201, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel"), b -> this.cancel()));
    }

    private void confirm() {
        for (HudPreset hudPreset : this.modMain.getHud().getPresetManager().getPresets()) {
            hudPreset.confirm();
        }
        ModuleManager manager = this.modMain.getHud().getModuleManager();
        for (HudModule<?> hudModule : manager.getModules()) {
            hudModule.confirmTransform();
        }
        try {
            this.modMain.getSettings().saveSettings();
        }
        catch (IOException iOException) {
            HudMod.LOGGER.error("suppressed exception", (Throwable)iOException);
        }
        this.minecraft.gui.setScreen(this.parent);
    }

    private void cancel() {
        for (HudPreset preset : this.modMain.getHud().getPresetManager().getPresets()) {
            preset.cancel();
        }
        ModuleManager manager = this.modMain.getHud().getModuleManager();
        for (HudModule<?> module : manager.getModules()) {
            module.cancelTransform();
        }
        this.goBack();
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.minecraft.player == null) {
            super.extractBackground(guiGraphics, mouseX, mouseY, partial);
            guiGraphics.centeredText(this.font, NOT_INGAME, this.width / 2, this.height / 6 + 128, -1);
        } else {
            guiGraphics.centeredText(this.font, this.message, this.width / 2, this.height / 6 + 128, -1);
        }
    }

    private void applyPushes() {
        double screenScale = this.minecraft.getWindow().getGuiScale();
        ModuleManager manager = this.modMain.getHud().getModuleManager();
        for (HudModule<?> module : manager.getModules()) {
            Object session = module.getCurrentSession();
            if (!((ModuleSession)session).isActive()) continue;
            PushboxHandler.State pushState = module.getPushState();
            pushState.resetForModule((ModuleSession<?>)session, this.width, this.height, screenScale);
            this.modMain.getHudRenderer().getPushboxHandler().applyScreenEdges(pushState, this.width, this.height, screenScale);
        }
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (XaeroMinimapSession.getCurrentSession() == null) {
            super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            return;
        }
        double screenScale = this.minecraft.getWindow().getGuiScale();
        this.handleDraggedModule(mouseX, mouseY, screenScale);
        this.applyPushes();
        ModuleManager manager = this.modMain.getHud().getModuleManager();
        this.lastFrameHoveredModule = this.getHoveredModule(mouseX, mouseY);
        HudModule<?> hoveredModule = this.lastFrameHoveredModule;
        for (HudModule<?> module : manager.getModules()) {
            if (!((ModuleSession)module.getCurrentSession()).isActive()) continue;
            this.renderModuleBox(module, hoveredModule, screenScale, guiGraphics);
        }
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.openDropdown != null && this.openDropdown.isHovered()) {
            return;
        }
        if (hoveredModule == null || this.draggedModule != null) {
            return;
        }
        Tooltip tooltip = new Tooltip(this.getTooltipText(hoveredModule));
        tooltip.setStartWidth(150);
        tooltip.drawBox(guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    private <MS extends ModuleSession<MS>> void renderModuleBox(HudModule<MS> module, HudModule<?> hoveredModule, double screenScale, GuiGraphicsExtractor guiGraphics) {
        MS session = module.getCurrentSession();
        int moduleW = ((ModuleSession)session).getWidth(screenScale);
        int moduleH = ((ModuleSession)session).getHeight(screenScale);
        boolean hovered = hoveredModule == module;
        PushboxHandler.State pushState = module.getPushState();
        int boxX = pushState.x;
        int boxY = pushState.y;
        guiGraphics.fill(boxX, boxY, boxX + moduleW, boxY + moduleH, this.selectedModule == module ? -2097152001 : (hovered ? 0x64FFFFFF : 1354612157));
    }

    private HudModule<?> getHoveredModule(int mouseX, int mouseY) {
        ModuleManager manager = this.modMain.getHud().getModuleManager();
        HudModule<?> result = null;
        int resultSize = 0;
        for (HudModule<?> module : manager.getModules()) {
            if (!((ModuleSession)module.getCurrentSession()).isActive() || !this.isHovered(module, mouseX, mouseY)) continue;
            int moduleW = module.getPushState().w;
            int moduleH = module.getPushState().h;
            if (module != this.selectedModule && result != null && moduleW * moduleH > resultSize) continue;
            result = module;
            resultSize = moduleW * moduleH;
        }
        return result;
    }

    private boolean isHovered(HudModule<?> module, int mouseX, int mouseY) {
        PushboxHandler.State pushState = module.getPushState();
        int boxX = pushState.x;
        int boxY = pushState.y;
        return mouseX >= boxX && mouseX < boxX + pushState.w && mouseY >= boxY && mouseY < boxY + pushState.h;
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean clickResult = super.mouseClicked(event, doubleClick);
        if (clickResult || XaeroMinimapSession.getCurrentSession() == null) {
            return true;
        }
        this.applyPushes();
        double mouseX = event.x();
        double mouseY = event.y();
        this.draggedModule = this.getHoveredModule((int)mouseX, (int)mouseY);
        this.selectedModule = this.draggedModule;
        if (this.draggedModule != null) {
            this.dragOffsetX = this.draggedModule.getPushState().x - (int)mouseX;
            this.dragOffsetY = this.draggedModule.getPushState().y - (int)mouseY;
            return true;
        }
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        this.draggedModule = null;
        return super.mouseReleased(event);
    }

    public boolean keyPressed(KeyEvent event) {
        HudModule<?> affectedModule;
        int code = event.key();
        HudModule<?> hudModule = affectedModule = this.selectedModule != null ? this.selectedModule : this.lastFrameHoveredModule;
        if (affectedModule != null && (code == 6 || code == 9 || code == 22)) {
            ModuleTransform transform = affectedModule.getUnconfirmedTransform();
            switch (code) {
                case 6: {
                    int oldModuleY = ((ModuleSession)affectedModule.getCurrentSession()).getEffectiveY(this.height, this.minecraft.getWindow().getGuiScale());
                    boolean bl = transform.centered = !transform.centered;
                    if (this.draggedModule == null) break;
                    int newModuleY = ((ModuleSession)affectedModule.getCurrentSession()).getEffectiveY(this.height, this.minecraft.getWindow().getGuiScale());
                    this.dragOffsetY += newModuleY - oldModuleY;
                    break;
                }
                case 9: {
                    boolean curFlippedHor = transform.flippedHor;
                    transform.flippedHor = !transform.flippedVer;
                    transform.flippedVer = curFlippedHor;
                    break;
                }
                case 22: {
                    Screen configScreen = affectedModule.getConfigScreenFactory().apply((Screen)this);
                    if (configScreen == null) break;
                    this.minecraft.gui.setScreen(configScreen);
                }
            }
        }
        return super.keyPressed(event);
    }

    private void handleDraggedModule(int mouseX, int mouseY, double screenScale) {
        if (this.draggedModule == null) {
            return;
        }
        Object session = this.draggedModule.getCurrentSession();
        if (session == null) {
            return;
        }
        ModuleTransform transform = this.draggedModule.getUnconfirmedTransform();
        transform.y = mouseY + this.dragOffsetY;
        transform.fromBottom = false;
        int moduleH = ((ModuleSession)session).getHeight(screenScale);
        int yFromBottom = this.height - transform.y - moduleH;
        if (transform.y > yFromBottom) {
            transform.fromBottom = true;
            transform.y = yFromBottom;
        }
        if (transform.centered) {
            return;
        }
        transform.x = mouseX + this.dragOffsetX;
        transform.fromRight = false;
        int moduleW = ((ModuleSession)session).getWidth(screenScale);
        int xFromRight = this.width - transform.x - moduleW;
        if (transform.x > xFromRight) {
            transform.fromRight = true;
            transform.x = xFromRight;
        }
    }

    private Component getTooltipText(HudModule<?> hoveredModule) {
        ModuleTransform transform = hoveredModule.getUnconfirmedTransform();
        MutableComponent centeredLine = Component.translatable((String)"%s %s %s", (Object[])new Object[]{CENTERED_COMPONENT, transform.centered ? TRUE_COMPONENT : FALSE_COMPONENT, PRESS_C_COMPONENT});
        MutableComponent flippedLine = Component.translatable((String)"%s %s %s %s", (Object[])new Object[]{FLIPPED_COMPONENT, transform.flippedHor ? TRUE_COMPONENT : FALSE_COMPONENT, transform.flippedVer ? TRUE_COMPONENT : FALSE_COMPONENT, PRESS_F_COMPONENT});
        return Component.translatable((String)"%s \n %s \n %s", (Object[])new Object[]{hoveredModule.getDisplayName(), centeredLine, flippedLine});
    }
}

