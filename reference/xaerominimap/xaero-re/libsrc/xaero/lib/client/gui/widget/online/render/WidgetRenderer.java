/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.lib.client.gui.widget.online.Widget;

public interface WidgetRenderer<T extends Widget> {
    public void render(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, double var6, T var8);
}

