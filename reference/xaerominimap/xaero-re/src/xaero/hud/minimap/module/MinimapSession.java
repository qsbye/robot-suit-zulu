/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.module;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import xaero.common.HudMod;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.write.MinimapWriter;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.player.tracker.synced.ClientSyncedTrackedPlayerManager;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.world.MinimapDimensionHelper;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.minimap.world.io.MinimapWorldManagerIO;
import xaero.hud.minimap.world.state.MinimapWorldState;
import xaero.hud.minimap.world.state.MinimapWorldStateUpdater;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public final class MinimapSession
extends ModuleSession<MinimapSession> {
    private Minimap minimap;
    private final Minecraft mc = Minecraft.getInstance();
    protected final MinimapProcessor processor;
    protected final MinimapWorldManager worldManager;
    private final MinimapWorldManagerIO worldManagerIO;
    private final MinimapWorldState worldState;
    private final MinimapWorldStateUpdater worldStateUpdater;
    private final MinimapDimensionHelper dimensionHelper;
    private final WaypointSession waypointSession;
    private final RadarSession radarSession;
    private final MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers;

    public MinimapSession(HudMod modMain, HudModule<MinimapSession> module, ClientPacketListener connection) {
        super(modMain, module);
        this.worldManager = new MinimapWorldManager(modMain, this);
        this.worldState = new MinimapWorldState();
        this.worldStateUpdater = new MinimapWorldStateUpdater(modMain, this, connection);
        this.dimensionHelper = new MinimapDimensionHelper();
        this.worldManagerIO = new MinimapWorldManagerIO(modMain);
        this.worldStateUpdater.init();
        try {
            this.worldManagerIO.loadWorldsFromAllSources(this, connection);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (modMain.getSettings().needsLegacySlimeSeedResave) {
            try {
                modMain.getSettings().saveSettings();
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
        this.waypointSession = new WaypointSession(modMain, this);
        this.multiTextureRenderTypeRenderers = new MultiTextureRenderTypeRendererProvider(2);
        HighlighterRegistry highlighterRegistry = new HighlighterRegistry();
        if (modMain.getSupportMods().worldmap()) {
            modMain.getSupportMods().worldmapSupport.registerHighlighters(highlighterRegistry);
        }
        if (modMain.getSupportMods().pac()) {
            modMain.getSupportMods().xaeroPac.registerHighlighters(highlighterRegistry);
        }
        highlighterRegistry.end();
        MinimapWriter minimapWriter = modMain.getPlatformContext().createMinimapWriter(this.modMain, this, new BlockStateShortShapeCache(modMain), highlighterRegistry);
        this.radarSession = RadarSession.Builder.begin().setCategoryManager(modMain.getEntityRadarCategoryManager()).build();
        ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager = ClientSyncedTrackedPlayerManager.Builder.begin().build();
        this.processor = new MinimapProcessor(modMain, this, minimapWriter, this.radarSession, clientSyncedTrackedPlayerManager);
        this.minimap = modMain.getMinimap();
        modMain.getSupportMods().onMinimapSessionStarted(this);
    }

    @Override
    public void prePotentialRender() {
        try {
            super.prePotentialRender();
            this.getProcessor().checkFBO();
            this.modMain.getTrackedPlayerRenderer().getCollector().update(Minecraft.getInstance());
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
            this.minimap.checkCrashes();
        }
    }

    @Override
    public void close() {
        this.processor.cleanup();
    }

    @Override
    public int getWidth(double screenScale) {
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        float minimapScale = GuiUtils.getMinimapScale(configManager);
        return (int)((double)((float)this.getConfiguredWidth() * minimapScale) / screenScale);
    }

    @Override
    public int getHeight(double screenScale) {
        return this.getWidth(screenScale);
    }

    public int getConfiguredWidth() {
        return this.getProcessor().getMinimapSize() / 2 + 18;
    }

    public MinimapProcessor getProcessor() {
        return this.processor;
    }

    public boolean getHideMinimapUnderScreen() {
        return (Boolean)this.modMain.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_SCREEN);
    }

    public boolean getHideMinimapUnderF3() {
        return (Boolean)this.modMain.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.HIDE_UNDER_F3);
    }

    public MultiTextureRenderTypeRendererProvider getMultiTextureRenderTypeRenderers() {
        return this.multiTextureRenderTypeRenderers;
    }

    public WaypointSession getWaypointSession() {
        return this.waypointSession;
    }

    public RadarSession getRadarSession() {
        return this.radarSession;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public MinimapWorldManager getWorldManager() {
        return this.worldManager;
    }

    public MinimapWorldState getWorldState() {
        return this.worldState;
    }

    public MinimapWorldStateUpdater getWorldStateUpdater() {
        return this.worldStateUpdater;
    }

    public MinimapDimensionHelper getDimensionHelper() {
        return this.dimensionHelper;
    }

    public MinimapWorldManagerIO getWorldManagerIO() {
        return this.worldManagerIO;
    }
}

