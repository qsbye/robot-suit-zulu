/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.util.graphics;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class GuiGraphicsUtils {
    public static void blit(PreparedRenderType.Texture texture, PoseStack pose, int x, int y, float u, float v, int w, int h, boolean depth) {
        GuiGraphicsUtils.blit(texture, pose, x, x + w, y, y + h, 0, w, h, u, v, 256, 256, depth);
    }

    public static void blit(PreparedRenderType.Texture texture, PoseStack pose, int x, int y, int z, float u, float v, int w, int h, int textureW, int textureH, boolean depth) {
        GuiGraphicsUtils.blit(texture, pose, x, x + w, y, y + h, z, w, h, u, v, textureW, textureH, depth);
    }

    static void blit(PreparedRenderType.Texture texture, PoseStack pose, int left, int right, int top, int bottom, int z, int uw, int vh, float u, float v, int textureW, int textureH, boolean depth) {
        GuiGraphicsUtils.innerBlit(texture, pose, left, right, top, bottom, z, (u + 0.0f) / (float)textureW, (u + (float)uw) / (float)textureW, (v + 0.0f) / (float)textureH, (v + (float)vh) / (float)textureH, depth);
    }

    static void innerBlit(PreparedRenderType.Texture texture, PoseStack pose, int left, int right, int top, int bottom, int z, float uLeft, float uRight, float vTop, float vBottom, boolean depth) {
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferBuilder = ImmediateRenderUtil.begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex((Matrix4fc)matrix4f, (float)left, (float)top, (float)z).setUv(uLeft, vTop);
        bufferBuilder.addVertex((Matrix4fc)matrix4f, (float)left, (float)bottom, (float)z).setUv(uLeft, vBottom);
        bufferBuilder.addVertex((Matrix4fc)matrix4f, (float)right, (float)bottom, (float)z).setUv(uRight, vBottom);
        bufferBuilder.addVertex((Matrix4fc)matrix4f, (float)right, (float)top, (float)z).setUv(uRight, vTop);
        ImmediateRenderUtil.drawImmediateMeshData(bufferBuilder.build(), depth ? XaeroRenderType.RP_POSITION_TEX_NO_ALPHA : XaeroRenderType.RP_POSITION_TEX_NO_ALPHA_NO_DEPTH, texture);
    }
}

