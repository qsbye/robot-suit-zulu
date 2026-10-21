/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.player.tracker.PlayerTrackerIconRenderer;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementCollector;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementReader;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderContext;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderProvider;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.font.util.FontUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;

public final class PlayerTrackerMinimapElementRenderer
extends MinimapElementRenderer<PlayerTrackerMinimapElement<?>, PlayerTrackerMinimapElementRenderContext> {
    private final double WORLD_MINIMUM_DISTANCE = 10.0;
    private final double WORLD_FADING_LENGTH = 10.0;
    private final PlayerTrackerMinimapElementCollector elementCollector;
    private final PlayerTrackerIconRenderer playerTrackerIconRenderer;
    private final IXaeroMinimap modMain;
    private float nameScale;

    private PlayerTrackerMinimapElementRenderer(PlayerTrackerMinimapElementCollector elementCollector, IXaeroMinimap modMain, PlayerTrackerMinimapElementRenderContext context, PlayerTrackerMinimapElementRenderProvider<PlayerTrackerMinimapElementRenderContext> provider, PlayerTrackerMinimapElementReader reader, PlayerTrackerIconRenderer playerTrackerIconRenderer) {
        super(reader, provider, context);
        this.elementCollector = elementCollector;
        this.modMain = modMain;
        this.playerTrackerIconRenderer = playerTrackerIconRenderer;
    }

    public Identifier getPlayerSkin(Player player, PlayerInfo info) {
        Identifier skinTextureLocation;
        Identifier identifier = skinTextureLocation = player instanceof AbstractClientPlayer ? ((AbstractClientPlayer)player).getSkin().body().texturePath() : info.getSkin().body().texturePath();
        if (skinTextureLocation == null) {
            skinTextureLocation = DefaultPlayerSkin.get((UUID)player.getUUID()).body().texturePath();
        }
        return skinTextureLocation;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider rendererProvider) {
        xaeroBufferProvider.endBatch();
        ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer = xaeroBufferProvider.getBuffer(CustomRenderTypes.COLORED_WAYPOINTS_BGS);
        ((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer = rendererProvider.getRenderer(CustomRenderTypes.GUI_NEAREST);
        ((PlayerTrackerMinimapElementRenderContext)this.context).renderEntityDimId = renderInfo.renderEntityDimension;
        ((PlayerTrackerMinimapElementRenderContext)this.context).mapDimId = renderInfo.mapDimension;
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        float trackedPlayerWorldIconScale = MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_ICON_SCALE);
        float trackedPlayerMinimapIconScale = MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE);
        ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale = renderInfo.location == MinimapElementRenderLocation.IN_WORLD ? trackedPlayerWorldIconScale : trackedPlayerMinimapIconScale;
        this.nameScale = MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_NAME_SCALE);
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider rendererProvider) {
        rendererProvider.draw(((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer);
        xaeroBufferProvider.endBatch();
        this.elementCollector.resetRenderedOnRadarFlags();
    }

    @Override
    public boolean renderElement(PlayerTrackerMinimapElement<?> e, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, XaeroBufferProvider xaeroBufferProvider) {
        float alpha;
        double trackedZ;
        double offZ;
        double trackedY;
        double offY;
        if (!outOfBounds && renderInfo.location != MinimapElementRenderLocation.IN_WORLD && e.wasRenderedOnRadar()) {
            return false;
        }
        PoseStack matrixStack = guiGraphics.pose();
        Minecraft mc = Minecraft.getInstance();
        PlayerInfo info = mc.getConnection().getPlayerInfo(e.getPlayerId());
        if (info == null) {
            return false;
        }
        Player clientPlayer = mc.level.getPlayerByUUID(e.getPlayerId());
        double trackedX = clientPlayer == null ? e.getX() : EntityUtils.getEntityX((Entity)clientPlayer, renderInfo.partialTicks);
        double offX = trackedX - renderInfo.renderEntityPos.x;
        double distance = Math.sqrt(offX * offX + (offY = (trackedY = clientPlayer == null ? e.getY() : EntityUtils.getEntityY((Entity)clientPlayer, renderInfo.partialTicks)) - renderInfo.renderEntityPos.y) * offY + (offZ = (trackedZ = clientPlayer == null ? e.getZ() : EntityUtils.getEntityZ((Entity)clientPlayer, renderInfo.partialTicks)) - renderInfo.renderEntityPos.z) * offZ);
        if (distance < 10.0) {
            return false;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, optionalDepth);
        boolean inWorld = renderInfo.location == MinimapElementRenderLocation.IN_WORLD;
        float f = alpha = inWorld ? 0.5f : 1.0f;
        if (highlighted && inWorld) {
            alpha = 0.8f;
        }
        if (!highlighted && inWorld && distance < 20.0) {
            alpha *= (float)((distance - 10.0) / 10.0);
        }
        matrixStack.translate(0.0, 0.0, 0.01);
        matrixStack.pushPose();
        matrixStack.scale(((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, 1.0f);
        RenderBufferUtil.addColoredRect(matrixStack.last().pose(), ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer, -5.0f, -5.0f, 10, 10, 1.0f, 1.0f, 1.0f, alpha);
        this.playerTrackerIconRenderer.renderIcon(mc, ((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer, matrixStack, clientPlayer, this.getPlayerSkin(clientPlayer, info), info, alpha);
        matrixStack.popPose();
        if (highlighted && inWorld) {
            matrixStack.translate(-5.0f * ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, 0.0f, 0.0f);
            matrixStack.scale(this.nameScale, this.nameScale, 1.0f);
            String playerName = info.getProfile().name();
            int playerNameWidth = mc.font.width(playerName);
            float labelAlpha = 0.3529412f;
            RenderBufferUtil.addColoredRect(matrixStack.last().pose(), ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer, -playerNameWidth - 1, -5.0f, playerNameWidth + 1, 10, 0.0f, 0.0f, 0.0f, labelAlpha);
            FontUtils.drawText((PoseStack)matrixStack, (String)playerName, (float)(-playerNameWidth), (float)-4.0f, (int)-1, (boolean)false, (Font.DisplayMode)Font.DisplayMode.NORMAL, (XaeroBufferProvider)xaeroBufferProvider);
        }
        matrixStack.popPose();
        return true;
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return location != MinimapElementRenderLocation.IN_WORLD && (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_ON_MINIMAP) != false || location == MinimapElementRenderLocation.IN_WORLD && (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_IN_WORLD) != false;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    public PlayerTrackerMinimapElementCollector getCollector() {
        return this.elementCollector;
    }

    public static final class Builder {
        private final IXaeroMinimap modMain;

        private Builder(IXaeroMinimap modMain) {
            this.modMain = modMain;
        }

        private Builder setDefault() {
            return this;
        }

        public PlayerTrackerMinimapElementRenderer build() {
            PlayerTrackerMinimapElementCollector collector = new PlayerTrackerMinimapElementCollector(this.modMain.getRenderedPlayerTrackerManager());
            return new PlayerTrackerMinimapElementRenderer(collector, this.modMain, new PlayerTrackerMinimapElementRenderContext(), new PlayerTrackerMinimapElementRenderProvider<PlayerTrackerMinimapElementRenderContext>(collector), new PlayerTrackerMinimapElementReader(), new PlayerTrackerIconRenderer());
        }

        public static Builder begin(IXaeroMinimap modMain) {
            return new Builder(modMain).setDefault();
        }
    }
}

