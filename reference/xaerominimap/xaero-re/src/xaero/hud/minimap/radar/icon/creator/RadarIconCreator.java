/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Vector4fc;
import xaero.common.graphics.ImprovedFramebuffer;
import xaero.common.icon.XaeroIcon;
import xaero.common.icon.XaeroIconAtlas;
import xaero.common.icon.XaeroIconAtlasManager;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.hud.compat.mods.ImmediatelyFastHelper;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.RadarIconManager;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.EntityRenderTracer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.shader.PositionTexAlphaTestShaderHelper;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.graphics.util.TextureUtils;
import xaero.lib.common.util.Constants;

public class RadarIconCreator {
    private static final int PREFERRED_ATLAS_WIDTH = 1024;
    public static final int ICON_WIDTH = 64;
    public static final int FAR_PLANE = 500;
    private ImprovedFramebuffer formRenderFramebuffer;
    private ImprovedFramebuffer iconRenderFramebuffer;
    private ImprovedFramebuffer atlasRenderFramebuffer;
    private final EntityRenderTracer renderTracer = new EntityRenderTracer();
    private final XaeroIconAtlasManager iconAtlasManager;
    private GpuBufferSlice projectionMatrixBackup;
    private ProjectionType projectionTypeBackup;
    private final ProjectionMatrixBuffer iconOrthoProjectionMatrixCache;
    private final Projection iconOrthoProjectionCache;
    private final ProjectionMatrixBuffer atlasOrthoProjectionMatrixCache;
    private final Projection atlasOrthoProjectionCache;

