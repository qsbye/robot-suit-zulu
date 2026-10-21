/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.core;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IBufferSource;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.core.IXaeroMinimapSMultiBlockChangePacket;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.icon.creator.render.trace.EntityRenderTracer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.pushbox.BuiltInPushBoxes;
import xaero.hud.pushbox.boss.IBossHealthPushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;
import xaero.lib.common.util.IOUtils;

public class XaeroMinimapCore {
    public static IXaeroMinimap modMain;
    public static Field chunkCleanField;
    public static XaeroMinimapSession currentSession;
    public static Matrix4f waypointsProjection;
    public static Matrix4f waypointModelView;

    public static void ensureField() {
        if (chunkCleanField == null) {
            try {
                chunkCleanField = LevelChunk.class.getDeclaredField("xaero_chunkClean");
            }
            catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void chunkUpdateCallback(int chunkX, int chunkZ) {
        XaeroMinimapCore.ensureField();
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            try {
                for (int x = chunkX - 1; x < chunkX + 2; ++x) {
                    for (int z = chunkZ - 1; z < chunkZ + 2; ++z) {
                        LevelChunk chunk = world.getChunk(x, z);
                        if (chunk == null) continue;
                        chunkCleanField.set(chunk, false);
                    }
                }
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void onHandleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet) {
        XaeroMinimapCore.chunkUpdateCallback(packet.x(), packet.z());
    }

    public static void onHandleLightUpdatePacket(ClientboundLightUpdatePacket packet) {
        XaeroMinimapCore.chunkUpdateCallback(packet.x(), packet.z());
    }

    public static void onQueueLightRemoval(ClientboundForgetLevelChunkPacket packet) {
        XaeroMinimapCore.chunkUpdateCallback(packet.pos().x(), packet.pos().z());
    }

    public static void onBlockChange(ClientboundBlockUpdatePacket packetIn) {
        XaeroMinimapCore.chunkUpdateCallback(packetIn.getPos().getX() >> 4, packetIn.getPos().getZ() >> 4);
    }

    public static void onMultiBlockChange(ClientboundSectionBlocksUpdatePacket packetIn) {
        IXaeroMinimapSMultiBlockChangePacket packetAccess = (IXaeroMinimapSMultiBlockChangePacket)packetIn;
        XaeroMinimapCore.chunkUpdateCallback(packetAccess.xaero_mm_getSectionPos().getX(), packetAccess.xaero_mm_getSectionPos().getZ());
    }

    public static void onPlayNetHandler(ClientPacketListener netHandler, ClientboundLoginPacket packet) {
        if (HudMod.INSTANCE != null) {
            HudMod.INSTANCE.tryLoadLater();
        }
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (modMain.getMinimap().getCrashedWith() != null) {
            return;
        }
        try {
            XaeroMinimapSession minimapSession;
            IXaeroMinimapClientPlayNetHandler netHandlerAccess = (IXaeroMinimapClientPlayNetHandler)netHandler;
            if (netHandlerAccess.getXaero_minimapSession() != null) {
                return;
            }
            if (currentSession != null) {
                MinimapLogs.LOGGER.info("Previous hud session still active. Probably using MenuMobs. Forcing it to end...");
                XaeroMinimapCore.cleanupCurrentSession();
            }
            currentSession = minimapSession = modMain.createSession();
            minimapSession.init(netHandler);
            netHandlerAccess.setXaero_minimapSession(minimapSession);
        }
        catch (Throwable e) {
            if (currentSession != null) {
                XaeroMinimapCore.cleanupCurrentSession();
            }
            RuntimeException wrappedException = new RuntimeException("Exception initializing Xaero's Minimap! ", e);
            modMain.getMinimap().setCrashedWith(wrappedException);
        }
    }

    private static void cleanupCurrentSession() {
        try {
            currentSession.tryCleanup();
        }
        catch (Throwable supressed) {
            MinimapLogs.LOGGER.error("suppressed exception", supressed);
        }
        finally {
            currentSession = null;
        }
    }

    public static void onPlayNetHandlerCleanup(ClientPacketListener netHandler) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        try {
            XaeroMinimapSession netHandlerSession = ((IXaeroMinimapClientPlayNetHandler)netHandler).getXaero_minimapSession();
            if (netHandlerSession == null) {
                return;
            }
            try {
                netHandlerSession.tryCleanup();
            }
            finally {
                if (netHandlerSession == currentSession) {
                    currentSession = null;
                }
                ((IXaeroMinimapClientPlayNetHandler)netHandler).setXaero_minimapSession(null);
            }
        }
        catch (Throwable e) {
            RuntimeException wrappedException = new RuntimeException("Exception finalizing Xaero's Minimap! ", e);
            modMain.getMinimap().setCrashedWith(wrappedException);
        }
    }

    public static void beforeRespawn(Player player) {
        MinimapSession minimapSession;
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (player == Minecraft.getInstance().player && (minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession()) != null) {
            minimapSession.getWaypointSession().getDeathpointHandler().createDeathpoint(player);
        }
    }

    public static void onProjectionMatrix(Matrix4f matrixIn) {
        waypointsProjection.identity();
        waypointsProjection.mul((Matrix4fc)matrixIn);
    }

    public static void onWorldModelViewMatrix(Matrix4fc matrix) {
        waypointModelView.identity();
        waypointModelView.mul(matrix);
    }

    public static void onRenderLevelMatrices(Matrix4f worldModelViewMatrix, Matrix4f projectionMatrix) {
        XaeroMinimapCore.onProjectionMatrix(projectionMatrix);
        XaeroMinimapCore.onWorldModelViewMatrix((Matrix4fc)worldModelViewMatrix);
    }

    public static void beforeIngameGuiRender() {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleRenderGameOverlayEventPre();
    }

    public static void afterIngameGuiRender() {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleRenderGameOverlayEventPost();
    }

    public static void onRenderStatusEffectOverlayPost(GuiGraphicsExtractor guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        IPotionEffectsPushBox potionEffectsPushBox = BuiltInPushBoxes.getPotionEffectPushBox(modMain);
        if (potionEffectsPushBox != null) {
            potionEffectsPushBox.setActive(true);
        }
    }

    public static void onBossHealthRender(int h) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        IBossHealthPushBox bossHealthPushBox = BuiltInPushBoxes.getBossHealthPushBox(modMain);
        if (bossHealthPushBox != null) {
            bossHealthPushBox.setActive(true);
            bossHealthPushBox.setLastBossHealthHeight(h);
        }
    }

    public static void onEntityIconsModelPrepareDetection(ModelFeatureRenderer.Submit<?> submit) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        modMain.getMinimap().getMinimapFBORenderer().onRadarIconModelPrepareDetection(submit);
    }

