/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.font;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4fc;
import xaero.lib.client.graphics.XaeroBufferProvider;

public class XaeroBufferFontRenderer
implements Font.GlyphVisitor {
    private PoseStack poseStack;
    private Font.DisplayMode displayMode;
    private int lightCoords;
    private XaeroBufferProvider xaeroBufferProvider;

    public void renderText(Font.PreparedText preparedText, PoseStack poseStack, Font.DisplayMode displayMode, int lightCoords, XaeroBufferProvider xaeroBufferProvider) {
        this.poseStack = poseStack;
        this.displayMode = displayMode;
        this.lightCoords = lightCoords;
        this.xaeroBufferProvider = xaeroBufferProvider;
        preparedText.visit((Font.GlyphVisitor)this);
    }

    public void acceptRenderable(TextRenderable renderable) {
        RenderType renderType = renderable.renderType(this.displayMode);
        BufferBuilder vertexConsumer = this.xaeroBufferProvider.getBuffer(renderType);
        renderable.render((Matrix4fc)this.poseStack.last().pose(), (VertexConsumer)vertexConsumer, this.lightCoords, false);
    }
}

