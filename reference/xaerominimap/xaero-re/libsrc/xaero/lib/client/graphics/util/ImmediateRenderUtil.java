/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import xaero.lib.client.graphics.DynamicImmediateGpuBuffer;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.shader.CustomUniforms;
import xaero.lib.client.graphics.shader.PositionTexAlphaTestShaderHelper;
import xaero.lib.platform.Services;

public class ImmediateRenderUtil {
    private static Vector4f SHADER_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final String RENDER_PASS_NAME = "xaero render pass";
    public static final List<PreparedRenderType.Texture> NO_TEXTURES = Collections.unmodifiableList(new ArrayList());
    private static final List<PreparedRenderType.Texture> defaultSingleTextureList = new ArrayList<PreparedRenderType.Texture>();
    private static ByteBufferBuilder sharedByteBufferBuilder = new ByteBufferBuilder(16384);
    private static Map<IndexType, DynamicImmediateGpuBuffer> immediateIndexBuffers = new HashMap<IndexType, DynamicImmediateGpuBuffer>();
    private static DynamicImmediateGpuBuffer immediateVertexBuffer = new DynamicImmediateGpuBuffer(512, 40);
    public static GpuTextureView outputColorTextureOverride;
    public static GpuTextureView outputDepthTextureOverride;

    public static BufferBuilder begin(PrimitiveTopology mode, @Nullable VertexFormat format) {
        return new BufferBuilder(sharedByteBufferBuilder, mode, Objects.requireNonNull(format));
    }

    public static void coloredRectangle(PoseStack matrices, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle(matrices.last().pose(), x1, y1, x2, y2, color);
    }

