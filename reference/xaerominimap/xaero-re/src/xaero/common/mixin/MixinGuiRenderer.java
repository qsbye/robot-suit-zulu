/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.IGuiRenderer;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={GuiRenderer.class})
public class MixinGuiRenderer
implements IGuiRenderer {
    @Shadow
    private Projection guiProjection;
    @Shadow
    private ProjectionMatrixBuffer guiProjectionMatrixBuffer;

    @Override
    public Projection xaero_mm_getGuiProjection() {
        return this.guiProjection;
    }

    @Override
    public ProjectionMatrixBuffer xaero_mm_getGuiProjectionMatrixBuffer() {
        return this.guiProjectionMatrixBuffer;
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    public void onRenderPre(CallbackInfo ci) {
        XaeroMinimapCore.beforeIngameGuiRender();
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    public void onRenderPost(CallbackInfo ci) {
        XaeroMinimapCore.afterIngameGuiRender();
    }
}

