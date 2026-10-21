/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import com.mojang.renderpearl.api.textures.GpuSampler;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.gui.widget.online.ImageWidget;
import xaero.lib.client.gui.widget.online.ScalableWidgetBuilder;
import xaero.lib.client.gui.widget.online.Widget;

public class ImageWidgetBuilder
extends ScalableWidgetBuilder {
    private String imageId;
    private int imageW;
    private int imageH;
    private GpuTextureAndView glTexture;
    private GpuSampler sampler;

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    public void setGlTexture(GpuTextureAndView glTexture) {
        this.glTexture = glTexture;
    }

    public void setSampler(GpuSampler sampler) {
        this.sampler = sampler;
    }

    @Override
    public boolean validate() {
        return super.validate() && this.imageId != null && this.imageW > 0 && this.imageH > 0 && this.glTexture != null && this.sampler != null;
    }

    @Override
    public Widget build() {
        return new ImageWidget(this.location, this.horizontalAnchor, this.verticalAnchor, this.onClick, this.onHover, this.x, this.y, this.scaledOffsetX, this.scaledOffsetY, this.url, this.tooltip, this.scale, this.imageId, this.imageW, this.imageH, this.glTexture, this.sampler, this.noGuiScale, this.minWidgetLevel, this.maxWidgetLevel);
    }
}

