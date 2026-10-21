/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.lib.client.graphics.IGameRenderer;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer
implements IGameRenderer {
    @Shadow
    private GuiRenderer guiRenderer;
    @Shadow
    private FogRenderer fogRenderer;

    @Override
    public GuiRenderer xaero_lib_getGuiRenderer() {
        return this.guiRenderer;
    }

    @Override
    public FogRenderer xaero_lib_getFogRenderer() {
        return this.fogRenderer;
    }
}

