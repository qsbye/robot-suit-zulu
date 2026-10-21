/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xaero.common.HudMod;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.XaeroLib;
import xaero.lib.client.graphics.XaeroBufferProvider;

public abstract class MinimapElementRendererHandler {
    private final HudMod modMain;
    protected final MinimapElementGraphics guiGraphics;
    private final List<MinimapElementRenderer<?, ?>> renderers;
    protected final MinimapElementRenderLocation location;
    private final int indexLimit;

    protected MinimapElementRendererHandler(HudMod modMain, List<MinimapElementRenderer<?, ?>> renderers, MinimapElementRenderLocation location, int indexLimit, MinimapElementGraphics guiGraphics) {
        this.modMain = modMain;
        this.guiGraphics = guiGraphics;
        this.renderers = renderers;
        this.location = location;
        this.indexLimit = indexLimit;
    }

    public void add(MinimapElementRenderer<?, ?> renderer) {
        this.renderers.add(renderer);
        Collections.sort(this.renderers);
    }

    public void render(Vec3 renderPos, float partialTicks, RenderTarget framebuffer, double backgroundCoordinateScale, ResourceKey<Level> mapDimension) {
        Minecraft mc = Minecraft.getInstance();
        Entity renderEntity = mc.getCameraEntity();
        LocalPlayer player = mc.player;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = session.getMultiTextureRenderTypeRenderers();
        XaeroBufferProvider xaeroBufferProvider = XaeroLib.INSTANCE.getClient().getBufferProvider();
        boolean cave = session.getProcessor().isCaveModeDisplayed();
        MinimapElementRenderInfo renderInfo = new MinimapElementRenderInfo(this.location, renderEntity, (Player)player, renderPos, cave, partialTicks, framebuffer, backgroundCoordinateScale, mapDimension);
        PoseStack matrixStack = this.guiGraphics.pose();
        this.beforeRender(renderInfo, xaeroBufferProvider);
        int indexLimit = this.getIndexLimit();
        for (int i = 0; i < this.renderers.size(); ++i) {
            MinimapElementRenderer<?, ?> renderer = this.renderers.get(i);
            int elementIndex = 0;
            elementIndex = this.renderForRenderer(renderer, elementIndex, multiTextureRenderTypeRenderers, indexLimit, renderInfo);
            matrixStack.translate(0.0, 0.0, this.getElementIndexDepth(elementIndex, indexLimit));
            if ((indexLimit -= elementIndex) >= 0) continue;
            indexLimit = 0;
        }
        this.afterRender(renderInfo, xaeroBufferProvider);
        this.guiGraphics.flush();
    }

    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> int renderForRenderer(RR renderer, int elementIndex, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers, int indexLimit, MinimapElementRenderInfo renderInfo) {
        MinimapElementRenderLocation location = this.location;
        if (!renderer.shouldRender(location)) {
            return elementIndex;
        }
        XaeroBufferProvider xaeroBufferProvider = XaeroLib.INSTANCE.getClient().getBufferProvider();
        MinimapElementReader elementReader = renderer.elementReader;
        MinimapElementRenderProvider provider = renderer.provider;
        Object context = renderer.context;
        renderer.preRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        provider.begin(location, context);
        while (provider.hasNext(location, context)) {
            double optionalDepth;
            Object element = provider.setupContextAndGetNext(location, context);
            if (element == null || elementReader.isHidden(element, context) || !this.transformAndRenderForRenderer(element, renderer, context, elementIndex, optionalDepth = this.getElementIndexDepth(elementIndex, indexLimit), renderInfo, xaeroBufferProvider)) continue;
            ++elementIndex;
        }
        provider.end(location, context);
        renderer.postRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        return elementIndex;
    }

    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E element, RR renderer, RRC context, int elementIndex, double optionalDepth, MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        MinimapElementReader<E, RRC> elementReader = renderer.elementReader;
        double elementX = elementReader.getRenderX(element, context, renderInfo.partialTicks);
        double elementY = elementReader.getRenderY(element, context, renderInfo.partialTicks);
        double elementZ = elementReader.getRenderZ(element, context, renderInfo.partialTicks);
        double elementCoordinateScale = elementReader.getCoordinateScale(element, context, renderInfo);
        double coordinateMultiplier = elementCoordinateScale / renderInfo.backgroundCoordinateScale;
        if (coordinateMultiplier == 1.0) {
            return this.transformAndRenderForRenderer(element, elementX, elementY, elementZ, renderer, context, elementIndex, optionalDepth, renderInfo, xaeroBufferProvider);
        }
        if (elementReader.shouldScalePartialCoordinates(element, context, renderInfo)) {
            elementX *= coordinateMultiplier;
            elementZ *= coordinateMultiplier;
        } else {
            int flooredRenderX = OptimizedMath.myFloor(elementX);
            int flooredRenderZ = OptimizedMath.myFloor(elementZ);
            elementX = (double)OptimizedMath.myFloor((double)flooredRenderX * coordinateMultiplier) + (elementX - (double)flooredRenderX);
            elementZ = (double)OptimizedMath.myFloor((double)flooredRenderZ * coordinateMultiplier) + (elementZ - (double)flooredRenderZ);
        }
        return this.transformAndRenderForRenderer(element, elementX, elementY, elementZ, renderer, context, elementIndex, optionalDepth, renderInfo, xaeroBufferProvider);
    }

    protected double getElementIndexDepth(int elementIndex, int indexLimit) {
        return (double)(elementIndex >= indexLimit ? indexLimit : elementIndex) * 0.1;
    }

    public MinimapElementGraphics getGuiGraphics() {
        return this.guiGraphics;
    }

    protected int getIndexLimit() {
        return this.indexLimit;
    }

    protected abstract <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E var1, double var2, double var4, double var6, RR var8, RRC var9, int var10, double var11, MinimapElementRenderInfo var13, XaeroBufferProvider var14);

    protected abstract void beforeRender(MinimapElementRenderInfo var1, XaeroBufferProvider var2);

    protected abstract void afterRender(MinimapElementRenderInfo var1, XaeroBufferProvider var2);

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;
        protected PoseStack poseStack;

        protected Builder() {
        }

        protected B setDefault() {
            this.setPoseStack(null);
            return this.self;
        }

        public B setPoseStack(PoseStack poseStack) {
            this.poseStack = poseStack;
            return this.self;
        }

        public MinimapElementRendererHandler build() {
            if (this.poseStack == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally(new ArrayList(), new MinimapElementGraphics(this.poseStack));
        }

        protected abstract MinimapElementRendererHandler buildInternally(List<MinimapElementRenderer<?, ?>> var1, MinimapElementGraphics var2);
    }
}

