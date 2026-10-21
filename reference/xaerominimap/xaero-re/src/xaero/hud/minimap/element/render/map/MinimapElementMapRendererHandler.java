/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render.map;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import xaero.common.HudMod;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.element.render.MinimapElementRendererHandler;
import xaero.lib.client.graphics.XaeroBufferProvider;

public final class MinimapElementMapRendererHandler
extends MinimapElementRendererHandler {
    private double ps;
    private double pc;
    private double zoom;
    private float halfWView;

    private MinimapElementMapRendererHandler(HudMod modMain, List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics guiGraphics) {
        super(modMain, renderers, MinimapElementRenderLocation.IN_MINIMAP, 19490, guiGraphics);
    }

    public void prepareRender(double ps, double pc, double zoom, float halfWView) {
        this.ps = ps;
        this.pc = pc;
        this.zoom = zoom;
        this.halfWView = halfWView;
    }

    @Override
    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E element, double elementX, double elementY, double elementZ, RR renderer, RRC context, int elementIndex, double optionalDepth, MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        PoseStack matrixStack = this.guiGraphics.pose();
        Vec3 renderPos = renderInfo.renderPos;
        double offx = elementX - renderPos.x;
        double offz = elementZ - renderPos.z;
        matrixStack.pushPose();
        double zoomedOffX = offx * this.zoom;
        double zoomedOffZ = offz * this.zoom;
        double translateX = this.ps * zoomedOffX - this.pc * zoomedOffZ;
        double translateY = this.pc * zoomedOffX + this.ps * zoomedOffZ;
        int roundedX = (int)Math.round(translateX);
        int roundedY = (int)Math.round(translateY);
        boolean outOfBounds = (float)Math.abs(roundedX) > this.halfWView || (float)Math.abs(roundedY) > this.halfWView;
        double partialX = translateX - (double)roundedX;
        double partialY = translateY - (double)roundedY;
        matrixStack.translate((float)roundedX, (float)roundedY, 0.0f);
        boolean result = renderer.renderElement(element, false, outOfBounds, optionalDepth, 1.0f, partialX, partialY, renderInfo, this.guiGraphics, xaeroBufferProvider);
        matrixStack.popPose();
        return result;
    }

    @Override
    protected void beforeRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
    }

    @Override
    protected void afterRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
    }

    public static final class Builder
    extends MinimapElementRendererHandler.Builder<Builder> {
        @Override
        public MinimapElementMapRendererHandler build() {
            return (MinimapElementMapRendererHandler)super.build();
        }

        @Override
        protected MinimapElementMapRendererHandler buildInternally(List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics graphics) {
            return new MinimapElementMapRendererHandler(HudMod.INSTANCE, renderers, graphics);
        }

        @Override
        protected Builder setDefault() {
            super.setDefault();
            return this;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

