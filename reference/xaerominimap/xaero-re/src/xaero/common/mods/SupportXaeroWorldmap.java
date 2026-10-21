/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.region.MinimapTile;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.shader.WorldMapShaderHelper;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;
import xaero.map.config.primary.option.WorldMapPrimaryClientConfigOptions;
import xaero.map.config.util.WorldMapClientConfigUtils;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.mods.SupportMods;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.LeafRegionTexture;
import xaero.map.world.MapDimension;

public class SupportXaeroWorldmap {
    public static int WORLDMAP_COMPATIBILITY_VERSION = 20;
    public static final String MINIMAP_MW = "minimap";
    public int compatibilityVersion;
    private static final HashMap<MapTileChunk, Long> seedsUsed = new HashMap();
    public static final int black = -16777216;
    public static final int slime = -2142047936;
    private IXaeroMinimap modMain;
    private int destinationCaving = Integer.MAX_VALUE;
    private long lastDestinationCavingSwitch;
    private int previousRenderedCaveLayer = Integer.MAX_VALUE;
    private int lastRenderedCaveLayer = Integer.MAX_VALUE;
    private ArrayList<MapRegion> regionBuffer = new ArrayList();

    public SupportXaeroWorldmap(IXaeroMinimap modMain) {
        this.modMain = modMain;
        try {
            this.compatibilityVersion = WorldMap.MINIMAP_COMPATIBILITY_VERSION;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        if (this.compatibilityVersion < 3) {
            throw new RuntimeException("Xaero's World Map 1.11.0 or newer required!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawMinimap(MinimapSession minimapSession, PoseStack matrixStack, MinimapRendererHelper helper, int xFloored, int zFloored, int minViewX, int minViewZ, int maxViewX, int maxViewZ, boolean zooming, double zoom, double mapDimensionScale, VertexConsumer overlayBufferBuilder, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            if (!mapProcessor.isRenderingPaused()) {
                if (mapProcessor.getCurrentDimension() == null) {
                    return;
                }
                int compatibilityVersion = this.compatibilityVersion;
                String worldString = mapProcessor.getCurrentWorldId();
                if (worldString == null) {
                    return;
                }
                int mapX = xFloored >> 4;
                int mapZ = zFloored >> 4;
                int chunkX = mapX >> 2;
                int chunkZ = mapZ >> 2;
                int tileX = mapX & 3;
                int tileZ = mapZ & 3;
                int insideX = xFloored & 0xF;
                int insideZ = zFloored & 0xF;
                ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                int minX = Math.min(minViewX, (mapX >> 2) - 4);
                int maxX = Math.max(maxViewX, (mapX >> 2) + 4);
                int minZ = Math.min(minViewZ, (mapZ >> 2) - 4);
                int maxZ = Math.max(maxViewZ, (mapZ >> 2) + 4);
                boolean slimeChunks = MinimapConfigClientUtils.getEffectiveSlimeChunks(minimapSession);
                mapProcessor.initMinimapRender(xFloored, zFloored);
                int renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
                float brightness = this.getMinimapBrightness();
                if (renderedCaveLayer != this.lastRenderedCaveLayer) {
                    this.previousRenderedCaveLayer = this.lastRenderedCaveLayer;
                }
                LocalPlayer player = Minecraft.getInstance().player;
                boolean noCaveMaps = !MinimapConfigClientUtils.getEffectiveCaveModeAllowed();
                Consumer<GpuTextureView> finalizer = null;
                RenderType mapRenderType = CustomRenderTypes.MINIMAP_WORLD_MAP;
                if (zooming) {
                    mapRenderType = CustomRenderTypes.MINIMAP_WORLD_MAP_ZOOM;
                }
                MultiTextureRenderTypeRenderer mapWithLightRenderer = multiTextureRenderTypeRenderers.getRenderer(finalizer, mapRenderType);
                MultiTextureRenderTypeRenderer mapNoLightRenderer = multiTextureRenderTypeRenderers.getRenderer(finalizer, mapRenderType);
                MinimapWorld world = minimapSession.getWorldManager().getAutoWorld();
                Long seed = !slimeChunks || world == null ? null : MinimapConfigClientUtils.getEffectiveSlimeChunksSeed(world);
                this.renderChunks(matrixStack, minX, maxX, minZ, maxZ, minViewX, maxViewX, minViewZ, maxViewZ, mapProcessor, noCaveMaps, slimeChunks, chunkX, chunkZ, tileX, tileZ, insideX, insideZ, seed, mapWithLightRenderer, mapNoLightRenderer, helper, overlayBufferBuilder);
                WorldMapShaderHelper.setBrightness((float)brightness);
                WorldMapShaderHelper.setWithLight((boolean)true);
                multiTextureRenderTypeRenderers.draw(mapWithLightRenderer);
                WorldMapShaderHelper.setWithLight((boolean)false);
                multiTextureRenderTypeRenderers.draw(mapNoLightRenderer);
                this.lastRenderedCaveLayer = renderedCaveLayer;
                mapProcessor.finalizeMinimapRender();
            }
        }
    }

    private void renderChunks(PoseStack matrixStack, int minX, int maxX, int minZ, int maxZ, int minViewX, int maxViewX, int minViewZ, int maxViewZ, MapProcessor mapProcessor, boolean noCaveMaps, boolean slimeChunks, int chunkX, int chunkZ, int tileX, int tileZ, int insideX, int insideZ, Long seed, MultiTextureRenderTypeRenderer mapWithLightRenderer, MultiTextureRenderTypeRenderer mapNoLightRenderer, MinimapRendererHelper helper, VertexConsumer overlayBufferBuilder) {
        Matrix4f matrix = matrixStack.last().pose();
        for (int i = minX; i <= maxX; ++i) {
            for (int j = minZ; j <= maxZ; ++j) {
                MapTileChunk previousLayerChunk;
                MapRegion previousLayerRegion;
                boolean chunkIsVisible;
                MapRegion region = mapProcessor.getMinimapMapRegion(i >> 3, j >> 3);
                mapProcessor.beforeMinimapRegionRender(region);
                if (i < minViewX || i > maxViewX || j < minViewZ || j > maxViewZ) continue;
                MapTileChunk chunk = region == null ? null : region.getChunk(i & 7, j & 7);
                boolean bl = chunkIsVisible = chunk != null && chunk.getLeafTexture().getGlColorTexture() != null;
                if (!(chunkIsVisible || noCaveMaps && this.previousRenderedCaveLayer != Integer.MAX_VALUE || (previousLayerRegion = mapProcessor.getLeafMapRegion(this.previousRenderedCaveLayer, i >> 3, j >> 3, false)) == null || (previousLayerChunk = previousLayerRegion.getChunk(i & 7, j & 7)) == null || previousLayerChunk.getLeafTexture().getGlColorTexture() == null)) {
                    region = previousLayerRegion;
                    chunk = previousLayerChunk;
                    chunkIsVisible = true;
                }
                if (!chunkIsVisible) continue;
                this.bumpLoadedRegion(mapProcessor, region);
                int drawX = 64 * (chunk.getX() - chunkX) - 16 * tileX - insideX;
                int drawZ = 64 * (chunk.getZ() - chunkZ) - 16 * tileZ - insideZ;
                this.prepareMapTexturedRect(matrix, drawX, drawZ, 0, 0, 64.0f, 64.0f, chunk, mapNoLightRenderer, mapWithLightRenderer, helper);
                if (!slimeChunks) continue;
                this.renderSlimeChunks(chunk, seed, drawX, drawZ, matrixStack, helper, overlayBufferBuilder);
            }
        }
    }

    public void bumpLoadedRegion(MapProcessor mapProcessor, MapRegion region) {
        if (!mapProcessor.isUploadingPaused() && region.isLoaded()) {
            mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(region);
        }
    }

    public void renderSlimeChunks(MapTileChunk chunk, Long seed, int drawX, int drawZ, PoseStack matrixStack, MinimapRendererHelper helper, VertexConsumer overlayBufferBuilder) {
        boolean newSeed;
        Long savedSeed = seedsUsed.get(chunk);
        boolean bl = newSeed = seed == null && savedSeed != null || seed != null && !seed.equals(savedSeed);
        if (newSeed) {
            seedsUsed.put(chunk, seed);
        }
        for (int t = 0; t < 16; ++t) {
            if (newSeed || (chunk.getTileGridsCache()[t % 4][t / 4] & 1) == 0) {
                chunk.getTileGridsCache()[t % 4][t / 4] = (byte)(1 | (MinimapTile.isSlimeChunk(this.modMain.getSettings(), chunk.getX() * 4 + t % 4, chunk.getZ() * 4 + t / 4, seed) ? 2 : 0));
            }
            if ((chunk.getTileGridsCache()[t % 4][t / 4] & 2) == 0) continue;
            int slimeDrawX = drawX + 16 * (t % 4);
            int slimeDrawZ = drawZ + 16 * (t / 4);
            RenderBufferUtil.addColoredRect(matrixStack.last().pose(), overlayBufferBuilder, slimeDrawX, slimeDrawZ, 16, 16, -2142047936);
        }
    }

    public boolean getWorldMapWaypoints() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
    }