    public static void coloredRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle(matrix, x1, y1, x2, y2, color, RenderPipelines.GUI);
    }

    public static void coloredRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color, RenderPipeline renderPipeline) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, renderPipeline.getVertexFormatBinding(0));
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y2, 0.0f).setColor(r, g, b, a);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y2, 0.0f).setColor(r, g, b, a);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y1, 0.0f).setColor(r, g, b, a);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y1, 0.0f).setColor(r, g, b, a);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.build(), renderPipeline, NO_TEXTURES, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void gradientRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color1, int color2) {
        float a1 = (float)(color1 >> 24 & 0xFF) / 255.0f;
        float r1 = (float)(color1 >> 16 & 0xFF) / 255.0f;
        float g1 = (float)(color1 >> 8 & 0xFF) / 255.0f;
        float b1 = (float)(color1 & 0xFF) / 255.0f;
        float a2 = (float)(color2 >> 24 & 0xFF) / 255.0f;
        float r2 = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color2 & 0xFF) / 255.0f;
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y2, 0.0f).setColor(r2, g2, b2, a2);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y2, 0.0f).setColor(r2, g2, b2, a2);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y1, 0.0f).setColor(r1, g1, b1, a1);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y1, 0.0f).setColor(r1, g1, b1, a1);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.build(), RenderPipelines.GUI, NO_TEXTURES, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void negativeColorRectangle(PoseStack matrices, float x1, float y1, float x2, float y2) {
        Matrix4f matrix = matrices.last().pose();
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y2, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y2, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y1, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y1, 0.0f);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.build(), XaeroRenderType.RP_NEGATIVE_COLOR, NO_TEXTURES, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture texture) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, texture, 0.0f);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture texture, float discardAlpha) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, texture, discardAlpha, false);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture texture, boolean premultiplied) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, texture, 0.0f, premultiplied);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture texture, float discardAlpha, boolean premultiplied) {
        RenderPipeline pipeline;
        if (discardAlpha < 0.0f) {
            pipeline = premultiplied ? XaeroRenderType.RP_POSITION_TEX_NO_ALPHA_PRE : XaeroRenderType.RP_POSITION_TEX_NO_ALPHA;
        } else {
            pipeline = premultiplied ? XaeroRenderType.RP_POSITION_TEX_ALPHA_PRE : XaeroRenderType.RP_POSITION_TEX_ALPHA;
            PositionTexAlphaTestShaderHelper.setDiscardAlpha(discardAlpha);
        }
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, pipeline, texture);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float textureH, float factor, RenderPipeline renderPipeline, PreparedRenderType.Texture texture) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, (float)textureX, (float)textureY, width, height, (float)textureX + width, (float)textureY + textureH, factor, renderPipeline, texture);
    }

    public static void texturedRect(PoseStack matrixStack, float x, float y, float textureX1, float textureY1, float width, float height, float textureX2, float textureY2, float factor, RenderPipeline renderPipeline, PreparedRenderType.Texture texture) {
        float f;
        float f1 = f = 1.0f / factor;
        Matrix4f matrix = matrixStack.last().pose();
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, renderPipeline.getVertexFormatBinding(0));
        vertexBuffer.addVertex((Matrix4fc)matrix, x + 0.0f, y + height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(textureX1 * f, textureY1 * f1);
        vertexBuffer.addVertex((Matrix4fc)matrix, x + width, y + height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(textureX2 * f, textureY1 * f1);
        vertexBuffer.addVertex((Matrix4fc)matrix, x + width, y + 0.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(textureX2 * f, textureY2 * f1);
        vertexBuffer.addVertex((Matrix4fc)matrix, x + 0.0f, y + 0.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(textureX1 * f, textureY2 * f1);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.build(), renderPipeline, texture, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void drawOutlineLayer(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture texture, float discardAlpha) {
        RenderPipeline pipeline = XaeroRenderType.RP_ICON_OUTLINE;
        PositionTexAlphaTestShaderHelper.setDiscardAlpha(discardAlpha);
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, pipeline, texture);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, PreparedRenderType.Texture texture) {
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, texture, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, PreparedRenderType.Texture texture, TextureTransform textureTransform) {
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, Minecraft.getInstance().gameRenderer.mainRenderTarget(), texture, textureTransform);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, RenderTarget target, PreparedRenderType.Texture texture, TextureTransform textureTransform) {
        List<PreparedRenderType.Texture> textures = NO_TEXTURES;
        if (texture != null) {
            textures = defaultSingleTextureList;
            textures.clear();
            textures.add(texture);
        }
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, target, textures, textureTransform);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, List<PreparedRenderType.Texture> textures) {
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, Minecraft.getInstance().gameRenderer.mainRenderTarget(), textures, TextureTransform.DEFAULT_TEXTURING);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, List<PreparedRenderType.Texture> textures, TextureTransform textureTransform) {
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, Minecraft.getInstance().gameRenderer.mainRenderTarget(), textures, textureTransform);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, RenderTarget target, List<PreparedRenderType.Texture> textures, TextureTransform textureTransform) {
        GpuTextureView colorTarget;
        GpuTextureView gpuTextureView = colorTarget = outputColorTextureOverride != null ? outputColorTextureOverride : target.getColorTextureView();
        GpuTextureView depthTarget = target.hasDepth() ? (outputDepthTextureOverride != null ? outputDepthTextureOverride : target.getDepthTextureView()) : null;
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, colorTarget, depthTarget, textures, textureTransform);
    }

    public static void drawImmediateMeshData(MeshData meshData, RenderPipeline renderPipeline, GpuTextureView colorTarget, GpuTextureView depthTarget, List<PreparedRenderType.Texture> textures, TextureTransform textureTransform) {
        IndexType gpuIndexType;
        GpuBufferSlice gpuIndexBuffer;
        ByteBuffer indexBuffer = meshData.indexBuffer();
        if (indexBuffer == null) {
            RenderSystem.AutoStorageIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer((PrimitiveTopology)meshData.drawState().primitiveTopology());
            gpuIndexBuffer = sequentialBuffer.getBuffer(meshData.drawState().indexCount()).slice();
            gpuIndexType = sequentialBuffer.type();
        } else {
            gpuIndexType = meshData.drawState().indexType();
            DynamicImmediateGpuBuffer immediateIndexBuffer = immediateIndexBuffers.computeIfAbsent(gpuIndexType, indexType -> new DynamicImmediateGpuBuffer(512, 72));
            gpuIndexBuffer = immediateIndexBuffer.allocateSlice(indexBuffer.remaining());
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(gpuIndexBuffer, indexBuffer);
        }
        GpuBufferSlice gpuVertexBuffer = immediateVertexBuffer.allocateSlice(meshData.vertexBuffer().remaining());
        RenderSystem.getDevice().createCommandEncoder().writeToBuffer(gpuVertexBuffer, meshData.vertexBuffer());
        try (MeshData meshData2 = meshData;
             RenderPass renderPass = ImmediateRenderUtil.createRenderPass(RENDER_PASS_NAME, renderPipeline, colorTarget, depthTarget, textures, textureTransform);){
            renderPass.setIndexBuffer(gpuIndexBuffer.buffer(), gpuIndexType);
            renderPass.setVertexBuffer(0, gpuVertexBuffer);
            renderPass.drawIndexed(meshData.drawState().indexCount(), 1, (int)(gpuIndexBuffer.offset() / (long)gpuIndexType.bytes), 0, 0);
        }
    }

    private static GpuBufferSlice getUpdatedDynamicUniforms(TextureTransform textureTransform) {
        return RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f((Vector4fc)SHADER_COLOR), new Vector3f(), textureTransform.createMatrix());
    }

    private static void prepareRenderPass(RenderPass renderPass, RenderPipeline renderPipeline, GpuBufferSlice dynamicUniformsBuffer, List<PreparedRenderType.Texture> textures) {
        renderPass.setPipeline(RenderSystem.getCompiledPipeline((RenderPipeline)renderPipeline));
        ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
        if (scissorState.enabled()) {
            renderPass.enableScissor(scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
        }
        renderPass.setUniform("DynamicTransforms", dynamicUniformsBuffer);
        RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
        Services.PLATFORM.getPlatformRenderHelper().onPrepareRenderPass(renderPass);
        for (PreparedRenderType.Texture texture : textures) {
            renderPass.setUniform(texture.name(), texture.textureView(), texture.sampler());
        }
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline, renderPass);
    }

    private static void updateCustomUniforms(RenderPipeline renderPipeline) {
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline, null);
    }

    private static void updateCustomUniforms(RenderPipeline renderPipeline, RenderPass renderPass) {
        for (BindGroupLayout layout : renderPipeline.getBindGroupLayouts()) {
            for (BindGroupLayout.UniformDescription uniform : layout.uniforms()) {
                GpuBufferSlice valueBuffer;
                if (!CustomUniforms.isCustom(uniform) || (valueBuffer = CustomUniforms.getUpdatedUniformBuffer(uniform)) == null || renderPass == null) continue;
                renderPass.setUniform(uniform.name(), valueBuffer);
            }
        }
    }

    public static RenderPass createRenderPass(String name, RenderPipeline renderPipeline, GpuTextureView colorTarget, GpuTextureView depthTarget, List<PreparedRenderType.Texture> textures, TextureTransform textureTransform) {
        GpuBufferSlice dynamicUniforms = ImmediateRenderUtil.getUpdatedDynamicUniforms(textureTransform);
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline);
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> name, colorTarget, Optional.empty(), depthTarget, OptionalDouble.empty());
        ImmediateRenderUtil.prepareRenderPass(renderPass, renderPipeline, dynamicUniforms, textures);
        return renderPass;
    }

    public static void setShaderColor(float red, float green, float blue, float alpha) {
        SHADER_COLOR.set(red, green, blue, alpha);
    }

    public static void endFrame() {
        immediateIndexBuffers.values().forEach(DynamicImmediateGpuBuffer::clear);
        immediateVertexBuffer.clear();
    }
}

