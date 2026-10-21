/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3fc;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.entity.LivingEntityPoseResetter;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.RadarIconModelPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RadarIconCustomPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.form.model.RadarIconModelForm;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.CustomTextureBinding;
import xaero.lib.client.graphics.ITextureBinding;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class RadarIconModelFormPrerenderer
implements IRadarIconFormPrerenderer {
    private final LivingEntityPoseResetter livingEntityPoseResetter = new LivingEntityPoseResetter();
    private ModelRenderTrace mainModelTrace;
    private final ArrayList<ModelPart> mainRenderedModels = new ArrayList();
    private final RadarIconModelPrerenderer modelPrerenderer = new RadarIconModelPrerenderer();
    private ModelPart mainPart;
    private List<String> hardcodedMainPartAliases;
    private List<String> hardcodedModelPartsFields;
    private boolean forceFieldCheck;
    private boolean fullModelIcon;

    @Override
    public boolean requiresEntityModel() {
        return true;
    }

    @Override
    public boolean isFlipped() {
        return false;
    }

    @Override
    public boolean isOutlined() {
        return true;
    }

    @Override
    public <S extends EntityRenderState> boolean prerender(MinimapElementGraphics guiGraphics, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, @Nullable EntityModel<S> entityModel, Entity entity, @Nullable List<ModelRenderTrace> traceList, RadarIconCreator.Parameters parameters, RenderTarget renderTarget) {
        PoseStack matrixStack = guiGraphics.pose();
        RadarIconModelForm modelForm = (RadarIconModelForm)parameters.form;
        XaeroBufferProvider xaeroBufferProvider = guiGraphics.getBufferSource();
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        if (parameters.debug) {
            matrixStack.pushPose();
            matrixStack.translate(0.0f, 10.0f, -10.0f);
            matrixStack.scale(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (float)9.0f, (float)9.0f, (int)-65536);
            matrixStack.popPose();
        }
        RadarIconModelConfig config = parameters.defaultModelConfig;
        RadarIconModelConfig variantModelConfig = modelForm.getConfig();
        if (variantModelConfig != null) {
            config = variantModelConfig;
        }
        matrixStack.pushPose();
        matrixStack.translate(32.0f, 32.0f, -450.0f);
        matrixStack.translate(config.offsetX, config.offsetY, 0.0f);
        int mainScale = 32;
        matrixStack.scale((float)mainScale, (float)mainScale, (float)(-mainScale));
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.scale(scale, scale, scale);
        }
        matrixStack.scale(config.baseScale, config.baseScale, config.baseScale);
        OptimizedMath.rotatePose(matrixStack, config.rotationY, (Vector3fc)OptimizedMath.YP);
        OptimizedMath.rotatePose(matrixStack, config.rotationX, (Vector3fc)OptimizedMath.XP);
        OptimizedMath.rotatePose(matrixStack, config.rotationZ, (Vector3fc)OptimizedMath.ZP);
        BuiltInRadarIconDefinitions.defaultTransformation(matrixStack, entityModel);
        if (entityRenderState instanceof LivingEntityRenderState) {
            LivingEntityRenderState livingEntityRenderState = (LivingEntityRenderState)entityRenderState;
            this.livingEntityPoseResetter.resetValues(livingEntityRenderState);
        }
        boolean result = this.renderLayers(matrixStack, xaeroBufferProvider, entityRenderer, entityRenderState, entityModel, traceList, entity, config, parameters.defaultModelConfig);
        BuiltInRadarIconDefinitions.defaultPostIconModelRender(matrixStack, entityModel);
        matrixStack.popPose();
        if (parameters.debug) {
            matrixStack.pushPose();
            matrixStack.translate(9.0f, 10.0f, -10.0f);
            matrixStack.scale(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle((PoseStack)matrixStack, (float)0.0f, (float)0.0f, (float)9.0f, (float)9.0f, (int)-16711936);
            matrixStack.popPose();
        }
        return result;
    }

    private <S extends EntityRenderState> boolean renderLayers(PoseStack matrixStack, XaeroBufferProvider bufferSource, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, EntityModel<S> mainEntityModel, List<ModelRenderTrace> traceList, Entity entity, RadarIconModelConfig config, RadarIconModelConfig defaultConfig) {
        boolean bl = this.forceFieldCheck = !(config.renderingFullModel != null && config.renderingFullModel != false || config.modelPartsFields == null && !BuiltInRadarIconDefinitions.forceFieldCheck(mainEntityModel));
        this.fullModelIcon = config.renderingFullModel == null ? !this.forceFieldCheck && BuiltInRadarIconDefinitions.fullModelIcon(mainEntityModel) : config.renderingFullModel;
        boolean renderedSomething = false;
        if (traceList.isEmpty()) {
            this.addDefaultLayer(traceList, entityRenderer, entityRenderState, mainEntityModel, entity);
        }
        boolean allEmpty = true;
        for (ModelRenderTrace mrt : traceList) {
            if (mrt.isEmpty()) continue;
            allEmpty = false;
            break;
        }
        if (allEmpty) {
            for (ModelRenderTrace mrt : traceList) {
                mrt.allVisible = true;
            }
        }
        this.mainPart = null;
        this.mainModelTrace = null;
        this.hardcodedMainPartAliases = BuiltInRadarIconDefinitions.getMainModelPartFields(entityRenderer, mainEntityModel, entity);
        this.hardcodedModelPartsFields = BuiltInRadarIconDefinitions.getSecondaryModelPartsFields(entityRenderer, mainEntityModel, entity);
        this.mainRenderedModels.clear();
        for (ModelRenderTrace mrt : traceList) {
            if (mrt.isEmpty() || renderedSomething && !config.layersAllowed) continue;
            int result = this.renderLayer(matrixStack, bufferSource, mrt, entityRenderState, mainEntityModel, entity, config, defaultConfig);
            if (result == -1) break;
            if (result != 1) continue;
            renderedSomething = true;
        }
        this.hardcodedMainPartAliases = null;
        this.hardcodedModelPartsFields = null;
        if (this.mainRenderedModels.isEmpty() || !config.layersAllowed) {
            return renderedSomething;
        }
        RadarIconCustomPrerenderer extraLayer = BuiltInRadarIconDefinitions.getCustomLayer(entityRenderer, entity);
        RadarIconModelPartPrerenderer partPrerenderer = this.modelPrerenderer.getPartPrerenderer();
        if (extraLayer != null) {
            this.mainPart = extraLayer.render(matrixStack, bufferSource, entityRenderer, entityRenderState, entity, mainEntityModel, partPrerenderer, this.mainRenderedModels, this.mainPart, config, this.mainModelTrace);
        }
        return renderedSomething;
    }

    private <S extends EntityRenderState> void addDefaultLayer(List<ModelRenderTrace> traceList, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, EntityModel<S> mainEntityModel, Entity entity) {
        Identifier mainEntityTexture = null;
        try {
            Identifier identifier;
            if (entityRenderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer livingEntityRenderer = (LivingEntityRenderer)entityRenderer;
                identifier = livingEntityRenderer.getTextureLocation((LivingEntityRenderState)entityRenderState);
            } else {
                identifier = null;
            }
            mainEntityTexture = identifier;
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("Couldn't fetch main entity texture when prerendering an icon with nothing detected!", t);
        }
        if (mainEntityTexture == null) {
            return;
        }
        HashMap<String, ITextureBinding> textures = new HashMap<String, ITextureBinding>();
        textures.put("Sampler0", (ITextureBinding)new CustomTextureBinding(mainEntityTexture, () -> null));
        RenderPipeline basicPipeline = XaeroRenderType.getBasicRenderPipeline();
        traceList.add(new ModelRenderTrace((Model)mainEntityModel, (Map<String, ITextureBinding>)textures, TextureTransform.DEFAULT_TEXTURING, true, true, true, LayeringTransform.NO_LAYERING, null, null, basicPipeline, -1));
    }

    private <S extends EntityRenderState> int renderLayer(PoseStack matrixStack, XaeroBufferProvider bufferSource, ModelRenderTrace mrt, S entityRenderState, EntityModel<S> mainEntityModel, Entity entity, RadarIconModelConfig config, RadarIconModelConfig defaultConfig) {
        boolean mainPartsVisibility;
        Model traceModel = mrt.model;
        if (entity instanceof Player && traceModel != mainEntityModel && traceModel instanceof PlayerModel) {
            return 0;
        }
        Map<String, ITextureBinding> traceTextures = mrt.textures;
        UvMapping uvMapping = mrt.uvMapping;
        boolean mainModel = traceModel == mainEntityModel;
        boolean bl = mainPartsVisibility = mainModel && this.mainModelTrace != null && mrt.sameVisibility(this.mainModelTrace);
        if (mainModel && !mainPartsVisibility) {
            if (traceTextures == null) {
                return 0;
            }
            if (!this.resetModelRotations(entityRenderState, traceModel)) {
                return -1;
            }
            this.mainRenderedModels.clear();
            RadarIconModelPrerenderer.Parameters parameters = new RadarIconModelPrerenderer.Parameters(config, defaultConfig, traceTextures, uvMapping, mrt, this.forceFieldCheck, this.fullModelIcon, this.hardcodedMainPartAliases, this.hardcodedModelPartsFields, this.mainRenderedModels);
            this.mainPart = this.modelPrerenderer.renderModel(matrixStack, bufferSource, entityRenderState, traceModel, entity, this.mainPart, parameters);
            this.mainModelTrace = mrt;
            if (!this.mainRenderedModels.isEmpty()) {
                return 1;
            }
            return 0;
        }
        if (!mainModel) {
            if (traceTextures == null) {
                return 0;
            }
            if (!this.resetModelRotations(entityRenderState, traceModel)) {
                return -1;
            }
            ArrayList<ModelPart> renderedModels = new ArrayList<ModelPart>();
            RadarIconModelPrerenderer.Parameters parameters = new RadarIconModelPrerenderer.Parameters(config, defaultConfig, traceTextures, uvMapping, mrt, this.forceFieldCheck, this.fullModelIcon, this.hardcodedMainPartAliases, this.hardcodedModelPartsFields, renderedModels);
            this.mainPart = this.modelPrerenderer.renderModel(matrixStack, bufferSource, entityRenderState, traceModel, entity, this.mainPart, parameters);
            if (!renderedModels.isEmpty()) {
                return 1;
            }
            return 0;
        }
        if (this.mainRenderedModels.isEmpty()) {
            return 0;
        }
        VertexConsumer vertexConsumer = this.modelPrerenderer.getLayerModelVertexConsumer(bufferSource, traceTextures, uvMapping, mrt);
        RadarIconModelPartPrerenderer.Parameters parameters = new RadarIconModelPartPrerenderer.Parameters(config, mrt, new ArrayList<ModelPart>());
        this.modelPrerenderer.getPartPrerenderer().renderPartsIterable(this.mainRenderedModels, matrixStack, vertexConsumer, this.mainPart, parameters);
        bufferSource.endBatch();
        return 0;
    }

    private <T extends Entity> boolean resetModelRotations(EntityRenderState entityRenderState, Model model) {
        if (!(model instanceof EntityModel)) {
            return true;
        }
        EntityModel entityModel = (EntityModel)model;
        try {
            entityModel.setupAnim((Object)entityRenderState);
            return true;
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("suppressed exception", t);
            return false;
        }
    }
}

