/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class MinimapRendererHelper {
    private ProjectionMatrixBuffer defaultOrthoMatrixCache;
    private Projection defaultOrthoCache;

    public void drawMyTexturedModalRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture tas, boolean premultiplied) {
        this.drawMyTexturedModalRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, tas, 0.0f, premultiplied);
    }

    public void drawMyTexturedModalRect(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture tas, float discardAlpha, boolean premultiplied) {
        ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)x, (float)y, (int)textureX, (int)textureY, (float)width, (float)height, (float)theight, (float)factor, (PreparedRenderType.Texture)tas, (float)discardAlpha, (boolean)premultiplied);
    }

    public void drawIconOutline(PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, PreparedRenderType.Texture tas, float discardAlpha) {
        ImmediateRenderUtil.drawOutlineLayer((PoseStack)matrixStack, (float)x, (float)y, (int)textureX, (int)textureY, (float)width, (float)height, (float)theight, (float)factor, (PreparedRenderType.Texture)tas, (float)discardAlpha);
    }

    void drawTexturedElipseInsideRectangle(PoseStack matrixStack, double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float widthFactor, PreparedRenderType.Texture tas, boolean premultiplied) {
        this.drawTexturedElipseInsideRectangle(matrixStack, startAngle, sides, x, y, textureX, textureY, width, width, widthFactor, tas, premultiplied);
    }

    void drawTexturedElipseInsideRectangle(PoseStack matrixStack, double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float theight, float widthFactor, PreparedRenderType.Texture tas, boolean premultiplied) {
        float f;
        float f1 = f = 1.0f / widthFactor;
        Matrix4f matrix = matrixStack.last().pose();
        float halfWidth = width / 2.0f;
        double centerX = x + halfWidth;
        double centerY = y + halfWidth;
        float centerU = ((float)textureX + halfWidth) * f;
        float centerV = (float)(((double)textureY + (double)theight * 0.5) * (double)f1);
        double fullCircle = Math.PI * 2;
        float prevVertexLocalX = 0.0f;
        float prevVertexLocalY = 0.0f;
        float prevVertexLocalV = 0.0f;
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin((PrimitiveTopology)PrimitiveTopology.TRIANGLES, (VertexFormat)DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i <= sides; ++i) {
            double angle = startAngle + (double)i / (double)sides * fullCircle;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            float vertexLocalX = halfWidth + (float)((double)halfWidth * sin);
            float vertexLocalY = (float)((double)halfWidth * (1.0 - cos));
            float vertexLocalV = (float)((double)theight * (1.0 - 0.5 * (1.0 - cos)));
            if (i > 0) {
                vertexBuffer.addVertex((Matrix4fc)matrix, x + vertexLocalX, y + vertexLocalY, 0.0f).setUv(((float)textureX + vertexLocalX) * f, ((float)textureY + vertexLocalV) * f1);
                vertexBuffer.addVertex((Matrix4fc)matrix, x + prevVertexLocalX, y + prevVertexLocalY, 0.0f).setUv(((float)textureX + prevVertexLocalX) * f, ((float)textureY + prevVertexLocalV) * f1);
                vertexBuffer.addVertex((Matrix4fc)matrix, (float)centerX, (float)centerY, 0.0f).setUv(centerU, centerV);
            }
            prevVertexLocalX = vertexLocalX;
            prevVertexLocalY = vertexLocalY;
            prevVertexLocalV = vertexLocalV;
        }
        ImmediateRenderUtil.drawImmediateMeshData((MeshData)vertexBuffer.build(), (RenderPipeline)(premultiplied ? XaeroRenderType.RP_POSITION_TEX_NO_ALPHA_PRE : XaeroRenderType.RP_POSITION_TEX_NO_ALPHA), (PreparedRenderType.Texture)tas);
    }

    void drawTexturedElipseInsideRectangleFrame(PoseStack matrixStack, boolean resetTexture, boolean reverseTexture, double startAngle, int startIndex, int endIndex, int sides, float thickness, float x, float y, int textureX, int textureY, float width, float twidth, float theight, int seamWidth, float widthFactor, PreparedRenderType.Texture tas) {
        float f;
        float f1 = f = 1.0f / widthFactor;
        Matrix4f matrix = matrixStack.last().pose();
        float halfWidth = width / 2.0f;
        double fullCircle = Math.PI * 2;
        float prevVertexLocalX = 0.0f;
        float prevVertexLocalY = 0.0f;
        float prevVertexLocalOuterX = 0.0f;
        float prevVertexLocalOuterY = 0.0f;
        float prevSegmentTextureX = 0.0f;
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin((PrimitiveTopology)PrimitiveTopology.QUADS, (VertexFormat)DefaultVertexFormat.POSITION_TEX);
        float outerRadius = halfWidth + thickness;
        float segmentOuterWidth = (float)(fullCircle / (double)sides * (double)outerRadius);
        startIndex = Math.max(Math.min(startIndex, sides), 0);
        endIndex = Math.max(Math.min(endIndex, sides), startIndex);
        int textureStartIndex = resetTexture ? (reverseTexture ? endIndex : startIndex) : 0;
        float seamThreshold = reverseTexture ? (float)seamWidth + segmentOuterWidth : (float)seamWidth;
        for (int i = startIndex; i <= endIndex; ++i) {
            double angle = startAngle + (double)i / (double)sides * fullCircle;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            float vertexLocalX = halfWidth + (float)((double)halfWidth * sin);
            float vertexLocalY = (float)((double)halfWidth * (1.0 - cos));
            float vertexLocalOuterX = halfWidth + (float)((double)outerRadius * sin);
            float vertexLocalOuterY = (float)((double)halfWidth - (double)outerRadius * cos);
            float segmentTextureStartX = textureX;
            float offsetX = Math.abs(segmentOuterWidth * (float)(i - textureStartIndex));
            if (offsetX >= seamThreshold) {
                segmentTextureStartX = textureX + seamWidth;
                if ((offsetX -= seamThreshold) >= twidth) {
                    offsetX %= twidth;
                }
            }
            float segmentTextureX = segmentTextureStartX + offsetX;
            if (i > startIndex) {
                vertexBuffer.addVertex((Matrix4fc)matrix, x + prevVertexLocalX, y + prevVertexLocalY, 0.0f).setUv(prevSegmentTextureX * f, ((float)textureY + theight) * f1);
                vertexBuffer.addVertex((Matrix4fc)matrix, x + vertexLocalX, y + vertexLocalY, 0.0f).setUv(segmentTextureX * f, ((float)textureY + theight) * f1);
                vertexBuffer.addVertex((Matrix4fc)matrix, x + vertexLocalOuterX, y + vertexLocalOuterY, 0.0f).setUv(segmentTextureX * f, (float)textureY * f1);
                vertexBuffer.addVertex((Matrix4fc)matrix, x + prevVertexLocalOuterX, y + prevVertexLocalOuterY, 0.0f).setUv(prevSegmentTextureX * f, (float)textureY * f1);
            }
            prevVertexLocalX = vertexLocalX;
            prevVertexLocalY = vertexLocalY;
            prevVertexLocalOuterX = vertexLocalOuterX;
            prevVertexLocalOuterY = vertexLocalOuterY;
            prevSegmentTextureX = segmentTextureX;
        }
        ImmediateRenderUtil.drawImmediateMeshData((MeshData)vertexBuffer.build(), (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_NO_ALPHA, (PreparedRenderType.Texture)tas);
    }

    public void addTexturedRectToExistingBuffer(Matrix4f matrix, VertexConsumer vertexBuffer, float x, float y, int u, int v, int w, int h) {
        float f = 0.00390625f;
        float normalizedU1 = (float)u * f;
        float normalizedV1 = (float)v * f;
        float normalizedU2 = (float)(u + w) * f;
        float normalizedV2 = (float)(v + h) * f;
        vertexBuffer.addVertex((Matrix4fc)matrix, x, y + (float)h, 0.0f).setUv(normalizedU1, normalizedV2);
        vertexBuffer.addVertex((Matrix4fc)matrix, x + (float)w, y + (float)h, 0.0f).setUv(normalizedU2, normalizedV2);
        vertexBuffer.addVertex((Matrix4fc)matrix, x + (float)w, y, 0.0f).setUv(normalizedU2, normalizedV1);
        vertexBuffer.addVertex((Matrix4fc)matrix, x, y, 0.0f).setUv(normalizedU1, normalizedV1);
    }

    public void drawMyColoredRect(PoseStack matrixStack, float x1, float y1, float x2, float y2, boolean depth) {
        Matrix4f matrix = matrixStack.last().pose();
        BufferBuilder vertexBuffer = ImmediateRenderUtil.begin((PrimitiveTopology)PrimitiveTopology.QUADS, (VertexFormat)DefaultVertexFormat.POSITION);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y2, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y2, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x2, y1, 0.0f);
        vertexBuffer.addVertex((Matrix4fc)matrix, x1, y1, 0.0f);
        ImmediateRenderUtil.drawImmediateMeshData((MeshData)vertexBuffer.build(), (RenderPipeline)(depth ? XaeroRenderType.RP_POSITION : XaeroRenderType.RP_POSITION_NO_DEPTH), (List)ImmediateRenderUtil.NO_TEXTURES);
    }

    public void addColoredLineToExistingBuffer(PoseStack.Pose matrices, VertexConsumer vertexBuffer, float x1, float y1, float x2, float y2, float r, float g, float b, float a, float lineWidth) {
        vertexBuffer.addVertex(matrices, x1, y1, 0.0f).setColor(r, g, b, a).setNormal(matrices, x2 - x1, y2 - y1, 0.0f).setLineWidth(lineWidth);
        vertexBuffer.addVertex(matrices, x2, y2, 0.0f).setColor(r, g, b, a).setNormal(matrices, x2 - x1, y2 - y1, 0.0f).setLineWidth(lineWidth);
    }

    public void drawMyColoredRect(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle((Matrix4f)matrix, (float)x1, (float)y1, (float)x2, (float)y2, (int)color);
    }

    void bindTextureBuffer(ByteBuffer image, int width, int height, int textureHeight, GpuTextureAndView texture) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture.texture, image, 0, 0, 0, textureHeight - height, width, height);
    }

    void putColor(int[] data, int x, int y, int red, int green, int blue, int size) {
        data[y * size + x] = 0xFF000000 | blue << 16 | green << 8 | red;
    }

    void gridOverlay(int[] result, int grid, int red, int green, int blue) {
        result[0] = (red * 3 + (grid >> 16 & 0xFF)) / 4;
        result[1] = (green * 3 + (grid >> 8 & 0xFF)) / 4;
        result[2] = (blue * 3 + (grid & 0xFF)) / 4;
    }

    void slimeOverlay(int[] result, int red, int green, int blue) {
        result[0] = (red + 82) / 2;
        result[1] = (green + 241) / 2;
        result[2] = (blue + 64) / 2;
    }

    public void defaultOrtho(RenderTarget framebuffer) {
        if (framebuffer != null) {
            if (this.defaultOrthoMatrixCache == null) {
                this.defaultOrthoMatrixCache = new ProjectionMatrixBuffer("xaero default ortho");
                this.defaultOrthoCache = new Projection();
            }
            this.defaultOrthoCache.setupOrtho(1000.0f, 21000.0f, (float)framebuffer.width, (float)framebuffer.height, true);
            GpuBufferSlice ortho = this.defaultOrthoMatrixCache.getBuffer(this.defaultOrthoCache);
            RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        }
    }

    public static void restoreDefaultShaderBlendState() {
    }
}

