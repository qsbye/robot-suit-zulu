/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import xaero.common.HudMod;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.render.MinimapSafeModeRenderer;
import xaero.common.misc.OptimizedMath;
import xaero.common.settings.ModSettings;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.compass.render.CompassRenderer;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.hud.render.TextureLocations;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.graphics.util.TextureUtils;
import xaero.lib.client.gui.util.graphics.GuiGraphicsUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;

public abstract class MinimapRenderer {
    public static final int black = -16777216;
    public static final int slime = -2142047936;
    protected HudMod modMain;
    protected Minecraft mc;
    protected Minimap minimap;
    protected MinimapRendererHelper helper;
    protected WaypointMapRenderer waypointMapRenderer;
    private int lastMinimapSize;
    protected double zoom = 1.0;
    private BlockPos.MutableBlockPos mutableBlockPos;
    protected final CompassRenderer compassRenderer;
    protected final PoseStack matrixStack;

    public MinimapRenderer(HudMod modMain, Minecraft mc, WaypointMapRenderer waypointMapRenderer, Minimap minimap, CompassRenderer compassRenderer, PoseStack matrixStack) {
        this.modMain = modMain;
        this.mc = mc;
        this.waypointMapRenderer = waypointMapRenderer;
        this.minimap = minimap;
        this.matrixStack = matrixStack;
        this.helper = new MinimapRendererHelper();
        this.mutableBlockPos = new BlockPos.MutableBlockPos();
        this.compassRenderer = compassRenderer;
    }

    public double getRenderAngle(boolean lockedNorth) {
        if (lockedNorth) {
            return 90.0;
        }
        return this.getActualAngle();
    }

    private double getActualAngle() {
        double rotation = this.mc.gameRenderer.mainCamera().yRot();
        return -90.0 - rotation;
    }

    protected abstract void renderChunks(MinimapSession var1, MinimapProcessor var2, Vec3 var3, ResourceKey<Level> var4, double var5, int var7, int var8, float var9, float var10, int var11, boolean var12, boolean var13, int var14, double var15, double var17, boolean var19, boolean var20, ModSettings var21, XaeroBufferProvider var22);