    public RadarIconCreator() {
        int maxTextureSize = RenderSystem.getDevice().getDeviceInfo().limits().maxTextureSize();
        int atlasTextureSize = Math.min(maxTextureSize, 1024) / 64 * 64;
        this.iconAtlasManager = new XaeroIconAtlasManager(64, atlasTextureSize, new ArrayList<XaeroIconAtlas>());
        this.initFramebuffers(atlasTextureSize);
        this.iconOrthoProjectionMatrixCache = new ProjectionMatrixBuffer("icon creator ortho");
        this.iconOrthoProjectionCache = new Projection();
        this.atlasOrthoProjectionMatrixCache = new ProjectionMatrixBuffer("icon creator atlas ortho");
        this.atlasOrthoProjectionCache = new Projection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <S extends EntityRenderState> XaeroIcon create(MinimapElementGraphics guiGraphics, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, Entity entity, RenderTarget defaultFramebuffer, Parameters parameters) {
        IRadarIconFormPrerenderer formPrerenderer = parameters.form.getPrerenderer();
        if (formPrerenderer == null) {
            MinimapLogs.LOGGER.error("Tried prerendering radar icon for {} variant {} but the icon form used doesn't have a prerenderer!", (Object)EntityType.getKey((EntityType)entity.getType()), parameters.variant);
            return RadarIconManager.FAILED;
        }
        PoseStack matrixStack = guiGraphics.pose();
        guiGraphics.flush();
        ImmediatelyFastHelper.triggerBatchingBuffersFlush(matrixStack);
        this.formRenderFramebuffer.bindAsMainTarget(true);
        this.setupMatrices(matrixStack, 64, 500);
        List<ModelRenderTrace> traceResult = null;
        EntityModel<? super S> entityModel = null;
        if (formPrerenderer.requiresEntityModel()) {
            if (Minecraft.getInstance().getEntityRenderDispatcher().camera != null) {
                traceResult = this.renderTracer.trace(matrixStack, entity, entityRenderer, entityRenderState, (RenderTarget)this.formRenderFramebuffer);
                this.formRenderFramebuffer.bindAsMainTarget(true);
            } else {
                MinimapLogs.LOGGER.info("Render info was null for entity " + entity.getScoreboardName());
            }
            entityModel = this.renderTracer.getEntityRendererModel(entityRenderer);
            if (entityModel == null) {
                this.endFormRendering();
                this.bindDefaultFramebuffer(defaultFramebuffer);
                this.restoreMatrices(matrixStack);
                return RadarIconManager.FAILED;
            }
        }
        boolean formRenderResult = false;
        PoseStack.Pose matrixEntryToRestore = matrixStack.last();
        matrixStack.pushPose();
        try {
            TextureUtils.clearRenderTarget((RenderTarget)this.formRenderFramebuffer, (Vector4fc)Constants.ZERO, (float)0.0f);
            formRenderResult = formPrerenderer.prerender(guiGraphics, entityRenderer, entityRenderState, entityModel, entity, traceResult, parameters, (RenderTarget)this.formRenderFramebuffer);
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("Exception using the radar icon form prerenderer for entity {} variant {}!", (Object)EntityType.getKey((EntityType)entity.getType()), parameters.variant, (Object)t);
        }
        finally {
            guiGraphics.flush();
            while (matrixStack.last() != matrixEntryToRestore) {
                matrixStack.popPose();
            }
        }
        this.endFormRendering();
        XaeroIcon icon = parameters.form.getFailureResult();
        if (formRenderResult) {
            icon = this.getFinalIcon(matrixStack, entity, formPrerenderer, parameters);
        }
        this.restoreMatrices(matrixStack);
        this.bindDefaultFramebuffer(defaultFramebuffer);
        return icon;
    }

    private XaeroIcon getFinalIcon(PoseStack matrixStack, Entity entity, IRadarIconFormPrerenderer formPrerenderer, Parameters parameters) {
        this.iconRenderFramebuffer.bindAsMainTarget(true);
        TextureUtils.clearRenderTarget((RenderTarget)this.iconRenderFramebuffer, (Vector4fc)Constants.ZERO);
        TextureUtils.clearRenderTargetDepth((RenderTarget)this.iconRenderFramebuffer, (float)0.0f);
        if (parameters.debug) {
            matrixStack.pushPose();
            matrixStack.translate(18.0f, 10.0f, -10.0f);
            matrixStack.scale(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (float)9.0f, (float)9.0f, (int)-16776961);
            matrixStack.popPose();
        }
        boolean outlined = formPrerenderer.isOutlined();
        boolean flipped = formPrerenderer.isFlipped();
        if (outlined) {
            this.renderOutline(matrixStack);
        }
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        PositionTexAlphaTestShaderHelper.setDiscardAlpha((float)0.05f);
        ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (int)0, (int)0, (float)64.0f, (float)64.0f, (float)64.0f, (float)64.0f, (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_ALPHA_NO_BLEND, (PreparedRenderType.Texture)this.formRenderFramebuffer.getTas());
        if (parameters.debug) {
            matrixStack.pushPose();
            matrixStack.translate(27.0f, 10.0f, -10.0f);
            matrixStack.scale(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (float)9.0f, (float)9.0f, (int)-16711681);
            matrixStack.popPose();
        }
        this.iconRenderFramebuffer.generateMipmaps();
        XaeroIcon icon = null;
        try {
            XaeroIconAtlas atlas = this.getCurrentAtlas();
            icon = atlas.createIcon();
            this.atlasRenderFramebuffer.bindAsMainTarget(false);
            TextureUtils.clearRenderTargetDepth((RenderTarget)this.atlasRenderFramebuffer, (float)0.0f);
            this.atlasOrthoProjectionCache.setupOrtho(-1.0f, 500.0f, (float)this.iconAtlasManager.getAtlasTextureSize(), (float)this.iconAtlasManager.getAtlasTextureSize(), true);
            GpuBufferSlice ortho = this.atlasOrthoProjectionMatrixCache.getBuffer(this.atlasOrthoProjectionCache);
            RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
            matrixStack.translate((float)icon.getOffsetX(), (float)(this.atlasRenderFramebuffer.height - 64 - icon.getOffsetY()), 0.0f);
            this.atlasRenderFramebuffer.setColorTexture(atlas);
            if (flipped) {
                ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (int)0, (int)64, (float)64.0f, (float)64.0f, (float)-64.0f, (float)64.0f, (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_NO_ALPHA_NO_BLEND, (PreparedRenderType.Texture)this.iconRenderFramebuffer.getTas());
            } else {
                ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (int)0, (int)0, (float)64.0f, (float)64.0f, (float)64.0f, (float)64.0f, (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_NO_ALPHA_NO_BLEND, (PreparedRenderType.Texture)this.iconRenderFramebuffer.getTas());
            }
            if (parameters.debug) {
                matrixStack.pushPose();
                matrixStack.translate(36.0f, 10.0f, -10.0f);
                matrixStack.scale(1.0f, 1.0f, 1.0f);
                ImmediateRenderUtil.coloredRectangle((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (float)9.0f, (float)9.0f, (int)-256);
                matrixStack.popPose();
            }
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("Exception rendering to a entity icon atlas for {} {}!", (Object)EntityType.getKey((EntityType)entity.getType()), parameters.variant, (Object)t);
        }
        MinimapRendererHelper.restoreDefaultShaderBlendState();
        return icon;
    }

    private void renderOutline(PoseStack matrixStack) {
        ImmediateRenderUtil.setShaderColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        for (int shadowOffsetX = -1; shadowOffsetX < 2; ++shadowOffsetX) {
            for (int shadowOffsetY = -1; shadowOffsetY < 2; ++shadowOffsetY) {
                if (shadowOffsetX == 0 && shadowOffsetY == 0) continue;
                ImmediateRenderUtil.drawOutlineLayer((PoseStack)matrixStack, (float)shadowOffsetX, (float)shadowOffsetY, (int)0, (int)0, (float)64.0f, (float)64.0f, (float)64.0f, (float)64.0f, (PreparedRenderType.Texture)this.formRenderFramebuffer.getTas(), (float)0.05f);
            }
        }
    }

    private void setupMatrices(PoseStack matrixStack, int finalIconSize, int farPlane) {
        this.projectionMatrixBackup = RenderSystem.getProjectionMatrixBuffer();
        this.projectionTypeBackup = RenderSystem.getProjectionType();
        matrixStack.pushPose();
        matrixStack.setIdentity();
        this.iconOrthoProjectionCache.setupOrtho(-1.0f, 500.0f, (float)finalIconSize, (float)finalIconSize, true);
        GpuBufferSlice ortho = this.iconOrthoProjectionMatrixCache.getBuffer(this.iconOrthoProjectionCache);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().identity();
    }

    private void restoreMatrices(PoseStack matrixStack) {
        matrixStack.popPose();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.projectionMatrixBackup, (ProjectionType)this.projectionTypeBackup);
        RenderSystem.getModelViewStack().popMatrix();
    }

    private void endFormRendering() {
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
    }

    private void bindDefaultFramebuffer(RenderTarget defaultFramebuffer) {
        if (defaultFramebuffer != null) {
            if (defaultFramebuffer instanceof ImprovedFramebuffer) {
                ((ImprovedFramebuffer)defaultFramebuffer).bindAsMainTarget(true);
                return;
            }
            ImprovedFramebuffer.restoreMainRenderTarget();
            return;
        }
        this.atlasRenderFramebuffer.bindDefaultFramebuffer(Minecraft.getInstance());
    }

    public void reset() {
        this.iconAtlasManager.clearAtlases();
        this.atlasRenderFramebuffer.setColorTexture(null, null);
        this.renderTracer.reset();
    }

    public EntityRenderTracer getRenderTracer() {
        return this.renderTracer;
    }

    private XaeroIconAtlas getCurrentAtlas() {
        return this.iconAtlasManager.getCurrentAtlas();
    }

    private void initFramebuffers(int atlasTextureSize) {
        this.formRenderFramebuffer = new ImprovedFramebuffer(512, 512, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        this.iconRenderFramebuffer = new ImprovedFramebuffer(512, 512, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        this.formRenderFramebuffer.setSampler(XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.NEAREST));
        String iconRenderColorTextureName = this.iconRenderFramebuffer.getColorTexture().getLabel();
        this.iconRenderFramebuffer.closeColorTexture();
        GpuTexture iconRenderColorTexture = RenderSystem.getDevice().createTexture(iconRenderColorTextureName, 15, this.iconRenderFramebuffer.getFormat(), 512, 512, 1, 4);
        GpuTextureView iconRenderColorTextureView = RenderSystem.getDevice().createTextureView(iconRenderColorTexture);
        this.iconRenderFramebuffer.setColorTexture(iconRenderColorTexture, iconRenderColorTextureView);
        GpuSampler iconRenderSampler = RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.of(4.0));
        this.iconRenderFramebuffer.setSampler(iconRenderSampler);
        this.atlasRenderFramebuffer = new ImprovedFramebuffer(atlasTextureSize, atlasTextureSize, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        this.atlasRenderFramebuffer.closeColorTexture();
        this.atlasRenderFramebuffer.setColorTexture(null, null);
    }

    public static class Parameters {
        public final Object variant;
        public final float scale;
        public final RadarIconModelConfig defaultModelConfig;
        public final RadarIconForm form;
        public final boolean debug;

        public Parameters(Object variant, RadarIconModelConfig defaultModelConfig, RadarIconForm form, float scale, boolean debug) {
            this.variant = variant;
            this.scale = scale;
            this.defaultModelConfig = defaultModelConfig;
            this.form = form;
            this.debug = debug;
        }
    }
}

