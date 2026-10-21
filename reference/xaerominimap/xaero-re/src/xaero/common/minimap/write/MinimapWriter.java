/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.minimap.region.MinimapChunk;
import xaero.common.minimap.region.MinimapTile;
import xaero.common.minimap.write.MinimapWriterHelper;
import xaero.common.minimap.write.biome.BiomeBlendCalculator;
import xaero.common.misc.CachedFunction;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.common.mods.SupportMods;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.level.block.BlockTextureColorUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.platform.ClientOnlyServices;

public abstract class MinimapWriter {
    private static final int VOID_COLOR = -16121833;
    private static final float DEFAULT_AMBIENT_LIGHT = 0.7f;
    private static final float DEFAULT_AMBIENT_LIGHT_COLORED = 0.2f;
    private static final float DEFAULT_AMBIENT_LIGHT_WHITE = 0.5f;
    private static final float DEFAULT_MAX_DIRECT_LIGHT = 0.6666667f;
    private static final float GLOWING_MAX_DIRECT_LIGHT = 0.22222224f;
    private static final String[] dimensionsToIgnore = new String[]{"FZHammer"};
    private static final int UPDATE_EVERY_RUNS = 5;
    private static final int MAXIMUM_OVERLAYS = 5;
    public static final int SUN_MINIMUM = 9;
    public static final int NO_Y_VALUE = Integer.MAX_VALUE;
    private static final int MAX_TRANSPARENCY_BLEND_DEPTH = 5;
    private IXaeroMinimap modMain;
    private MinimapSession minimapSession;
    private MinimapWriterHelper helper;
    private Minimap minimap;
    protected final RandomSource usedRandom = RandomSource.create((long)0L);
    private int loadingSideInChunks;
    private double updateRadius;
    private MinimapChunk[][] loadingBlocks;
    private int loadingMapChunkX;
    private int loadingMapChunkZ;
    private int loadingStartX;
    private int loadingStartZ;
    private int loadingEndX;
    private int loadingEndZ;
    private int loadingCaving;
    private int loadingLevels;
    private boolean loadingLighting;
    private float loadingSingleLevelBrightness;
    private int loadingTerrainSlopes;
    private boolean loadingTerrainDepth;
    private boolean loadingRedstone;
    private int loadingColours;
    private boolean loadingTransparency;
    private boolean loadingBiomesVanillaMode;
    private ResourceKey<Level> loadingDimension;
    private boolean loadingIgnoreHeightmaps;
    private int loadingCaveMapsDepth;
    public int loadingLightOverlayType;
    public int loadingLightOverlayMaxLight;
    public int loadingLightOverlayMinLight;
    public int loadingLightOverlayColor;
    private boolean loadingFlowers;
    private boolean loadingAdjustHeightForCarpetLikeBlocks;
    private boolean loadingStainedGlass;
    private boolean loadingLegibleCaveMode;
    private boolean loadingBiomeBlending;
    private boolean loadingNonWorldMap;
    private Long loadingSlimeSeed;
    private int loadingHighlightVersion;
    private int loadedSideInChunks;
    private MinimapChunk[][] loadedBlocks;
    private int loadedMapChunkX;
    private int loadedMapChunkZ;
    private int loadedCaving;
    private int prevLoadedCaving;
    private int loadedLevels;
    private boolean loadedLighting;
    private int loadedTerrainSlopes;
    private boolean loadedTerrainDepth;
    private boolean loadedRedstone;
    private int loadedColours;
    private boolean loadedTransparency;
    private boolean loadedBiomesVanillaMode;
    private ResourceKey<Level> loadedDimension;
    private boolean loadedIgnoreHeightmaps;
    private int loadedCaveMapsDepth;
    public int loadedLightOverlayType;
    public int loadedLightOverlayMaxLight;
    public int loadedLightOverlayMinLight;
    public int loadedLightOverlayColor;
    private boolean loadedFlowers;
    private boolean loadedAdjustHeightForCarpetLikeBlocks;
    private boolean loadedStainedGlass;
    private boolean loadedLegibleCaveMode;
    private boolean loadedBiomeBlending;
    private boolean loadedNonWorldMap;
    private Long loadedSlimeSeed;
    private int loadedHighlightVersion;
    private long loadedTime;
    private boolean settingsChanged;
    private ArrayList<Long> detectedChunkChanges;
    private int workingFrameCount;
    private long framesFreedTime = -1L;
    public long writeFreeSinceLastWrite = -1L;
    private int writeFreeSizeTiles;
    private int writeFreeFullUpdateTargetTime;
    private int updateChunkX;
    private int updateChunkZ;
    private int tileInsideX;
    private int tileInsideZ;
    private int runNumber;
    private boolean previousShouldLoad;
    private int lastCaving;
    private boolean clearBlockColours;
    private final CachedFunction<StateHolder<?, ?>, Boolean> transparentCache;
    private boolean clearTransparentCache;
    private final Map<BlockState, Boolean> glowingCache;
    private long lastWrite = -1L;
    private long lastWriteTry = -1L;
    private boolean forcedRefresh;
    private MinimapChunk oldChunk;
    private CachedFunction<FluidState, BlockState> fluidToBlock;
    private int updates;
    private int loads;
    private long before;
    private int processingTime;
    public boolean debugTotalTime = false;
    public long minTime = -1L;
    public long maxTime = -1L;
    public long totalTime;
    public long totalRuns;
    public long lastDebugTime = -1L;
    public long minTimeDebug;
    public long maxTimeDebug;
    public long averageTimeDebug;
    private long currentComparisonCode;
    private final List<Integer> pixelTransparentSizes;
    private final List<BlockState> pixelBlockStates;
    private final List<Integer> pixelBlockLights;
    private int firstBlockY;
    boolean isglowing;
    private final int[] underRed;
    private final int[] underGreen;
    private final int[] underBlue;
    private int sun;
    private float currentTransparencyMultiplier;
    private int blockY;
    private int blockColor;
    private final int[] red;
    private final int[] green;
    private final int[] blue;
    private final float[] brightness;
    private final float[] postBrightness;
    private final int[] tempColor;
    private boolean underair;
    private BlockState previousTransparentState;
    private int firstTransparentStateY;
    private final BlockPos.MutableBlockPos mutableBlockPos;
    private final BlockPos.MutableBlockPos mutableBlockPos2;
    private final BlockPos.MutableBlockPos mutableBlockPos3;
    private ArrayList<BlockState> buggedStates;
    private final Minecraft mc;
    private final BiomeBlendCalculator biomeBlendCalculator;
    private final BlockStateShortShapeCache blockStateShortShapeCache;
    private final HighlighterRegistry highlighterRegistry;
    private Level prevWorld;
    private DimensionHighlighterHandler dimensionHighlightHandler;
    private BlockTextureColorUtils textureColors;
    private Registry<Block> blockRegistry;

    public MinimapWriter(IXaeroMinimap modMain, MinimapSession minimapSession, BlockStateShortShapeCache blockStateShortShapeCache, HighlighterRegistry highlighterRegistry) {
        this.modMain = modMain;
        this.minimapSession = minimapSession;
        this.loadingSideInChunks = 9;
        this.updateRadius = 16.0;
        this.loadingCaving = Integer.MAX_VALUE;
        this.loadedCaving = Integer.MAX_VALUE;
        this.prevLoadedCaving = Integer.MAX_VALUE;
        this.lastCaving = Integer.MAX_VALUE;
        this.loadedCaving = Integer.MAX_VALUE;
        this.red = new int[5];
        this.green = new int[5];
        this.blue = new int[5];
        this.underRed = new int[5];
        this.underGreen = new int[5];
        this.underBlue = new int[5];
        this.brightness = new float[5];
        this.postBrightness = new float[5];
        this.tempColor = new int[3];
        this.helper = new MinimapWriterHelper();
        this.mutableBlockPos = new BlockPos.MutableBlockPos();
        this.mutableBlockPos2 = new BlockPos.MutableBlockPos();
        this.mutableBlockPos3 = new BlockPos.MutableBlockPos();
        this.pixelBlockStates = new ArrayList<BlockState>();
        this.pixelTransparentSizes = new ArrayList<Integer>();
        this.pixelBlockLights = new ArrayList<Integer>();
        this.transparentCache = new CachedFunction<StateHolder, Boolean>(state -> {
            if (state instanceof BlockState) {
                BlockState blockState = (BlockState)state;
                if (blockState.getBlock() instanceof AirBlock || blockState.getBlock() instanceof TransparentBlock) {
                    return true;
                }
                ClientLevel level = (ClientLevel)Minecraft.getInstance().getCameraEntity().level();
                BlockPos fakePos = new BlockPos(0, 70, 0);
                return ClientOnlyServices.PLATFORM.getBlockUtils().getTranslucencyUtils().shouldConsiderBlockTranslucent((BlockAndTintGetter)level, fakePos, blockState);
            }
            FluidState fluidState = (FluidState)state;
            return ClientOnlyServices.PLATFORM.getBlockUtils().getTranslucencyUtils().shouldConsiderFluidTranslucent(fluidState);
        });
        this.glowingCache = new HashMap<BlockState, Boolean>();
        this.minimap = modMain.getMinimap();
        this.buggedStates = new ArrayList();
        this.detectedChunkChanges = new ArrayList();
        this.mc = Minecraft.getInstance();
        this.biomeBlendCalculator = new BiomeBlendCalculator();
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.fluidToBlock = new CachedFunction<FluidState, BlockState>(FluidState::createLegacyBlock);
        this.highlighterRegistry = highlighterRegistry;
        this.textureColors = ClientOnlyServices.PLATFORM.getBlockUtils().getTextureColorUtils();
    }

    protected abstract int getBlockStateLightEmission(BlockState var1, Level var2, BlockPos var3);

    public void setupDimensionHighlightHandler(ResourceKey<Level> dimension) {
        this.dimensionHighlightHandler = new DimensionHighlighterHandler(dimension, this.highlighterRegistry, this);
    }

    private void updateTimeDebug(long before) {
        if (this.debugTotalTime) {
            long debugPassed = System.nanoTime() - before;
            this.totalTime += debugPassed;
            ++this.totalRuns;
            if (debugPassed > this.maxTime) {
                this.maxTime = debugPassed;
            }
            if (this.minTime == -1L || debugPassed < this.minTime) {
                this.minTime = debugPassed;
            }
            long time = System.currentTimeMillis();
            if (this.lastDebugTime == -1L) {
                this.lastDebugTime = time;
            } else if (time - this.lastDebugTime > 1000L) {
                this.maxTimeDebug = this.maxTime;
                this.minTimeDebug = this.minTime;
                this.averageTimeDebug = this.totalTime / this.totalRuns;
                this.maxTime = -1L;
                this.minTime = -1L;
                this.totalTime = 0L;
                this.totalRuns = 0L;
                this.lastDebugTime = time;
            }
        }
    }

