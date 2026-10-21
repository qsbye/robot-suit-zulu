/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import xaero.common.HudMod;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.effect.Effects;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypoints;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.misc.Misc;
import xaero.hud.HudSession;
import xaero.hud.controls.key.KeyMappingTickHandler;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.element.render.world.MinimapElementWorldRendererHandler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.reflection.util.ReflectionUtils;
import xaero.lib.patreon.Patreon;

public class ClientEvents {
    protected HudMod modMain;
    private Screen lastGuiOpen;
    private Field realmsTaskField;
    private Field realmsTaskServerField;
    public RealmsServer latestRealm;

    public ClientEvents(HudMod modMain) {
        this.modMain = modMain;
    }

    public Screen handleGuiOpen(Screen gui) {
        if (!this.modMain.isFirstStageLoaded()) {
            return gui;
        }
        if (gui instanceof TitleScreen || gui instanceof JoinMultiplayerScreen) {
            this.modMain.getSettings().resetServerSettings();
        }
        Minecraft mc = Minecraft.getInstance();
        if (gui instanceof RealmsLongRunningMcoTaskScreen) {
            try {
                if (this.realmsTaskField == null) {
                    this.realmsTaskField = ReflectionUtils.getFieldReflection(RealmsLongRunningMcoTaskScreen.class, (String)"queuedTasks", (String)"Ljava/util/List;");
                    this.realmsTaskField.setAccessible(true);
                }
                if (this.realmsTaskServerField == null) {
                    this.realmsTaskServerField = ReflectionUtils.getFieldReflection(GetServerDetailsTask.class, (String)"server", (String)"Lcom/mojang/realmsclient/dto/RealmsServer;");
                    this.realmsTaskServerField.setAccessible(true);
                }
                RealmsLongRunningMcoTaskScreen realmsTaskScreen = (RealmsLongRunningMcoTaskScreen)gui;
                List tasks = (List)this.realmsTaskField.get(realmsTaskScreen);
                for (LongRunningTask task : tasks) {
                    GetServerDetailsTask realmsTask;
                    RealmsServer realm;
                    if (!(task instanceof GetServerDetailsTask) || (realm = (RealmsServer)this.realmsTaskServerField.get(realmsTask = (GetServerDetailsTask)task)) == null || this.latestRealm != null && realm.id == this.latestRealm.id) continue;
                    this.latestRealm = realm;
                }
            }
            catch (Exception e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        } else if ((gui instanceof GuiAddWaypoint || gui instanceof GuiWaypoints) && (Misc.hasEffect(Effects.NO_WAYPOINTS) || Misc.hasEffect(Effects.NO_WAYPOINTS_HARMFUL))) {
            gui = null;
        }
        this.lastGuiOpen = gui;
        return gui;
    }

    public void handleRenderGameOverlayEventPre() {
        if (Minecraft.getInstance().gui.hud.isHidden()) {
            return;
        }
        if (Minecraft.getInstance().getCameraEntity() == null) {
            return;
        }
        DeltaTracker deltaTracker = Minecraft.getInstance().getDeltaTracker();
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            Window mainwindow = Minecraft.getInstance().getWindow();
            GpuBufferSlice projectionMatrixBU = RenderSystem.getProjectionMatrixBuffer();
            ProjectionType projectionTypeBU = RenderSystem.getProjectionType();
            MinimapElementWorldRendererHandler worldRendererHandler = HudMod.INSTANCE.getMinimap().getWorldRendererHandler();
            GpuBufferSlice orthoBufferSlice = worldRendererHandler.getOrthoProjection(mainwindow.getWidth(), mainwindow.getHeight());
            RenderSystem.setProjectionMatrix((GpuBufferSlice)orthoBufferSlice, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
            RenderSystem.getModelViewStack().pushMatrix();
            RenderSystem.getModelViewStack().identity();
            Minecraft mc = Minecraft.getInstance();
            Vec3 renderPos = mc.gameRenderer.mainCamera().position();
            worldRendererHandler.prepareRender(XaeroMinimapCore.waypointsProjection, XaeroMinimapCore.waypointModelView);
            worldRendererHandler.render(renderPos, partialTicks, null, mc.level.dimensionType().coordinateScale(), (ResourceKey<Level>)mc.level.dimension());
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionMatrixBU, (ProjectionType)projectionTypeBU);
        }
    }