    public int getWorldMapColours() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS);
    }

    public boolean getWorldMapFlowers() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS);
    }

    public boolean getWorldMapTerrainDepth() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH);
    }

    public int getWorldMapTerrainSlopes() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES);
    }

    public boolean getWorldMapBiomeColorsVanillaMode() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA);
    }

    public boolean getWorldMapIgnoreHeightmaps() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return false;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        return mapProcessor.getMapWorld().isIgnoreHeightmaps();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String tryToGetMultiworldId(ResourceKey<Level> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return null;
            }
            return this.getMultiworldIdUnsynced(mapProcessor, dimId);
        }
    }

    private String getMultiworldIdUnsynced(MapProcessor mapProcessor, ResourceKey<Level> dimId) {
        MapDimension mapDim;
        MapDimension mapDimension = mapDim = !mapProcessor.isMapWorldUsable() || mapProcessor.isWaitingForWorldUpdate() ? null : mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
        return mapDim == null ? null : (!mapDim.currentMultiworldWritable ? MINIMAP_MW : mapDim.getCurrentMultiworld());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getPotentialMultiworldIds(ResourceKey<Level> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiSync;
        synchronized (object) {
            MapDimension mapDim = mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
            return mapDim == null || !mapProcessor.isWaitingForWorldUpdate() && mapDim.currentMultiworldWritable ? null : mapDim.getMultiworldIdsCopy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getMultiworldIds(ResourceKey<Level> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiSync;
        synchronized (object) {
            MapDimension mapDim = mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
            return mapDim == null ? null : mapDim.getMultiworldIdsCopy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String tryToGetMultiworldName(ResourceKey<Level> dimId, String multiworldId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return null;
            }
            return this.getMultiworldNameUnsynced(mapProcessor, dimId, multiworldId);
        }
    }

    private String getMultiworldNameUnsynced(MapProcessor mapProcessor, ResourceKey<Level> dimId, String multiworldId) {
        MapDimension mapDim = !mapProcessor.isMapWorldUsable() ? null : mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
        return mapDim == null ? null : mapDim.getMultiworldName(multiworldId);
    }

    public void openSettings() {
        Screen current = Minecraft.getInstance().gui.screen();
        Minecraft.getInstance().gui.setScreen(this.getSettingsScreen(current));
    }

    public Screen getSettingsScreen(Screen current) {
        Screen currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        return this.getSettingsScreen(current, currentEscScreen);
    }

    public Screen getSettingsScreen(Screen current, Screen currentEscScreen) {
        return new GuiWorldMapSettings(current, currentEscScreen, BuiltInEditConfigScreenContexts.CLIENT);
    }

    public float getMinimapBrightness() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return 1.0f;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        boolean lightingConfig = (Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHTING);
        return mapProcessor.getBrightness(lightingConfig);
    }

    public void prepareMapTexturedRect(Matrix4f matrix, float x, float y, int textureX, int textureY, float width, float height, MapTileChunk chunk, MultiTextureRenderTypeRenderer noLightRenderer, MultiTextureRenderTypeRenderer withLightrenderer, MinimapRendererHelper helper) {
        LeafRegionTexture leafTexture = chunk.getLeafTexture();
        GpuTextureAndView texture = leafTexture.getGlColorTexture();
        if (texture == null) {
            return;
        }
        MultiTextureRenderUtil.prepareTexturedRect(matrix, x, y, textureX, (int)height, width, height, -height, 64.0f, texture.view, leafTexture.getTextureHasLight() ? withLightrenderer : noLightRenderer);
    }

    public boolean getAdjustHeightForCarpetLikeBlocks() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS);
    }

    public void registerHighlighters(HighlighterRegistry highlighterRegistry) {
        SupportMods.xaeroMinimap.registerMinimapHighlighters((Object)highlighterRegistry);
    }

    public void createRadarRenderWrapper(RadarRenderer radarRenderer) {
        SupportMods.xaeroMinimap.createRadarRendererWrapper((Object)radarRenderer);
    }

    public boolean worldMapIsRenderingRadar() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
    }

    public boolean getPartialYTeleport() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.PARTIAL_Y_TELEPORT);
    }

    public boolean isStainedGlassDisplayed() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS);
    }

    public boolean isMultiplayerMap() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return false;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        return mapProcessor.getMapWorld().isMultiplayer();
    }

    public int getManualCaveStart() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        SingleConfigManager wmPrimaryConfigManager = wmConfigManager.getPrimaryConfigManager();
        int caveModeStart = (Integer)wmPrimaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START);
        return caveModeStart == Integer.MAX_VALUE ? Integer.MAX_VALUE : caveModeStart;
    }

    public boolean hasEnabledCaveLayers() {
        return this.getCaveModeType() == 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCaveModeType() {
        if (!WorldMapClientConfigUtils.getEffectiveCaveModeAllowed()) {
            return 0;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        int defaultCaveModeType = (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DEFAULT_CAVE_MODE_TYPE);
        if (worldmapSession == null) {
            return defaultCaveModeType;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return defaultCaveModeType;
            }
            MapDimension mapDim = mapProcessor.getMapWorld().getCurrentDimension();
            if (mapDim != null) {
                return mapDim.getCaveModeType();
            }
        }
        return defaultCaveModeType;
    }

    public Screen getWorldMapScreenForOption(ConfigOption<?> option, Screen current) {
        Screen currentEscScreen;
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        Screen screen = currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        if (currentEscScreen instanceof GuiMap) {
            currentEscScreen = null;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        GuiMap screen2 = new GuiMap(current, currentEscScreen, mapProcessor, Minecraft.getInstance().getCameraEntity());
        if (option == MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START) {
            screen2.enableCaveModeOptions();
        }
        return screen2;
    }

    public int getCaveModeDepth() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
    }

    public boolean isLegibleCaveMaps() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS);
    }

    public boolean getBiomeBlending() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING);
    }

    public void confirmPlayerRadarRender(Player e) {
        if (WorldMap.trackedPlayerRenderer.getCollector().playerExists(e.getUUID())) {
            WorldMap.trackedPlayerRenderer.getCollector().confirmPlayerRadarRender(e);
        }
    }

    public boolean getDisplayClaims() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Boolean)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS);
    }

    public int getClaimsBorderOpacity() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY);
    }

    public int getClaimsFillOpacity() {
        ClientConfigManager wmConfigManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (Integer)wmConfigManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY);
    }

    public void toggleChunkClaims() {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS);
    }

    public boolean caveLayersAreUsable() {
        boolean result = this.hasEnabledCaveLayers();
        if (result) {
            WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession == null) {
                return result;
            }
            Entity player = Minecraft.getInstance().getCameraEntity();
            if (player == null) {
                return result;
            }
            MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            if (mapDimension == null) {
                return result;
            }
            if (mapDimension.getDimId() != player.level().dimension()) {
                return false;
            }
        }
        return result;
    }

    public boolean shouldPreventAutoCaveMode(Level world) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return false;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        if (mapDimension == null) {
            return false;
        }
        return mapDimension.getDimId() != world.dimension();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getMapDimensionScale() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return Minecraft.getInstance().level.dimensionType().coordinateScale();
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            if (mapProcessor.isRenderingPaused()) {
                return 0.0;
            }
            Registry dimTypes = mapProcessor.getWorldDimensionTypeRegistry();
            if (dimTypes == null) {
                return 0.0;
            }
            return mapProcessor.getMapWorld().getCurrentDimension().calculateDimScale(dimTypes);
        }
    }

    public ResourceKey<Level> getMapDimension() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return Minecraft.getInstance().level.dimension();
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        return mapDimension == null ? Minecraft.getInstance().level.dimension() : mapDimension.getDimId();
    }
}

