/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.render.module;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.hud.module.ModuleSession;
import xaero.hud.render.module.ModuleRenderContext;

public interface IModuleRenderer<MS extends ModuleSession<MS>> {
    public void render(MS var1, ModuleRenderContext var2, GuiGraphicsExtractor var3, float var4);
}

