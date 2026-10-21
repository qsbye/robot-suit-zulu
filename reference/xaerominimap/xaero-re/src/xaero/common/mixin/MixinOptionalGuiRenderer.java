/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.hud.minimap.render.MinimapPipRenderState;
import xaero.hud.minimap.render.MinimapPipRenderer;

@Mixin(value={GuiRenderer.class})
public class MixinOptionalGuiRenderer {
    @Shadow
    private FeatureRenderDispatcher featureRenderDispatcher;
    @Shadow
    GuiRenderState renderState;
    private MinimapPipRenderer<MinimapPipRenderState> xaeroMinimapRenderer;
    private MinimapPipRenderer<MinimapPipRenderState.Enlarged> xaeroMinimapEnlargedRenderer;

    @ModifyVariable(method={"preparePictureInPictureState"}, at={@At(value="HEAD")}, index=1)
    public <T extends PictureInPictureRenderState> T onPreparePictureInPictureState(T state) {
        if (this.xaeroMinimapRenderer == null) {
            this.xaeroMinimapRenderer = new MinimapPipRenderer<MinimapPipRenderState>(MinimapPipRenderState.class);
        }
        if (state.getClass() == this.xaeroMinimapRenderer.getRenderStateClass()) {
            this.xaeroMinimapRenderer.prepare((MinimapPipRenderState)state, this.renderState, this.featureRenderDispatcher, Minecraft.getInstance().getWindow().getGuiScale());
            return state;
        }
        if (this.xaeroMinimapEnlargedRenderer == null) {
            this.xaeroMinimapEnlargedRenderer = new MinimapPipRenderer<MinimapPipRenderState.Enlarged>(MinimapPipRenderState.Enlarged.class);
        }
        if (state.getClass() == this.xaeroMinimapEnlargedRenderer.getRenderStateClass()) {
            this.xaeroMinimapEnlargedRenderer.prepare((MinimapPipRenderState.Enlarged)state, this.renderState, this.featureRenderDispatcher, Minecraft.getInstance().getWindow().getGuiScale());
        }
        return state;
    }

    @Inject(method={"close"}, at={@At(value="HEAD")})
    public void onClose(CallbackInfo ci) {
        if (this.xaeroMinimapRenderer != null) {
            this.xaeroMinimapRenderer.close();
        }
        if (this.xaeroMinimapEnlargedRenderer != null) {
            this.xaeroMinimapEnlargedRenderer.close();
        }
    }
}

