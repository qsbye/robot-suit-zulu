/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import xaero.common.IXaeroMinimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.MySmallButton;

public class GuiNewSet
extends ScreenBase {
    private EditBox nameTextField;
    private MinimapSession session;
    private MinimapWorldManager manager;
    private MinimapWorld minimapWorld;
    private Button confirmButton;

    public GuiNewSet(IXaeroMinimap modMain, MinimapSession session, Screen par1GuiScreen, Screen escapeScreen, MinimapWorld minimapWorld) {
        super(par1GuiScreen, escapeScreen, (Component)Component.translatable((String)"gui.xaero_create_set"));
        this.session = session;
        this.manager = this.session.getWorldManager();
        this.minimapWorld = minimapWorld;
        this.canSkipWorldRender = true;
    }

    public void init() {
        super.init();
        this.nameTextField = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, (Component)Component.translatable((String)"gui.xaero_set_name"));
        this.setFocused((GuiEventListener)this.nameTextField);
        this.nameTextField.setFocused(true);
        this.addRenderableWidget((GuiEventListener)this.nameTextField);
        this.confirmButton = new MySmallButton(200, this.width / 2 - 155, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            if (this.canConfirm()) {
                String setName = this.nameTextField.getValue().replace(":", "\u00a7\u00a7");
                this.minimapWorld.setCurrentWaypointSetId(setName);
                this.minimapWorld.addWaypointSet(setName);
                try {
                    this.session.getWorldManagerIO().saveWorld(this.minimapWorld);
                }
                catch (IOException e) {
                    MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                }
                this.goBack();
            }
        });
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)new MySmallButton(201, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.updateConfirmButton();
    }

    protected void setInitialFocus() {
    }

    private boolean canConfirm() {
        return this.nameTextField.getValue().length() > 0 && this.minimapWorld.getWaypointSet(this.nameTextField.getValue()) == null;
    }

    private void updateConfirmButton() {
        this.confirmButton.active = this.canConfirm();
    }

    public boolean keyPressed(KeyEvent event) {
        boolean result = super.keyPressed(event);
        if (event.key() == 40 && this.canConfirm()) {
            this.confirmButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
            return true;
        }
        return result;
    }

    public void tick() {
        this.updateConfirmButton();
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        this.renderEscapeScreen(guiGraphics, 0, 0, par3);
        super.extractBackground(guiGraphics, par1, par2, par3);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        this.nameTextField.extractRenderState(guiGraphics, par1, par2, par3);
    }
}

