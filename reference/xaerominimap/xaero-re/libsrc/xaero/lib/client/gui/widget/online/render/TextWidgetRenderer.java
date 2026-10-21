/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.lib.client.gui.widget.online.TextWidget;
import xaero.lib.client.gui.widget.online.render.ScalableWidgetRenderer;

public class TextWidgetRenderer
extends ScalableWidgetRenderer<TextWidget> {
    @Override
    protected void renderScaled(GuiGraphicsExtractor guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, TextWidget widget) {
        guiGraphics.text(Minecraft.getInstance().font, widget.getText(), 0, 0, -1);
    }
}

