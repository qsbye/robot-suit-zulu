/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.core;

import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public interface IPictureInPictureRenderer {
    public GpuTexture xaero_mm_getTexture();

    public GpuTextureView xaero_mm_getTextureView();

    public GpuTextureView xaero_mm_getDepthTextureView();

    public void xaero_mm_prepareTexturesAndProjection(boolean var1, int var2, int var3);
}

