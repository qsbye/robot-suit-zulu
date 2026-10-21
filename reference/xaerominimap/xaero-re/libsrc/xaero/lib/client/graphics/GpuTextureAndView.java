/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public class GpuTextureAndView {
    public final GpuTexture texture;
    public final GpuTextureView view;

    public GpuTextureAndView(GpuTexture texture, GpuTextureView view) {
        this.texture = texture;
        this.view = view;
    }

    public void close() {
        this.view.close();
        this.texture.close();
    }
}

