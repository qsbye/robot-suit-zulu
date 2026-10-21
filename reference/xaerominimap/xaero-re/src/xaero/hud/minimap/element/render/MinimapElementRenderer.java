/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.lib.client.graphics.XaeroBufferProvider;

public abstract class MinimapElementRenderer<E, RC>
implements Comparable<MinimapElementRenderer<?, ?>> {
    protected final MinimapElementReader<E, RC> elementReader;
    protected final RC context;
    protected final MinimapElementRenderProvider<E, RC> provider;

    public MinimapElementRenderer(MinimapElementReader<E, RC> elementReader, MinimapElementRenderProvider<E, RC> provider, RC context) {
        this.elementReader = elementReader;
        this.context = context;
        this.provider = provider;
    }

    public int getOrder() {
        return 0;
    }

    @Override
    public int compareTo(MinimapElementRenderer<?, ?> o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }

    public RC getContext() {
        return this.context;
    }

    public MinimapElementRenderProvider<E, RC> getProvider() {
        return this.provider;
    }

    public MinimapElementReader<E, RC> getElementReader() {
        return this.elementReader;
    }

    public abstract boolean renderElement(E var1, boolean var2, boolean var3, double var4, float var6, double var7, double var9, MinimapElementRenderInfo var11, MinimapElementGraphics var12, XaeroBufferProvider var13);

    public abstract void preRender(MinimapElementRenderInfo var1, XaeroBufferProvider var2, MultiTextureRenderTypeRendererProvider var3);

    public abstract void postRender(MinimapElementRenderInfo var1, XaeroBufferProvider var2, MultiTextureRenderTypeRendererProvider var3);

    public abstract boolean shouldRender(MinimapElementRenderLocation var1);
}

