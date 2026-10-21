/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import xaero.common.icon.XaeroIconAtlas;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.graphics.util.TextureUtils;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class ImprovedFramebuffer
extends TextureTarget {
    private static Field MAIN_RENDER_TARGET_FIELD = ReflectionUtils.getFieldReflection(GameRenderer.class, (String)"mainRenderTarget", (String)"Lcom/mojang/blaze3d/pipeline/RenderTarget;");
    private static RenderTarget mainRenderTargetBackup;
    private static GpuTextureView outputColorTextureOverrideBU;
    private static GpuTextureView outputDepthTextureOverrideBU;
    private GpuSampler sampler = XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR);
    private PreparedRenderType.Texture tas;

    public ImprovedFramebuffer(int width, int height, GpuFormat format, GpuFormat depthFormat) {
        super(null, width, height, format, depthFormat);
        GpuTextureView textureView = this.getColorTextureView();
        if (textureView == null) {
            return;
        }
        this.tas = new PreparedRenderType.Texture("Sampler0", this.getColorTextureView(), this.sampler);
    }

    public void bindDefaultFramebuffer(Minecraft mc) {
        ImprovedFramebuffer.restoreMainRenderTarget();
    }

    public void generateMipmaps() {
        if (this.colorTextureView == null) {
            return;
        }
        TextureUtils.generateMipmaps((GpuTextureView)this.colorTextureView);
    }

    private void forceAsMainRenderTarget() {
        if (mainRenderTargetBackup == null) {
            mainRenderTargetBackup = (RenderTarget)ReflectionUtils.getReflectFieldValue((Object)Minecraft.getInstance().gameRenderer, (Field)MAIN_RENDER_TARGET_FIELD);
            outputColorTextureOverrideBU = ImmediateRenderUtil.outputColorTextureOverride;
            outputDepthTextureOverrideBU = ImmediateRenderUtil.outputDepthTextureOverride;
            ImmediateRenderUtil.outputColorTextureOverride = null;
            ImmediateRenderUtil.outputDepthTextureOverride = null;
        }
        ReflectionUtils.setReflectFieldValue((Object)Minecraft.getInstance().gameRenderer, (Field)MAIN_RENDER_TARGET_FIELD, (Object)((Object)this));
    }

    public static void restoreMainRenderTarget() {
        if (mainRenderTargetBackup != null) {
            ReflectionUtils.setReflectFieldValue((Object)Minecraft.getInstance().gameRenderer, (Field)MAIN_RENDER_TARGET_FIELD, (Object)mainRenderTargetBackup);
            if (ImmediateRenderUtil.outputColorTextureOverride == null && ImmediateRenderUtil.outputDepthTextureOverride == null) {
                ImmediateRenderUtil.outputColorTextureOverride = outputColorTextureOverrideBU;
                ImmediateRenderUtil.outputDepthTextureOverride = outputDepthTextureOverrideBU;
            }
            mainRenderTargetBackup = null;
            outputColorTextureOverrideBU = null;
            outputDepthTextureOverrideBU = null;
        }
    }

    public void bindAsMainTarget(boolean viewport) {
        this.forceAsMainRenderTarget();
    }

    public void setColorTexture(GpuTexture texture, GpuTextureView textureView) {
        if (this.colorTexture == texture && this.colorTextureView == textureView) {
            return;
        }
        if (texture != null && texture.getFormat() != this.colorFormat) {
            throw new IllegalArgumentException("The texture's format must match the render target's: " + String.valueOf(this.colorFormat));
        }
        this.colorTexture = texture;
        this.colorTextureView = textureView;
        if (textureView == null) {
            this.tas = null;
            return;
        }
        this.tas = new PreparedRenderType.Texture("Sampler0", textureView, this.sampler);
    }

    public void setColorTexture(XaeroIconAtlas atlas) {
        this.setColorTexture(atlas.getTextureId(), atlas.getTextureView());
    }

    public void setDepthTexture(GpuTexture depthTexture, GpuTextureView textureView) {
        this.depthTexture = depthTexture;
        this.depthTextureView = textureView;
    }

    public GpuSampler getSampler() {
        return this.sampler;
    }

    public void setSampler(GpuSampler sampler) {
        if (this.sampler == sampler) {
            return;
        }
        this.sampler = sampler;
        if (this.colorTextureView != null) {
            this.tas = new PreparedRenderType.Texture("Sampler0", this.colorTextureView, sampler);
        }
    }

    public void closeColorTexture() {
        this.colorTextureView.close();
        this.colorTexture.close();
    }

    public void closeDepthTexture() {
        this.depthTextureView.close();
        this.depthTexture.close();
    }

    public PreparedRenderType.Texture getTas() {
        return this.tas;
    }

    public GpuFormat getFormat() {
        return this.colorFormat;
    }
}

