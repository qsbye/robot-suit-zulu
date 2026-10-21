/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.font.util.FontUtils;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class MinimapElementGraphics {
    private final PoseStack poseStack;
    private final XaeroBufferProvider xaeroBufferProvider = XaeroBufferProvider.Builder.begin().build();

    public MinimapElementGraphics(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public final void fill(int x1, int y1, int x2, int y2, int color) {
        this.fill(RenderPipelines.GUI, x1, y1, x2, y2, color);
    }

    public final void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
        this.flush();
        if (x1 < x2) {
            int x1bu = x1;
            x1 = x2;
            x2 = x1bu;
        }
        if (y1 < y2) {
            int y1bu = y1;
            y1 = y2;
            y2 = y1bu;
        }
        ImmediateRenderUtil.coloredRectangle((Matrix4f)this.pose().last().pose(), (float)x1, (float)y1, (float)x2, (float)y2, (int)color, (RenderPipeline)pipeline);
    }

    public final void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
        this.flush();
        ImmediateRenderUtil.gradientRectangle((Matrix4f)this.pose().last().pose(), (float)x1, (float)y1, (float)x2, (float)y2, (int)color1, (int)color2);
    }

    public final void blit(TextureAtlasSprite sprite, int x, int y, int width, int height, RenderPipeline renderPipeline) {
        this.flush();
        AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());
        if (abstractTexture == null) {
            return;
        }
        ImmediateRenderUtil.texturedRect((PoseStack)this.pose(), (float)x, (float)y, (float)sprite.getU0(), (float)sprite.getV1(), (float)width, (float)height, (float)sprite.getU1(), (float)sprite.getV0(), (float)1.0f, (RenderPipeline)renderPipeline, (PreparedRenderType.Texture)new PreparedRenderType.Texture("Sampler0", abstractTexture.getTextureView(), abstractTexture.getSampler()));
    }

    public final void blit(Identifier texture, int x, int y, int u1, int v1, int width, int height, int u2, int v2, int textureSize, RenderPipeline renderPipeline) {
        this.flush();
        AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(texture);
        if (abstractTexture == null) {
            return;
        }
        ImmediateRenderUtil.texturedRect((PoseStack)this.pose(), (float)x, (float)y, (float)u1, (float)v2, (float)width, (float)height, (float)u2, (float)v1, (float)textureSize, (RenderPipeline)renderPipeline, (PreparedRenderType.Texture)new PreparedRenderType.Texture("Sampler0", abstractTexture.getTextureView(), abstractTexture.getSampler()));
    }

    public final void blit(GpuTextureView texture, int x, int y, int u1, int v1, int width, int height, int u2, int v2, int textureSize, RenderPipeline renderPipeline) {
        this.flush();
        GpuSampler sampler = XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR, (FilterMode)FilterMode.NEAREST);
        ImmediateRenderUtil.texturedRect((PoseStack)this.pose(), (float)x, (float)y, (float)u1, (float)v2, (float)width, (float)height, (float)u2, (float)v1, (float)textureSize, (RenderPipeline)renderPipeline, (PreparedRenderType.Texture)new PreparedRenderType.Texture("Sampler0", texture, sampler));
    }

    public final void drawCenteredString(Font font, String text, int x, int y, int color) {
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }

    public final void drawCenteredString(Font font, Component text, int x, int y, int color) {
        this.drawString(font, text, x - font.width((FormattedText)text) / 2, y, color);
    }

    public final void drawCenteredString(Font font, FormattedCharSequence charSequence, int x, int y, int color) {
        this.drawString(font, charSequence, x - font.width(charSequence) / 2, y, color);
    }

    public final void drawString(Font font, String text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, true);
    }

    public final void drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        if (ARGB.alpha((int)color) == 0) {
            return;
        }
        this.flush();
        FontUtils.drawText((Font)font, (PoseStack)this.pose(), (String)text, (float)x, (float)y, (int)color, (boolean)shadow, (Font.DisplayMode)Font.DisplayMode.NORMAL, (XaeroBufferProvider)this.xaeroBufferProvider);
        this.flush();
    }

    public final void drawString(Font font, Component text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, true);
    }

    public final void drawString(Font font, Component text, int x, int y, int color, boolean shadow) {
        if (ARGB.alpha((int)color) == 0) {
            return;
        }
        this.flush();
        FontUtils.drawText((Font)font, (PoseStack)this.pose(), (Component)text, (float)x, (float)y, (int)color, (boolean)shadow, (Font.DisplayMode)Font.DisplayMode.NORMAL, (XaeroBufferProvider)this.xaeroBufferProvider);
        this.flush();
    }

    public final void drawString(Font font, FormattedCharSequence charSequence, int x, int y, int color) {
        this.drawString(font, charSequence, x, y, color, true);
    }

    public final void drawString(Font font, FormattedCharSequence charSequence, int x, int y, int color, boolean shadow) {
        if (ARGB.alpha((int)color) == 0) {
            return;
        }
        this.flush();
        FontUtils.drawText((Font)font, (PoseStack)this.pose(), (FormattedCharSequence)charSequence, (float)x, (float)y, (int)color, (boolean)shadow, (Font.DisplayMode)Font.DisplayMode.NORMAL, (XaeroBufferProvider)this.xaeroBufferProvider);
        this.flush();
    }

    public XaeroBufferProvider getBufferSource() {
        return this.xaeroBufferProvider;
    }

    public PoseStack pose() {
        return this.poseStack;
    }

    public void flush() {
        this.xaeroBufferProvider.endBatch();
    }
}

