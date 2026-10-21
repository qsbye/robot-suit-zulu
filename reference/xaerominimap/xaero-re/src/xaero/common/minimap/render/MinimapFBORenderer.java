/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import xaero.common.HudMod;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.ImprovedFramebuffer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.region.MinimapChunk;
import xaero.common.minimap.render.MinimapRenderer;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.write.MinimapWriter;
import xaero.common.misc.OptimizedMath;
import xaero.common.settings.ModSettings;
import xaero.hud.compat.mods.ImmediatelyFastHelper;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.compass.render.CompassRenderer;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.map.MinimapElementMapRendererHandler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.icon.RadarIconManager;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.hud.render.TextureLocations;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.shader.FramebufferLinesShaderHelper;
import xaero.lib.client.graphics.shader.PositionTexAlphaTestShaderHelper;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.graphics.util.TextureUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.util.Constants;

public class MinimapFBORenderer
extends MinimapRenderer {
    private ImprovedFramebuffer scalingFramebuffer;
    private ImprovedFramebuffer rotationFramebuffer;
    private PreparedRenderType.Texture scalingFramebufferNearestTAS;
    private MinimapElementMapRendererHandler minimapElementMapRendererHandler;
    private RadarRenderer entityRadarRenderer;
    private RadarIconManager radarIconManager;
    private boolean triedFBO;
    private boolean loadedFBO;

    public MinimapFBORenderer(HudMod modMain, Minecraft mc, WaypointMapRenderer waypointMapRenderer, Minimap minimap, CompassRenderer compassRenderer, PoseStack matrixStack) {
        super(modMain, mc, waypointMapRenderer, minimap, compassRenderer, matrixStack);
    }

    public void loadFrameBuffer(MinimapProcessor minimapProcessor) {
        if (!minimapProcessor.canUseFrameBuffer()) {
            MinimapLogs.LOGGER.info("FBO mode not supported! Using minimap safe mode.");
        } else {
            this.scalingFramebuffer = new ImprovedFramebuffer(512, 512, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
            this.rotationFramebuffer = new ImprovedFramebuffer(512, 512, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
            this.radarIconManager = new RadarIconManager(new RadarIconCreator());
            this.loadedFBO = this.scalingFramebuffer.getColorTexture() != null;
            this.minimapElementMapRendererHandler = ((MinimapElementMapRendererHandler.Builder)MinimapElementMapRendererHandler.Builder.begin().setPoseStack(this.matrixStack)).build();
            this.entityRadarRenderer = RadarRenderer.Builder.begin().setRadarIconManager(this.radarIconManager).setMinimap(this.minimap).build();
            this.minimapElementMapRendererHandler.add(this.entityRadarRenderer);
            this.minimap.getOverMapRendererHandler().add(this.entityRadarRenderer);
            if (this.modMain.getSupportMods().worldmap()) {
                this.modMain.getSupportMods().worldmapSupport.createRadarRenderWrapper(this.entityRadarRenderer);
            }
            if (this.loadedFBO) {
                this.scalingFramebuffer.setSampler(XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR));
                this.scalingFramebufferNearestTAS = new PreparedRenderType.Texture("Sampler0", this.scalingFramebuffer.getColorTextureView(), XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.NEAREST));
                this.rotationFramebuffer.setSampler(XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR));
            }
        }
        this.triedFBO = true;
    }

    private void restoreDefaultTarget() {
        this.scalingFramebuffer.bindDefaultFramebuffer(Minecraft.getInstance());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void renderChunks(MinimapSession minimapSession, MinimapProcessor minimap, Vec3 renderPos, ResourceKey<Level> mapDimension, double mapDimensionScale, int mapSize, int bufferSize, float sizeFix, float partial, int lightLevel, boolean useWorldMap, boolean lockedNorth, int shape, double ps, double pc, boolean cave, boolean circle, ModSettings settings, XaeroBufferProvider cvc) {
        MinimapWriter minimapWriter = minimap.getMinimapWriter();
        synchronized (minimapWriter) {
            try {
                this.renderChunksToFBO(minimapSession, this.matrixStack, minimap, renderPos, mapDimension, mapDimensionScale, mapSize, partial, lightLevel, useWorldMap, lockedNorth, shape, ps, pc, cave, cvc);
            }
            catch (Throwable t) {
                ImprovedFramebuffer.restoreMainRenderTarget();
                throw t;
            }
        }
        this.restoreDefaultTarget();
    }

    public void renderChunksToFBO(MinimapSession minimapSession, PoseStack matrixStack, MinimapProcessor minimap, Vec3 renderPos, ResourceKey<Level> mapDimension, double mapDimensionScale, int viewW, float partial, int level, boolean useWorldMap, boolean lockedNorth, int shape, double ps, double pc, boolean cave, XaeroBufferProvider cvc) {
        int chunkGridConfig;
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        GpuBufferSlice projectionMatrixBackup = RenderSystem.getProjectionMatrixBuffer();
        ProjectionType projectionTypeBackup = RenderSystem.getProjectionType();
        matrixStack.pushPose();
        matrixStack.setIdentity();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = minimapSession.getMultiTextureRenderTypeRenderers();
        double maxVisibleLength = lockedNorth || shape == 1 ? (double)viewW : (double)viewW * Math.sqrt(2.0);
        double halfMaxVisibleLength = maxVisibleLength / 2.0;
        double radiusBlocks = halfMaxVisibleLength / this.zoom;
        int xFloored = OptimizedMath.myFloor(renderPos.x);
        int zFloored = OptimizedMath.myFloor(renderPos.z);
        if (this.zoom == 0.5) {
            xFloored = xFloored >> 1 << 1;
            zFloored = zFloored >> 1 << 1;
        }
        int playerChunkX = xFloored >> 6;
        int playerChunkZ = zFloored >> 6;
        int offsetX = xFloored & 0x3F;
        int offsetZ = zFloored & 0x3F;
        boolean zooming = (double)((int)this.zoom) != this.zoom;
        ImmediatelyFastHelper.triggerBatchingBuffersFlush(matrixStack);
        this.scalingFramebuffer.bindAsMainTarget(true);
        TextureUtils.clearRenderTarget((RenderTarget)this.scalingFramebuffer, (Vector4fc)Constants.ZERO, (float)0.0f);
        this.mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        long before = System.currentTimeMillis();
        float clearAlpha = (float)((Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.UNDISCOVERED_OPACITY)).intValue() / 100.0f;
        TextureUtils.clearRenderTarget((RenderTarget)this.scalingFramebuffer, (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, clearAlpha), (float)0.0f);
        this.helper.defaultOrtho((RenderTarget)this.scalingFramebuffer);
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        before = System.currentTimeMillis();
        double xInsidePixel = renderPos.x - (double)xFloored;
        double zInsidePixel = renderPos.z - (double)zFloored;
        float halfWView = (float)viewW / 2.0f;
        float angle = (float)(90.0 - this.getRenderAngle(lockedNorth));
        shaderMatrixStack.translate(256.0f, 256.0f, -2000.0f);
        shaderMatrixStack.scale((float)this.zoom, (float)this.zoom, 1.0f);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        XaeroBufferProvider renderTypeBuffers = XaeroLib.INSTANCE.getClient().getBufferProvider();
        BufferBuilder overlayBufferBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_CHUNK_OVERLAY);
        float chunkGridAlphaMultiplier = 1.0f;
        int minX = playerChunkX + (int)Math.floor(((double)offsetX - radiusBlocks) / 64.0);
        int minZ = playerChunkZ + (int)Math.floor(((double)offsetZ - radiusBlocks) / 64.0);
        int maxX = playerChunkX + (int)Math.floor(((double)(offsetX + 1) + radiusBlocks) / 64.0);
        int maxZ = playerChunkZ + (int)Math.floor(((double)(offsetZ + 1) + radiusBlocks) / 64.0);
        if (!cave || MinimapConfigClientUtils.getEffectiveCaveModeAllowed()) {
            if (useWorldMap) {
                chunkGridAlphaMultiplier = this.modMain.getSupportMods().worldmapSupport.getMinimapBrightness();
                this.modMain.getSupportMods().worldmapSupport.drawMinimap(minimapSession, matrixStack, this.getHelper(), xFloored, zFloored, minX, minZ, maxX, maxZ, zooming, this.zoom, mapDimensionScale, (VertexConsumer)overlayBufferBuilder, multiTextureRenderTypeRenderers);
            } else if (minimap.getMinimapWriter().getLoadedBlocks() != null && level >= 0) {
                int loadedLevels = minimap.getMinimapWriter().getLoadedLevels();
                chunkGridAlphaMultiplier = loadedLevels <= 1 ? 1.0f : 0.375f + 0.625f * (1.0f - (float)level / (float)(loadedLevels - 1));
                int loadedMapChunkX = minimap.getMinimapWriter().getLoadedMapChunkX();
                int loadedMapChunkZ = minimap.getMinimapWriter().getLoadedMapChunkZ();
                int loadedWidth = minimap.getMinimapWriter().getLoadedBlocks().length;
                boolean slimeChunks = MinimapConfigClientUtils.getEffectiveSlimeChunks(minimapSession);
                int loadedMinX = Math.max(minX, loadedMapChunkX);
                int loadedMinZ = Math.max(minZ, loadedMapChunkZ);
                int loadedMaxX = Math.min(maxX, loadedMapChunkX + loadedWidth - 1);
                int loadedMaxZ = Math.min(maxZ, loadedMapChunkZ + loadedWidth - 1);
                AbstractTexture guiTextures = this.mc.getTextureManager().getTexture(TextureLocations.GUI_TEXTURES);
                MultiTextureRenderTypeRenderer multiTextureRenderTypeRenderer = multiTextureRenderTypeRenderers.getRenderer(zooming ? CustomRenderTypes.MINIMAP_ZOOM : CustomRenderTypes.MINIMAP);
                MinimapRendererHelper helper = this.getHelper();
                for (int X = loadedMinX; X <= loadedMaxX; ++X) {
                    int canvasX = X - minimap.getMinimapWriter().getLoadedMapChunkX();
                    for (int Z = loadedMinZ; Z <= loadedMaxZ; ++Z) {
                        int canvasZ = Z - minimap.getMinimapWriter().getLoadedMapChunkZ();
                        MinimapChunk mchunk = minimap.getMinimapWriter().getLoadedBlocks()[canvasX][canvasZ];
                        if (mchunk == null) continue;
                        GpuTextureAndView texture = mchunk.bindTexture(level);
                        if (!mchunk.isHasSomething() || level >= mchunk.getLevelsBuffered() || texture == null) continue;
                        int drawX = (X - playerChunkX) * 64 - offsetX;
                        int drawZ = (Z - playerChunkZ) * 64 - offsetZ;
                        MultiTextureRenderUtil.prepareTexturedColoredRect(matrixStack.last().pose(), (float)drawX, (float)drawZ, 0, 64, 64.0f, 64.0f, -64.0f, 64.0f, 1.0f, 1.0f, 1.0f, 1.0f, texture.view, multiTextureRenderTypeRenderer);
                        if (!slimeChunks) continue;
                        for (int t = 0; t < 16; ++t) {
                            if (mchunk.getTile(t % 4, t / 4) == null || !mchunk.getTile(t % 4, t / 4).isSlimeChunk()) continue;
                            int slimeDrawX = drawX + 16 * (t % 4);
                            int slimeDrawZ = drawZ + 16 * (t / 4);
                            RenderBufferUtil.addColoredRect(matrixStack.last().pose(), (VertexConsumer)overlayBufferBuilder, slimeDrawX, slimeDrawZ, 16, 16, -2142047936);
                        }
                    }
                }
                multiTextureRenderTypeRenderers.draw(multiTextureRenderTypeRenderer);
            }
        }
        if ((chunkGridConfig = ((Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID)).intValue()) > -1) {
            BufferBuilder lineBufferBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_LINES);
            int grid = MinimapConfigConstants.COLORS[chunkGridConfig];
            int r = grid >> 16 & 0xFF;
            int g = grid >> 8 & 0xFF;
            int b = grid & 0xFF;
            FramebufferLinesShaderHelper.setFrameSize((float)this.scalingFramebuffer.width, (float)this.scalingFramebuffer.height);
            float red = (float)r / 255.0f;
            float green = (float)g / 255.0f;
            float blue = (float)b / 255.0f;
            float alpha = 0.8f;
            float colorMultiplier = chunkGridAlphaMultiplier;
            red *= colorMultiplier;
            green *= colorMultiplier;
            blue *= colorMultiplier;
            int chunkGridLineWidthConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID_LINE_WIDTH);
            boolean bias = true;
            PoseStack.Pose matrices = matrixStack.last();
            float halfLineLength = (float)radiusBlocks;
            for (int X = minX; X <= maxX; ++X) {
                int drawX = (X - playerChunkX + 1) * 64 - offsetX;
                for (int i = 0; i < 4; ++i) {
                    float lineX = (float)drawX + (float)(-16 * i);
                    this.helper.addColoredLineToExistingBuffer(matrices, (VertexConsumer)lineBufferBuilder, lineX, -halfLineLength, lineX, halfLineLength + (float)bias, red, green, blue, alpha, chunkGridLineWidthConfig);
                }
            }
            for (int Z = minZ; Z <= maxZ; ++Z) {
                int drawZ = (Z - playerChunkZ + 1) * 64 - offsetZ;
                for (int i = 0; i < 4; ++i) {
                    float lineZ = (float)drawZ + (float)((double)(-16 * i) - 1.0 / this.zoom);
                    this.helper.addColoredLineToExistingBuffer(matrices, (VertexConsumer)lineBufferBuilder, -halfLineLength, lineZ, halfLineLength + (float)bias, lineZ, red, green, blue, alpha, chunkGridLineWidthConfig);
                }
            }
        }
        renderTypeBuffers.endBatch();
        this.rotationFramebuffer.bindAsMainTarget(false);
        TextureUtils.clearRenderTarget((RenderTarget)this.rotationFramebuffer, (Vector4fc)Constants.ZERO, (float)0.0f);
        shaderMatrixStack.identity();
        boolean antiAliasing = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ANTI_ALIASING);
        PreparedRenderType.Texture scalingFramebufferTAS = antiAliasing ? this.scalingFramebuffer.getTas() : this.scalingFramebufferNearestTAS;
        shaderMatrixStack.translate(halfWView, halfWView, -2980.0f);
        shaderMatrixStack.pushMatrix();
        if (!lockedNorth) {
            OptimizedMath.rotateMatrix((Matrix4f)shaderMatrixStack, -angle, (Vector3fc)OptimizedMath.ZP);
        }
        shaderMatrixStack.translate((float)(-xInsidePixel * this.zoom), (float)(-zInsidePixel * this.zoom), 0.0f);
        int opacityConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPACITY);
        float opacityF = (float)opacityConfig / 100.0f;
        ImmediateRenderUtil.setShaderColor((float)opacityF, (float)opacityF, (float)opacityF, (float)opacityF);
        PositionTexAlphaTestShaderHelper.setDiscardAlpha((float)0.0f);
        ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)-256.0f, (float)-256.0f, (int)0, (int)0, (float)512.0f, (float)512.0f, (float)512.0f, (float)512.0f, (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_ALPHA_PRE, (PreparedRenderType.Texture)scalingFramebufferTAS);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        shaderMatrixStack.popMatrix();
        before = System.currentTimeMillis();
        this.minimapElementMapRendererHandler.prepareRender(ps, pc, this.zoom, halfWView);
        this.minimapElementMapRendererHandler.render(renderPos, partial, (RenderTarget)this.rotationFramebuffer, mapDimensionScale, mapDimension);
        renderTypeBuffers.endBatch();
        ImmediatelyFastHelper.triggerBatchingBuffersFlush(matrixStack);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionMatrixBackup, (ProjectionType)projectionTypeBackup);
        shaderMatrixStack.popMatrix();
        matrixStack.popPose();
    }

    public void deleteFramebuffers() {
        this.scalingFramebuffer.destroyBuffers();
        this.rotationFramebuffer.destroyBuffers();
        if (this.radarIconManager != null) {
            this.radarIconManager.reset();
        }
    }

    public boolean isLoadedFBO() {
        return this.loadedFBO;
    }

    public void setLoadedFBO(boolean loadedFBO) {
        this.loadedFBO = loadedFBO;
    }

    public boolean isTriedFBO() {
        return this.triedFBO;
    }

    public boolean assumeUsingFBO() {
        boolean mapSafeMode = (Boolean)this.modMain.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE);
        return !this.isTriedFBO() && !mapSafeMode || this.minimap.usingFBO();
    }

    public void resetEntityIcons() {
        if (this.radarIconManager != null) {
            this.radarIconManager.reset();
        }
    }

    public void resetEntityIconsResources() {
        if (this.radarIconManager != null) {
            this.radarIconManager.resetResources();
        }
    }

    public void onRadarIconModelPrepareDetection(ModelFeatureRenderer.Submit<?> submit) {
        this.radarIconManager.onModelPrepareDetection(submit);
    }

    public void onRadarIconModelRenderTrace(Model model, VertexConsumer vertexConsumer, int color) {
        this.radarIconManager.onModelRenderTrace(model, vertexConsumer, color);
    }

    public void onEntityIconModelPartRenderTrace(ModelPart modelRenderer, int color) {
        this.radarIconManager.onModelPartRenderTrace(modelRenderer, color);
    }

    public void renderMainEntityDot(Entity renderEntity, boolean cave, XaeroBufferProvider renderTypeBuffers) {
        MinimapElementGraphics guiGraphics = this.minimapElementMapRendererHandler.getGuiGraphics();
        guiGraphics.pose().pushPose();
        this.entityRadarRenderer.renderSingleEntity(renderEntity, cave, false, 2.0f, false, false, MinimapElementRenderLocation.OVER_MINIMAP, null, guiGraphics, false);
        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }

    @Override
    public PreparedRenderType.Texture getTas() {
        return this.rotationFramebuffer.getTas();
    }

    public RadarRenderer getEntityRadarRenderer() {
        return this.entityRadarRenderer;
    }
}

