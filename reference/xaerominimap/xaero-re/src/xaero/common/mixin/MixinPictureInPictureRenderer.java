/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IPictureInPictureRenderer;

@Mixin(value={PictureInPictureRenderer.class})
public abstract class MixinPictureInPictureRenderer
implements IPictureInPictureRenderer {
    @Shadow
    private GpuTexture texture;
    @Shadow
    private GpuTextureView textureView;
    @Shadow
    private GpuTextureView depthTextureView;

    @Shadow
    public abstract void prepareTexturesAndProjection(boolean var1, int var2, int var3);

    @Override
    public GpuTexture xaero_mm_getTexture() {
        return this.texture;
    }

    @Override
    public GpuTextureView xaero_mm_getTextureView() {
        return this.textureView;
    }

    @Override
    public GpuTextureView xaero_mm_getDepthTextureView() {
        return this.depthTextureView;
    }

    @Override
    public void xaero_mm_prepareTexturesAndProjection(boolean needsAResize, int width, int height) {
        this.prepareTexturesAndProjection(needsAResize, width, height);
    }
}

