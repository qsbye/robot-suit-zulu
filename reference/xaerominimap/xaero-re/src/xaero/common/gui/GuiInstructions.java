/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.IXaeroMinimap;
import xaero.lib.client.gui.ScreenBase;

public class GuiInstructions
extends ScreenBase {
    public GuiInstructions(IXaeroMinimap modMain, Screen par1GuiScreen, Screen escape) {
        super(par1GuiScreen, escape, (Component)Component.translatable((String)"gui.xaero_instructions"));
    }

    public void init() {
        super.init();
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.xaero_OK", (Object[])new Object[0]), b -> this.goBack()).bounds(this.width / 2 - 100, this.height / 6 + 168, 200, 20).build());
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractBackground(guiGraphics, par1, par2, par3);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_select", (Object[])new Object[0]), this.width / 2, this.height / 7 + 10, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_drag", (Object[])new Object[0]), this.width / 2, this.height / 7 + 21, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_deselect", (Object[])new Object[0]), this.width / 2, this.height / 7 + 32, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_center", (Object[])new Object[0]), this.width / 2, this.height / 7 + 43, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_different_centered", (Object[])new Object[0]), this.width / 2, this.height / 7 + 54, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_flip", (Object[])new Object[0]), this.width / 2, this.height / 7 + 65, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_settings", (Object[])new Object[0]), this.width / 2, this.height / 7 + 76, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_preset", (Object[])new Object[0]), this.width / 2, this.height / 7 + 87, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_save", (Object[])new Object[0]), this.width / 2, this.height / 7 + 98, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_howto_cancel", (Object[])new Object[0]), this.width / 2, this.height / 7 + 109, -1);
    }
}