    public void handleRenderGameOverlayEventPost() {
        if (!this.modMain.isLoadedClient()) {
            return;
        }
        if (Minecraft.getInstance().getCameraEntity() == null) {
            return;
        }
        this.modMain.getHud().getEventHandler().handleRenderGameOverlayEventPost();
    }

    public boolean handleClientSendChatEvent(String message) {
        if (message.startsWith("xaero_waypoint_add:")) {
            String[] args = message.split(":");
            WaypointSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession().getWaypointSession();
            minimapSession.getSharing().onWaypointAdd(args);
            return true;
        }
        if (message.equals("xaero_tp_anyway")) {
            WaypointSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession().getWaypointSession();
            minimapSession.getTeleport().teleportAnyway();
            return true;
        }
        return false;
    }

    public boolean handleClientPlayerChatReceivedEvent(ChatType.Bound chatType, Component component, GameProfile gameProfile) {
        if (component == null) {
            return false;
        }
        return this.handleChatMessage(gameProfile == null ? I18n.get((String)"gui.xaero_waypoint_somebody_shared", (Object[])new Object[0]) : gameProfile.name(), component);
    }

    public boolean handleClientSystemChatReceivedEvent(Component component) {
        String probableName;
        XaeroMinimapSession minimapSession;
        if (component == null) {
            return false;
        }
        String textString = component.getString();
        if (textString.contains("\u00a7r\u00a7e\u00a7s\u00a7e\u00a7t\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setNoMinimapMessageReceived(false);
            minimapSession.getMinimapProcessor().setFairPlayOnlyMessageReceived(false);
            minimapSession.getMinimapProcessor().setConsideringNetherFairPlayMessage(false);
        }
        if (textString.contains("\u00a7n\u00a7o\u00a7m\u00a7i\u00a7n\u00a7i\u00a7m\u00a7a\u00a7p")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setNoMinimapMessageReceived(true);
        }
        if (textString.contains("\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o\u00a7m\u00a7m\u00a7n\u00a7e\u00a7t\u00a7h\u00a7e\u00a7r\u00a7i\u00a7s\u00a7f\u00a7a\u00a7i\u00a7r")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setConsideringNetherFairPlayMessage(true);
        }
        if (textString.contains("\u00a7f\u00a7a\u00a7i\u00a7r\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setFairPlayOnlyMessageReceived(true);
        }
        return this.handleChatMessage((probableName = StringUtils.substringBetween((String)textString, (String)"<", (String)">")) == null ? I18n.get((String)"gui.xaero_waypoint_server_shared", (Object[])new Object[0]) : probableName, component);
    }

    private boolean handleChatMessage(String playerName, Component text) {
        String textString = text.getString();
        if (textString.contains("xaero_waypoint:") || textString.contains("xaero-waypoint:")) {
            MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (minimapSession == null) {
                return false;
            }
            minimapSession.getWaypointSession().getSharing().onWaypointReceived(playerName, textString);
            return true;
        }
        return false;
    }

    public void handleDrawScreenEventPost(Screen gui) {
        if (!Patreon.needsNotification() && this.modMain.isOutdated()) {
            this.modMain.setOutdated(false);
        }
    }

    public void handlePlayerSetSpawnEvent(BlockPos newSpawnPoint, Level world) {
        if (world instanceof ClientLevel) {
            MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (minimapSession != null) {
                minimapSession.getWorldStateUpdater().setCurrentWorldSpawn(newSpawnPoint);
            }
            if (MinimapClientWorldDataHelper.getWorldData((ClientLevel)((ClientLevel)world)).serverLevelId == null) {
                // empty if block
            }
        }
    }

    public Object getLastGuiOpen() {
        return this.lastGuiOpen;
    }

    public void worldUnload(LevelAccessor world) {
        XaeroMinimapSession minimapSession;
        if (world instanceof ClientLevel && (minimapSession = XaeroMinimapSession.getCurrentSession()) != null) {
            MinimapProcessor minimap = minimapSession.getMinimapProcessor();
            minimap.getRadarSession().update(null, null, null);
        }
    }

    public void handleClientTickStart() {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            MinimapProcessor minimap = minimapSession.getMinimapProcessor();
            minimap.onClientTick();
            if (Minecraft.getInstance().gui.screen() == null) {
                minimapSession.getKeyMappingTickHandler().tick();
            }
            HudSession hudSession = HudSession.getCurrentSession();
            this.modMain.getClientEventsListener().clientTickPost(hudSession);
        }
    }

    public void handlePlayerTickStart(Player player) {
        if (player != Minecraft.getInstance().player) {
            return;
        }
        if (!this.modMain.isLoadedClient()) {
            return;
        }
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            try {
                MinimapProcessor minimap = minimapSession.getProcessor();
                minimapSession.getWorldStateUpdater().update();
                minimap.onPlayerTick();
                Minecraft mc = Minecraft.getInstance();
                HudSession hudSession = HudSession.getCurrentSession();
                this.modMain.getClientEventsListener().playerTickPost(hudSession);
            }
            catch (Throwable t) {
                this.modMain.getMinimap().setCrashedWith(t);
            }
        }
    }

