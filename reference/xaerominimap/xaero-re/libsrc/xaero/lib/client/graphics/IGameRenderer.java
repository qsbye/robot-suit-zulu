/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;

public interface IGameRenderer {
    public GuiRenderer xaero_lib_getGuiRenderer();

    public FogRenderer xaero_lib_getFogRenderer();
}

