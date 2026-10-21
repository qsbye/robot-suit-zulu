/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import xaero.common.HudMod;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.element.render.MinimapElementRendererHandler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.util.TextureUtils;

public class MinimapElementWorldRendererHandler
extends MinimapElementRendererHandler {
    private static final float DEFAULT_SCALE = 0.8f;
    private static final float MINECRAFT_SCALE = 0.02666667f;
    private static final double ELEMENT_WORLD_SCALE = 0.02133333496749401;
    private final PoseStack matrixStackWorld;
    private final Vector4f origin4f;
    private Matrix4f waypointsProjection;
    private Matrix4f worldModelView;
    private int screenWidth;
    private int screenHeight;
    private Object workingClosestHoveredElement;
    private float workingClosestHoveredElementDistance;
    private MinimapElementRenderer<?, ?> workingClosestHoveredElementRenderer;
    private Object previousClosestHoveredElement;
    private MinimapElementRenderer<?, ?> previousClosestHoveredElementRenderer;
    private boolean previousClosestHoveredElementPresent;
    private boolean renderingMainHighlightedElement;
    private ProjectionMatrixBuffer orthoProjectionMatrixCache;
    private Projection orthoProjectionCache;

    protected MinimapElementWorldRendererHandler(HudMod modMain, List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics guiGraphics, PoseStack matrixStackWorld, Vector4f origin4f) {
        super(modMain, renderers, MinimapElementRenderLocation.IN_WORLD, 19499, guiGraphics);
        this.matrixStackWorld = matrixStackWorld;
        this.origin4f = origin4f;
    }

    public void prepareRender(Matrix4f waypointsProjection, Matrix4f worldModelView) {
        this.waypointsProjection = waypointsProjection;
        this.worldModelView = worldModelView;
    }

    @Override
    public void render(Vec3 renderPos, float partialTicks, RenderTarget framebuffer, double backgroundCoordinateScale, ResourceKey<Level> mapDimension) {
        if (HudMod.INSTANCE.getSupportMods().vivecraft) {
            return;
        }
        this.renderingMainHighlightedElement = false;
        super.render(renderPos, partialTicks, framebuffer, backgroundCoordinateScale, mapDimension);
    }

    @Override
    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E element, double elementX, double elementY, double elementZ, RR renderer, RRC context, int elementIndex, double optionalDepth, MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        PoseStack matrixStackOverlay = this.guiGraphics.pose();
        float partialTicks = renderInfo.partialTicks;
        Vec3 renderPos = renderInfo.renderPos;
        MinimapElementReader<E, RRC> elementReader = renderer.getElementReader();
        double offX = elementX - renderPos.x;
        double offY = elementReader.getRenderY(element, context, partialTicks) - renderPos.y;
        double offZ = elementZ - renderPos.z;
        Vector3f lookVector = Minecraft.getInstance().gameRenderer.mainCamera().forwardVector().get(new Vector3f());
        double depth = offX * (double)lookVector.x() + offY * (double)lookVector.y() + offZ * (double)lookVector.z();
        if (depth < 0.05) {
            return false;
        }
        if (!this.renderingMainHighlightedElement && element == this.previousClosestHoveredElement) {
            this.previousClosestHoveredElementPresent = true;
            return false;
        }
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        if (distance > 250000.0) {
            double offScaler = 250000.0 / distance;
            offX *= offScaler;
            offY *= offScaler;
            offZ *= offScaler;
        }
        matrixStackOverlay.pushPose();
        this.matrixStackWorld.pushPose();
        this.matrixStackWorld.translate(offX, offY, offZ);
        this.origin4f.mul((Matrix4fc)this.matrixStackWorld.last().pose());
        this.matrixStackWorld.popPose();
        this.origin4f.mul((Matrix4fc)this.waypointsProjection);
        float translateX = (1.0f + this.origin4f.x() / this.origin4f.w()) / 2.0f * (float)this.screenWidth;
        float translateY = (1.0f - this.origin4f.y() / this.origin4f.w()) / 2.0f * (float)this.screenHeight;
        this.origin4f.set(0.0f, 0.0f, 0.0f, 1.0f);
        int roundedX = Math.round(translateX);
        int roundedY = Math.round(translateY);
        boolean outOfBounds = roundedX < 0 || roundedY < 0 || roundedX >= this.screenWidth || roundedY >= this.screenHeight;
        boolean renderingHoveredElement = this.isElementHovered(element, roundedX, roundedY, elementReader, context, renderInfo);
        double partialX = translateX - (float)roundedX;
        double partialY = translateY - (float)roundedY;
        matrixStackOverlay.translate((float)roundedX, (float)roundedY, 0.0f);
        boolean highlighted = this.renderingMainHighlightedElement;
        highlighted = highlighted || renderingHoveredElement && elementReader.isAlwaysHighlightedWhenHovered(element, context);
        boolean result = renderer.renderElement(element, highlighted, outOfBounds, optionalDepth, 1.0f, partialX, partialY, renderInfo, this.guiGraphics, xaeroBufferProvider);
        matrixStackOverlay.popPose();
        if (result && renderingHoveredElement) {
            this.handleClosestHovered(element, renderer, roundedX, roundedY);
        }
        return result;
    }

    private <E, RRC> boolean isElementHovered(E element, int roundedX, int roundedY, MinimapElementReader<E, RRC> elementReader, RRC context, MinimapElementRenderInfo renderInfo) {
        int centerX;
        if (!elementReader.isInteractable(this.location, element)) {
            return false;
        }
        float partialTicks = renderInfo.partialTicks;
        int interactionLeft = elementReader.getInteractionBoxLeft(element, context, partialTicks);
        int interactionRight = elementReader.getInteractionBoxRight(element, context, partialTicks);
        int interactionTop = elementReader.getInteractionBoxTop(element, context, partialTicks);
        int interactionBottom = elementReader.getInteractionBoxBottom(element, context, partialTicks);
        double boxScale = elementReader.getBoxScale(this.location, element, context);
        if (boxScale != 1.0) {
            interactionLeft = (int)((double)interactionLeft * boxScale);
            interactionRight = (int)((double)interactionRight * boxScale);
            interactionTop = (int)((double)interactionTop * boxScale);
            interactionBottom = (int)((double)interactionBottom * boxScale);
        }
        if ((centerX = this.screenWidth / 2) - roundedX < interactionLeft || centerX - roundedX >= interactionRight) {
            return false;
        }
        int centerY = this.screenHeight / 2;
        return centerY - roundedY >= interactionTop && centerY - roundedY < interactionBottom;
    }

    private <E, RRC, RR extends MinimapElementRenderer<E, RRC>> void handleClosestHovered(E element, RR renderer, int roundedX, int roundedY) {
        int centerX = this.screenWidth / 2;
        int centerY = this.screenHeight / 2;
        int screenOffX = roundedX - centerX;
        int screenOffY = roundedY - centerY;
        float squaredScreenDistance = screenOffX * screenOffX + screenOffY * screenOffY;
        if (this.workingClosestHoveredElement == null || squaredScreenDistance < this.workingClosestHoveredElementDistance || element == this.previousClosestHoveredElement && squaredScreenDistance <= this.workingClosestHoveredElementDistance) {
            this.workingClosestHoveredElement = element;
            this.workingClosestHoveredElementDistance = squaredScreenDistance;
            this.workingClosestHoveredElementRenderer = renderer;
        }
    }

    private <E, RR extends MinimapElementRenderer<E, RRC>, RRC> void renderMainHighlightedElement(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        if (!this.previousClosestHoveredElementPresent) {
            return;
        }
        PoseStack matrixStack = this.guiGraphics.pose();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = session.getMultiTextureRenderTypeRenderers();
        Object element = this.previousClosestHoveredElement;
        MinimapElementRenderer<?, ?> renderer = this.previousClosestHoveredElementRenderer;
        this.renderingMainHighlightedElement = true;
        renderer.preRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        boolean result = this.transformAndRenderForRenderer(element, renderer, renderer.getContext(), 0, 0.0, renderInfo, xaeroBufferProvider);
        renderer.postRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        this.renderingMainHighlightedElement = false;
        this.previousClosestHoveredElementPresent = false;
        if (!result) {
            return;
        }
        matrixStack.translate(0.0, 0.0, this.getElementIndexDepth(1, 1));
    }

    @Override
    protected void beforeRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        this.screenWidth = Minecraft.getInstance().getWindow().getWidth();
        this.screenHeight = Minecraft.getInstance().getWindow().getHeight();
        this.matrixStackWorld.pushPose();
        this.matrixStackWorld.last().pose().mul((Matrix4fc)this.worldModelView);
        PoseStack matrixStackOverlay = this.guiGraphics.pose();
        matrixStackOverlay.pushPose();
        matrixStackOverlay.translate(0.0f, 0.0f, -2980.0f);
    }

    @Override
    protected void afterRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider) {
        this.renderMainHighlightedElement(renderInfo, xaeroBufferProvider);
        this.previousClosestHoveredElement = this.workingClosestHoveredElement;
        this.previousClosestHoveredElementRenderer = this.workingClosestHoveredElementRenderer;
        this.workingClosestHoveredElement = null;
        this.workingClosestHoveredElementRenderer = null;
        PoseStack matrixStackOverlay = this.guiGraphics.pose();
        matrixStackOverlay.popPose();
        this.matrixStackWorld.popPose();
        TextureUtils.clearRenderTargetDepth((RenderTarget)Minecraft.getInstance().gameRenderer.mainRenderTarget(), (float)0.0f);
    }

    private void ensureOrthoProjectionCache() {
        if (this.orthoProjectionMatrixCache != null) {
            return;
        }
        this.orthoProjectionMatrixCache = new ProjectionMatrixBuffer("minimap element world render");
        this.orthoProjectionCache = new Projection();
    }

    public GpuBufferSlice getOrthoProjection(int width, int height) {
        this.ensureOrthoProjectionCache();
        this.orthoProjectionCache.setupOrtho(1000.0f, 3000.0f, (float)width, (float)height, true);
        return this.orthoProjectionMatrixCache.getBuffer(this.orthoProjectionCache);
    }

    public static final class Builder
    extends MinimapElementRendererHandler.Builder<Builder> {
        @Override
        protected MinimapElementRendererHandler buildInternally(List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics graphics) {
            return new MinimapElementWorldRendererHandler(HudMod.INSTANCE, renderers, graphics, new PoseStack(), new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
        }

        @Override
        public MinimapElementWorldRendererHandler build() {
            return (MinimapElementWorldRendererHandler)super.build();
        }

        @Override
        protected Builder setDefault() {
            return this;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