    public void handleRenderTickStart() {
        if (this.modMain.getMinimap() != null) {
            this.modMain.getMinimap().checkCrashes();
        }
        if (Minecraft.getInstance().player != null) {
            XaeroMinimapSession minimapSession;
            if (!this.modMain.isLoadedClient()) {
                return;
            }
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                MinimapClientWorldDataHelper.getWorldData(world).getAttributeSystem().invalidateTickCache();
            }
            if ((minimapSession = XaeroMinimapSession.getCurrentSession()) != null) {
                MinimapProcessor minimap = minimapSession.getMinimapProcessor();
                minimap.getMinimapWriter().onRender();
            }
        }
    }

    public boolean handleRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphics) {
        if (!this.modMain.isLoadedClient()) {
            return false;
        }
        return this.modMain.getClientEventsListener().handleRenderStatusEffectOverlay(guiGraphics);
    }

    public boolean handleRenderCrosshairOverlay(GuiGraphicsExtractor guiGraphics) {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            return minimapSession.getMinimapProcessor().isEnlargedMap() && (Boolean)this.modMain.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.CENTERED_ENLARGED) != false;
        }
        return false;
    }

    public boolean handleForceToggleKeyMapping(ToggleKeyMapping keyMapping) {
        if (KeyMappingTickHandler.DISABLE_KEY_MAPPING_OVERRIDES) {
            return false;
        }
        return this.modMain.getClientEventsListener().handleForceToggleKeyMapping(keyMapping);
    }

    public void handleTextureStitchEventPost(TextureAtlas texture) {
        if (texture == Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)) {
            Minimap minimap;
            XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                minimapSession.getMinimapProcessor().getMinimapWriter().setClearBlockColours(true);
                minimapSession.getMinimapProcessor().getMinimapWriter().onResourceReload();
                minimapSession.getMinimapProcessor().getMinimapWriter().resetShortBlocks();
            }
            if ((minimap = this.modMain.getMinimap()) != null) {
                minimap.getMinimapFBORenderer().resetEntityIcons();
                this.handleTextureStitchEventPost_onReset();
            }
        }
    }

    protected void handleTextureStitchEventPost_onReset() {
    }
}

