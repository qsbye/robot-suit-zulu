/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.util;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fStack;
import org.joml.Vector4fc;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class TextureUtils {
    private static ProjectionMatrixBuffer mipmapGenerationProjectionBuffer;
    private static Projection mipmapGenerationProjection;

    public static GpuTextureView getView(Identifier identifier) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(identifier);
        return texture == null ? null : texture.getTextureView();
    }

    public static GpuSampler getSampler(Identifier identifier) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(identifier);
        return texture == null ? null : texture.getSampler();
    }

    public static void clearRenderTarget(RenderTarget renderTarget, Vector4fc color, float depth) {
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(renderTarget.getColorTexture(), color, renderTarget.getDepthTexture(), (double)depth);
    }

    public static void clearRenderTarget(RenderTarget renderTarget, Vector4fc color) {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(renderTarget.getColorTexture(), color);
    }

    public static void clearRenderTargetDepth(RenderTarget renderTarget, float depth) {
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(renderTarget.getDepthTexture(), (double)depth);
    }

    public static IntBuffer allocateIntBuffer(int capacity, ByteOrder order) {
        return ByteBuffer.allocateDirect(capacity * 4).order(order).asIntBuffer();
    }

    public static ByteBuffer allocateByteBuffer(int capacity, ByteOrder order) {
        return ByteBuffer.allocateDirect(capacity).order(order);
    }

    public static void generateMipmaps(GpuTextureView baseTexture) {
        if (baseTexture.mipLevels() < 2) {
            return;
        }
        if (mipmapGenerationProjectionBuffer == null) {
            mipmapGenerationProjectionBuffer = new ProjectionMatrixBuffer("xaero mipmap generation");
            mipmapGenerationProjection = new Projection();
        }
        GpuDevice gpuDevice = RenderSystem.getDevice();
        ArrayList<PreparedRenderType.Texture> textures = new ArrayList<PreparedRenderType.Texture>();
        GpuSampler sampler = XaeroRenderType.getSimpleSampler(FilterMode.LINEAR);
        GpuBufferSlice projectionMatrixBackup = RenderSystem.getProjectionMatrixBuffer();
        ProjectionType projectionTypeBackup = RenderSystem.getProjectionType();
        mipmapGenerationProjection.setupOrtho(1.0f, 3.0f, 1.0f, 1.0f, false);
        GpuBufferSlice projectionBuffer = mipmapGenerationProjectionBuffer.getBuffer(mipmapGenerationProjection);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionBuffer, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        GpuTextureView prevView = gpuDevice.createTextureView(baseTexture.texture(), baseTexture.baseMipLevel(), 1);
        for (int mip = baseTexture.baseMipLevel() + 1; mip < baseTexture.baseMipLevel() + baseTexture.mipLevels(); ++mip) {
            GpuTextureView currentView = gpuDevice.createTextureView(baseTexture.texture(), mip, 1);
            textures.clear();
            textures.add(new PreparedRenderType.Texture("Sampler0", prevView, sampler));
            BufferBuilder vertexBuffer = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
            vertexBuffer.addVertex(0.0f, 1.0f, -2.0f).setUv(0.0f, 1.0f);
            vertexBuffer.addVertex(1.0f, 1.0f, -2.0f).setUv(1.0f, 1.0f);
            vertexBuffer.addVertex(1.0f, 0.0f, -2.0f).setUv(1.0f, 0.0f);
            vertexBuffer.addVertex(0.0f, 0.0f, -2.0f).setUv(0.0f, 0.0f);
            ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.build(), XaeroRenderType.RP_MIPMAP, currentView, null, textures, TextureTransform.DEFAULT_TEXTURING);
            prevView.close();
            prevView = currentView;
        }
        prevView.close();
        modelViewStack.popMatrix();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionMatrixBackup, (ProjectionType)projectionTypeBackup);
    }
}

