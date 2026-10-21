/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.Logger;
import xaero.common.graphics.CustomRenderTypes;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.ModelPartUtil;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.ResolvedFieldModelPartRenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.resolver.RadarIconModelFieldResolver;
import xaero.hud.minimap.radar.icon.creator.render.form.model.resolver.ResolvedFieldModelRootPathListener;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.ITextureBinding;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class RadarIconModelPrerenderer {
    private static String[] EXTRA_PART_NAMES = new String[]{"beak", "nose", "jaw", "left_ear", "right_ear", "mane", "mouth", "eyes", "tongue", "hat"};
    private static final Object[] ONE_RENDERER_ARRAY = new Object[1];
    private static final Object[] ONE_OBJECT_ARRAY = new Object[1];
    public static final Identifier LIGHTMAP = Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"entity_icon_model_lightmap");
    private final RadarIconModelPartPrerenderer partPrerenderer;
    private final ResolvedFieldModelPartRenderer resolvedFieldRenderer;
    private final ResolvedFieldModelRootPathListener modelRootPathListener;
    private ModelPart mainPart;
    private final GpuTextureView lightTextureView;
    private PoseStack.Pose decalTextureCameraPose = new PoseStack.Pose();

    public RadarIconModelPrerenderer() {
        this.partPrerenderer = new RadarIconModelPartPrerenderer();
        this.resolvedFieldRenderer = new ResolvedFieldModelPartRenderer();
        this.modelRootPathListener = new ResolvedFieldModelRootPathListener();
        DynamicTexture lightmapTexture = new DynamicTexture("entity_icon_lightmap", 16, 16, false);
        lightmapTexture.getPixels().fillRect(0, 0, 16, 16, -1);
        lightmapTexture.upload();
        Minecraft.getInstance().getTextureManager().register(LIGHTMAP, (AbstractTexture)lightmapTexture);
        this.lightTextureView = lightmapTexture.getTextureView();
    }

    public ModelPart renderModel(PoseStack matrixStack, XaeroBufferProvider bufferSource, EntityRenderState entityRenderState, Model model, Entity entity, ModelPart mainPart, Parameters parameters) {
        this.mainPart = mainPart;
        boolean forceFieldCheck = parameters.forceFieldCheck;
        boolean fullModelIcon = parameters.fullModelIcon;
        RadarIconModelConfig config = parameters.config;
        Object modelRoot = null;
        if (config.modelRootPath != null) {
            modelRoot = this.resolveModelRoot(model, config.modelRootPath, entity);
        }
        if (modelRoot == null) {
            modelRoot = BuiltInRadarIconDefinitions.getModelRoot(model);
        }
        boolean treatAsHierarchicalRoot = false;
        VertexConsumer vertexConsumer = this.getLayerModelVertexConsumer(bufferSource, parameters.textures, parameters.uvMapping, parameters.mrt);
        if (config.modelMainPartFieldAliases != null && !config.modelMainPartFieldAliases.isEmpty()) {
            this.searchAndRenderFields(matrixStack, vertexConsumer, modelRoot, config.modelMainPartFieldAliases, true, parameters);
        }
        if (!forceFieldCheck && !fullModelIcon && (modelRoot instanceof HumanoidModel || modelRoot instanceof QuadrupedModel)) {
            this.renderAgeableListModel((Model)modelRoot, matrixStack, vertexConsumer, parameters);
            bufferSource.endBatch();
            return this.mainPart;
        }
        if (!forceFieldCheck && (treatAsHierarchicalRoot || modelRoot instanceof EntityModel) && this.renderHierarchicalModel(modelRoot, treatAsHierarchicalRoot, matrixStack, vertexConsumer, parameters)) {
            bufferSource.endBatch();
            return this.mainPart;
        }
        if (!forceFieldCheck && modelRoot instanceof HeadedModel) {
            ModelPart headPart = ((HeadedModel)modelRoot).getHead();
            this.renderPart(matrixStack, vertexConsumer, headPart, parameters);
        }
        List<String> hardcodedMainPartAliases = parameters.hardcodedMainPartAliases;
        List<String> hardcodedModelPartsFields = parameters.hardcodedModelPartsFields;
        if (config.modelPartsFields == null) {
            this.searchAndRenderFields(matrixStack, vertexConsumer, modelRoot, hardcodedMainPartAliases, true, parameters);
        }
        List<String> headPartsFields = hardcodedModelPartsFields;
        if (fullModelIcon) {
            headPartsFields = null;
        } else if (config.modelPartsFields != null) {
            headPartsFields = config.modelPartsFields;
        }
        this.searchAndRenderFields(matrixStack, vertexConsumer, modelRoot, headPartsFields, false, parameters);
        bufferSource.endBatch();
        return this.mainPart;
    }

    private void renderAgeableListModel(Model modelRoot, PoseStack matrixStack, VertexConsumer vertexConsumer, Parameters parameters) {
        ModelPart headPart = modelRoot instanceof HumanoidModel ? ((HumanoidModel)modelRoot).head : (ModelPart)ReflectionUtils.getReflectFieldValue((Object)modelRoot, (Field)this.partPrerenderer.quadrupedHeadField);
        this.renderPart(matrixStack, vertexConsumer, headPart, parameters);
    }

    private boolean renderHierarchicalModel(Object modelRoot, boolean treatAsHierarchicalRoot, PoseStack matrixStack, VertexConsumer vertexConsumer, Parameters parameters) {
        ModelPart headPart;
        ModelPart rootPart;
        boolean success;
        block17: {
            success = false;
            if (treatAsHierarchicalRoot) {
                rootPart = (ModelPart)modelRoot;
            } else {
                EntityModel singlePartModel = (EntityModel)modelRoot;
                rootPart = singlePartModel.root();
            }
            if (rootPart == null) {
                return false;
            }
            try {
                headPart = rootPart.getChild("head");
            }
            catch (NoSuchElementException nsee) {
                try {
                    headPart = rootPart.getChild("head_parts");
                }
                catch (NoSuchElementException nsee2) {
                    if (parameters.fullModelIcon) {
                        headPart = null;
                        break block17;
                    }
                    try {
                        headPart = rootPart.getChild("body").getChild("head");
                    }
                    catch (NoSuchElementException nsee3) {
                        try {
                            headPart = rootPart.getChild("body").getChild("head_parts");
                        }
                        catch (NoSuchElementException nsee4) {
                            headPart = null;
                        }
                    }
                }
            }
        }
        if (headPart != null) {
            this.renderPart(matrixStack, vertexConsumer, headPart, parameters);
            success = true;
        }
        if (!parameters.fullModelIcon) {
            for (String extraPartName : EXTRA_PART_NAMES) {
                ModelPart extraPart;
                try {
                    extraPart = rootPart.getChild(extraPartName);
                }
                catch (NoSuchElementException nsee) {
                    extraPart = null;
                }
                if (extraPart == null) continue;
                this.renderPart(matrixStack, vertexConsumer, extraPart, parameters);
                success = true;
            }
            return success;
        }
        Map<String, ModelPart> rootChildren = ModelPartUtil.getChildren(rootPart);
        this.mainPart = this.partPrerenderer.renderPartsIterable(rootChildren.values(), matrixStack, vertexConsumer, this.mainPart, parameters);
        return true;
    }

    private void renderPart(PoseStack matrixStack, VertexConsumer vertexConsumer, ModelPart part, Parameters parameters) {
        if (this.mainPart == null) {
            this.mainPart = part;
        }
        this.partPrerenderer.renderPart(matrixStack, vertexConsumer, part, this.mainPart, parameters);
    }

    private void searchAndRenderFields(PoseStack matrixStack, VertexConsumer vertexBuilder, Object modelRoot, List<String> filter, boolean justOne, Parameters parameters) {
        this.resolvedFieldRenderer.prepare(matrixStack, vertexBuilder, justOne, this.mainPart, parameters, this.partPrerenderer);
        RadarIconModelFieldResolver.searchSuperclassFields(modelRoot, filter, this.resolvedFieldRenderer, ONE_RENDERER_ARRAY);
        this.mainPart = this.resolvedFieldRenderer.getMainPart();
    }

    public VertexConsumer getLayerModelVertexConsumer(XaeroBufferProvider xaeroBufferProvider, Map<String, ITextureBinding> textures, UvMapping uvMapping, ModelRenderTrace mrt) {
        RenderType renderType = CustomRenderTypes.entityIconRenderType(textures, mrt.textureTransform, mrt.useOverlay, mrt.affectsCrumbling, mrt.sortOnUpload, mrt.layeringTransform, mrt.layerPipeline);
        BufferBuilder regularConsumer = xaeroBufferProvider.getBuffer(renderType);
        if (uvMapping != null) {
            return uvMapping.wrap((VertexConsumer)regularConsumer);
        }
        if (mrt.decalTextureScale != null) {
            return new SheetedDecalTextureGenerator((VertexConsumer)regularConsumer, this.decalTextureCameraPose, mrt.decalTextureScale.floatValue());
        }
        return regularConsumer;
    }

    private Object resolveModelRoot(Model model, ArrayList<ArrayList<String>> rootPath, Entity entity) {
        Object currentChainNode = model;
        for (ArrayList<String> pathStep : rootPath) {
            this.modelRootPathListener.prepare();
            RadarIconModelFieldResolver.searchSuperclassFields(currentChainNode, pathStep, this.modelRootPathListener, ONE_OBJECT_ARRAY);
            currentChainNode = this.modelRootPathListener.getCurrentNode();
            if (currentChainNode != null && !this.modelRootPathListener.failed()) continue;
            MinimapLogs.LOGGER.info(String.format("The following entity icon model root path step couldn't be resolved for %s:", EntityType.getKey((EntityType)entity.getType())));
            pathStep.forEach(arg_0 -> ((Logger)MinimapLogs.LOGGER).info(arg_0));
            return null;
        }
        return currentChainNode;
    }

    public RadarIconModelPartPrerenderer getPartPrerenderer() {
        return this.partPrerenderer;
    }

    public static final class Parameters
    extends RadarIconModelPartPrerenderer.Parameters {
        public final RadarIconModelConfig defaultConfig;
        public final Map<String, ITextureBinding> textures;
        public final UvMapping uvMapping;
        public final boolean forceFieldCheck;
        public final boolean fullModelIcon;
        public final List<String> hardcodedMainPartAliases;
        public final List<String> hardcodedModelPartsFields;

        public Parameters(RadarIconModelConfig config, RadarIconModelConfig defaultConfig, Map<String, ITextureBinding> textures, UvMapping uvMapping, ModelRenderTrace mrt, boolean forceFieldCheck, boolean fullModelIcon, List<String> hardcodedMainPartAliases, List<String> hardcodedModelPartsFields, List<ModelPart> renderedDest) {
            super(config, mrt, renderedDest);
            this.defaultConfig = defaultConfig;
            this.textures = textures;
            this.uvMapping = uvMapping;
            this.forceFieldCheck = forceFieldCheck;
            this.fullModelIcon = fullModelIcon;
            this.hardcodedMainPartAliases = hardcodedMainPartAliases;
            this.hardcodedModelPartsFields = hardcodedModelPartsFields;
        }
    }
}

