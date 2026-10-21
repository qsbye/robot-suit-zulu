/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.NonNull;
import xaero.lib.client.graphics.IRenderSetup;
import xaero.lib.client.graphics.IRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class XaeroBufferProvider {
    private final Map<RenderType, ByteBufferBuilder> byteBufferBuilders;
    private final Map<RenderType, BufferBuilder> usedBufferBuilders;
    private final LinkedHashSet<RenderType> fixedOrder;

    private XaeroBufferProvider(@NonNull Map<RenderType, ByteBufferBuilder> byteBufferBuilders, @NonNull Map<RenderType, BufferBuilder> usedBufferBuilders, @NonNull LinkedHashSet<RenderType> fixedOrder) {
        this.byteBufferBuilders = byteBufferBuilders;
        this.usedBufferBuilders = usedBufferBuilders;
        this.fixedOrder = fixedOrder;
    }

    public void addToFixedOrder(RenderType renderType) {
        if (this.fixedOrder.contains(renderType)) {
            throw new IllegalArgumentException("The provided render type is already in the fixed order!");
        }
        this.fixedOrder.add(renderType);
    }

    public @NonNull BufferBuilder getBuffer(@NonNull RenderType renderType) {
        ByteBufferBuilder byteBufferBuilder;
        BufferBuilder existingBuilder = this.usedBufferBuilders.get(renderType);
        if (existingBuilder != null) {
            if (renderType.canConsolidateConsecutiveGeometry()) {
                return existingBuilder;
            }
            this.endBatch(renderType);
        }
        if ((byteBufferBuilder = this.byteBufferBuilders.get(renderType)) == null) {
            byteBufferBuilder = new ByteBufferBuilder(16384);
            this.byteBufferBuilders.put(renderType, byteBufferBuilder);
        }
        BufferBuilder createdBuilder = new BufferBuilder(byteBufferBuilder, renderType.primitiveTopology(), renderType.format());
        this.usedBufferBuilders.put(renderType, createdBuilder);
        return createdBuilder;
    }

    public void endBatch(@NonNull RenderType renderType) {
        BufferBuilder builder = this.usedBufferBuilders.remove(renderType);
        if (builder == null) {
            return;
        }
        this.render(renderType, builder);
    }

    public void endBatch() {
        BufferBuilder builder;
        RenderType renderType;
        Set<Map.Entry<RenderType, BufferBuilder>> entrySet = this.usedBufferBuilders.entrySet();
        for (Map.Entry<RenderType, BufferBuilder> entry : entrySet) {
            renderType = entry.getKey();
            if (this.fixedOrder.contains(renderType) || renderType.sortOnUpload()) continue;
            builder = entry.getValue();
            this.render(renderType, builder);
        }
        for (Map.Entry<RenderType, BufferBuilder> entry : entrySet) {
            renderType = entry.getKey();
            if (this.fixedOrder.contains(renderType) || !renderType.sortOnUpload()) continue;
            builder = entry.getValue();
            this.render(renderType, builder);
        }
        for (RenderType renderType2 : this.fixedOrder) {
            BufferBuilder builder2 = this.usedBufferBuilders.get(renderType2);
            if (builder2 == null) continue;
            this.render(renderType2, builder2);
        }
        this.usedBufferBuilders.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void render(@NonNull RenderType renderType, @NonNull BufferBuilder builder) {
        MeshData meshData = builder.build();
        if (meshData == null) {
            return;
        }
        if (renderType.sortOnUpload()) {
            meshData.sortQuads(this.byteBufferBuilders.get(renderType), RenderSystem.getProjectionType().vertexSorting());
        }
        RenderSetup renderSetup = ((IRenderType)renderType).xaero_lib_getSetup();
        IRenderSetup iRenderSetup = (IRenderSetup)renderSetup;
        LayeringTransform layeringTransform = iRenderSetup.xaero_lib_getLayeringTransform();
        Consumer layeringModifier = layeringTransform.getModifier();
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        if (layeringModifier != null) {
            modelViewStack.pushMatrix();
            layeringModifier.accept(modelViewStack);
        }
        try {
            ImmediateRenderUtil.drawImmediateMeshData(meshData, renderType.pipeline(), Minecraft.getInstance().gameRenderer.mainRenderTarget(), renderSetup.prepareTextures(Minecraft.getInstance().getTextureManager(), RenderSystem.getSamplerCache(), Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), Minecraft.getInstance().gameRenderer.lightmap()), iRenderSetup.xaero_lib_getTextureTransform());
        }
        finally {
            if (layeringModifier != null) {
                modelViewStack.popMatrix();
            }
        }
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public XaeroBufferProvider build() {
            return new XaeroBufferProvider(new HashMap<RenderType, ByteBufferBuilder>(), new HashMap<RenderType, BufferBuilder>(), new LinkedHashSet<RenderType>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

