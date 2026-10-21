/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderContext;

public class PlayerTrackerMinimapElementReader
extends MinimapElementReader<PlayerTrackerMinimapElement<?>, PlayerTrackerMinimapElementRenderContext> {
    @Override
    public boolean isHidden(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context) {
        return context.renderEntityDimId != element.getDimension() && context.mapDimId != element.getDimension();
    }

    @Override
    public double getRenderX(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Player clientPlayer = mc.level.getPlayerByUUID(element.getPlayerId());
        return clientPlayer == null ? element.getX() : EntityUtils.getEntityX((Entity)clientPlayer, partialTicks);
    }

    @Override
    public double getRenderY(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Player clientPlayer = mc.level.getPlayerByUUID(element.getPlayerId());
        return clientPlayer == null ? element.getY() : EntityUtils.getEntityY((Entity)clientPlayer, partialTicks);
    }

    @Override
    public double getRenderZ(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Player clientPlayer = mc.level.getPlayerByUUID(element.getPlayerId());
        return clientPlayer == null ? element.getZ() : EntityUtils.getEntityZ((Entity)clientPlayer, partialTicks);
    }

    @Override
    public double getCoordinateScale(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, MinimapElementRenderInfo renderInfo) {
        if (element.getDimension() == renderInfo.renderEntityDimension) {
            return renderInfo.renderEntityDimensionScale;
        }
        return renderInfo.backgroundCoordinateScale;
    }

    @Override
    public int getInteractionBoxLeft(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -10;
    }

    @Override
    public int getInteractionBoxRight(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 10;
    }

    @Override
    public int getInteractionBoxTop(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -10;
    }

    @Override
    public int getInteractionBoxBottom(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 10;
    }

    @Override
    public int getRenderBoxLeft(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxRight(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getRenderBoxTop(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxBottom(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getLeftSideLength(PlayerTrackerMinimapElement<?> element, Minecraft mc) {
        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(element.getPlayerId());
        if (info == null) {
            return 9;
        }
        return 9 + mc.font.width(info.getProfile().name());
    }

    @Override
    public String getMenuName(PlayerTrackerMinimapElement<?> element) {
        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(element.getPlayerId());
        if (info == null) {
            return String.valueOf(element.getPlayerId());
        }
        return info.getProfile().name();
    }

    @Override
    public String getFilterName(PlayerTrackerMinimapElement<?> element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(PlayerTrackerMinimapElement<?> element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(PlayerTrackerMinimapElement<?> element) {
        return -11184641;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public float getBoxScale(MinimapElementRenderLocation location, PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context) {
        return context.iconScale;
    }

    @Override
    public boolean isInteractable(MinimapElementRenderLocation location, PlayerTrackerMinimapElement<?> element) {
        return true;
    }
}

