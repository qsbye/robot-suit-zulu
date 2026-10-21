/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.IdentifierException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.anim.MultiplyAnimationHelper;
import xaero.common.core.IGuiRenderer;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.minimap.render.MinimapRenderer;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.write.MinimapWriter;
import xaero.common.misc.Misc;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.player.tracker.synced.ClientSyncedTrackedPlayerManager;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.render.MinimapPipRenderState;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.IGameRenderer;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapProcessor {
    public static final boolean DEBUG = false;
    public static final int FRAME = 9;
    private IXaeroMinimap modMain;
    private MinimapSession minimapSession;
    private MinimapWriter minimapWriter;
    private RadarSession radarSession;
    private Minimap minimap;
    private EntityRadarCategoryManager entityCategoryManager;
    private ClientSyncedTrackedPlayerManager syncedTrackedPlayerManager;
    private double minimapZoom;
    private boolean toResetImage;
    private boolean enlargedMap;
    private boolean manualCaveMode;
    private boolean noMinimapMessageReceived;
    private boolean fairPlayOnlyMessageReceived;
    private boolean consideringNetherFairPlayMessage;
    private double lastMapDimensionScale = 1.0;
    private ResourceKey<Level> lastMapDimension;
    private double lastPlayerDimDiv = 1.0;
    private Item minimapItem;
    private final MinimapPipRenderState renderState;
    private final MinimapPipRenderState.Enlarged enlargedRenderState;

    public MinimapProcessor(IXaeroMinimap modMain, MinimapSession minimapSession, MinimapWriter minimapWriter, RadarSession radarSession, ClientSyncedTrackedPlayerManager syncedTrackedPlayerManager) {
        this.modMain = modMain;
        this.minimapSession = minimapSession;
        this.minimapWriter = minimapWriter;
        this.radarSession = radarSession;
        this.minimapZoom = 1.0;
        this.toResetImage = true;
        this.minimap = modMain.getMinimap();
        this.syncedTrackedPlayerManager = syncedTrackedPlayerManager;
        this.updateMinimapItem();
        this.renderState = new MinimapPipRenderState();
        this.enlargedRenderState = new MinimapPipRenderState.Enlarged();
    }

    public int getMinimapSize() {
        int minimapSizeConfig = MinimapConfigClientUtils.getEffectiveMinimapSize();
        return this.enlargedMap ? 500 : minimapSizeConfig * 2;
    }

    public int getMinimapBufferSize(int minimapSize) {
        int bufferSize = 128 * (int)Math.pow(2.0, Math.ceil(Math.log((double)minimapSize / 128.0) / Math.log(2.0)));
        if (bufferSize < 128) {
            return 128;
        }
        if (bufferSize > 512) {
            return 512;
        }
        return bufferSize;
    }

    public boolean isEnlargedMap() {
        return this.enlargedMap;
    }

    public void setEnlargedMap(boolean enlargedMap) {
        this.enlargedMap = enlargedMap;
    }

    public double getMinimapZoom() {
        return this.minimapZoom;
    }

    public boolean isCaveModeDisplayed() {
        return this.minimapWriter.getLoadedCaving() != Integer.MAX_VALUE;
    }

    public double getTargetZoom() {
        double enlargedZoomConfig;
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        double target = MinimapConfigClientUtils.getActualZoom((ConfigOption<Integer>)MinimapProfiledConfigOptions.ZOOM);
        if (this.enlargedMap && (enlargedZoomConfig = MinimapConfigClientUtils.getActualZoom((ConfigOption<Integer>)MinimapProfiledConfigOptions.ZOOM_ENLARGED)) > 0.0) {
            target = enlargedZoomConfig;
        }
        if (this.isCaveModeDisplayed()) {
            target = Math.round(target * (double)((Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.CAVE_ZOOM)).intValue());
        }
        if (target > 6.0) {
            target = 6.0;
        }
        return target;
    }

    public void instantZoom() {
        this.minimapZoom = this.getTargetZoom();
    }

    public void updateZoom() {
        double target = this.getTargetZoom();
        double off = target - this.minimapZoom;
        off = off > 0.01 || off < -0.01 ? (double)((float)MultiplyAnimationHelper.animate(off, 0.8)) : 0.0;
        this.minimapZoom = target - off;
    }

    public MinimapWriter getMinimapWriter() {
        return this.minimapWriter;
    }

    public boolean canUseFrameBuffer() {
        return true;
    }

    public int getFBOBufferSize() {
        return 512;
    }

    public void onClientTick() {
        Level world = null;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.level() instanceof ClientLevel) {
            world = player.level();
        }
        Entity renderEntity = Minecraft.getInstance().getCameraEntity();
        this.radarSession.update((ClientLevel)world, renderEntity, (Player)player);
        this.modMain.getSupportMods().onClientTick();
    }

    public void onPlayerTick() {
    }

    public void checkFBO() {
        if (this.minimap.getMinimapFBORenderer().isLoadedFBO() && !this.canUseFrameBuffer()) {
            this.minimap.getMinimapFBORenderer().setLoadedFBO(false);
            this.minimap.getMinimapFBORenderer().deleteFramebuffers();
            this.toResetImage = true;
        }
        boolean mapSafeMode = (Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.SAFE_MODE);
        if (!(this.minimap.getMinimapFBORenderer().isLoadedFBO() || mapSafeMode || this.minimap.getMinimapFBORenderer().isTriedFBO())) {
            if (Minecraft.getInstance().gui.overlay() != null) {
                return;
            }
            this.minimap.getMinimapFBORenderer().loadFrameBuffer(this);
        }
    }

    public void onRender(int x, int y, int width, int height, double scale, float minimapScale, int size, int boxSize, float partial, XaeroBufferProvider cvc, int pipWidth, int pipHeight) {
        MinimapRendererHelper.restoreDefaultShaderBlendState();
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().identity();
        RenderSystem.getModelViewStack().translate(0.0f, 0.0f, -11000.0f);
        try {
            Minecraft mc = Minecraft.getInstance();
            IGuiRenderer guiRenderer = (IGuiRenderer)((IGameRenderer)mc.gameRenderer).xaero_lib_getGuiRenderer();
            ProjectionMatrixBuffer guiProjectionMatrixCache = guiRenderer.xaero_mm_getGuiProjectionMatrixBuffer();
            Projection guiProjection = guiRenderer.xaero_mm_getGuiProjection();
            if (guiProjection.width() != (float)pipWidth || guiProjection.height() != (float)pipHeight) {
                guiProjection.setSize((float)pipWidth, (float)pipHeight);
            }
            RenderSystem.setProjectionMatrix((GpuBufferSlice)guiProjectionMatrixCache.getBuffer(guiProjection), (ProjectionType)ProjectionType.ORTHOGRAPHIC);
            this.minimap.getMatrixStack().pushPose();
            this.minimap.getMatrixStack().translate(0.0, 0.0, 0.01);
            this.getRenderer().renderMinimap(this.minimapSession, this, x, y, width, height, scale, minimapScale, size, partial, cvc);
            this.minimap.getMatrixStack().popPose();
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
        }
        RenderSystem.getModelViewStack().popMatrix();
        MinimapRendererHelper.restoreDefaultShaderBlendState();
    }

    public MinimapRenderer getRenderer() {
        if (this.minimap.usingFBO()) {
            return this.minimap.getMinimapFBORenderer();
        }
        return this.minimap.getMinimapSafeModeRenderer();
    }

    public static boolean hasMinimapItem(Player player) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null) {
            return true;
        }
        MinimapProcessor processor = session.getProcessor();
        return processor.minimapItem == null || Misc.hasItem(player, processor.minimapItem);
    }

    public boolean isToResetImage() {
        return this.toResetImage;
    }

    public void setToResetImage(boolean toResetImage) {
        this.toResetImage = toResetImage;
    }

    public RadarSession getRadarSession() {
        return this.radarSession;
    }

    public void cleanup() {
        this.minimapWriter.cleanup();
    }

    public boolean isManualCaveMode() {
        return this.manualCaveMode || this.modMain.getSupportMods().shouldUseWorldMapCaveChunks() && this.modMain.getSupportMods().worldmapSupport.getManualCaveStart() != Integer.MAX_VALUE;
    }

    public void toggleManualCaveMode() {
        this.manualCaveMode = !this.isManualCaveMode();
    }

    public Minimap getMinimap() {
        return this.minimap;
    }

    public boolean getNoMinimapMessageReceived() {
        if (HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getServerSynced().isChannelPresentOnServer()) {
            return false;
        }
        return this.noMinimapMessageReceived;
    }

    public void setNoMinimapMessageReceived(boolean noMinimapMessageReceived) {
        this.noMinimapMessageReceived = noMinimapMessageReceived;
    }

    public boolean getForcedFairPlay() {
        return this.fairPlayOnlyMessageReceived;
    }

    public void setFairPlayOnlyMessageReceived(boolean fairPlayOnlyMessageReceived) {
        this.fairPlayOnlyMessageReceived = fairPlayOnlyMessageReceived;
    }

    public ClientSyncedTrackedPlayerManager getSyncedTrackedPlayerManager() {
        return this.syncedTrackedPlayerManager;
    }

    public boolean serverHasMod() {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        return worldData != null && worldData.serverLevelId != null;
    }

    public void setServerModNetworkVersion(int networkVersion) {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return;
        }
        worldData.setServerModNetworkVersion(networkVersion);
    }

    public int getServerModNetworkVersion() {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return 0;
        }
        return worldData.getServerModNetworkVersion();
    }

    public double getLastMapDimensionScale() {
        return this.lastMapDimensionScale;
    }

    public void setLastMapDimensionScale(double lastMapDimensionScale) {
        this.lastMapDimensionScale = lastMapDimensionScale;
    }

    public ResourceKey<Level> getLastMapDimension() {
        return this.lastMapDimension;
    }

    public void setLastMapDimension(ResourceKey<Level> lastMapDimension) {
        this.lastMapDimension = lastMapDimension;
    }

    public MinimapSession getSession() {
        return this.minimapSession;
    }

    public void updateMinimapItem() {
        Identifier minimapItemRL;
        String minimapItemString = ((String)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective(MinimapProfiledConfigOptions.MINIMAP_ITEM)).trim();
        if (minimapItemString.isEmpty() || minimapItemString.equals("-")) {
            this.minimapItem = null;
            MinimapLogs.LOGGER.info("Minimap required item set to nothing.");
            return;
        }
        try {
            minimapItemRL = Identifier.parse((String)minimapItemString);
        }
        catch (IdentifierException rle) {
            this.minimapItem = null;
            MinimapLogs.LOGGER.error("Tried setting the minimap required item to a misformatted ID: {}; Error: {}", (Object)minimapItemString, (Object)rle.getMessage());
            return;
        }
        this.minimapItem = (Item)BuiltInRegistries.ITEM.getValue(minimapItemRL);
        if (this.minimapItem == Items.AIR) {
            this.minimapItem = null;
            MinimapLogs.LOGGER.error("Tried setting the minimap required item to an invalid ID: {}", (Object)minimapItemString);
            return;
        }
        MinimapLogs.LOGGER.info("Minimap item set: " + this.minimapItem.getDefaultInstance().getItemName().getString());
    }

    public Item getMinimapItem() {
        return this.minimapItem;
    }

    public void setConsideringNetherFairPlayMessage(boolean consideringNetherFairPlay) {
        this.consideringNetherFairPlayMessage = consideringNetherFairPlay;
    }

    public boolean isConsideringNetherFairPlayMessage() {
        return this.consideringNetherFairPlayMessage;
    }

    public MinimapPipRenderState getRenderState() {
        return this.renderState;
    }

    public MinimapPipRenderState.Enlarged getEnlargedRenderState() {
        return this.enlargedRenderState;
    }
}

