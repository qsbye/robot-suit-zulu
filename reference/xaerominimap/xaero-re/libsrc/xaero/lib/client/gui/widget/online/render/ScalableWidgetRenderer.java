/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;
import xaero.lib.client.gui.widget.online.ScalableWidget;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.render.WidgetRenderer;

public abstract class ScalableWidgetRenderer<T extends ScalableWidget>
implements WidgetRenderer<T> {
    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, T widget) {
        Matrix3x2fStack matrixStack = guiGraphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate((float)((Widget)widget).getX(width), (float)((Widget)widget).getY(height));
        if (((ScalableWidget)widget).isNoGuiScale()) {
            matrixStack.scale((float)(1.0 / guiScale), (float)(1.0 / guiScale));
        }
        matrixStack.scale((float)((ScalableWidget)widget).getScale(), (float)((ScalableWidget)widget).getScale());
        matrixStack.translate((float)((ScalableWidget)widget).getScaledOffsetX(), (float)((ScalableWidget)widget).getScaledOffsetY());
        this.renderScaled(guiGraphics, width, height, mouseX, mouseY, guiScale, widget);
        matrixStack.popMatrix();
    }

    protected abstract void renderScaled(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, double var6, T var8);
}

