/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IGuiGraphics;

@Mixin(value={GuiGraphicsExtractor.class})
public class MixinGuiGraphics
implements IGuiGraphics {
    @Shadow
    private GuiRenderState guiRenderState;

    @Override
    public GuiRenderState xaero_mm_getGuiRenderState() {
        return this.guiRenderState;
    }
}

