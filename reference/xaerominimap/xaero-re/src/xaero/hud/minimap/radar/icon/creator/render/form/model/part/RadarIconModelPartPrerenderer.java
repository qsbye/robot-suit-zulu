/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.part;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import xaero.common.graphics.CustomRenderTypes;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.ModelPartUtil;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelPartRenderTrace;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class RadarIconModelPartPrerenderer {
    public final Field quadrupedHeadField = ReflectionUtils.getFieldReflection(QuadrupedModel.class, (String)"head", (String)"Lnet/minecraft/client/model/geom/ModelPart;");
    private final VertexConsumerWrapper vertexConsumerWrapper = new VertexConsumerWrapper();
    private boolean testedRenderEngineWrapperCompatibility;
    private boolean renderEngineIsWrapperCompatible;
    private XaeroBufferProvider testBufferSource = XaeroBufferProvider.Builder.begin().build();

    public void renderPart(PoseStack matrixStack, VertexConsumer vertexConsumer, ModelPart part, ModelPart mainPart, Parameters parameters) {
        if (part == null) {
            return;
        }
        if (parameters.renderedDest.contains(part)) {
            return;
        }
        ModelPartRenderTrace renderInfo = parameters.mrt.getModelPartRenderInfo(part);
        if (renderInfo == null) {
            return;
        }
        if (!ModelPartUtil.hasCubes(part)) {
            return;
        }
        boolean showModelBU = part.visible;
        boolean skipDrawBU = part.skipDraw;
        if (!this.testedRenderEngineWrapperCompatibility) {
            this.testRenderEngineWrapperCompatibility(part, parameters.mrt, renderInfo);
        }
        float centerPointX = mainPart.x;
        float centerPointY = mainPart.y;
        float centerPointZ = mainPart.z;
        ModelPart.Cube biggestMainPartCuboid = ModelPartUtil.getBiggestCuboid(mainPart);
        if (biggestMainPartCuboid != null) {
            centerPointY += (biggestMainPartCuboid.maxY + biggestMainPartCuboid.minY) / 2.0f;
            centerPointZ += (biggestMainPartCuboid.maxZ + biggestMainPartCuboid.minZ) / 2.0f;
        }
        float xRotBU = 0.0f;
        float yRotBU = 0.0f;
        float zRotBU = 0.0f;
        if (parameters.config.modelPartsRotationReset) {
            xRotBU = part.xRot;
            yRotBU = part.yRot;
            zRotBU = part.zRot;
            PartPose initPose = part.getInitialPose();
            part.setRotation(initPose.xRot(), initPose.yRot(), initPose.zRot());
        }
        part.visible = true;
        part.skipDraw = false;
        float xBU = part.x;
        float yBU = part.y;
        float zBU = part.z;
        part.setPos(part.x - centerPointX, part.y - centerPointY, part.z - centerPointZ);
        try {
            if (this.renderEngineIsWrapperCompatible) {
                vertexConsumer = this.vertexConsumerWrapper.prepareDetection(vertexConsumer, 3.0, 61.0, 3.0, 61.0, -497.0, -2.0);
            }
            part.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY, renderInfo.color);
            if ((!this.renderEngineIsWrapperCompatible || this.vertexConsumerWrapper.hasDetectedVertex()) && ARGB.alpha((int)renderInfo.color) > 0) {
                parameters.renderedDest.add(part);
            }
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.info("Exception when rendering entity part. " + String.valueOf(part) + " " + t.getMessage());
        }
        part.setPos(xBU, yBU, zBU);
        if (parameters.config.modelPartsRotationReset) {
            part.setRotation(xRotBU, yRotBU, zRotBU);
        }
        part.visible = showModelBU;
        part.skipDraw = skipDrawBU;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void testRenderEngineWrapperCompatibility(ModelPart part, ModelRenderTrace mrt, ModelPartRenderTrace renderInfo) {
        boolean normalWorks = false;
        try {
            PoseStack testMatrix = new PoseStack();
            testMatrix.translate(0.0f, 0.0f, -2500.0f);
            BufferBuilder actualVertexConsumer = this.testBufferSource.getBuffer(CustomRenderTypes.entityIconRenderType(mrt.textures, mrt.textureTransform, mrt.useOverlay, mrt.affectsCrumbling, mrt.sortOnUpload, mrt.layeringTransform, mrt.layerPipeline));
            part.render(testMatrix, (VertexConsumer)actualVertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY, renderInfo.color);
            normalWorks = true;
            testMatrix = new PoseStack();
            testMatrix.translate(0.0f, 0.0f, -2500.0f);
            this.vertexConsumerWrapper.prepareDetection((VertexConsumer)actualVertexConsumer, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            part.visible = true;
            part.skipDraw = false;
            part.render(testMatrix, (VertexConsumer)this.vertexConsumerWrapper, 0xF000F0, OverlayTexture.NO_OVERLAY, renderInfo.color);
            this.renderEngineIsWrapperCompatible = this.vertexConsumerWrapper.hasDetectedVertex();
            if (!this.renderEngineIsWrapperCompatible) {
                throw new Exception("can't detect vertices");
            }
        }
        catch (Throwable t) {
            if (normalWorks) {
                MinimapLogs.LOGGER.warn("Render engine used for entities is not fully compatible with the minimap entity icons. Using fallback. " + t.getMessage());
            }
        }
        finally {
            this.testBufferSource.endBatch();
        }
        if (normalWorks) {
            this.testedRenderEngineWrapperCompatibility = true;
        }
    }

    public <T extends EntityRenderState> ModelPart renderDeclaredMethod(PoseStack matrixStack, VertexConsumer vertexConsumer, Method method, EntityModel<T> model, ModelPart mainPart, Parameters parameters) {
        if (method == null) {
            return mainPart;
        }
        return this.renderPartsIterable((Iterable)ReflectionUtils.getReflectMethodValue(model, (Method)method, (Object[])new Object[0]), matrixStack, vertexConsumer, mainPart, parameters);
    }

    public ModelPart renderPartsIterable(Iterable<ModelPart> parts, PoseStack matrixStack, VertexConsumer vertexConsumer, ModelPart mainPart, Parameters parameters) {
        if (parts == null) {
            return mainPart;
        }
        Iterator<ModelPart> partsIterator = parts.iterator();
        if (!partsIterator.hasNext()) {
            return mainPart;
        }
        if (mainPart == null) {
            mainPart = partsIterator.next();
            this.renderPart(matrixStack, vertexConsumer, mainPart, mainPart, parameters);
        }
        while (partsIterator.hasNext()) {
            this.renderPart(matrixStack, vertexConsumer, partsIterator.next(), mainPart, parameters);
        }
        return mainPart;
    }

    public static class VertexConsumerWrapper
    implements VertexConsumer {
        private VertexConsumer consumer;
        private boolean detectedVertex;
        private double detectionMinX;
        private double detectionMaxX;
        private double detectionMinY;
        private double detectionMaxY;
        private double detectionMinZ;
        private double detectionMaxZ;

        public VertexConsumerWrapper prepareDetection(VertexConsumer consumer, double detectionMinX, double detectionMaxX, double detectionMinY, double detectionMaxY, double detectionMinZ, double detectionMaxZ) {
            this.consumer = consumer;
            this.detectionMinX = detectionMinX;
            this.detectionMaxX = detectionMaxX;
            this.detectionMinY = detectionMinY;
            this.detectionMaxY = detectionMaxY;
            this.detectionMinZ = detectionMinZ;
            this.detectionMaxZ = detectionMaxZ;
            this.detectedVertex = false;
            return this;
        }

        public VertexConsumer addVertex(float d, float e, float f) {
            if ((double)d >= this.detectionMinX && (double)d <= this.detectionMaxX && (double)e >= this.detectionMinY && (double)e <= this.detectionMaxY && (double)f >= this.detectionMinZ && (double)f <= this.detectionMaxZ) {
                this.detectedVertex = true;
            }
            return this.consumer.addVertex(d, e, f);
        }

        public VertexConsumer setColor(int i, int j, int k, int l) {
            return this.consumer.setColor(i, j, k, l);
        }

        public VertexConsumer setColor(int var1) {
            return this.consumer.setColor(var1);
        }

        public VertexConsumer setUv(float f, float g) {
            return this.consumer.setUv(f, g);
        }

        public VertexConsumer setUv1(int var1, int var2) {
            return this.consumer.setUv1(var1, var2);
        }

        public VertexConsumer setOverlay(int i) {
            return this.consumer.setOverlay(i);
        }

        public VertexConsumer setUv2(int i, int j) {
            return this.consumer.setUv2(i, j);
        }

        public VertexConsumer setUv3(float u, float v) {
            return this.consumer.setUv3(u, v);
        }

        public VertexConsumer setNormal(float f, float g, float h) {
            return this.consumer.setNormal(f, g, h);
        }

        public VertexConsumer setLineWidth(float var1) {
            return this.consumer.setLineWidth(var1);
        }

        public boolean hasDetectedVertex() {
            return this.detectedVertex;
        }
    }

    public static class Parameters {
        public final RadarIconModelConfig config;
        public final ModelRenderTrace mrt;
        public final List<ModelPart> renderedDest;

        public Parameters(RadarIconModelConfig config, ModelRenderTrace mrt, List<ModelPart> renderedDest) {
            this.config = config;
            this.mrt = mrt;
            this.renderedDest = renderedDest;
        }
    }
}

