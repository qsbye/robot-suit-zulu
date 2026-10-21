/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import xaero.lib.client.gui.widget.online.ImageWidget;
import xaero.lib.client.gui.widget.online.render.ScalableWidgetRenderer;
import xaero.lib.client.render.util.GuiRenderUtil;

public class ImageWidgetRenderer
extends ScalableWidgetRenderer<ImageWidget> {
    @Override
    protected void renderScaled(GuiGraphicsExtractor guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, ImageWidget widget) {
        GuiRenderUtil.submitBlit(guiGraphics, RenderPipelines.GUI_TEXTURED, widget.getGlTexture().view, widget.getSampler(), 0, 0, widget.getW(), widget.getH(), 0.0f, 1.0f, 0.0f, 1.0f, -1);
    }
}

