/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics.renderer.multitexture;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import xaero.lib.client.graphics.DynamicImmediateGpuBuffer;
import xaero.lib.client.graphics.IRenderSetup;
import xaero.lib.client.graphics.IRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class MultiTextureRenderTypeRenderer {
    private static final String RENDER_PASS_NAME = "xaero multitexture render pass";
    private boolean used;
    private ByteBufferBuilder sharedBuffer = new ByteBufferBuilder(16384);
    private BufferBuilder currentBufferBuilder;
    private List<MeshData> buffersForDrawCalls = new ArrayList<MeshData>();
    private DynamicImmediateGpuBuffer immediateVertexBuffer;
    private ArrayList<GpuTextureView> texturesForDrawCalls = new ArrayList();
    private String textureBindTarget;
    private Consumer<GpuTextureView> textureFinalizer;
    private GpuTextureView prevTexture;
    private RenderType renderType;
    private List<DrawCall> drawCalls = new ArrayList<DrawCall>();

    MultiTextureRenderTypeRenderer() {
    }

    void init(String textureBindTarget, Consumer<GpuTextureView> textureFinalizer, RenderType renderType) {
        if (this.used) {
            throw new IllegalStateException("Multi-texture renderer already in use!");
        }
        this.used = true;
        this.textureBindTarget = textureBindTarget;
        this.textureFinalizer = textureFinalizer;
        this.prevTexture = null;
        this.renderType = renderType;
    }

    void draw() {
        if (!this.used) {
            throw new IllegalStateException("Multi-texture renderer is not in use!");
        }
        if (!this.texturesForDrawCalls.isEmpty()) {
            GpuTextureView colorTarget;
            String textureBindTarget = this.textureBindTarget;
            Consumer<GpuTextureView> textureFinalizer = this.textureFinalizer;
            boolean hasTextureFinalizer = textureFinalizer != null;
            this.endBuffer(this.currentBufferBuilder);
            RenderPipeline renderPipeline = this.renderType.pipeline();
            RenderSetup renderSetup = ((IRenderType)this.renderType).xaero_lib_getSetup();
            IRenderSetup iRenderSetup = (IRenderSetup)renderSetup;
            RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
            for (int i = 0; i < this.texturesForDrawCalls.size(); ++i) {
                MeshData meshData = this.buffersForDrawCalls.get(i);
                ByteBuffer indexBuffer = meshData.indexBuffer();
                if (indexBuffer != null) {
                    meshData.close();
                    throw new IllegalArgumentException();
                }
                GpuBufferSlice gpuVertexBufferSlice = this.uploadImmediateVertexBuffer(meshData.vertexBuffer());
                int firstIndex = 0;
                RenderSystem.AutoStorageIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer((PrimitiveTopology)this.renderType.primitiveTopology());
                GpuBuffer gpuIndexBuffer = sequentialBuffer.getBuffer(firstIndex + meshData.drawState().indexCount());
                this.drawCalls.add(new DrawCall(gpuVertexBufferSlice, gpuIndexBuffer, firstIndex, meshData.drawState().indexCount(), sequentialBuffer.type()));
                meshData.close();
            }
            GpuTextureView gpuTextureView = colorTarget = ImmediateRenderUtil.outputColorTextureOverride != null ? ImmediateRenderUtil.outputColorTextureOverride : target.getColorTextureView();
            GpuTextureView depthTarget = target.hasDepth() ? (ImmediateRenderUtil.outputDepthTextureOverride != null ? ImmediateRenderUtil.outputDepthTextureOverride : target.getDepthTextureView()) : null;
            List textures = renderSetup.prepareTextures(Minecraft.getInstance().getTextureManager(), RenderSystem.getSamplerCache(), Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), Minecraft.getInstance().gameRenderer.lightmap());
            GpuSampler sampler = null;
            for (PreparedRenderType.Texture texture : textures) {
                if (!texture.name().equals(textureBindTarget)) continue;
                sampler = texture.sampler();
                break;
            }
            if (sampler == null) {
                throw new IllegalArgumentException("The target sampler used for multi-texture rendering is not part of the render setup: " + textureBindTarget);
            }
            try (RenderPass renderPass = ImmediateRenderUtil.createRenderPass((String)RENDER_PASS_NAME, (RenderPipeline)renderPipeline, (GpuTextureView)colorTarget, (GpuTextureView)depthTarget, (List)textures, (TextureTransform)iRenderSetup.xaero_lib_getTextureTransform());){
                for (int i = 0; i < this.texturesForDrawCalls.size(); ++i) {
                    if (i == 1) {
                        for (PreparedRenderType.Texture texture : textures) {
                            if (texture.name().equals(textureBindTarget)) continue;
                            renderPass.setUniform(texture.name(), null, null);
                        }
                    }
                    GpuTextureView texture = this.texturesForDrawCalls.get(i);
                    renderPass.setUniform(textureBindTarget, texture, sampler);
                    DrawCall drawCall = this.drawCalls.get(i);
                    renderPass.setIndexBuffer(drawCall.indexBuffer, drawCall.indexType);
                    renderPass.setVertexBuffer(0, drawCall.vertexBuffer);
                    renderPass.drawIndexed(drawCall.indexCount, 1, drawCall.firstIndex, 0, 0);
                }
            }
            this.drawCalls.clear();
            if (hasTextureFinalizer) {
                for (int i = 0; i < this.texturesForDrawCalls.size(); ++i) {
                    GpuTextureView texture = this.texturesForDrawCalls.get(i);
                    textureFinalizer.accept(texture);
                }
            }
        }
        this.texturesForDrawCalls.clear();
        this.buffersForDrawCalls.clear();
        if (this.immediateVertexBuffer != null) {
            this.immediateVertexBuffer.clear();
        }
        this.used = false;
        this.renderType = null;
    }

    private GpuBufferSlice uploadImmediateVertexBuffer(ByteBuffer vertexBuffer) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (this.immediateVertexBuffer == null) {
            this.immediateVertexBuffer = new DynamicImmediateGpuBuffer(vertexBuffer.remaining(), 32);
        }
        GpuBufferSlice slice = this.immediateVertexBuffer.allocateSlice((long)vertexBuffer.remaining());
        gpuDevice.createCommandEncoder().writeToBuffer(slice, vertexBuffer);
        return slice;
    }

    private void endBuffer(BufferBuilder builder) {
        this.buffersForDrawCalls.add(builder.build());
    }

    public BufferBuilder begin(GpuTextureView texture) {
        if (!this.used) {
            throw new IllegalStateException("Multi-texture renderer is not in use!");
        }
        if (texture == null) {
            throw new IllegalStateException("Attempted to use the multi-texture renderer with texture null!");
        }
        if (texture != this.prevTexture) {
            if (this.prevTexture != null) {
                this.endBuffer(this.currentBufferBuilder);
            }
            this.currentBufferBuilder = new BufferBuilder(this.sharedBuffer, this.renderType.primitiveTopology(), this.renderType.format());
            this.prevTexture = texture;
            this.texturesForDrawCalls.add(texture);
        }
        return this.currentBufferBuilder;
    }

    private static class DrawCall {
        private final GpuBufferSlice vertexBuffer;
        private final GpuBuffer indexBuffer;
        private final int firstIndex;
        private final int indexCount;
        private final IndexType indexType;

        private DrawCall(GpuBufferSlice vertexBuffer, GpuBuffer indexBuffer, int firstIndex, int indexCount, IndexType indexType) {
            this.vertexBuffer = vertexBuffer;
            this.indexBuffer = indexBuffer;
            this.firstIndex = firstIndex;
            this.indexCount = indexCount;
            this.indexType = indexType;
        }
    }
}

