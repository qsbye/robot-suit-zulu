/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import xaero.common.core.IPictureInPictureRenderer;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.render.MinimapPipRenderState;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class MinimapPipRenderer<T extends MinimapPipRenderState>
extends PictureInPictureRenderer<T> {
    private final Class<T> stateClass;

    public MinimapPipRenderer(Class<T> stateClass) {
        this.stateClass = stateClass;
    }

    public Class<T> getRenderStateClass() {
        return this.stateClass;
    }

    public void prepare(T state, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int guiScale) {
        if (((MinimapPipRenderState)state).prepared) {
            return;
        }
        ((MinimapPipRenderState)state).prepared = true;
        int width = (((MinimapPipRenderState)state).x1() - ((MinimapPipRenderState)state).x0()) * guiScale;
        int height = (((MinimapPipRenderState)state).y1() - ((MinimapPipRenderState)state).y0()) * guiScale;
        GpuTexture texture = ((IPictureInPictureRenderer)((Object)this)).xaero_mm_getTexture();
        boolean needsResize = texture == null || texture.getWidth(0) != width || texture.getHeight(0) != height;
        ((IPictureInPictureRenderer)((Object)this)).xaero_mm_prepareTexturesAndProjection(needsResize, width, height);
        GpuTextureView textureView = ((IPictureInPictureRenderer)((Object)this)).xaero_mm_getTextureView();
        GpuTextureView depthTextureView = ((IPictureInPictureRenderer)((Object)this)).xaero_mm_getDepthTextureView();
        ImmediateRenderUtil.outputColorTextureOverride = textureView;
        ImmediateRenderUtil.outputDepthTextureOverride = depthTextureView;
        this.renderToTexture(state, null, null);
        ImmediateRenderUtil.outputColorTextureOverride = null;
        ImmediateRenderUtil.outputDepthTextureOverride = null;
        this.blitTexture((PictureInPictureRenderState)state, guiRenderState);
    }

    protected void renderToTexture(T state, PoseStack unused1, SubmitNodeCollector unused2) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        int horizontalPadding = ((MinimapPipRenderState)state).getScaledHorizontalPadding();
        int verticalPadding = ((MinimapPipRenderState)state).getScaledVerticalPadding();
        minimapSession.getProcessor().onRender(horizontalPadding, verticalPadding, ((MinimapPipRenderState)state).getWidth(), ((MinimapPipRenderState)state).getHeight(), ((MinimapPipRenderState)state).getScale(), ((MinimapPipRenderState)state).getMinimapScale(), ((MinimapPipRenderState)state).getSize(), ((MinimapPipRenderState)state).getBoxSize(), ((MinimapPipRenderState)state).getPartial(), ((MinimapPipRenderState)state).getCvc(), ((MinimapPipRenderState)state).x1() - ((MinimapPipRenderState)state).x0(), ((MinimapPipRenderState)state).y1() - ((MinimapPipRenderState)state).y0());
    }

    protected String getTextureLabel() {
        return "minimap";
    }
}