    public void onRender() {
        if (!ModSettings.canEditIngameSettings()) {
            return;
        }
        long before = System.nanoTime();
        MinimapProcessor minimapProcessor = this.minimapSession.getProcessor();
        try {
            long passed;
            boolean shouldLoad;
            Entity player = Minecraft.getInstance().getCameraEntity();
            if (player == null) {
                return;
            }
            ClientLevel world = (ClientLevel)player.level();
            if (world != this.prevWorld) {
                if (world != null) {
                    this.setupDimensionHighlightHandler((ResourceKey<Level>)world.dimension());
                } else {
                    this.dimensionHighlightHandler = null;
                }
                this.loadedDimension = null;
                this.updateChunkZ = 0;
                this.updateChunkX = 0;
                this.tileInsideZ = 0;
                this.tileInsideX = 0;
                this.prevWorld = world;
                if (this.modMain.getSupportMods().framedBlocks()) {
                    this.modMain.getSupportMods().supportFramedBlocks.onWorldChange();
                }
            }
            double playerX = player.getX();
            double playerY = player.getY();
            double playerZ = player.getZ();
            ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
            boolean displayMinimap = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_MINIMAP);
            if (this.modMain.getSettings() == null || !displayMinimap || world == null) {
                this.updateTimeDebug(before);
                return;
            }
            int lightOverlayTypeConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE);
            int cavingDestination = this.getCaving(playerX, playerY, playerZ, (Level)world);
            boolean attemptUsingWorldMapChunks = this.modMain.getSupportMods().shouldUseWorldMapChunks() && (cavingDestination == Integer.MAX_VALUE || this.modMain.getSupportMods().shouldUseWorldMapCaveChunks()) && lightOverlayTypeConfig <= 0;
            boolean bl = shouldLoad = !this.ignoreWorld((Level)world) && (!attemptUsingWorldMapChunks || this.loadedNonWorldMap || this.loadingNonWorldMap || this.loadedCaving != cavingDestination || this.loadedCaving != this.loadingCaving);
            if (shouldLoad != this.previousShouldLoad) {
                this.updateChunkZ = 0;
                this.updateChunkX = 0;
                this.tileInsideZ = 0;
                this.tileInsideX = 0;
                this.previousShouldLoad = shouldLoad;
            }
            if (!shouldLoad) {
                this.updateTimeDebug(before);
                return;
            }
            XaeroMinimapCore.ensureField();
            if (this.clearTransparentCache) {
                this.transparentCache.clear();
                this.clearTransparentCache = false;
            }
            this.blockRegistry = (Registry)world.registryAccess().lookup(Registries.BLOCK).get();
            int lengthX = Math.min(this.loadingSideInChunks, this.loadingEndX - this.loadingStartX + 1);
            int lengthZ = Math.min(this.loadingSideInChunks, this.loadingEndZ - this.loadingStartZ + 1);
            if (this.lastWriteTry == -1L) {
                lengthX = 3;
                lengthZ = 3;
            } else {
                if (lengthX > this.loadingSideInChunks) {
                    lengthX = this.loadingSideInChunks;
                }
                if (lengthZ > this.loadingSideInChunks) {
                    lengthZ = this.loadingSideInChunks;
                }
            }
            int sizeTileChunks = lengthX * lengthZ;
            int sizeTiles = sizeTileChunks * 4 * 4;
            int sizeBasedTargetTime = sizeTiles * 1000 / 1500;
            int fullUpdateTargetTime = Math.max(100, sizeBasedTargetTime);
            long time = System.currentTimeMillis();
            long l = passed = this.lastWrite == -1L ? 0L : time - this.lastWrite;
            if (this.lastWriteTry == -1L || this.writeFreeSizeTiles != sizeTiles || this.writeFreeFullUpdateTargetTime != fullUpdateTargetTime || this.workingFrameCount > 30) {
                this.framesFreedTime = time;
                this.writeFreeSizeTiles = sizeTiles;
                this.writeFreeFullUpdateTargetTime = fullUpdateTargetTime;
                this.workingFrameCount = 0;
            }
            long sinceLastWrite = Math.min(passed, this.writeFreeSinceLastWrite);
            if (this.framesFreedTime != -1L) {
                sinceLastWrite = time - this.framesFreedTime;
            }
            long tilesToUpdate = Math.min(sinceLastWrite * (long)sizeTiles / (long)fullUpdateTargetTime, 100L);
            if (this.lastWrite == -1L || tilesToUpdate != 0L) {
                this.lastWrite = time;
            }
            int flickeringTimer = ((Double)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER)).intValue();
            if (tilesToUpdate != 0L) {
                if (this.framesFreedTime == -1L) {
                    int timeLimit = (int)(Math.min(sinceLastWrite, 50L) * 86960L);
                    long writeStartNano = System.nanoTime();
                    if (cavingDestination == Integer.MAX_VALUE != (this.loadingCaving == Integer.MAX_VALUE) || attemptUsingWorldMapChunks == this.loadingNonWorldMap) {
                        this.updateChunkZ = 0;
                        this.updateChunkX = 0;
                        this.tileInsideZ = 0;
                        this.tileInsideX = 0;
                        this.loadedTime = time;
                    }
                    int circleInsideX = this.tileInsideX;
                    int circleInsideZ = this.tileInsideZ;
                    int circleX = this.updateChunkX;
                    int circleZ = this.updateChunkZ;
                    boolean hitEnd = false;
                    int i = 0;
                    while ((long)i < tilesToUpdate && !this.beforeWriting(attemptUsingWorldMapChunks, cavingDestination, flickeringTimer, time)) {
                        if (this.writeChunk(minimapProcessor, playerX, playerY, playerZ, world, cavingDestination, attemptUsingWorldMapChunks)) {
                            --i;
                        }
                        if (this.tileInsideX == circleInsideX && this.tileInsideZ == circleInsideZ && this.updateChunkX == circleX && this.updateChunkZ == circleZ) break;
                        if (this.tileInsideX == 0 && this.tileInsideZ == 0 && this.updateChunkX == 0 && this.updateChunkZ == 0) {
                            if (hitEnd) break;
                            hitEnd = true;
                        }
                        if (System.nanoTime() - writeStartNano >= (long)timeLimit) break;
                        ++i;
                    }
                    ++this.workingFrameCount;
                } else {
                    this.writeFreeSinceLastWrite = sinceLastWrite;
                    this.framesFreedTime = -1L;
                }
            }
            this.lastWriteTry = time;
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
        }
        this.updateTimeDebug(before);
    }

    private boolean beforeWriting(boolean attemptUsingWorldMapChunks, int cavingDestination, int flickeringTimer, long time) {
        if (this.tileInsideX == 0 && this.tileInsideZ == 0 && this.updateChunkX == 0 && this.updateChunkZ == 0 && attemptUsingWorldMapChunks) {
            this.loadingCaving = cavingDestination;
            this.loadingNonWorldMap = false;
            if (this.loadedCaving == Integer.MAX_VALUE == (this.loadingCaving == Integer.MAX_VALUE) || this.loadedTime == 0L || time - this.loadedTime >= (long)flickeringTimer) {
                this.loadedCaving = this.loadingCaving;
                this.loadedNonWorldMap = false;
            }
            if (!this.loadedNonWorldMap) {
                this.detectedChunkChanges.clear();
            }
            return true;
        }
        return this.tileInsideX == 3 && this.tileInsideZ == 3 && this.updateChunkX == this.loadingSideInChunks - 1 && this.updateChunkZ == this.loadingSideInChunks - 1 && this.loadingCaving == Integer.MAX_VALUE != (this.loadedCaving == Integer.MAX_VALUE) && this.loadedTime != 0L && time - this.loadedTime < (long)flickeringTimer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean writeChunk(MinimapProcessor minimapProcessor, double playerX, double playerY, double playerZ, ClientLevel world, int cavingDestination, boolean attemptUsingWorldMapChunks) {
        MinimapChunk mchunk;
        long processStart = System.nanoTime();
        if (this.tileInsideX == 0 && this.tileInsideZ == 0) {
            if (this.updateChunkX == 0 && this.updateChunkZ == 0) {
                boolean isEndLike;
                this.settingsChanged = false;
                if (this.clearBlockColours) {
                    this.settingsChanged = true;
                    this.clearBlockColours = false;
                    ClientOnlyServices.PLATFORM.getBlockUtils().getTextureColorUtils().requestCachedColoursClear();
                }
                this.loadingSideInChunks = this.getLoadSide();
                this.updateRadius = this.getUpdateRadiusInChunks();
                int playerXInt = OptimizedMath.myFloor(playerX);
                int playerZInt = OptimizedMath.myFloor(playerZ);
                this.loadingMapChunkX = this.getMapCoord(this.loadingSideInChunks, playerXInt);
                this.loadingMapChunkZ = this.getMapCoord(this.loadingSideInChunks, playerZInt);
                int loadDistance = (Integer)this.mc.options.renderDistance().get();
                int playerTileX = playerXInt >> 4;
                int playerTileZ = playerZInt >> 4;
                int globalStartX = playerTileX - loadDistance >> 2;
                int globalStartZ = playerTileZ - loadDistance >> 2;
                int globalEndX = playerTileX + loadDistance >> 2;
                int globalEndZ = playerTileZ + loadDistance >> 2;
                this.loadingStartX = globalStartX - this.loadingMapChunkX;
                this.loadingStartZ = globalStartZ - this.loadingMapChunkZ;
                this.loadingEndX = globalEndX - this.loadingMapChunkX;
                this.loadingEndZ = globalEndZ - this.loadingMapChunkZ;
                this.loadingCaving = cavingDestination;
                if (this.loadingCaving != Integer.MAX_VALUE || this.loadedCaving != Integer.MAX_VALUE) {
                    int maxDistance = 2;
                    if (this.loadingCaving != Integer.MAX_VALUE && this.loadedCaving == Integer.MAX_VALUE) {
                        maxDistance = 1;
                    }
                    this.loadingStartX = Math.max(this.loadingStartX, this.loadingSideInChunks / 2 - maxDistance);
                    this.loadingStartZ = Math.max(this.loadingStartZ, this.loadingSideInChunks / 2 - maxDistance);
                    this.loadingEndX = Math.min(this.loadingEndX, this.loadingSideInChunks / 2 + maxDistance);
                    this.loadingEndZ = Math.min(this.loadingEndZ, this.loadingSideInChunks / 2 + maxDistance);
                }
                if (this.loadingCaving != this.loadedCaving || this.loadingCaving != Integer.MAX_VALUE && this.prevLoadedCaving == Integer.MAX_VALUE) {
                    this.runNumber = 0;
                }
                ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
                this.loadingLighting = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHTING);
                this.loadingLevels = this.loadingLighting ? 5 : 1;
                this.loadingSingleLevelBrightness = 1.0f;
                this.loadingLegibleCaveMode = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LEGIBLE_CAVE_MAPS);
                this.loadingBiomeBlending = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.BIOME_BLENDING);
                boolean bl = isEndLike = world.dimensionType().ambientLight() >= 0.25f || world.endFlashState() != null;
                if (isEndLike || this.loadingLegibleCaveMode && this.loadingCaving != Integer.MAX_VALUE) {
                    this.loadingLighting = false;
                    this.loadingLevels = 1;
                } else if (this.loadingLighting && !world.dimensionType().hasSkyLight()) {
                    this.loadingLevels = 1;
                    this.loadingSingleLevelBrightness = this.minimap.getMinimapFBORenderer().getSunBrightness(minimapProcessor, true);
                }
                this.loadingTerrainSlopes = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_SLOPES);
                this.loadingTerrainDepth = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.TERRAIN_DEPTH);
                this.loadingRedstone = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_REDSTONE);
                this.loadingColours = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.BLOCK_COLORS);
                this.loadingTransparency = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.BLOCK_TRANSPARENCY);
                this.loadingBiomesVanillaMode = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.BIOMES_IN_VANILLA_COLORS);
                this.loadingDimension = world.dimension();
                this.loadingCaveMapsDepth = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_DEPTH);
                this.loadingIgnoreHeightmaps = this.modMain.getSettings().isIgnoreHeightmaps();
                this.loadingLightOverlayColor = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_COLOR);
                this.loadingLightOverlayMaxLight = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MAX_LIGHT);
                this.loadingLightOverlayMinLight = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_MIN_LIGHT);
                this.loadingLightOverlayType = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE);
                this.loadingFlowers = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_FLOWERS);
                this.loadingAdjustHeightForCarpetLikeBlocks = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS);
                MinimapWorld minimapWorld = this.minimapSession.getWorldManager().getAutoWorld();
                if (minimapWorld != null) {
                    this.loadingSlimeSeed = MinimapConfigClientUtils.getEffectiveSlimeChunksSeed(minimapWorld);
                }
                this.loadingHighlightVersion = this.dimensionHighlightHandler.getVersion();
                this.loadingStainedGlass = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_STAINED_GLASS);
                this.loadingNonWorldMap = true;
                this.settingsChanged = this.settingsChanged || this.loadedDimension != this.loadingDimension;
                this.settingsChanged = this.settingsChanged || this.loadedTerrainSlopes != this.loadingTerrainSlopes;
                this.settingsChanged = this.settingsChanged || this.loadedTerrainDepth != this.loadingTerrainDepth;
                this.settingsChanged = this.settingsChanged || this.loadedRedstone != this.loadingRedstone;
                this.settingsChanged = this.settingsChanged || this.loadedColours != this.loadingColours;
                this.settingsChanged = this.settingsChanged || this.loadedTransparency != this.loadingTransparency;
                this.settingsChanged = this.settingsChanged || this.loadingBiomesVanillaMode != this.loadedBiomesVanillaMode;
                this.settingsChanged = this.settingsChanged || this.loadingCaveMapsDepth != this.loadedCaveMapsDepth;
                this.settingsChanged = this.settingsChanged || this.loadingIgnoreHeightmaps != this.loadedIgnoreHeightmaps;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayColor != this.loadedLightOverlayColor;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayMaxLight != this.loadedLightOverlayMaxLight;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayMinLight != this.loadedLightOverlayMinLight;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayType != this.loadedLightOverlayType;
                this.settingsChanged = this.settingsChanged || this.loadingFlowers != this.loadedFlowers;
                boolean bl2 = this.settingsChanged = this.settingsChanged || this.loadingAdjustHeightForCarpetLikeBlocks != this.loadedAdjustHeightForCarpetLikeBlocks;
                this.settingsChanged = this.settingsChanged || this.loadingCaving == Integer.MAX_VALUE != (this.loadedCaving == Integer.MAX_VALUE);
                this.settingsChanged = this.settingsChanged || !Objects.equals(this.loadingSlimeSeed, this.loadedSlimeSeed);
                this.settingsChanged = this.settingsChanged || this.loadingHighlightVersion != this.loadedHighlightVersion;
                this.settingsChanged = this.settingsChanged || this.loadingStainedGlass != this.loadedStainedGlass;
                this.settingsChanged = this.settingsChanged || this.loadingLegibleCaveMode != this.loadedLegibleCaveMode;
                this.settingsChanged = this.settingsChanged || this.loadingLighting != this.loadedLighting;
                this.settingsChanged = this.settingsChanged || this.loadingBiomeBlending != this.loadedBiomeBlending;
                boolean bl3 = this.settingsChanged = this.settingsChanged || this.loadingNonWorldMap != this.loadedNonWorldMap;
                if (this.loadingBlocks == null || this.loadingBlocks.length != this.loadingSideInChunks) {
                    this.loadingBlocks = new MinimapChunk[this.loadingSideInChunks][this.loadingSideInChunks];
                }
                if (this.minimap.usingFBO() && minimapProcessor.isToResetImage()) {
                    this.forcedRefresh = true;
                    minimapProcessor.setToResetImage(false);
                }
                this.biomeBlendCalculator.prepare(world, this.loadingBiomeBlending);
            }
            this.oldChunk = null;
            if (this.loadedBlocks != null) {
                int updateChunkXOld = this.loadingMapChunkX + this.updateChunkX - this.loadedMapChunkX;
                int updateChunkZOld = this.loadingMapChunkZ + this.updateChunkZ - this.loadedMapChunkZ;
                if (updateChunkXOld > -1 && updateChunkXOld < this.loadedBlocks.length && updateChunkZOld > -1 && updateChunkZOld < this.loadedBlocks.length) {
                    this.oldChunk = this.loadedBlocks[updateChunkXOld][updateChunkZOld];
                }
            }
        }
        if ((mchunk = this.loadingBlocks[this.updateChunkX][this.updateChunkZ]) == null) {
            MinimapChunk minimapChunk = new MinimapChunk(this.loadingMapChunkX + this.updateChunkX, this.loadingMapChunkZ + this.updateChunkZ);
            this.loadingBlocks[this.updateChunkX][this.updateChunkZ] = minimapChunk;
            mchunk = minimapChunk;
        } else if (this.tileInsideX == 0 && this.tileInsideZ == 0) {
            mchunk.reset(this.loadingMapChunkX + this.updateChunkX, this.loadingMapChunkZ + this.updateChunkZ);
        }
        boolean onlyLoad = this.runNumber % 5 != 0 && this.loadingLightOverlayType <= 0;
        boolean outsideRender = this.updateChunkX < this.loadingStartX || this.updateChunkX > this.loadingEndX || this.updateChunkZ < this.loadingStartZ || this.updateChunkZ > this.loadingEndZ;
        MinimapChunk topChunk = this.updateChunkZ > 0 ? this.loadingBlocks[this.updateChunkX][this.updateChunkZ - 1] : null;
        MinimapChunk topLeftChunk = this.updateChunkX > 0 && this.updateChunkZ > 0 ? this.loadingBlocks[this.updateChunkX - 1][this.updateChunkZ - 1] : null;
        MinimapChunk leftChunk = this.updateChunkX > 0 ? this.loadingBlocks[this.updateChunkX - 1][this.updateChunkZ] : null;
        boolean wasProperWrite = this.writeTile(minimapProcessor, playerX, playerY, playerZ, world, mchunk, this.oldChunk, topChunk, topLeftChunk, leftChunk, this.updateChunkX, this.updateChunkZ, this.tileInsideX, this.tileInsideZ, onlyLoad, outsideRender);
        if (this.loadingLightOverlayType > 0 && this.loadedLightOverlayType > 0) {
            onlyLoad = true;
        }
        ++this.tileInsideZ;
        if (this.tileInsideZ >= 4) {
            this.tileInsideZ = 0;
            ++this.tileInsideX;
            if (this.tileInsideX >= 4) {
                this.tileInsideX = 0;
                mchunk = this.loadingBlocks[this.updateChunkX][this.updateChunkZ];
                if (this.minimap.usingFBO() && mchunk.isHasSomething() && mchunk.isChanged()) {
                    mchunk.updateBuffers(this.loadingLevels);
                    mchunk.setChanged(false);
                }
                mchunk.setLevelsBuffered(this.loadingLevels);
                if (this.updateChunkX == this.loadingSideInChunks - 1 && this.updateChunkZ == this.loadingSideInChunks - 1) {
                    if (this.runNumber % 5 == 0 && !MinimapTile.recycled.isEmpty()) {
                        MinimapTile.recycled.subList(MinimapTile.recycled.size() / 2, MinimapTile.recycled.size()).clear();
                    }
                    if (this.loadedBlocks != null) {
                        for (int i = 0; i < this.loadedBlocks.length; ++i) {
                            for (int j = 0; j < this.loadedBlocks.length; ++j) {
                                boolean shouldTransfer;
                                MinimapChunk m = this.loadedBlocks[i][j];
                                MinimapChunk lm = null;
                                if (m == null) continue;
                                m.recycleTiles();
                                int loadingX = this.loadedMapChunkX + i - this.loadingMapChunkX;
                                int loadingZ = this.loadedMapChunkZ + j - this.loadingMapChunkZ;
                                if (loadingX > -1 && loadingZ > -1 && loadingX < this.loadingSideInChunks && loadingZ < this.loadingSideInChunks) {
                                    lm = this.loadingBlocks[loadingX][loadingZ];
                                }
                                boolean bl = shouldTransfer = m.getLevelsBuffered() == this.loadingLevels && lm != null;
                                if (shouldTransfer) {
                                    MinimapChunk minimapChunk = m;
                                    synchronized (minimapChunk) {
                                        m.setBlockTextureUpload(true);
                                    }
                                }
                                for (int l = 0; l < m.getLevelsBuffered(); ++l) {
                                    if (m.getGlTexture(l) != null) {
                                        if (shouldTransfer) {
                                            lm.setGlTexture(l, m.getGlTexture(l));
                                        } else {
                                            m.getGlTexture(l).close();
                                        }
                                    }
                                    if (!shouldTransfer || lm.isRefreshRequired(l) || !m.isRefreshRequired(l)) continue;
                                    lm.copyBuffer(l, m.getBuffer(l));
                                    lm.setRefreshRequired(l, true);
                                    m.setRefreshRequired(l, false);
                                }
                            }
                        }
                    }
                    MinimapWriter i = this;
                    synchronized (i) {
                        MinimapChunk[][] bu = this.loadedBlocks;
                        this.loadedSideInChunks = this.loadingSideInChunks;
                        this.loadedBlocks = this.loadingBlocks;
                        this.loadingBlocks = bu;
                        this.loadedMapChunkX = this.loadingMapChunkX;
                        this.loadedMapChunkZ = this.loadingMapChunkZ;
                        this.loadedLevels = this.loadingLevels;
                        this.loadedLighting = this.loadingLighting;
                        this.loadedTerrainSlopes = this.loadingTerrainSlopes;
                        this.loadedTerrainDepth = this.loadingTerrainDepth;
                        this.loadedRedstone = this.loadingRedstone;
                        this.loadedColours = this.loadingColours;
                        this.loadedTransparency = this.loadingTransparency;
                        this.loadedBiomesVanillaMode = this.loadingBiomesVanillaMode;
                        this.loadedDimension = this.loadingDimension;
                        this.loadedCaveMapsDepth = this.loadingCaveMapsDepth;
                        this.loadedIgnoreHeightmaps = this.loadingIgnoreHeightmaps;
                        this.loadedLightOverlayColor = this.loadingLightOverlayColor;
                        this.loadedLightOverlayMaxLight = this.loadingLightOverlayMaxLight;
                        this.loadedLightOverlayMinLight = this.loadingLightOverlayMinLight;
                        this.loadedLightOverlayType = this.loadingLightOverlayType;
                        this.loadedFlowers = this.loadingFlowers;
                        this.loadedAdjustHeightForCarpetLikeBlocks = this.loadingAdjustHeightForCarpetLikeBlocks;
                        this.loadedSlimeSeed = this.loadingSlimeSeed;
                        this.loadedHighlightVersion = this.loadingHighlightVersion;
                        this.loadedStainedGlass = this.loadingStainedGlass;
                        this.loadedLegibleCaveMode = this.loadingLegibleCaveMode;
                        this.loadedBiomeBlending = this.loadingBiomeBlending;
                        this.loadedTime = System.currentTimeMillis();
                    }
                    this.detectedChunkChanges.clear();
                    this.prevLoadedCaving = this.loadedCaving;
                    this.loadedCaving = this.loadingCaving;
                    this.loadedNonWorldMap = true;
                    this.forcedRefresh = false;
                    ++this.runNumber;
                }
                ++this.updateChunkZ;
                if (this.updateChunkZ >= this.loadingSideInChunks) {
                    this.updateChunkZ = 0;
                    this.updateChunkX = (this.updateChunkX + 1) % this.loadingSideInChunks;
                }
            }
        }
        int passed = (int)(System.nanoTime() - processStart);
        return outsideRender && !wasProperWrite;
    }

    private boolean writeTile(MinimapProcessor minimapProcessor, double playerX, double playerY, double playerZ, ClientLevel world, MinimapChunk mchunk, MinimapChunk oldChunk, MinimapChunk topChunk, MinimapChunk topLeftChunk, MinimapChunk leftChunk, int canvasX, int canvasZ, int insideX, int insideZ, boolean onlyLoad, boolean outsideRender) {
        int[] highlights;
        int mChunkInsideZ;
        int mChunkInsideX;
        int mRegionZ;
        int tileX = mchunk.getX() * 4 + insideX;
        int tileZ = mchunk.getZ() * 4 + insideZ;
        int halfSide = this.loadingSideInChunks / 2;
        int minCanvasPos = (int)((double)halfSide - this.updateRadius);
        int maxCanvasPos = (int)Math.ceil((double)halfSide + this.updateRadius);
        MinimapTile oldTile = null;
        if (oldChunk != null) {
            oldTile = oldChunk.getTile(insideX, insideZ);
        }
        LevelChunk bchunk = (LevelChunk)world.getChunk(tileX, tileZ, ChunkStatus.FULL, false);
        boolean neighborsLoaded = true;
        block4: for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                LevelChunk nChunk;
                if (i == 0 && j == 0 || (nChunk = world.getChunk(tileX + i, tileZ + j)) != null && !(nChunk instanceof EmptyLevelChunk)) continue;
                neighborsLoaded = false;
                continue block4;
            }
        }
        boolean chunkUpdated = false;
        boolean chunkIsClean = true;
        try {
            chunkIsClean = bchunk == null || (Boolean)XaeroMinimapCore.chunkCleanField.get(bchunk) != false;
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        boolean bl = chunkUpdated = !chunkIsClean;
        if (chunkIsClean && bchunk != null && this.detectedChunkChanges.contains(Misc.getChunkPosAsLong(bchunk))) {
            chunkUpdated = true;
        }
        chunkUpdated = chunkUpdated || oldTile == null || oldTile.caveLevel != this.loadingCaving;
        boolean effectivelyUnloaded = bchunk == null || bchunk instanceof EmptyLevelChunk || !neighborsLoaded;
        int mRegionX = mchunk.getX() >> 3;
        boolean needsHighlights = this.dimensionHighlightHandler.shouldApplyTileChunkHighlights(mRegionX, mRegionZ = mchunk.getZ() >> 3, mChunkInsideX = mchunk.getX() & 7, mChunkInsideZ = mchunk.getZ() & 7, !effectivelyUnloaded);
        if ((!needsHighlights || oldTile != null) && (outsideRender || effectivelyUnloaded) || (!chunkUpdated || onlyLoad || canvasX > maxCanvasPos || canvasZ > maxCanvasPos || canvasX < minCanvasPos || canvasZ < minCanvasPos) && oldTile != null && oldTile.isSuccess() && oldChunk.getLevelsBuffered() == this.loadingLevels && !this.settingsChanged) {
            if (oldTile != null && oldChunk.getLevelsBuffered() == this.loadingLevels && !this.settingsChanged) {
                mchunk.setTile(insideX, insideZ, oldTile);
                oldTile.setWasTransfered(true);
                if (oldTile.isHasSomething()) {
                    mchunk.setHasSomething(true);
                }
                if (this.forcedRefresh) {
                    mchunk.setChanged(true);
                }
            }
            return false;
        }
        if (oldTile != null && oldChunk.getLevelsBuffered() != this.loadingLevels) {
            oldTile = null;
        }
        MinimapTile tile = null;
        Heightmap.Types typeWorldSurface = Heightmap.Types.WORLD_SURFACE;
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getWorldData(world);
        float shadowR = worldData.shadowR;
        float shadowG = worldData.shadowG;
        float shadowB = worldData.shadowB;
        int playerYi = (int)playerY;
        boolean sameCaveLevel = oldTile != null && this.loadingCaving == oldTile.caveLevel;
        boolean sameHighlights = oldTile != null && this.loadedDimension == this.loadingDimension && this.loadingHighlightVersion == oldTile.getHighlightVersion();
        boolean settingsChanged = this.settingsChanged;
        int loadingCaving = this.loadingCaving;
        int loadingLevels = this.loadingLevels;
        boolean loadingLighting = this.loadingLighting;
        float loadingSingleLevelBrightness = this.loadingSingleLevelBrightness;
        int loadingTerrainSlopes = this.loadingTerrainSlopes;
        boolean loadingTerrainDepth = this.loadingTerrainDepth;
        List<Integer> pixelTransparentSizes = this.pixelTransparentSizes;
        List<BlockState> pixelBlockStates = this.pixelBlockStates;
        List<Integer> pixelBlockLights = this.pixelBlockLights;
        int[] underRed = this.underRed;
        int[] underGreen = this.underGreen;
        int[] underBlue = this.underBlue;
        float[] postBrightness = this.postBrightness;
        float[] brightness = this.brightness;
        int[] red = this.red;
        int[] green = this.green;
        int[] blue = this.blue;
        int[] tempColor = this.tempColor;
        boolean loadingIgnoreHeightmaps = this.loadingIgnoreHeightmaps;
        int loadingCaveMapsDepth = this.loadingCaveMapsDepth;
        BlockPos.MutableBlockPos mutableBlockPos = this.mutableBlockPos;
        BlockPos.MutableBlockPos mutableBlockPos2 = this.mutableBlockPos2;
        BlockPos.MutableBlockPos mutableBlockPos3 = this.mutableBlockPos3;
        Long loadingSlimeSeed = this.loadingSlimeSeed;
        int loadedLevels = this.loadedLevels;
        IXaeroMinimap modMain = this.modMain;
        MinimapWriterHelper helper = this.helper;
        int loadingColours = this.loadingColours;
        boolean loadingRedstone = this.loadingRedstone;
        boolean loadingTransparency = this.loadingTransparency;
        int loadingLightOverlayType = this.loadingLightOverlayType;
        int loadingLightOverlayMaxLight = this.loadingLightOverlayMaxLight;
        int loadingLightOverlayMinLight = this.loadingLightOverlayMinLight;
        int loadingLightOverlayColor = this.loadingLightOverlayColor;
        boolean loadingFlowers = this.loadingFlowers;
        boolean adjustHeightForCarpetLikeBlocks = this.loadingAdjustHeightForCarpetLikeBlocks;
        boolean loadingStainedGlass = this.loadingStainedGlass;
        boolean loadingLegibleCaveMode = this.loadingLegibleCaveMode;
        boolean framedBlocksExist = modMain.getSupportMods().framedBlocks();
        int[] nArray = highlights = oldTile != null && needsHighlights ? oldTile.getHighlights() : null;
        if (needsHighlights && (highlights == null || !sameHighlights)) {
            highlights = this.dimensionHighlightHandler.applyChunkHighlightColors(tileX, tileZ);
        }
        if (effectivelyUnloaded) {
            tile = MinimapTile.getANewTile(modMain.getSettings(), tileX, tileZ, loadingSlimeSeed);
            boolean hasNonEmptyHighlight = false;
            for (int l = 0; l < loadingLevels; ++l) {
                float overlayBrightness = this.getBlockBrightness(9.0f, loadingCaving != Integer.MAX_VALUE && (loadingLighting || loadingLegibleCaveMode) ? 0 : 15, l, 0);
                for (int blockX = 0; blockX < 16; ++blockX) {
                    for (int blockZ = 0; blockZ < 16; ++blockZ) {
                        int highlight = highlights[blockZ << 4 | blockX];
                        if (highlight != 0) {
                            hasNonEmptyHighlight = true;
                        }
                        int hlRed = highlight >> 8 & 0xFF;
                        int hlGreen = highlight >> 16 & 0xFF;
                        int hlBlue = highlight >> 24 & 0xFF;
                        tile.setRGB(l, blockX, blockZ, (int)((float)hlRed * overlayBrightness), (int)((float)hlGreen * overlayBrightness), (int)((float)hlBlue * overlayBrightness));
                        tile.setCode(blockX, blockZ, 0L, (byte)0, (byte)0, (byte)0, (byte)0);
                    }
                }
            }
            mchunk.setTile(this.tileInsideX, this.tileInsideZ, tile);
            tile.setSuccess(false);
            if (hasNonEmptyHighlight) {
                tile.setHasSomething(true);
                mchunk.setHasSomething(true);
                mchunk.setChanged(true);
            }
        } else {
            int sectionBasedHeight = loadingIgnoreHeightmaps ? this.getSectionBasedHeight(bchunk, 64) : 0;
            for (int blockX = 0; blockX < 16; ++blockX) {
                for (int blockZ = 0; blockZ < 16; ++blockZ) {
                    tile = this.loadBlockColor(playerYi, world, blockX, blockZ, bchunk, tileX, tileZ, insideX, insideZ, sectionBasedHeight, typeWorldSurface, oldTile, mchunk, topChunk, topLeftChunk, leftChunk, shadowR, shadowG, shadowB, sameCaveLevel, sameHighlights, canvasX, canvasZ, !needsHighlights ? 0 : highlights[blockZ << 4 | blockX], settingsChanged, loadingCaving, loadingLevels, loadingLighting, loadingSingleLevelBrightness, loadingTerrainSlopes, loadingTerrainDepth, pixelTransparentSizes, pixelBlockStates, pixelBlockLights, underRed, underGreen, underBlue, postBrightness, brightness, red, green, blue, tempColor, loadingIgnoreHeightmaps, loadingCaveMapsDepth, mutableBlockPos, mutableBlockPos2, loadingSlimeSeed, loadedLevels, modMain, helper, loadingColours, loadingRedstone, loadingTransparency, loadingLightOverlayType, loadingLightOverlayMaxLight, loadingLightOverlayMinLight, loadingLightOverlayColor, loadingFlowers, adjustHeightForCarpetLikeBlocks, loadingStainedGlass, loadingLegibleCaveMode, mutableBlockPos3, framedBlocksExist);
                }
            }
            tile.setHasTerrain(true);
        }
        tile.caveLevel = loadingCaving;
        tile.setHighlights(highlights);
        tile.setHighlightVersion(this.loadingHighlightVersion);
        if (!chunkIsClean) {
            long chunkPosLong = Misc.getChunkPosAsLong(bchunk);
            if (!this.detectedChunkChanges.contains(chunkPosLong)) {
                this.detectedChunkChanges.add(chunkPosLong);
            }
            try {
                XaeroMinimapCore.chunkCleanField.set(bchunk, true);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public MinimapTile loadBlockColor(int playerYi, ClientLevel world, int insideX, int insideZ, LevelChunk bchunk, int tileX, int tileZ, int tileInsideX, int tileInsideZ, int sectionBasedHeight, Heightmap.Types typeWorldSurface, MinimapTile oldTile, MinimapChunk mchunk, MinimapChunk topChunk, MinimapChunk topLeftChunk, MinimapChunk leftChunk, float shadowR, float shadowG, float shadowB, boolean sameCaveLevel, boolean sameHighlights, int canvasX, int canvasZ, int highlight, boolean settingsChanged, int loadingCaving, int loadingLevels, boolean loadingLighting, float loadingSingleLevelBrightness, int loadingTerrainSlopes, boolean loadingTerrainDepth, List<Integer> pixelTransparentSizes, List<BlockState> pixelBlockStates, List<Integer> pixelBlockLights, int[] underRed, int[] underGreen, int[] underBlue, float[] postBrightness, float[] brightness, int[] red, int[] green, int[] blue, int[] tempColor, boolean loadingIgnoreHeightmaps, int loadingCaveMapsDepth, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, Long loadingSlimeSeed, int loadedLevels, IXaeroMinimap modMain, MinimapWriterHelper helper, int loadingColours, boolean loadingRedstone, boolean loadingTransparency, int loadingLightOverlayType, int loadingLightOverlayMaxLight, int loadingLightOverlayMinLight, int loadingLightOverlayColor, boolean loadingFlowers, boolean adjustHeightForCarpetLikeBlocks, boolean loadingStainedGlass, boolean loadingLegibleCaveMode, BlockPos.MutableBlockPos mutableBlockPos3, boolean framedBlocksExist) {
        int i;
        boolean reuseColour;
        LevelChunk prevChunk2;
        MinimapTile tile;
        BlockState state;
        int bottom;
        int lowY;
        int highY;
        int worldBottomY = world.getMinY();
        if (loadingCaving != Integer.MAX_VALUE) {
            highY = loadingCaving;
        } else {
            int height = bchunk.getHeight(typeWorldSurface, insideX, insideZ);
            highY = loadingIgnoreHeightmaps || height < worldBottomY ? sectionBasedHeight : height;
        }
        if (highY >= world.getMaxY()) {
            highY = world.getMaxY() - 1;
        }
        if ((lowY = (bottom = loadingCaving != Integer.MAX_VALUE ? highY + 1 - loadingCaveMapsDepth : worldBottomY)) < worldBottomY) {
            lowY = worldBottomY;
        }
        pixelTransparentSizes.clear();
        pixelBlockStates.clear();
        pixelBlockLights.clear();
        this.currentComparisonCode = 0L;
        byte currentComparisonCodeAdd = 0;
        byte currentComparisonCodeAdd2 = 0;
        this.blockY = 0;
        for (int i2 = 0; i2 < loadingLevels; ++i2) {
            underRed[i2] = 0;
            underGreen[i2] = 0;
            underBlue[i2] = 0;
        }
        this.currentTransparencyMultiplier = 1.0f;
        this.sun = 15;
        this.blockColor = 0;
        this.isglowing = false;
        double secondaryBR = 1.0;
        double secondaryBG = 1.0;
        double secondaryBB = 1.0;
        Block block = this.findBlock((Level)world, bchunk, insideX, insideZ, highY, lowY, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass, mutableBlockPos3, framedBlocksExist);
        BlockState blockState = state = pixelBlockStates.isEmpty() ? null : pixelBlockStates.get(pixelBlockStates.size() - 1);
        if (adjustHeightForCarpetLikeBlocks && state != null && this.blockStateShortShapeCache.isShort(state)) {
            --this.blockY;
        }
        boolean isglowing = this.isglowing;
        int blockY = this.blockY;
        long currentComparisonCode = this.currentComparisonCode;
        boolean success = true;
        int prevHeight = Integer.MAX_VALUE;
        int prevHeightDiagonal = Integer.MAX_VALUE;
        int prevInsideX = insideX - 1;
        int prevInsideZ = insideZ - 1;
        boolean xEdge = prevInsideX < 0;
        boolean zEdge = prevInsideZ < 0;
        MinimapTile prevHeightSrc = tile = mchunk.getTile(tileInsideX, tileInsideZ);
        MinimapTile prevHeightDiagonalSrc = tile;
        if (zEdge) {
            prevInsideZ = 15;
            if (tileInsideZ > 0) {
                prevHeightSrc = mchunk.getTile(tileInsideX, tileInsideZ - 1);
            } else if (topChunk != null) {
                prevHeightSrc = topChunk.getTile(tileInsideX, 3);
            }
        }
        if (xEdge) {
            prevInsideX = 15;
            if (zEdge) {
                if (tileInsideZ > 0 && tileInsideX > 0) {
                    prevHeightDiagonalSrc = mchunk.getTile(tileInsideX - 1, tileInsideZ - 1);
                } else if (tileInsideX == 0 && tileInsideZ == 0) {
                    if (topLeftChunk != null) {
                        prevHeightDiagonalSrc = topLeftChunk.getTile(3, 3);
                    }
                } else if (tileInsideX == 0) {
                    if (leftChunk != null) {
                        prevHeightDiagonalSrc = leftChunk.getTile(3, tileInsideZ - 1);
                    }
                } else if (topChunk != null) {
                    prevHeightDiagonalSrc = topChunk.getTile(tileInsideX - 1, 3);
                }
            } else if (tileInsideX > 0) {
                prevHeightDiagonalSrc = mchunk.getTile(tileInsideX - 1, tileInsideZ);
            } else if (leftChunk != null) {
                prevHeightDiagonalSrc = leftChunk.getTile(3, tileInsideZ);
            }
        } else {
            prevHeightDiagonalSrc = prevHeightSrc;
        }
        if (prevHeightSrc != null && (prevHeightSrc == tile || prevHeightSrc.hasTerrain())) {
            prevHeight = prevHeightSrc.getHeight(insideX, prevInsideZ);
            if (prevHeightSrc != tile && prevHeightSrc.caveLevel != loadingCaving) {
                success = false;
            }
        } else if (zEdge) {
            prevHeight = blockY;
            if (pixelTransparentSizes.isEmpty()) {
                try {
                    prevChunk2 = world.getChunk(tileX, tileZ - 1);
                    if (prevChunk2 != null) {
                        prevHeight = prevChunk2.getHeight(typeWorldSurface, insideX, prevInsideZ);
                    }
                }
                catch (IllegalStateException prevChunk2) {
                    // empty catch block
                }
            }
            success = false;
        }
        if (prevHeightDiagonalSrc != null && (prevHeightDiagonalSrc == tile || prevHeightDiagonalSrc.hasTerrain())) {
            prevHeightDiagonal = prevHeightDiagonalSrc.getHeight(prevInsideX, prevInsideZ);
            if (prevHeightDiagonalSrc != tile && prevHeightDiagonalSrc.caveLevel != loadingCaving) {
                success = false;
            }
        } else if (xEdge || zEdge) {
            prevHeightDiagonal = blockY;
            if (pixelTransparentSizes.isEmpty()) {
                try {
                    LevelChunk levelChunk = xEdge && zEdge ? world.getChunk(tileX - 1, tileZ - 1) : (prevChunk2 = zEdge ? world.getChunk(tileX, tileZ - 1) : world.getChunk(tileX - 1, tileZ));
                    if (prevChunk2 != null) {
                        prevHeightDiagonal = prevChunk2.getHeight(typeWorldSurface, prevInsideX, prevInsideZ);
                    }
                }
                catch (IllegalStateException prevChunk3) {
                    // empty catch block
                }
            }
            success = false;
        }
        int verticalSlope = 0;
        int diagonalSlope = 0;
        if (loadingTerrainSlopes > 0) {
            if (prevHeight != Integer.MAX_VALUE) {
                verticalSlope = Math.max(-128, Math.min(127, blockY - prevHeight));
            }
            if (prevHeightDiagonal != Integer.MAX_VALUE) {
                diagonalSlope = Math.max(-128, Math.min(127, blockY - prevHeightDiagonal));
            }
        }
        for (int i3 = 0; i3 < pixelBlockLights.size(); ++i3) {
            int l = pixelBlockLights.get(i3);
            if (i3 <= 1) {
                currentComparisonCodeAdd = (byte)(currentComparisonCodeAdd | l << 4 * i3 + 1);
            }
            if (i3 < 1) continue;
            currentComparisonCode |= (long)(l << 4 * (i3 - 1) >> 3);
        }
        int add2Calculation = 17;
        for (int i4 = 0; i4 < pixelTransparentSizes.size(); ++i4) {
            add2Calculation = add2Calculation * 37 + pixelTransparentSizes.get(i4);
        }
        currentComparisonCodeAdd = (byte)(currentComparisonCodeAdd | add2Calculation >> 8 & 1);
        currentComparisonCodeAdd2 = (byte)add2Calculation;
        boolean bl = reuseColour = !settingsChanged && sameCaveLevel && sameHighlights && !oldTile.pixelChanged(insideX, insideZ, currentComparisonCode, currentComparisonCodeAdd, currentComparisonCodeAdd2, (byte)verticalSlope, (byte)diagonalSlope);
        if (!reuseColour) {
            int i5;
            boolean hasTransparentLayer;
            if (highlight != 0 && block == null) {
                this.sun = 0;
            }
            int firstSun = this.sun;
            boolean bl2 = hasTransparentLayer = highlight != 0 || !pixelTransparentSizes.isEmpty();
            if (hasTransparentLayer && firstSun != 15) {
                this.sun = 15;
            }
            if (highlight != 0) {
                int hlRed = highlight >> 8 & 0xFF;
                int hlGreen = highlight >> 16 & 0xFF;
                int hlBlue = highlight >> 24 & 0xFF;
                int hlAlpha = highlight & 0xFF;
                float hlAlphaFloat = (float)hlAlpha / 255.0f;
                this.applyTransparentLayer(hlRed, hlGreen, hlBlue, hlAlphaFloat, true);
            }
            boolean legibleCaveMode = loadingLegibleCaveMode && loadingCaving != Integer.MAX_VALUE;
            this.calculateBlockColors(world, bchunk, insideX, insideZ, mutableBlockPos2, pixelTransparentSizes, pixelBlockStates, pixelBlockLights, loadingColours, loadingLightOverlayColor, loadingCaving, loadingLevels, loadingLighting, loadingSingleLevelBrightness, legibleCaveMode);
            int blockColor = this.blockColor;
            float currentTransparencyMultiplier = this.currentTransparencyMultiplier;
            int sun = this.sun;
            if (block == null) {
                sun = 15;
            }
            isglowing = block != null && isglowing;
            int topLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(0);
            int cr = blockColor >> 16 & 0xFF;
            int cg = blockColor >> 8 & 0xFF;
            int cb = blockColor & 0xFF;
            if (isglowing) {
                helper.getGlowingColour(cr, cg, cb, tempColor);
                cr = tempColor[0];
                cg = tempColor[1];
                cb = tempColor[2];
                if (hasTransparentLayer && pixelTransparentSizes.isEmpty()) {
                    topLight = 15;
                }
            }
            if (!isglowing || hasTransparentLayer) {
                int blockLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(pixelBlockLights.size() - 1);
                int firstBlockY = this.firstBlockY;
                for (i5 = 0; i5 < loadingLevels; ++i5) {
                    postBrightness[i5] = 1.0f;
                    if (legibleCaveMode) {
                        if (!isglowing) {
                            float f = brightness[i5] = block == null ? 1.0f : (1.0f + (float)blockY - (float)bottom) / (float)(1 + highY - bottom);
                        }
                        if (!hasTransparentLayer) continue;
                        float transparentLayerCaveBrightness = block == null && pixelTransparentSizes.isEmpty() ? this.getFixedSkyLightBlockBrightness(9.0f, 0.0f, 0) : (1.0f + (float)firstBlockY - (float)bottom) / (float)(1 + highY - bottom);
                        int n = i5;
                        underRed[n] = (int)((float)underRed[n] * transparentLayerCaveBrightness);
                        int n2 = i5;
                        underGreen[n2] = (int)((float)underGreen[n2] * transparentLayerCaveBrightness);
                        int n3 = i5;
                        underBlue[n3] = (int)((float)underBlue[n3] * transparentLayerCaveBrightness);
                        continue;
                    }
                    if (!isglowing) {
                        brightness[i5] = !hasTransparentLayer ? (block == null ? 1.0f : (loadingLevels != 1 ? this.getBlockBrightness(9.0f, sun, i5, blockLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, blockLight))) : this.getBlockBrightness(9.0f, sun, 0, blockLight);
                    }
                    if (!hasTransparentLayer) continue;
                    postBrightness[i5] = loadingLevels != 1 ? this.getBlockBrightness(9.0f, firstSun, i5, topLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, topLight);
                }
            }
            float depthBrightness = 1.0f;
            if (block != null && !isglowing && loadingTerrainDepth && !legibleCaveMode) {
                float min;
                depthBrightness = loadingCaving != Integer.MAX_VALUE ? 0.7f + 0.3f * (float)(blockY - bottom) / (float)(highY - bottom) : (float)blockY / 63.0f;
                float max = loadingTerrainSlopes >= 2 ? 1.0f : 1.15f;
                float f = min = loadingTerrainSlopes >= 2 ? 0.9f : 0.7f;
                if (depthBrightness > max) {
                    depthBrightness = max;
                } else if (depthBrightness < min) {
                    depthBrightness = min;
                }
            }
            if (block != null && loadingTerrainSlopes > 0) {
                if (loadingTerrainSlopes == 1) {
                    if (!isglowing) {
                        if (verticalSlope > 0) {
                            depthBrightness = (float)((double)depthBrightness * 1.15);
                        } else if (verticalSlope < 0) {
                            depthBrightness = (float)((double)depthBrightness * 0.85);
                        }
                    }
                } else {
                    float ambientLightColored = 0.2f;
                    float ambientLightWhite = 0.5f;
                    float maxDirectLight = 0.6666667f;
                    if (isglowing) {
                        ambientLightColored = 0.0f;
                        ambientLightWhite = 1.0f;
                        maxDirectLight = 0.22222224f;
                    }
                    float cos = 0.0f;
                    if (loadingTerrainSlopes == 2) {
                        float crossZ = -verticalSlope;
                        if (crossZ < 1.0f) {
                            if (verticalSlope == 1 && diagonalSlope == 1) {
                                cos = 1.0f;
                            } else {
                                float crossX = verticalSlope - diagonalSlope;
                                float cast = 1.0f - crossZ;
                                float crossMagnitude = (float)Math.sqrt(crossX * crossX + 1.0f + crossZ * crossZ);
                                cos = (float)((double)(cast / crossMagnitude) / Math.sqrt(2.0));
                            }
                        }
                    } else if (verticalSlope >= 0) {
                        if (verticalSlope == 1) {
                            cos = 1.0f;
                        } else {
                            float surfaceDirectionMagnitude = (float)Math.sqrt(verticalSlope * verticalSlope + 1);
                            float castToMostLit = verticalSlope + 1;
                            cos = (float)((double)(castToMostLit / surfaceDirectionMagnitude) / Math.sqrt(2.0));
                        }
                    }
                    float directLightClamped = 0.0f;
                    if (cos == 1.0f) {
                        directLightClamped = maxDirectLight;
                    } else if (cos > 0.0f) {
                        directLightClamped = (float)Math.ceil(cos * 10.0f) / 10.0f * maxDirectLight * 0.88388f;
                    }
                    float whiteLight = ambientLightWhite + directLightClamped;
                    secondaryBR *= (double)(shadowR * ambientLightColored + whiteLight);
                    secondaryBG *= (double)(shadowG * ambientLightColored + whiteLight);
                    secondaryBB *= (double)(shadowB * ambientLightColored + whiteLight);
                }
            }
            secondaryBR *= (double)depthBrightness;
            secondaryBG *= (double)depthBrightness;
            secondaryBB *= (double)depthBrightness;
            if (loadingLightOverlayType > 0) {
                int testLight;
                int blockLight;
                int n = blockLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(0);
                int n4 = loadingLightOverlayType == 1 ? blockLight : (testLight = loadingLightOverlayType == 2 ? firstSun : Math.max(blockLight, firstSun));
                if (testLight >= loadingLightOverlayMinLight && testLight <= loadingLightOverlayMaxLight) {
                    int overlayColor = MinimapConfigConstants.COLORS[loadingLightOverlayColor];
                    int overlayRed = (overlayColor >> 16 & 0xFF) * 2 / 3;
                    int overlayGreen = (overlayColor >> 8 & 0xFF) * 2 / 3;
                    int overlayBlue = (overlayColor & 0xFF) * 2 / 3;
                    int i6 = 0;
                    while (i6 < loadingLevels) {
                        float destColorScale = (isglowing ? 1.0f : postBrightness[i6]) / 3.0f;
                        int n5 = i6;
                        underRed[n5] = (int)((float)underRed[n5] * destColorScale);
                        int n6 = i6;
                        underGreen[n6] = (int)((float)underGreen[n6] * destColorScale);
                        int n7 = i6;
                        underBlue[n7] = (int)((float)underBlue[n7] * destColorScale);
                        int n8 = i6;
                        brightness[n8] = brightness[n8] * destColorScale;
                        postBrightness[i6] = 1.0f;
                        int n9 = i6;
                        underRed[n9] = underRed[n9] + overlayRed;
                        int n10 = i6;
                        underGreen[n10] = underGreen[n10] + overlayGreen;
                        int n11 = i6++;
                        underBlue[n11] = underBlue[n11] + overlayBlue;
                    }
                    if (isglowing) {
                        secondaryBR /= 3.0;
                        secondaryBG /= 3.0;
                        secondaryBB /= 3.0;
                    }
                }
            }
            for (i5 = 0; i5 < loadingLevels; ++i5) {
                float b;
                if (isglowing) {
                    b = 1.0f;
                    if (!hasTransparentLayer) {
                        postBrightness[i5] = 1.0f;
                    }
                } else {
                    b = brightness[i5];
                }
                red[i5] = (int)(((double)((float)cr * b) * secondaryBR * (double)currentTransparencyMultiplier + (double)underRed[i5]) * (double)postBrightness[i5]);
                if (red[i5] > 255) {
                    red[i5] = 255;
                }
                green[i5] = (int)(((double)((float)cg * b) * secondaryBG * (double)currentTransparencyMultiplier + (double)underGreen[i5]) * (double)postBrightness[i5]);
                if (green[i5] > 255) {
                    green[i5] = 255;
                }
                blue[i5] = (int)(((double)((float)cb * b) * secondaryBB * (double)currentTransparencyMultiplier + (double)underBlue[i5]) * (double)postBrightness[i5]);
                if (blue[i5] <= 255) continue;
                blue[i5] = 255;
            }
        } else {
            for (i = 0; i < loadingLevels; ++i) {
                red[i] = oldTile.getRed(i, insideX, insideZ);
                green[i] = oldTile.getGreen(i, insideX, insideZ);
                blue[i] = oldTile.getBlue(i, insideX, insideZ);
            }
        }
        if (tile == null) {
            tile = MinimapTile.getANewTile(modMain.getSettings(), tileX, tileZ, loadingSlimeSeed);
            mchunk.setTile(tileInsideX, tileInsideZ, tile);
        }
        if (this.notEmptyColor(red, green, blue)) {
            tile.setHasSomething(true);
            mchunk.setHasSomething(true);
        }
        tile.setHeight(insideX, insideZ, blockY);
        tile.setCode(insideX, insideZ, currentComparisonCode, currentComparisonCodeAdd, currentComparisonCodeAdd2, (byte)verticalSlope, (byte)diagonalSlope);
        if (tile.isSuccess()) {
            tile.setSuccess(success);
        }
        if (oldTile != null) {
            int oldTileDarkestLevel = loadedLevels - 1;
            int tileDarkestLevel = loadingLevels - 1;
            if (oldTile.getRed(oldTileDarkestLevel, insideX, insideZ) != red[tileDarkestLevel] || oldTile.getGreen(oldTileDarkestLevel, insideX, insideZ) != green[tileDarkestLevel] || oldTile.getBlue(oldTileDarkestLevel, insideX, insideZ) != blue[tileDarkestLevel]) {
                mchunk.setChanged(true);
            }
        } else {
            mchunk.setChanged(true);
        }
        for (i = 0; i < loadingLevels; ++i) {
            tile.setRGB(i, insideX, insideZ, red[i], green[i], blue[i]);
        }
        return tile;
    }

    private BlockState unpackFramedBlocks(BlockState original, Level world, BlockPos globalPos) {
        BlockEntity tileEntity;
        if (original.getBlock() instanceof AirBlock) {
            return original;
        }
        BlockState result = original;
        SupportMods supportMods = this.modMain.getSupportMods();
        if (supportMods.supportFramedBlocks.isFrameBlock(world, null, original) && (tileEntity = world.getBlockEntity(globalPos)) != null && ((result = supportMods.supportFramedBlocks.unpackFramedBlock(world, null, original, tileEntity)) == null || result.getBlock() instanceof AirBlock)) {
            result = original;
        }
        return result;
    }

    public Block findBlock(Level world, LevelChunk bchunk, int insideX, int insideZ, int highY, int lowY, int loadingCaving, boolean loadingRedstone, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, int loadingColours, boolean loadingTransparency, List<Integer> pixelBlockLights, List<BlockState> pixelBlockStates, int loadingLevels, boolean loadingLighting, List<Integer> pixelTransparentSizes, boolean loadingFlowers, boolean loadingStainedGlass, BlockPos.MutableBlockPos mutableBlockPos3, boolean framedBlocksExist) {
        this.underair = loadingCaving == Integer.MAX_VALUE;
        this.previousTransparentState = null;
        if (highY == Integer.MAX_VALUE || lowY == Integer.MAX_VALUE) {
            return null;
        }
        boolean shouldExtendTillTheBottom = false;
        int transparentSkipY = 0;
        int i = highY;
        while (i >= lowY) {
            block14: {
                BlockState state;
                block13: {
                    mutableBlockPos.set(insideX, i, insideZ);
                    BlockPos.MutableBlockPos globalPos = mutableBlockPos2.set((bchunk.getPos().x() << 4) + insideX, i, (bchunk.getPos().z() << 4) + insideZ);
                    state = bchunk.getBlockState((BlockPos)mutableBlockPos);
                    if (state == null) {
                        state = Blocks.AIR.defaultBlockState();
                    }
                    if (framedBlocksExist) {
                        state = this.unpackFramedBlocks(state, world, (BlockPos)globalPos);
                    }
                    FluidState fluidFluidState = state.getFluidState();
                    boolean bl = shouldExtendTillTheBottom = !shouldExtendTillTheBottom && !pixelBlockStates.isEmpty() && this.firstTransparentStateY - i >= 5;
                    if (shouldExtendTillTheBottom) {
                        for (transparentSkipY = i - 1; transparentSkipY >= lowY; --transparentSkipY) {
                            FluidState traceFluidState;
                            BlockState traceState = bchunk.getBlockState((BlockPos)mutableBlockPos3.set(insideX, transparentSkipY, insideZ));
                            if (traceState == null) {
                                traceState = Blocks.AIR.defaultBlockState();
                            }
                            if (!(traceFluidState = traceState.getFluidState()).isEmpty()) {
                                if (!this.isTransparent((StateHolder<?, ?>)traceFluidState)) break;
                                if (!(traceState.getBlock() instanceof AirBlock) && traceState.getBlock() == this.fluidToBlock.apply(traceFluidState).getBlock()) continue;
                            }
                            if (!this.isTransparent((StateHolder<?, ?>)traceState)) break;
                        }
                    }
                    if (fluidFluidState.isEmpty()) break block13;
                    this.underair = true;
                    BlockState fluidState = this.fluidToBlock.apply(fluidFluidState);
                    if (this.findBlockHelp(world, (ChunkAccess)bchunk, insideX, i, insideZ, fluidState, fluidFluidState, transparentSkipY, shouldExtendTillTheBottom, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass)) {
                        return fluidState.getBlock();
                    }
                    if (!(state.getBlock() instanceof AirBlock) && state.getBlock() == this.fluidToBlock.apply(fluidFluidState).getBlock()) break block14;
                }
                if ((state.getBlock() instanceof AirBlock || this.underair) && this.findBlockHelp(world, (ChunkAccess)bchunk, insideX, i, insideZ, state, null, transparentSkipY, shouldExtendTillTheBottom, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass)) {
                    return state.getBlock();
                }
            }
            i = shouldExtendTillTheBottom ? transparentSkipY : i - 1;
        }
        return null;
    }

    private boolean findBlockHelp(Level world, ChunkAccess bchunk, int insideX, int i, int insideZ, BlockState state, FluidState fluidFluidState, int transparentSkipY, boolean shouldExtendTillTheBottom, int loadingCaving, boolean loadingRedstone, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, int loadingColours, boolean loadingTransparency, List<Integer> pixelBlockLights, List<BlockState> pixelBlockStates, int loadingLevels, boolean loadingLighting, List<Integer> pixelTransparentSizes, boolean loadingFlowers, boolean loadingStainedGlass) {
        Block got = state.getBlock();
        if (!(got instanceof AirBlock)) {
            boolean isTransparent;
            boolean isFlower;
            boolean isRedstone = false;
            if (!(got instanceof LiquidBlock) && state.getRenderShape() == RenderShape.INVISIBLE) {
                return false;
            }
            if (got == Blocks.TORCH) {
                return false;
            }
            if (got == Blocks.SHORT_GRASS) {
                return false;
            }
            if (got == Blocks.GLASS || got == Blocks.GLASS_PANE || !loadingStainedGlass && (got instanceof StainedGlassBlock || got instanceof StainedGlassPaneBlock)) {
                return false;
            }
            boolean bl = isFlower = got instanceof PitcherCropBlock || got instanceof TallFlowerBlock || got instanceof FlowerBlock || state.is(BlockTags.FLOWERS) && !state.is(BlockTags.LEAVES);
            if (got instanceof DoublePlantBlock && !isFlower) {
                return false;
            }
            if (isFlower && !loadingFlowers) {
                return false;
            }
            boolean bl2 = isRedstone = got == Blocks.REDSTONE_TORCH || got == Blocks.REDSTONE_WIRE || got instanceof RepeaterBlock || got instanceof ComparatorBlock;
            if (!loadingRedstone && isRedstone) {
                return false;
            }
            if (this.buggedStates.contains(state)) {
                return false;
            }
            this.blockY = i;
            BlockPos.MutableBlockPos globalPos = mutableBlockPos2;
            MapColor mapColor = null;
            try {
                mapColor = state.getMapColor((BlockGetter)world, (BlockPos)globalPos);
            }
            catch (Throwable t) {
                this.buggedStates.add(state);
                MinimapLogs.LOGGER.info("Broken vanilla map color definition found: " + String.valueOf(((Registry)world.registryAccess().lookup(Registries.BLOCK).get()).getKey((Object)got)));
            }
            boolean bl3 = loadingTransparency && (state == this.previousTransparentState || this.isTransparent((StateHolder<?, ?>)(fluidFluidState == null ? state : fluidFluidState))) ? true : (isTransparent = false);
            if (!((isTransparent || isRedstone) && loadingColours != 1 || mapColor != null && mapColor.col != 0)) {
                return false;
            }
            if (!this.underair) {
                return !isTransparent;
            }
            BlockPos.MutableBlockPos lightPos = mutableBlockPos.set(globalPos.getX(), globalPos.getY() + 1, globalPos.getZ());
            if (this.currentComparisonCode == 0L) {
                this.firstBlockY = i;
                if (loadingLighting && loadingCaving != Integer.MAX_VALUE) {
                    this.sun = world.getBrightness(LightLayer.SKY, (BlockPos)lightPos);
                }
            }
            if (loadingTransparency && isTransparent) {
                if (!shouldExtendTillTheBottom && pixelBlockStates.size() < 5 && state != this.previousTransparentState) {
                    if (pixelBlockStates.isEmpty()) {
                        this.firstTransparentStateY = i;
                    }
                    this.currentComparisonCode += (long)Block.getId((BlockState)state) & 0xFFFFFFFFL;
                    pixelBlockStates.add(state);
                    pixelTransparentSizes.add(1);
                    pixelBlockLights.add(!loadingLighting ? 0 : world.getBrightness(LightLayer.BLOCK, (BlockPos)lightPos));
                    this.previousTransparentState = state;
                } else {
                    int depthToAdd = 1;
                    if (shouldExtendTillTheBottom) {
                        depthToAdd = i - transparentSkipY;
                    }
                    pixelTransparentSizes.set(pixelTransparentSizes.size() - 1, pixelTransparentSizes.get(pixelTransparentSizes.size() - 1) + depthToAdd);
                }
                return false;
            }
            this.currentComparisonCode += (long)Block.getId((BlockState)state) & 0xFFFFFFFFL;
            this.currentComparisonCode <<= 29;
            this.currentComparisonCode |= ((long)i & 0xFFFL) << 17;
            pixelBlockLights.add(!loadingLighting ? 0 : world.getBrightness(LightLayer.BLOCK, (BlockPos)lightPos));
            pixelBlockStates.add(state);
            this.isglowing = this.isGlowing(state, world, (BlockPos)globalPos);
            return true;
        }
        if (got instanceof AirBlock) {
            this.underair = true;
        }
        return false;
    }

    private void calculateBlockColors(ClientLevel world, LevelChunk bchunk, int insideX, int insideZ, BlockPos.MutableBlockPos mutableBlockPos2, List<Integer> pixelTransparentSizes, List<BlockState> pixelBlockStates, List<Integer> pixelBlockLights, int loadingColours, int loadingLightOverlayColor, int loadingCaving, int loadingLevels, boolean loadingLighting, float loadingSingleLevelBrightness, boolean legibleCaveMaps) {
        int firstBlockY = this.firstBlockY;
        BlockPos.MutableBlockPos globalPos = mutableBlockPos2.set(bchunk.getPos().x() * 16 + insideX, firstBlockY, bchunk.getPos().z() * 16 + insideZ);
        if (!pixelTransparentSizes.isEmpty()) {
            for (int i = 0; i < pixelTransparentSizes.size(); ++i) {
                BlockState state = pixelBlockStates.get(i);
                Block b = state.getBlock();
                int size = pixelTransparentSizes.get(i);
                int opacity = state.getLightDampening();
                this.applyTransparentLayer(world, bchunk, b, state, opacity * size, (BlockPos)globalPos, pixelBlockLights.get(i), loadingLighting, loadingSingleLevelBrightness, legibleCaveMaps);
                int nextY = globalPos.getY() - size;
                globalPos.setY(nextY);
            }
        }
        if (pixelBlockStates.size() > pixelTransparentSizes.size()) {
            int color;
            BlockState state = pixelBlockStates.get(pixelBlockStates.size() - 1);
            Block b = state.getBlock();
            if (loadingColours == 1) {
                MapColor minimapColor = state.getMapColor((BlockGetter)world, (BlockPos)globalPos);
                color = minimapColor.col;
            } else {
                color = this.textureColors.getBlockTextureColor(state, true, world, this.blockRegistry, (BlockPos)globalPos);
            }
            this.blockColor = this.addBlockColorMultipliers(color, state, (Level)world, (BlockPos)globalPos);
        } else {
            this.blockColor = loadingCaving != Integer.MAX_VALUE ? 0 : -16121833;
        }
    }

    private boolean isTransparent(StateHolder<?, ?> state) {
        return this.transparentCache.apply(state);
    }

    private boolean isGlowing(BlockState state, Level world, BlockPos pos) {
        Boolean cachedValue = this.glowingCache.get(state);
        if (cachedValue != null) {
            return cachedValue;
        }
        boolean isGlowing = false;
        try {
            isGlowing = this.getBlockStateLightEmission(state, world, pos) > 0;
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.glowingCache.put(state, isGlowing);
        return isGlowing;
    }

    private void applyTransparentLayer(ClientLevel world, LevelChunk bchunk, Block b, BlockState state, int opacity, BlockPos globalPos, int blockLight, boolean lighting, float loadingSingleLevelBrightness, boolean legibleCaveMaps) {
        int color;
        float vanillaTransparency;
        int red = 0;
        int green = 0;
        int blue = 0;
        float f = b instanceof LiquidBlock ? 0.75f : (vanillaTransparency = b instanceof IceBlock ? 0.85f : 0.5f);
        if (this.loadingColours == 0) {
            color = this.textureColors.getBlockTextureColor(state, true, world, this.blockRegistry, globalPos);
        } else {
            color = state.getMapColor((BlockGetter)world, (BlockPos)globalPos).col;
            color = (int)(255.0f * vanillaTransparency) << 24 | color & 0xFFFFFF;
        }
        color = this.addBlockColorMultipliers(color, state, (Level)world, globalPos);
        red = color >> 16 & 0xFF;
        green = color >> 8 & 0xFF;
        blue = color & 0xFF;
        float transparency = (float)(color >> 24 & 0xFF) / 255.0f;
        if (transparency == 0.0f) {
            transparency = vanillaTransparency;
        }
        if (this.isGlowing(state, bchunk.getLevel(), globalPos)) {
            this.helper.getGlowingColour(red, green, blue, this.tempColor);
            red = this.tempColor[0];
            green = this.tempColor[1];
            blue = this.tempColor[2];
        }
        float brightness = legibleCaveMaps ? 1.0f : (lighting ? this.getBlockBrightness(9.0f, this.sun, 0, blockLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, blockLight));
        this.applyTransparentLayer(red, green, blue, transparency * brightness, false);
        this.sun -= opacity;
        if (this.sun < 0) {
            this.sun = 0;
        }
    }

    private void applyTransparentLayer(int red, int green, int blue, float transparency, boolean premultiplied) {
        float overlayIntensity = this.currentTransparencyMultiplier * (premultiplied ? 1.0f : transparency);
        int i = 0;
        while (i < this.loadingLevels) {
            int n = i;
            this.underRed[n] = (int)((float)this.underRed[n] + (float)red * overlayIntensity);
            int n2 = i;
            this.underGreen[n2] = (int)((float)this.underGreen[n2] + (float)green * overlayIntensity);
            int n3 = i++;
            this.underBlue[n3] = (int)((float)this.underBlue[n3] + (float)blue * overlayIntensity);
        }
        this.currentTransparencyMultiplier *= 1.0f - transparency;
    }

    private int getBlockTint(BlockState state, Level world, BlockPos pos) {
        BlockTintSource tintSource;
        int tintIndex = ClientOnlyServices.PLATFORM.getBlockUtils().getTextureColorUtils().getBlockTintIndex(state);
        if (tintIndex == -1) {
            return -1;
        }
        if (state.getBlock() instanceof LiquidBlock) {
            FluidState fluidState = state.getFluidState();
            if (fluidState.isEmpty()) {
                return -1;
            }
            tintSource = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState).tintSource();
        } else {
            tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, tintIndex);
        }
        if (tintSource == null) {
            return -1;
        }
        return tintSource.colorInWorld(state, (BlockAndTintGetter)this.biomeBlendCalculator, pos);
    }

    @Deprecated
    protected List<BakedQuad> getQuads(BlockStateModel model, Level world, BlockPos pos, BlockState state, Direction direction) {
        return null;
    }

    @Deprecated
    protected TextureAtlasSprite getParticleIcon(BlockStateModelSet bms, BlockStateModel model, Level world, BlockPos pos, BlockState state) {
        return null;
    }

    @Deprecated
    private int loadBlockColourFromTexture(ClientLevel world, BlockState state, Block b, BlockPos pos, boolean convert) {
        List<BakedQuad> upQuads = null;
        BlockStateModelSet bms = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        BlockStateModel model = bms.get(state);
        if (convert) {
            upQuads = this.getQuads(model, (Level)world, pos, state, Direction.UP);
        }
        TextureAtlasSprite missingTexture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(MissingTextureAtlasSprite.getLocation());
        if (upQuads == null || upQuads.isEmpty() || ((BakedQuad)upQuads.getFirst()).materialInfo().sprite() == missingTexture) {
            TextureAtlasSprite texture = this.getParticleIcon(bms, model, (Level)world, pos, state);
            int tintIndex = 0;
            if (texture == missingTexture) {
                for (int i = Direction.values().length - 1; i >= 0; --i) {
                    List<BakedQuad> quads;
                    if (i == 1 || (quads = this.getQuads(model, (Level)world, pos, state, Direction.values()[i])).isEmpty()) continue;
                    texture = ((BakedQuad)quads.getFirst()).materialInfo().sprite();
                    tintIndex = ((BakedQuad)quads.getFirst()).materialInfo().tintIndex();
                    if (texture == missingTexture) {
                        continue;
                    }
                    break;
                }
            }
        } else {
            TextureAtlasSprite texture = ((BakedQuad)upQuads.getFirst()).materialInfo().sprite();
            int tintIndex = ((BakedQuad)upQuads.getFirst()).materialInfo().tintIndex();
        }
        return -1;
    }

    private int addBlockColorMultipliers(int c, BlockState state, Level world, BlockPos pos) {
        if (this.loadingColours == 1 && !this.loadingBiomesVanillaMode) {
            return c;
        }
        int grassColor = 0xFFFFFF;
        try {
            grassColor = this.getBlockTint(state, world, pos);
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("suppressed exception", t);
        }
        if (grassColor != -1 && grassColor != 0xFFFFFF) {
            float rMultiplier = (float)(c >> 16 & 0xFF) / 255.0f;
            float gMultiplier = (float)(c >> 8 & 0xFF) / 255.0f;
            float bMultiplier = (float)(c & 0xFF) / 255.0f;
            int red = (int)((float)(grassColor >> 16 & 0xFF) * rMultiplier);
            int green = (int)((float)(grassColor >> 8 & 0xFF) * gMultiplier);
            int blue = (int)((float)(grassColor & 0xFF) * bMultiplier);
            c = c & 0xFF000000 | red << 16 | green << 8 | blue;
        }
        return c;
    }

    private boolean ignoreWorld(Level world) {
        for (int i = 0; i < dimensionsToIgnore.length; ++i) {
            if (!dimensionsToIgnore[i].equals(world.dimension().identifier().getPath())) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private int getCaving(double playerX, double playerY, double playerZ, Level world) {
        int defaultResult;
        boolean caveModeConfig = MinimapConfigClientUtils.getEffectiveCaveModeAllowed();
        if (!caveModeConfig) {
            return Integer.MAX_VALUE;
        }
        boolean manualCaveMode = this.minimapSession.getProcessor().isManualCaveMode();
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        int autoCaveModeConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.AUTO_CAVE_MODE);
        if (autoCaveModeConfig == 0 && !manualCaveMode) {
            return Integer.MAX_VALUE;
        }
        if (this.ignoreWorld(world)) {
            return this.lastCaving;
        }
        if (manualCaveMode) {
            int customCaveStart = (Integer)configManager.getEffective(MinimapProfiledConfigOptions.MANUAL_CAVE_MODE_START);
            if (customCaveStart != Integer.MAX_VALUE) {
                return customCaveStart;
            }
        } else if (this.modMain.getSupportMods().worldmap() && this.modMain.getSupportMods().worldmapSupport.shouldPreventAutoCaveMode(world)) {
            return Integer.MAX_VALUE;
        }
        int worldBottomY = world.getMinY();
        int worldTopY = world.getMaxY() - 1;
        int y = (int)playerY + 1;
        int defaultCaveStart = y + 3;
        int n = defaultResult = manualCaveMode ? defaultCaveStart : Integer.MAX_VALUE;
        if (y > worldTopY || y < worldBottomY) {
            return defaultResult;
        }
        int x = OptimizedMath.myFloor(playerX);
        int z = OptimizedMath.myFloor(playerZ);
        int roofRadius = autoCaveModeConfig - 1;
        int roofDiameter = 1 + roofRadius * 2;
        int startX = x - roofRadius;
        int startZ = z - roofRadius;
        boolean ignoringHeightmaps = this.modMain.getSettings().isIgnoreHeightmaps();
        int bottom = y;
        int top = Integer.MAX_VALUE;
        LevelChunk prevBChunk = null;
        int potentialResult = defaultCaveStart;
        for (int o = 0; o < roofDiameter; ++o) {
            block1: for (int p = 0; p < roofDiameter; ++p) {
                int currentX = startX + o;
                int currentZ = startZ + p;
                this.mutableBlockPos.set(currentX, y, currentZ);
                LevelChunk bchunk = world.getChunk(currentX >> 4, currentZ >> 4);
                if (bchunk == null) {
                    return defaultResult;
                }
                int skyLight = world.getBrightness(LightLayer.SKY, (BlockPos)this.mutableBlockPos);
                if (!ignoringHeightmaps) {
                    if (skyLight >= 15) return defaultResult;
                    int insideX = currentX & 0xF;
                    int insideZ = currentZ & 0xF;
                    top = bchunk.getHeight(Heightmap.Types.WORLD_SURFACE, insideX, insideZ);
                } else if (bchunk != prevBChunk) {
                    LevelChunkSection[] sections = bchunk.getSections();
                    if (sections.length == 0) {
                        return defaultResult;
                    }
                    int playerSection = y - worldBottomY >> 4;
                    boolean foundSomething = false;
                    for (int i = playerSection; i < sections.length; ++i) {
                        LevelChunkSection searchedSection = sections[i];
                        if (searchedSection.hasOnlyAir()) continue;
                        if (!foundSomething) {
                            bottom = Math.max(bottom, worldBottomY + (i << 4));
                            foundSomething = true;
                        }
                        top = worldBottomY + (i << 4) + 15;
                    }
                    if (!foundSomething) {
                        return defaultResult;
                    }
                    prevBChunk = bchunk;
                }
                if (top < worldBottomY) {
                    return defaultResult;
                }
                if (top > worldTopY) {
                    top = worldTopY;
                }
                for (int i = bottom; i <= top; ++i) {
                    this.mutableBlockPos.setY(i);
                    BlockState state = world.getBlockState((BlockPos)this.mutableBlockPos);
                    if (state.isAir() || state.getPistonPushReaction() == PushReaction.POPPED || state.getBlock() instanceof LiquidBlock || state.is(BlockTags.LEAVES) || this.isTransparent((StateHolder<?, ?>)state) || state.getBlock() == Blocks.BARRIER) continue;
                    if (o != p || o != roofRadius) continue block1;
                    potentialResult = Math.min(i, defaultCaveStart);
                    continue block1;
                }
                return defaultResult;
            }
        }
        this.lastCaving = potentialResult;
        return this.lastCaving;
    }

    public int getSectionBasedHeight(LevelChunk bchunk, int startY) {
        LevelChunkSection searchedSection;
        int i;
        LevelChunkSection[] sections = bchunk.getSections();
        if (sections.length == 0) {
            return 0;
        }
        int chunkBottomY = bchunk.getMinY();
        int playerSection = Math.min(startY - chunkBottomY >> 4, sections.length - 1);
        if (playerSection < 0) {
            playerSection = 0;
        }
        int result = 0;
        for (i = playerSection; i < sections.length; ++i) {
            searchedSection = sections[i];
            if (searchedSection.hasOnlyAir()) continue;
            result = chunkBottomY + (i << 4) + 15;
        }
        if (playerSection > 0 && result == 0) {
            for (i = playerSection - 1; i >= 0; --i) {
                searchedSection = sections[i];
                if (searchedSection.hasOnlyAir()) continue;
                result = chunkBottomY + (i << 4) + 15;
                break;
            }
        }
        return result;
    }

    public boolean furtherZoomout() {
        return (Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.FURTHER_ZOOMOUT);
    }

    public int getLoadSide() {
        if (this.furtherZoomout()) {
            return 18;
        }
        return 9;
    }

    public double getUpdateRadiusInChunks() {
        return (double)this.loadingSideInChunks / 2.0 / (this.minimapSession.getProcessor().getMinimapZoom() / MinimapConfigClientUtils.getActualZoom(1));
    }

    public int getMapCoord(int side, int coord) {
        return (coord >> 6) - side / 2;
    }

    public int getLoadedCaving() {
        return this.loadedCaving;
    }

    private boolean notEmptyColor(int[] red, int[] green, int[] blue) {
        return red[0] != 0 || green[0] != 0 || blue[0] != 0;
    }

    public float getFixedSkyLightBlockBrightness(float min, float fixedSun, int blockLight) {
        return (min + Math.max(fixedSun * 15.0f, (float)blockLight)) / (15.0f + min);
    }

    public float getBlockBrightness(float min, int sun, int lightLevel, int blockLight) {
        return (min + Math.max((lightLevel == -1 || lightLevel == 0 ? 1.0f : ((float)this.loadingLevels - 1.0f - (float)lightLevel) / ((float)this.loadingLevels - 1.0f)) * (float)sun, (float)blockLight)) / (15.0f + min);
    }

    public int getLoadingMapChunkX() {
        return this.loadingMapChunkX;
    }

    public int getLoadingMapChunkZ() {
        return this.loadingMapChunkZ;
    }

    public int getLoadingSideInChunks() {
        return this.loadingSideInChunks;
    }

    public MinimapChunk[][] getLoadedBlocks() {
        return this.loadedBlocks;
    }

    public int getLoadedMapChunkZ() {
        return this.loadedMapChunkZ;
    }

    public int getLoadedMapChunkX() {
        return this.loadedMapChunkX;
    }

    public int getLoadedLevels() {
        return this.loadedLevels;
    }

    public void setClearBlockColours(boolean clearBlockColours) {
        this.clearBlockColours = clearBlockColours;
    }

    public void cleanup() {
        if (this.loadedBlocks != null) {
            for (int i = 0; i < this.loadedBlocks.length; ++i) {
                for (int j = 0; j < this.loadedBlocks.length; ++j) {
                    MinimapChunk m = this.loadedBlocks[i][j];
                    if (m == null) continue;
                    m.cleanup(this.minimap);
                }
            }
        }
    }

    public void resetShortBlocks() {
        this.blockStateShortShapeCache.reset();
    }

    public DimensionHighlighterHandler getDimensionHighlightHandler() {
        return this.dimensionHighlightHandler;
    }

    public int getLoadedSideInChunks() {
        return this.loadedSideInChunks;
    }

    public boolean isLoadedNonWorldMap() {
        return this.loadedNonWorldMap;
    }

    public void onResourceReload() {
        this.clearTransparentCache = true;
    }
}

