/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import com.mojang.renderpearl.api.textures.GpuSampler;
import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.ScalableWidget;
import xaero.lib.client.gui.widget.online.WidgetType;

public class ImageWidget
extends ScalableWidget {
    private String imageId;
    private int imageW;
    private int imageH;
    private GpuTextureAndView glTexture;
    private final GpuSampler sampler;

    public ImageWidget(Class<? extends Screen> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, int scaledOffsetX, int scaledOffsetY, String url, String tooltip, double scale, String imageId, int imageW, int imageH, GpuTextureAndView glTexture, GpuSampler sampler, boolean noGuiScale, int minWidgetLevel, int maxWidgetLevel) {
        super(WidgetType.IMAGE, location, horizontalAnchor, verticalAnchor, onClick, onHover, x, y, scaledOffsetX, scaledOffsetY, url, tooltip, noGuiScale, scale, minWidgetLevel, maxWidgetLevel);
        this.imageId = imageId;
        this.imageW = imageW;
        this.imageH = imageH;
        this.glTexture = glTexture;
        this.sampler = sampler;
    }

    public String getImageId() {
        return this.imageId;
    }

    public int getImageW() {
        return this.imageW;
    }

    public int getImageH() {
        return this.imageH;
    }

    public GpuTextureAndView getGlTexture() {
        return this.glTexture;
    }

    @Override
    public int getW() {
        return this.imageW;
    }

    @Override
    public int getH() {
        return this.imageH;
    }

    public GpuSampler getSampler() {
        return this.sampler;
    }
}