    public static void onEntityIconsModelRenderDetection(Model model, VertexConsumer vertexConsumer, int color) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        modMain.getMinimap().getMinimapFBORenderer().onRadarIconModelRenderTrace(model, vertexConsumer, color);
    }

    public static void onEntityIconsModelPartRenderDetection(ModelPart modelRenderer, int color) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        modMain.getMinimap().getMinimapFBORenderer().onEntityIconModelPartRenderTrace(modelRenderer, color);
    }

    public static void onDeleteWorld(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        String worldFolder = levelStorageAccess.getLevelPath(LevelResource.ROOT).getParent().getFileName().toString();
        if (!worldFolder.isEmpty()) {
            String minimapWorldFolder = MinimapWorldContainerUtil.convertWorldFolderToContainerNode(worldFolder);
            Path minimapWorldFolderPath = HudMod.INSTANCE.getMinimapFolder().resolve(minimapWorldFolder);
            if (minimapWorldFolderPath.toFile().exists()) {
                try {
                    IOUtils.deleteFile((Path)minimapWorldFolderPath, (int)20);
                    MinimapLogs.LOGGER.info(String.format("Deleted minimap world data at %s", minimapWorldFolderPath));
                }
                catch (IOException e) {
                    MinimapLogs.LOGGER.error(String.format("Failed to delete minimap world data at %s!", minimapWorldFolderPath), (Throwable)e);
                }
            }
        }
    }

    public static void onSpawn(ClientboundSetDefaultSpawnPositionPacket packetIn) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        modMain.getEvents().handlePlayerSetSpawnEvent(packetIn.respawnData().pos(), (Level)Minecraft.getInstance().level);
    }

    public static boolean onLocalPlayerCommand(String command) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleClientSendChatEvent(command);
    }

    public static boolean onSystemChat(Component component) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleClientSystemChatReceivedEvent(component);
    }

    public static boolean onHandleDisguisedChatMessage(ChatType.Bound chatType, Component component) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return true;
        }
        return !modMain.getEvents().handleClientPlayerChatReceivedEvent(chatType, component, null);
    }

    public static boolean isModLoaded() {
        return modMain != null && modMain.isLoadedClient();
    }

    public static boolean onRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleRenderStatusEffectOverlay(guiGraphics);
    }

    public static boolean onRenderCrosshair(GuiGraphicsExtractor guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleRenderCrosshairOverlay(guiGraphics);
    }

    public static void handleRenderModOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        modMain.getModClientEvents().handleRenderModOverlay(guiGraphics, deltaTracker);
    }

    public static void onBufferSourceGetBuffer(IBufferSource mixinBufferSource, RenderType renderType) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        mixinBufferSource.setXaero_lastRenderType(renderType);
    }

    public static boolean onToggleKeyIsDown(ToggleKeyMapping keyMapping) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        if (Minecraft.getInstance().options == null) {
            return false;
        }
        return modMain.getEvents().handleForceToggleKeyMapping(keyMapping);
    }

    static {
        chunkCleanField = null;
        waypointsProjection = new Matrix4f();
        waypointModelView = new Matrix4f();
    }
}

