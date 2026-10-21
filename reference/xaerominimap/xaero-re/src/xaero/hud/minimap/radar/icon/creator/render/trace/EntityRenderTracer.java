/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.trace;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import xaero.common.misc.CachedFunction;
import xaero.common.mixin.MixinSheetedDecalTextureGeneratorAccessor;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.render.pipeline.RenderPipelineWrapper;
import xaero.lib.client.graphics.IRenderType;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class EntityRenderTracer {
    public static boolean TRACING_MODEL_RENDERS;
    private Entity tracedEntity;
    private EntityRenderState tracedEntityRenderState;
    private EntityRenderer tracedEntityRenderer;
    private Class<?> tracedEntityModelClass;
    private List<ModelRenderTrace> traceResultList;
    private ModelRenderTrace lastModelRenderDetected;
    private RenderType lastRenderTypeDetected;
    private final SubmitNodeStorage modelRenderDetectionSubmitNodeStorage = new SubmitNodeStorage();
    private Field enderDragonModelField;
    private Class<?> irisRenderLayerWrapperClass;
    private Method irisRenderLayerWrapperUnwrapMethod;
    private CachedFunction<RenderPipeline, RenderPipeline> fixAdditiveBlendFunction;

    public EntityRenderTracer() {
        this.initReflection();
        this.traceResultList = new ArrayList<ModelRenderTrace>();
        this.fixAdditiveBlendFunction = new CachedFunction<RenderPipeline, RenderPipeline>(this::fixAdditiveBlendUncached);
    }

    public <S extends EntityRenderState> List<ModelRenderTrace> trace(PoseStack matrixStack, Entity entity, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, RenderTarget renderTarget) {
        TRACING_MODEL_RENDERS = true;
        this.tracedEntity = entity;
        this.tracedEntityRenderState = entityRenderState;
        this.tracedEntityRenderer = entityRenderer;
        this.tracedEntityModelClass = null;
        this.traceResultList.clear();
        this.lastModelRenderDetected = null;
        PoseStack.Pose matrixEntryToRestore = matrixStack.last();
        matrixStack.pushPose();
        try {
            Minecraft mc = Minecraft.getInstance();
            EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
            FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.featureRenderDispatcher();
            CameraRenderState cameraState = new CameraRenderState();
            renderDispatcher.submit(entityRenderState, cameraState, 0.0, 0.0, 0.0, matrixStack, (SubmitNodeCollector)this.modelRenderDetectionSubmitNodeStorage);
            try (FeatureRenderDispatcher.PreparedFrame preparedFrame = featureRenderDispatcher.prepareFrame(this.modelRenderDetectionSubmitNodeStorage);
                 RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Item to GUI item atlas", renderTarget.getColorTextureView(), Optional.empty(), renderTarget.hasDepth() ? renderTarget.getDepthTextureView() : null, OptionalDouble.empty());){
                RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
                FeatureRenderDispatcher.renderAllFeatures((RenderPass)renderPass, (FeatureRenderDispatcher.PreparedFrame)preparedFrame);
            }
            this.modelRenderDetectionSubmitNodeStorage.getSubmitsPerOrder().clear();
        }
        catch (Throwable e) {
            this.traceResultList.clear();
            MinimapLogs.LOGGER.error("Exception when calling the full entity renderer for {} before prerendering the icon.", (Object)entity.getScoreboardName(), (Object)e);
        }
        TRACING_MODEL_RENDERS = false;
        this.tracedEntity = null;
        this.tracedEntityRenderState = null;
        this.tracedEntityRenderer = null;
        while (matrixStack.last() != matrixEntryToRestore) {
            matrixStack.popPose();
        }
        return this.traceResultList;
    }

    public void onModelPrepare(ModelFeatureRenderer.Submit<?> submit) {
        this.lastRenderTypeDetected = submit.renderType();
    }

    public void onModelRender(Model<?> model, VertexConsumer vertexConsumer, int color) {
        this.lastModelRenderDetected = null;
        if (this.tracedEntityModelClass == null) {
            EntityModel currentMainModel = this.getEntityRendererModel(this.tracedEntityRenderer);
            Class<?> clazz = this.tracedEntityModelClass = currentMainModel == null ? null : currentMainModel.getClass();
            if (this.tracedEntityModelClass == null) {
                return;
            }
        }
        if (!model.getClass().isAssignableFrom(this.tracedEntityModelClass) && !this.tracedEntityModelClass.isAssignableFrom(model.getClass())) {
            return;
        }
        RenderType lastRenderType = this.lastRenderTypeDetected;
        if (lastRenderType == null && this.traceResultList.isEmpty()) {
            Identifier textureLocation = null;
            try {
                Identifier textureLocationUnchecked;
                Identifier identifier;
                EntityRenderer entityRenderer = this.tracedEntityRenderer;
                if (entityRenderer instanceof LivingEntityRenderer) {
                    LivingEntityRenderer livingEntityRenderer = (LivingEntityRenderer)entityRenderer;
                    identifier = livingEntityRenderer.getTextureLocation((LivingEntityRenderState)this.tracedEntityRenderState);
                } else {
                    identifier = null;
                }
                textureLocation = textureLocationUnchecked = identifier;
            }
            catch (Throwable t) {
                MinimapLogs.LOGGER.error("Couldn't fetch main entity texture when trying to use an alternative render type for an icon!", t);
            }
            if (textureLocation != null) {
                lastRenderType = model.renderType(textureLocation);
            }
        }
        if (lastRenderType == null) {
            return;
        }
        RenderSetup renderState = this.getRenderState(lastRenderType);
        if (renderState == null) {
            return;
        }
        RenderPipeline renderPipeline = lastRenderType.pipeline();
        if (renderPipeline.getColorTargetStates().size() < 1 || ((ColorTargetState)renderPipeline.getColorTargetStates().get(0)).format() != GpuFormat.RGBA8_UNORM) {
            return;
        }
        Map textures = XaeroRenderType.getSetupTextures((RenderSetup)renderState);
        TextureTransform textureTransform = XaeroRenderType.getSetupTextureTransform((RenderSetup)renderState);
        boolean useOverlay = XaeroRenderType.getSetupUseOverlay((RenderSetup)renderState);
        LayeringTransform layeringTransform = XaeroRenderType.getSetupLayeringTransform((RenderSetup)renderState);
        renderPipeline = this.fixAdditiveBlendFunction.apply(renderPipeline);
        UvMapping spriteUvMapping = null;
        Float decalTextureScale = null;
        if (vertexConsumer instanceof SpriteCoordinateExpander) {
            SpriteCoordinateExpander spriteCoordinateExpander = (SpriteCoordinateExpander)vertexConsumer;
            spriteUvMapping = spriteCoordinateExpander.mapping();
        } else if (vertexConsumer instanceof SheetedDecalTextureGenerator) {
            decalTextureScale = Float.valueOf(((MixinSheetedDecalTextureGeneratorAccessor)vertexConsumer).getTextureScale());
        }
        this.lastModelRenderDetected = new ModelRenderTrace(model, textures, textureTransform, useOverlay, lastRenderType.affectsCrumbling(), lastRenderType.sortOnUpload(), layeringTransform, spriteUvMapping, decalTextureScale, renderPipeline, color);
        this.traceResultList.add(this.lastModelRenderDetected);
    }

    private RenderPipeline fixAdditiveBlendUncached(RenderPipeline renderPipeline) {
        RenderPipelineWrapper wrapper = null;
        List originalColorTargetStates = renderPipeline.getColorTargetStates();
        for (int i = 0; i < originalColorTargetStates.size(); ++i) {
            wrapper = this.fixAdditiveBlendUncached(i, wrapper, renderPipeline);
        }
        if (wrapper != null) {
            return wrapper;
        }
        return renderPipeline;
    }

    private RenderPipelineWrapper fixAdditiveBlendUncached(int index, RenderPipelineWrapper wrapper, RenderPipeline renderPipeline) {
        BlendFactor blendSrcFactor;
        ColorTargetState originalColorTargetState = (ColorTargetState)renderPipeline.getColorTargetStates().get(index);
        if (originalColorTargetState == null) {
            return wrapper;
        }
        BlendFunction blendFunction = originalColorTargetState.blendFunction().orElse(null);
        if (blendFunction == null) {
            return wrapper;
        }
        BlendFactor blendDestFactor = blendFunction.color().destFactor();
        if (blendDestFactor == BlendFactor.ONE && (blendSrcFactor = blendFunction.color().sourceFactor()) != BlendFactor.ZERO) {
            blendFunction = new BlendFunction(blendSrcFactor, blendDestFactor, BlendFactor.ZERO, BlendFactor.ONE);
            if (wrapper == null) {
                wrapper = new RenderPipelineWrapper(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)("wrapper/" + renderPipeline.getLocation().getPath())), renderPipeline);
            }
            wrapper.setColorTargetStateOverride(index, new ColorTargetState(Optional.of(blendFunction), originalColorTargetState.format(), originalColorTargetState.writeMask()));
        }
        return wrapper;
    }

    public void onModelPartRender(ModelPart modelRenderer, int color) {
        if (this.lastModelRenderDetected != null) {
            this.lastModelRenderDetected.addVisibleModelPart(modelRenderer, color);
        }
    }

    private RenderSetup getRenderState(RenderType renderType) {
        while (renderType.getClass() == this.irisRenderLayerWrapperClass && this.irisRenderLayerWrapperUnwrapMethod != null) {
            renderType = (RenderType)ReflectionUtils.getReflectMethodValue((Object)renderType, (Method)this.irisRenderLayerWrapperUnwrapMethod, (Object[])new Object[0]);
        }
        return ((IRenderType)renderType).xaero_lib_getSetup();
    }

    public <S extends EntityRenderState> EntityModel<S> getEntityRendererModel(EntityRenderer<?, ? super S> entityRenderer) {
        if (entityRenderer instanceof LivingEntityRenderer) {
            return ((LivingEntityRenderer)entityRenderer).getModel();
        }
        if (entityRenderer instanceof EnderDragonRenderer) {
            return (EntityModel)ReflectionUtils.getReflectFieldValue(entityRenderer, (Field)this.enderDragonModelField);
        }
        return null;
    }

    private void initReflection() {
        this.enderDragonModelField = ReflectionUtils.getFieldReflection(EnderDragonRenderer.class, (String)"model", (String)"Lnet/minecraft/client/model/monster/dragon/EnderDragonModel;");
        try {
            try {
                this.irisRenderLayerWrapperClass = Class.forName("net.coderbot.iris.layer.IrisRenderTypeWrapper");
            }
            catch (ClassNotFoundException e) {
                this.irisRenderLayerWrapperClass = Class.forName("net.coderbot.iris.layer.IrisRenderLayerWrapper");
            }
            this.irisRenderLayerWrapperUnwrapMethod = ReflectionUtils.getMethodReflection(this.irisRenderLayerWrapperClass, (String)"unwrap", (String)"()Lnet/minecraft/client/renderer/rendertype/RenderType;", (Class[])new Class[0]);
            MinimapLogs.LOGGER.info("Old Iris detected and supported!");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void reset() {
        this.fixAdditiveBlendFunction = new CachedFunction<RenderPipeline, RenderPipeline>(this::fixAdditiveBlendUncached);
    }
}

