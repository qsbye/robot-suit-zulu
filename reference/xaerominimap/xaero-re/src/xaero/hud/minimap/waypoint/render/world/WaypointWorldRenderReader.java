/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.render.world;

import net.minecraft.client.Minecraft;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderContext;

public class WaypointWorldRenderReader
extends MinimapElementReader<Waypoint, WaypointWorldRenderContext> {
    private final WaypointWorldRenderContext context;

    public WaypointWorldRenderReader(WaypointWorldRenderContext context) {
        this.context = context;
    }

    @Override
    public boolean isHidden(Waypoint element, WaypointWorldRenderContext context) {
        return false;
    }

    @Override
    public double getRenderX(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return (double)element.getX() + 0.5;
    }

    @Override
    public double getRenderY(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        if (element.isYIncluded()) {
            return element.getY() + 1;
        }
        if (context.renderEntityPos == null) {
            return EntityUtils.getEntityY(Minecraft.getInstance().getCameraEntity(), partialTicks) + 1.0;
        }
        return context.renderEntityPos.y + 1.0;
    }

    @Override
    public double getRenderZ(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return (double)element.getZ() + 0.5;
    }

    @Override
    public double getCoordinateScale(Waypoint element, WaypointWorldRenderContext context, MinimapElementRenderInfo renderInfo) {
        return context.dimCoordinateScale;
    }

    @Override
    public boolean shouldScalePartialCoordinates(Waypoint element, WaypointWorldRenderContext context, MinimapElementRenderInfo renderInfo) {
        return false;
    }

    @Override
    public int getInteractionBoxLeft(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return context.interactionBoxLeft;
    }

    @Override
    public int getInteractionBoxRight(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return -context.interactionBoxLeft;
    }

    @Override
    public int getInteractionBoxTop(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return context.interactionBoxTop;
    }

    @Override
    public int getInteractionBoxBottom(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return -context.interactionBoxTop;
    }

    @Override
    public int getRenderBoxLeft(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return -32;
    }

    @Override
    public int getRenderBoxRight(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return 32;
    }

    @Override
    public int getRenderBoxTop(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return -32;
    }

    @Override
    public int getRenderBoxBottom(Waypoint element, WaypointWorldRenderContext context, float partialTicks) {
        return 32;
    }

    @Override
    public int getLeftSideLength(Waypoint element, Minecraft mc) {
        return 0;
    }

    @Override
    public String getMenuName(Waypoint element) {
        return "n/a";
    }

    @Override
    public String getFilterName(Waypoint element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(Waypoint element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(Waypoint element) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }

    @Override
    public boolean isInteractable(MinimapElementRenderLocation location, Waypoint element) {
        return true;
    }

    @Override
    public boolean isAlwaysHighlightedWhenHovered(Waypoint element, WaypointWorldRenderContext context) {
        return !context.onlyMainInfo;
    }
}

