/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xaero.common.HudMod;
import xaero.common.core.IXaeroMinimapMinecraftClient;
import xaero.common.graphics.MinimapTexture;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.region.MinimapChunk;
import xaero.common.minimap.region.MinimapTile;
import xaero.common.minimap.render.MinimapRenderer;
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
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.util.RadarUtils;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapSafeModeRenderer
extends MinimapRenderer {
    private static final Identifier mapTextures = Identifier.parse((String)"xaeromaptexture");
    private int[] data;
    private byte drawYState;
    private final int[] tempColor = new int[3];
    private MinimapTexture mapTexture = new MinimapTexture(mapTextures);

    public MinimapSafeModeRenderer(HudMod modMain, Minecraft mc, WaypointMapRenderer waypointMapRenderer, Minimap minimap, CompassRenderer compassRenderer, PoseStack matrixStack) throws IOException {
        super(modMain, mc, waypointMapRenderer, minimap, compassRenderer, matrixStack);
    }

    public void updateMapFrameSafeMode(MinimapSession session, MinimapProcessor minimap, Player player, Entity renderEntity, int bufferSize, int mapW, float partial, int level, boolean lockedNorth, int shape, double ps, double pc, boolean cave, ModSettings settings) {
        int mainEntityAs;
        int debugFPS;
        if (level < 0) {
            return;
        }
        Player p = player;
        long before = System.currentTimeMillis();
        if (minimap.isToResetImage()) {
            this.data = new int[bufferSize * bufferSize];
            minimap.setToResetImage(false);
        }
        boolean motionBlur = (debugFPS = ((IXaeroMinimapMinecraftClient)this.mc).getXaeroMinimap_fps()) >= 35;
        int increaseY = motionBlur ? 2 : 1;
        int mapH = mapW;
        int halfW = mapW / 2;
        int halfH = mapH / 2;
        double halfWZoomed = (double)halfW / this.zoom;
        double halfHZoomed = (double)halfH / this.zoom;
        int currentState = this.drawYState;
        RadarSession radarSession = minimap.getRadarSession();
        double playerX = EntityUtils.getEntityX(renderEntity, partial);
        double playerZ = EntityUtils.getEntityZ(renderEntity, partial);
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean terrainMapVisible = !cave || MinimapConfigClientUtils.getEffectiveCaveModeAllowed();
        int chunkGridConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CHUNK_GRID);
        boolean slimeChunks = MinimapConfigClientUtils.getEffectiveSlimeChunks(session);
        for (int currentX = 0; currentX < mapW; ++currentX) {
            int currentY;
            double currentXZoomed = ((double)currentX + 0.5) / this.zoom;
            double offx = currentXZoomed - halfWZoomed;
            double psx = ps * offx;
            double pcx = pc * offx;
            int n = currentY = motionBlur ? currentState : 0;
            while (currentY < mapH) {
                double offy = ((double)currentY + 0.5) / this.zoom - halfHZoomed;
                if (terrainMapVisible) {
                    this.getLoadedBlockColor(session, minimap, this.tempColor, OptimizedMath.myFloor(playerX + psx + pc * offy), OptimizedMath.myFloor(playerZ + ps * offy - pcx), level, chunkGridConfig, slimeChunks);
                } else {
                    this.tempColor[2] = 1;
                    this.tempColor[1] = 1;
                    this.tempColor[0] = 1;
                }
                this.getHelper().putColor(this.data, currentX, bufferSize - 1 - currentY, this.tempColor[0], this.tempColor[1], this.tempColor[2], bufferSize);
                currentY += increaseY;
            }
            currentState = (byte)(currentState != 1 ? 1 : 0);
        }
        boolean displayRadar = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR);
        double maxDistance = RadarUtils.getMaxDistance(minimap, shape == 1);
        if (displayRadar) {
            Iterable<RadarList> entityLists = radarSession.getState().getRadarLists();
            for (RadarList entityList : entityLists) {
                int heightLimit = entityList.getEffective(EntityRadarCategorySettings.HEIGHT_LIMIT).intValue();
                boolean heightBasedFade = entityList.getEffective(EntityRadarCategorySettings.HEIGHT_FADE);
                int colorIndex = entityList.getEffective(EntityRadarCategorySettings.COLOR).intValue();
                int startFadingAt = entityList.getEffective(EntityRadarCategorySettings.START_FADING_AT).intValue();
                RadarColor radarColor = RadarColor.fromIndex(colorIndex);
                RadarColor fallbackColor = radarSession.getColorHelper().getFallbackColor(entityList);
                this.renderEntityListSafeMode(minimap, renderEntity, entityList.getEntities().iterator(), pc, ps, mapW, bufferSize, halfW, halfH, playerX, playerZ, partial, heightLimit, heightBasedFade, startFadingAt, radarColor, fallbackColor, maxDistance);
            }
        }
        if ((mainEntityAs = ((Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_ENTITY)).intValue()) == 1) {
            EntityRadarCategoryManager categoryManager = radarSession.getCategoryManager();
            EntityRadarCategory mainEntityCategory = categoryManager.getRuleResolver().resolve(categoryManager.getRootCategory(), renderEntity, p);
            if (mainEntityCategory == null) {
                mainEntityCategory = categoryManager.getRootCategory();
            }
            int colorIndex = mainEntityCategory.getSettingValue(EntityRadarCategorySettings.COLOR).intValue();
            RadarColor radarColor = RadarColor.fromIndex(colorIndex);
            RadarColor fallbackColor = radarSession.getColorHelper().getFallbackColor(mainEntityCategory, null);
            this.renderEntityDotSafeMode(minimap, renderEntity, renderEntity, pc, ps, mapW, bufferSize, halfW, halfH, playerX, playerZ, partial, 100, false, 100, radarColor, fallbackColor, maxDistance);
        }
        this.drawYState = (byte)(this.drawYState != 1 ? 1 : 0);
        IntBuffer buffer = this.mapTexture.intBuffer;
        buffer.clear();
        buffer.put(this.data);
        buffer.flip();
    }

    public void renderEntityListSafeMode(MinimapProcessor minimap, Entity renderEntity, Iterator<Entity> iter, double pc, double ps, int mapW, int bufferSize, int halfW, int halfH, double playerX, double playerZ, float partial, int heightLimit, boolean heightBasedFade, int startFadingAt, RadarColor radarColor, RadarColor fallbackColor, double maxDistance) {
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (renderEntity != e && this.renderEntityDotSafeMode(minimap, renderEntity, e, pc, ps, mapW, bufferSize, halfW, halfH, playerX, playerZ, partial, heightLimit, heightBasedFade, startFadingAt, radarColor, fallbackColor, maxDistance)) continue;
        }
    }

    public boolean renderEntityDotSafeMode(MinimapProcessor minimap, Entity renderEntity, Entity e, double pc, double ps, int mapW, int bufferSize, int halfW, int halfH, double playerX, double playerZ, float partial, int heightLimit, boolean heightBasedFade, int startFadingAt, RadarColor radarColor, RadarColor fallbackColor, double maxDistance) {
        double offx = EntityUtils.getEntityX(e, partial) - playerX;
        double offx2 = offx * offx;
        if (offx2 > maxDistance) {
            return false;
        }
        double offz = EntityUtils.getEntityZ(e, partial) - playerZ;
        double offz2 = offz * offz;
        if (offz2 > maxDistance) {
            return false;
        }
        if (e instanceof Player && this.modMain.getTrackedPlayerRenderer().getCollector().playerExists(e.getUUID())) {
            this.modMain.getTrackedPlayerRenderer().getCollector().confirmPlayerRadarRender((Player)e);
        }
        float offh = (float)(renderEntity.getY() - e.getY());
        double Z = pc * offx + ps * offz;
        double X = ps * offx - pc * offz;
        double drawXDouble = (double)halfW + X * this.zoom;
        double drawYDouble = (double)halfH + Z * this.zoom;
        float drawLeft = (float)drawXDouble - 2.5f;
        float drawTop = (float)drawYDouble - 2.5f;
        int drawX = mapW - Math.round((float)mapW - drawLeft) + 2;
        int drawY = Math.round(drawTop) + 2;
        int color = minimap.getRadarSession().getColorHelper().getEntityColor(e, offh, false, heightLimit, startFadingAt, heightBasedFade, radarColor, fallbackColor);
        for (int a = drawX - 2; a < drawX + 4; ++a) {
            if (a < 0 || a >= mapW) continue;
            for (int b = drawY - 2; b < drawY + 4; ++b) {
                if (b < 0 || b >= mapW || (a == drawX - 2 || a == drawX + 3) && (b == drawY - 2 || b == drawY + 3) || a == drawX + 2 && b == drawY - 2 || a == drawX + 3 && b == drawY - 1 || a == drawX - 2 && b == drawY + 2 || a == drawX - 1 && b == drawY + 3) continue;
                if (a == drawX + 3 || b == drawY + 3 || a == drawX + 2 && b == drawY + 2) {
                    this.getHelper().putColor(this.data, a, bufferSize - 1 - b, 0, 0, 0, bufferSize);
                    continue;
                }
                this.getHelper().putColor(this.data, a, bufferSize - 1 - b, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, bufferSize);
            }
        }
        return true;
    }

    @Override
    protected void renderChunks(MinimapSession session, MinimapProcessor minimap, Vec3 renderPos, ResourceKey<Level> mapDimension, double mapDimensionScale, int mapSize, int bufferSize, float sizeFix, float partial, int lightLevel, boolean useWorldMap, boolean lockedNorth, int shape, double ps, double pc, boolean cave, boolean circle, ModSettings settings, XaeroBufferProvider cvc) {
        this.updateMapFrameSafeMode(session, minimap, (Player)this.mc.player, this.mc.getCameraEntity(), bufferSize, mapSize, partial, lightLevel, lockedNorth, shape, ps, pc, cave, settings);
        this.matrixStack.scale(sizeFix, sizeFix, 1.0f);
        try {
            this.mapTexture.loadIfNeeded();
            this.getHelper().bindTextureBuffer(this.mapTexture.buffer, bufferSize, bufferSize, 512, this.mapTexture.getTextureAndView());
            ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
            int opacityConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPACITY);
            float opacity = (float)opacityConfig / 100.0f;
            ImmediateRenderUtil.setShaderColor((float)opacity, (float)opacity, (float)opacity, (float)opacity);
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
        }
    }

    private void getLoadedBlockColor(MinimapSession session, MinimapProcessor minimap, int[] result, int par1, int par2, int level, int chunkGridConfig, boolean slimeChunks) {
        int tileX = par1 >> 4;
        int tileZ = par2 >> 4;
        int chunkX = (tileX >> 2) - minimap.getMinimapWriter().getLoadedMapChunkX();
        int chunkZ = (tileZ >> 2) - minimap.getMinimapWriter().getLoadedMapChunkZ();
        if (minimap.getMinimapWriter().getLoadedBlocks() == null || chunkX < 0 || chunkX >= minimap.getMinimapWriter().getLoadedBlocks().length || chunkZ < 0 || chunkZ >= minimap.getMinimapWriter().getLoadedBlocks().length) {
            result[2] = 1;
            result[1] = 1;
            result[0] = 1;
            return;
        }
        try {
            MinimapTile tile;
            MinimapChunk current = minimap.getMinimapWriter().getLoadedBlocks()[chunkX][chunkZ];
            if (current != null && (tile = current.getTile(tileX & 3, tileZ & 3)) != null) {
                int insideX = par1 & 0xF;
                int insideZ = par2 & 0xF;
                this.chunkOverlay(session, result, tile.getRed(level, insideX, insideZ), tile.getGreen(level, insideX, insideZ), tile.getBlue(level, insideX, insideZ), chunkGridConfig, slimeChunks, tile);
                return;
            }
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            // empty catch block
        }
        result[2] = 1;
        result[1] = 1;
        result[0] = 1;
    }

    private void chunkOverlay(MinimapSession session, int[] result, int red, int green, int blue, int chunkGridConfig, boolean slimeChunks, MinimapTile c) {
        if (slimeChunks && c.isSlimeChunk()) {
            this.getHelper().slimeOverlay(result, red, green, blue);
            return;
        }
        if (chunkGridConfig > -1 && c.isChunkGrid()) {
            this.getHelper().gridOverlay(result, MinimapConfigConstants.COLORS[chunkGridConfig], red, green, blue);
            return;
        }
        result[0] = red;
        result[1] = green;
        result[2] = blue;
    }

    @Override
    public PreparedRenderType.Texture getTas() {
        return this.mapTexture.getTas();
    }
}