    public void renderMinimap(MinimapSession minimapSession, MinimapProcessor minimap, int x, int y, int width, int height, double scale, float minimapScale, int size, float partial, XaeroBufferProvider cvc) {
        boolean crosshairDisplayed;
        int specW;
        int frameType;
        boolean renderFrame;
        ModSettings settings = this.modMain.getSettings();
        int minimapSizeConfig = MinimapConfigClientUtils.getEffectiveMinimapSize();
        if (minimapSizeConfig != this.lastMinimapSize) {
            this.lastMinimapSize = minimapSizeConfig;
            minimap.setToResetImage(true);
        }
        minimap.getRadarSession().getStateUpdater().setLastRenderViewEntity(this.mc.getCameraEntity());
        int mapSize = minimapSession.getProcessor().getMinimapSize();
        int bufferSize = minimapSession.getProcessor().getMinimapBufferSize(mapSize);
        if (this.minimap.usingFBO()) {
            bufferSize = minimap.getFBOBufferSize();
        }
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        float mapScale = (float)(scale / (double)minimapScale);
        minimap.updateZoom();
        this.zoom = minimap.getMinimapZoom();
        this.mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        float sizeFix = 1.0f;
        int shape = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.SHAPE);
        boolean lockedNorth = MinimapConfigClientUtils.getEffectiveNorthLocked(mapSize / 2, shape);
        double angle = Math.toRadians(this.getRenderAngle(lockedNorth));
        double ps = Math.sin(Math.PI - angle);
        double pc = Math.cos(Math.PI - angle);
        boolean useWorldMap = this.modMain.getSupportMods().shouldUseWorldMapChunks() && !minimap.getMinimapWriter().isLoadedNonWorldMap();
        boolean lightingConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHTING);
        int lightLevel = (int)((1.0f - Math.min(1.0f, this.getSunBrightness(minimap, lightingConfig))) * (float)(minimap.getMinimapWriter().getLoadedLevels() - 1));
        boolean cave = minimap.isCaveModeDisplayed();
        boolean circleShape = shape == 1;
        double playerX = EntityUtils.getEntityX(this.mc.getCameraEntity(), partial);
        double playerY = EntityUtils.getEntityY(this.mc.getCameraEntity(), partial);
        double playerZ = EntityUtils.getEntityZ(this.mc.getCameraEntity(), partial);
        double renderX = playerX;
        double renderZ = playerZ;
        double mapDimensionScale = this.mc.level.dimensionType().coordinateScale();
        ResourceKey<Level> mapDimension = this.mc.level.dimension();
        double playerDimDiv = 1.0;
        if (useWorldMap) {
            double playerCoordinateScale = mapDimensionScale;
            mapDimensionScale = this.modMain.getSupportMods().worldmapSupport.getMapDimensionScale();
            mapDimension = this.modMain.getSupportMods().worldmapSupport.getMapDimension();
            if (mapDimensionScale == 0.0) {
                mapDimensionScale = minimap.getLastMapDimensionScale();
                mapDimension = minimap.getLastMapDimension();
            }
            playerDimDiv = mapDimensionScale / playerCoordinateScale;
            renderX /= playerDimDiv;
            renderZ /= playerDimDiv;
        }
        minimap.setLastMapDimensionScale(mapDimensionScale);
        minimap.setLastMapDimension(mapDimension);
        Vec3 renderPos = new Vec3(renderX, playerY, renderZ);
        this.matrixStack.pushPose();
        this.renderChunks(minimapSession, minimap, renderPos, mapDimension, mapDimensionScale, mapSize, bufferSize, sizeFix, partial, lightLevel, useWorldMap, lockedNorth, shape, ps, pc, cave, circleShape, settings, cvc);
        if (this.minimap.usingFBO()) {
            sizeFix = 1.0f;
        }
        this.matrixStack.scale(1.0f / mapScale, 1.0f / mapScale, 1.0f);
        int scaledX = (int)((float)x * mapScale);
        int scaledY = (int)((float)y * mapScale);
        int minimapFrameSize = (int)((float)(mapSize / 2) / sizeFix);
        int circleSides = Math.max(32, (int)Math.ceil(Math.PI * (double)(minimapFrameSize + 8) / 8.0 / 4.0) * 4);
        double circleSeamAngle = -0.7853981633974483;
        int circleSeamWidth = 32;
        int circleFrameThickness = 4;
        double circleStartAngle = 0.0;
        PreparedRenderType.Texture minimapViewTas = this.getTas();
        if (!circleShape) {
            this.getHelper().drawMyTexturedModalRect(this.matrixStack, (int)((float)(scaledX + 9) / sizeFix), (int)((float)(scaledY + 9) / sizeFix), 0, 256 - minimapFrameSize, minimapFrameSize, minimapFrameSize, minimapFrameSize, 256.0f, minimapViewTas, true);
        } else {
            float outerRadius = mapSize / 4 + circleFrameThickness;
            circleStartAngle = circleSeamAngle - (double)((float)(circleSeamWidth / 2) / outerRadius);
            this.getHelper().drawTexturedElipseInsideRectangle(this.matrixStack, circleStartAngle, circleSides, (int)((float)(scaledX + 9) / sizeFix), (int)((float)(scaledY + 9) / sizeFix), 0, 256 - minimapFrameSize, minimapFrameSize, 256.0f, minimapViewTas, true);
        }
        if (!this.minimap.usingFBO()) {
            this.matrixStack.scale(1.0f / sizeFix, 1.0f / sizeFix, 1.0f);
            ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        boolean bl = renderFrame = (frameType = ((Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.FRAME)).intValue()) < MinimapConfigConstants.FRAME_NAMES.length - 1;
        if (frameType > 0) {
            int frameColorConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.FRAME_COLOR);
            int frameColor = MinimapConfigConstants.COLORS[frameColorConfig];
            ImmediateRenderUtil.setShaderColor((float)((float)(frameColor >> 16 & 0xFF) / 255.0f), (float)((float)(frameColor >> 8 & 0xFF) / 255.0f), (float)((float)(frameColor & 0xFF) / 255.0f), (float)1.0f);
        }
        MinimapRendererHelper helper = this.getHelper();
        PreparedRenderType.Texture minimapFrameTAS = null;
        if (renderFrame) {
            GpuTextureView minimapFrameView = TextureUtils.getView((Identifier)TextureLocations.MINIMAP_FRAME_TEXTURES);
            GpuSampler minimapFrameViewSampler = TextureUtils.getSampler((Identifier)TextureLocations.MINIMAP_FRAME_TEXTURES);
            minimapFrameTAS = new PreparedRenderType.Texture("Sampler0", minimapFrameView, minimapFrameViewSampler);
        }
        if (renderFrame && !circleShape) {
            int rightCornerStartX = scaledX + 9 + mapSize / 2 + 4 - 16;
            int bottomCornerStartY = scaledY + 9 + mapSize / 2 + 4 - 16;
            BufferBuilder vertexBuffer = ImmediateRenderUtil.begin((PrimitiveTopology)PrimitiveTopology.QUADS, (VertexFormat)DefaultVertexFormat.POSITION_TEX);
            Matrix4f matrix = this.matrixStack.last().pose();
            int cornerTextureX = frameType == 0 ? 192 : (frameType == 1 ? 208 : 224);
            helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, scaledX + 9 - 4, scaledY + 9 - 4, cornerTextureX, 97, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, rightCornerStartX, scaledY + 9 - 4, cornerTextureX, 113, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, scaledX + 9 - 4, bottomCornerStartY, cornerTextureX, 129, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, rightCornerStartX, bottomCornerStartY, cornerTextureX, 145, 16, 16);
            int horLineStartX = scaledX + 9 - 4 + 16;
            int horLineWidth = rightCornerStartX - horLineStartX;
            int horPieceTextureY = frameType == 0 ? 0 : (frameType == 1 ? 32 : 64);
            int horPieceWidth = 226;
            int horLineLength = (int)Math.ceil((double)horLineWidth / (double)horPieceWidth);
            for (int i = 0; i < horLineLength; ++i) {
                int pieceX = scaledX + 9 - 4 + 16 + i * horPieceWidth;
                int pieceW = horPieceWidth;
                if (i == horLineLength - 1 && pieceX + pieceW > rightCornerStartX) {
                    pieceW = rightCornerStartX - pieceX;
                }
                helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, pieceX, scaledY + 9 - 4, 0, horPieceTextureY, pieceW, 16);
                helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, pieceX, scaledY + 9 + mapSize / 2 - 12, 0, horPieceTextureY + 16, pieceW, 16);
            }
            int verLineStartY = scaledY + 9 - 4 + 16;
            int verLineHeight = bottomCornerStartY - verLineStartY;
            int verPieceTextureX = frameType == 0 ? 0 : (frameType == 1 ? 64 : 128);
            int verPieceHeight = 113;
            int vertLineLength = (int)Math.ceil((double)verLineHeight / (double)verPieceHeight);
            for (int i = 0; i < vertLineLength; ++i) {
                int pieceY = scaledY + 9 - 4 + 16 + i * verPieceHeight;
                int pieceU = verPieceTextureX + 32 * (i & 1);
                int pieceH = verPieceHeight;
                if (i == vertLineLength - 1 && pieceY + pieceH > bottomCornerStartY) {
                    pieceH = bottomCornerStartY - pieceY;
                }
                helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, scaledX + 9 - 4, pieceY, pieceU, 97, 16, pieceH);
                helper.addTexturedRectToExistingBuffer(matrix, (VertexConsumer)vertexBuffer, scaledX + 9 + mapSize / 2 - 12, pieceY, pieceU + 16, 97, 16, pieceH);
            }
            ImmediateRenderUtil.drawImmediateMeshData((MeshData)vertexBuffer.build(), (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_NO_ALPHA, (PreparedRenderType.Texture)minimapFrameTAS);
        } else if (renderFrame) {
            int frameTextureY = frameType == 0 ? 210 : (frameType == 1 ? 214 : 218);
            double shadeStartAngle = 0.7853981633974483 - circleStartAngle;
            int shadeStartIndex = (int)(shadeStartAngle / 2.0 / Math.PI * (double)circleSides);
            int circleLeftX = scaledX + 9;
            int circleTopY = scaledY + 9;
            int innerCircleDiameter = mapSize / 2;
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, false, false, circleStartAngle, 0, shadeStartIndex, circleSides, circleFrameThickness, circleLeftX, circleTopY, 0, frameTextureY, innerCircleDiameter, 73.0f, circleFrameThickness, circleSeamWidth, 256.0f, minimapFrameTAS);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, true, false, circleStartAngle, shadeStartIndex, shadeStartIndex + circleSides / 4, circleSides, circleFrameThickness, circleLeftX, circleTopY, 138, frameTextureY, innerCircleDiameter, 68.0f, circleFrameThickness, 20, 256.0f, minimapFrameTAS);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, true, true, circleStartAngle, shadeStartIndex + circleSides / 4, shadeStartIndex + circleSides / 2, circleSides, circleFrameThickness, circleLeftX, circleTopY, 138, frameTextureY, innerCircleDiameter, 68.0f, circleFrameThickness, 20, 256.0f, minimapFrameTAS);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, false, false, circleStartAngle, shadeStartIndex + circleSides / 2, circleSides, circleSides, circleFrameThickness, circleLeftX, circleTopY, 0, frameTextureY, innerCircleDiameter, 73.0f, circleFrameThickness, circleSeamWidth, 256.0f, minimapFrameTAS);
        }
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.matrixStack.pushPose();
        this.matrixStack.translate((float)(scaledX + 9), (float)(scaledY + 9), 0.0f);
        this.matrixStack.scale(1.0f / minimapScale, 1.0f / minimapScale, 1.0f);
        int halfFrame = (int)((float)mapSize * minimapScale / 2.0f / 2.0f);
        this.matrixStack.translate((double)halfFrame, (double)halfFrame, 0.5);
        int specH = specW = halfFrame + (int)(3.0f * minimapScale);
        boolean safeMode = this instanceof MinimapSafeModeRenderer;
        XaeroBufferProvider renderTypeBuffers = XaeroLib.INSTANCE.getClient().getBufferProvider();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = minimapSession.getMultiTextureRenderTypeRenderers();
        double scaledZoom = this.zoom * (double)minimapScale / 2.0;
        boolean compassOverEverythingConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_OVER_EVERYTHING);
        if (!compassOverEverythingConfig) {
            this.renderCompass(this.matrixStack, settings, configManager, renderTypeBuffers, specW, specH, halfFrame, ps, pc, circleShape, minimapScale);
        }
        this.minimap.getOverMapRendererHandler().prepareRender(ps, pc, scaledZoom, specW, specH, halfFrame, halfFrame, circleShape, minimapScale);
        this.minimap.getOverMapRendererHandler().render(renderPos, partial, null, mapDimensionScale, mapDimension);
        if (compassOverEverythingConfig) {
            this.renderCompass(this.matrixStack, settings, configManager, renderTypeBuffers, specW, specH, halfFrame, ps, pc, circleShape, minimapScale);
        }
        renderTypeBuffers.endBatch();
        this.matrixStack.popPose();
        int mainEntityAs = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_ENTITY);
        boolean bl2 = crosshairDisplayed = mainEntityAs == 0 && !lockedNorth;
        if (crosshairDisplayed) {
            this.matrixStack.pushPose();
            this.matrixStack.translate((float)(scaledX + 9), (float)(scaledY + 9), 0.0f);
            this.matrixStack.scale(0.5f, 0.5f, 1.0f);
            this.matrixStack.translate((float)(mapSize / 2), (float)(mapSize / 2), 0.0f);
            ImmediateRenderUtil.negativeColorRectangle((PoseStack)this.matrixStack, (float)-5.0f, (float)-1.0f, (float)5.0f, (float)1.0f);
            ImmediateRenderUtil.negativeColorRectangle((PoseStack)this.matrixStack, (float)-1.0f, (float)3.0f, (float)1.0f, (float)5.0f);
            ImmediateRenderUtil.negativeColorRectangle((PoseStack)this.matrixStack, (float)-1.0f, (float)-5.0f, (float)1.0f, (float)-3.0f);
            RadarSession radarSession = minimap.getRadarSession();
            EntityRadarCategoryManager categoryManager = radarSession.getCategoryManager();
            EntityRadarCategory mainEntityCategory = categoryManager.getRuleResolver().resolve(categoryManager.getRootCategory(), this.mc.getCameraEntity(), this.mc.player);
            if (mainEntityCategory == null) {
                mainEntityCategory = categoryManager.getRootCategory();
            }
            RadarColor crosshairRadarColor = RadarColor.fromIndex(mainEntityCategory.getSettingValue(EntityRadarCategorySettings.COLOR).intValue());
            RadarColor crosshairFallbackColor = radarSession.getColorHelper().getFallbackColor(mainEntityCategory, null);
            int crosshairColor = radarSession.getColorHelper().getEntityColor(this.mc.getCameraEntity(), 0.0f, false, 100, 100, false, crosshairRadarColor, crosshairFallbackColor);
            ImmediateRenderUtil.setShaderColor((float)((float)(crosshairColor >> 16 & 0xFF) / 255.0f), (float)((float)(crosshairColor >> 8 & 0xFF) / 255.0f), (float)((float)(crosshairColor & 0xFF) / 255.0f), (float)1.0f);
            this.getHelper().drawMyColoredRect(this.matrixStack, 1.0f, -1.0f, 3.0f, 1.0f, false);
            this.getHelper().drawMyColoredRect(this.matrixStack, -3.0f, -1.0f, -1.0f, 1.0f, false);
            this.getHelper().drawMyColoredRect(this.matrixStack, -1.0f, 1.0f, 1.0f, 3.0f, false);
            this.getHelper().drawMyColoredRect(this.matrixStack, -1.0f, -3.0f, 1.0f, -1.0f, false);
            ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.matrixStack.popPose();
        }
        double centerX = 2 * scaledX + 18 + mapSize / 2;
        double centerY = 2 * scaledY + 18 + mapSize / 2;
        this.matrixStack.pushPose();
        this.matrixStack.scale(0.5f, 0.5f, 1.0f);
        this.matrixStack.translate(centerX, centerY, 0.0);
        AbstractTexture guiTextures = this.mc.getTextureManager().getTexture(TextureLocations.GUI_TEXTURES);
        Entity mainEntity = this.mc.getCameraEntity();
        if (!safeMode && mainEntityAs == 1) {
            this.minimap.getMinimapFBORenderer().renderMainEntityDot(mainEntity, cave, cvc);
        }
        PreparedRenderType.Texture guiTexturesLinearTas = new PreparedRenderType.Texture("Sampler0", guiTextures.getTextureView(), XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR));
        if (lockedNorth || mainEntityAs == 2) {
            float b;
            float g;
            float r;
            float arrowAngle = lockedNorth ? mainEntity.getViewYRot(partial) : 180.0f;
            int arrowOpacityInt = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ARROW_OPACITY);
            float arrowOpacity = (float)arrowOpacityInt / 100.0f;
            int arrowColour = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ARROW_COLOR);
            if (arrowColour != -1) {
                float[] c = MinimapConfigConstants.ARROW_COLORS[arrowColour];
                r = c[0];
                g = c[1];
                b = c[2];
                a = c[3];
            } else {
                int rgb = minimap.getRadarSession().getColorHelper().getTeamColor((Entity)(this.mc.player == null ? mainEntity : this.mc.player));
                if (rgb != -1) {
                    r = (float)(rgb >> 16 & 0xFF) / 255.0f;
                    g = (float)(rgb >> 8 & 0xFF) / 255.0f;
                    b = (float)(rgb & 0xFF) / 255.0f;
                    a = 1.0f;
                } else {
                    float[] c = MinimapConfigConstants.ARROW_COLORS[0];
                    r = c[0];
                    g = c[1];
                    b = c[2];
                    a = c[3];
                }
            }
            float shadowR = Mth.lerp((float)arrowOpacity, (float)r, (float)0.0f);
            float shadowG = Mth.lerp((float)arrowOpacity, (float)g, (float)0.0f);
            float shadowB = Mth.lerp((float)arrowOpacity, (float)b, (float)0.0f);
            this.drawArrow(this.matrixStack, arrowAngle, 0.0, -2.0, shadowR, shadowG, shadowB, arrowOpacity * arrowOpacity * 0.3f, guiTexturesLinearTas, configManager);
            this.drawArrow(this.matrixStack, arrowAngle, 0.0, 2.0, shadowR, shadowG, shadowB, arrowOpacity * arrowOpacity * 0.5f, guiTexturesLinearTas, configManager);
            this.drawArrow(this.matrixStack, arrowAngle, 0.0, 0.0, r, g, b, a *= arrowOpacity, guiTexturesLinearTas, configManager);
        }
        this.matrixStack.popPose();
        this.matrixStack.popPose();
        this.mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
    }

    public void renderOutsidePip(MinimapSession minimapSession, int x, int y, int width, int height, double scale, float minimapScale, int size, float partial, GuiGraphicsExtractor guiGraphics) {
        float mapScale = (float)(scale / (double)minimapScale);
        int scaledX = (int)((float)x * mapScale);
        int scaledY = (int)((float)y * mapScale);
        Entity mainEntity = this.mc.getCameraEntity();
        int playerBlockX = OptimizedMath.myFloor(mainEntity.getX());
        int playerBlockY = OptimizedMath.myFloor(mainEntity.getY());
        int playerBlockZ = OptimizedMath.myFloor(mainEntity.getZ());
        BlockPos.MutableBlockPos pos = this.mutableBlockPos.set(playerBlockX, playerBlockY, playerBlockZ);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(1.0f / mapScale, 1.0f / mapScale);
        this.minimap.getInfoDisplays().getRenderer().render(minimapSession, this.minimap, height, size, (BlockPos)pos, scaledX, scaledY, mapScale, guiGraphics);
        guiGraphics.pose().popMatrix();
    }

    private void renderCompass(PoseStack matrixStack, ModSettings settings, ClientConfigManager configManager, XaeroBufferProvider renderTypeBuffers, int specW, int specH, int halfFrame, double ps, double pc, boolean circleShape, float minimapScale) {
        BufferBuilder nameBgBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.RADAR_NAME_BGS);
        int compassScale = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_SCALE);
        int compassLocation = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_LOCATION);
        compassScale = compassScale <= 0 ? (compassLocation == 1 ? (int)Math.ceil(minimapScale / 2.0f) : (int)minimapScale) : (int)MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.COMPASS_SCALE);
        if (compassLocation == 1) {
            if (Minecraft.getInstance().isEnforceUnicode()) {
                compassScale *= 2;
            }
            halfFrame = (int)((float)halfFrame - 7.0f * minimapScale / 2.0f);
            this.compassRenderer.drawCompass(matrixStack, halfFrame - 3 * compassScale, halfFrame - 3 * compassScale, ps, pc, 1.0, circleShape, compassScale, true, renderTypeBuffers, (VertexConsumer)nameBgBuilder);
        } else if (compassLocation == 2) {
            this.compassRenderer.drawCompass(matrixStack, specW, specH, ps, pc, this.zoom, circleShape, compassScale, false, renderTypeBuffers, null);
        }
    }

    private void drawArrow(PoseStack matrixStack, float angle, double arrowX, double arrowY, float r, float g, float b, float a, PreparedRenderType.Texture tas, ClientConfigManager configManager) {
        matrixStack.pushPose();
        matrixStack.translate(arrowX, arrowY, 0.0);
        OptimizedMath.rotatePose(matrixStack, angle, (Vector3fc)OptimizedMath.ZP);
        double arrowScale = (Double)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ARROW_SCALE);
        matrixStack.scale((float)(0.5 * arrowScale), (float)(0.5 * arrowScale), 1.0f);
        int offsetY = -6;
        int h = 28;
        boolean ty = false;
        matrixStack.translate(-13.0f, (float)offsetY, 0.0f);
        ImmediateRenderUtil.setShaderColor((float)r, (float)g, (float)b, (float)a);
        GuiGraphicsUtils.blit((PreparedRenderType.Texture)tas, (PoseStack)matrixStack, (int)0, (int)0, (float)49.0f, (float)((float)ty), (int)26, (int)h, (boolean)false);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        matrixStack.popPose();
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public float getSunBrightness(MinimapProcessor minimap, boolean lighting) {
        ClientLevel world = this.mc.level;
        float sunBrightness = (((Float)MinimapClientWorldDataHelper.getWorldData(world).getAttributeSystem().getValue(EnvironmentAttributes.SKY_LIGHT_FACTOR, BlockPos.ZERO)).floatValue() - 0.24f) / 0.76f;
        float ambient = world.dimensionType().ambientLight() * 24.0f / 15.0f;
        if (ambient > 1.0f) {
            ambient = 1.0f;
        }
        return ambient + (1.0f - ambient) * Mth.clamp((float)sunBrightness, (float)0.0f, (float)1.0f);
    }

    public MinimapRendererHelper getHelper() {
        return this.helper;
    }

    public abstract PreparedRenderType.Texture getTas();
}

