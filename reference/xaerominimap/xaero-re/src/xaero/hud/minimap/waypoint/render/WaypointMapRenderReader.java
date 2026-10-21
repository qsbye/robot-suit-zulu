/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.render;

import net.minecraft.client.Minecraft;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderContext;

public class WaypointMapRenderReader
extends MinimapElementReader<Waypoint, WaypointMapRenderContext> {
    @Override
    public double getRenderX(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return (double)element.getX() + 0.5;
    }

    @Override
    public double getRenderY(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return element.getY() + 1;
    }

    @Override
    public double getRenderZ(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return (double)element.getZ() + 0.5;
    }

    @Override
    public double getCoordinateScale(Waypoint element, WaypointMapRenderContext context, MinimapElementRenderInfo renderInfo) {
        return context.dimCoordinateScale;
    }

    @Override
    public boolean shouldScalePartialCoordinates(Waypoint element, WaypointMapRenderContext context, MinimapElementRenderInfo renderInfo) {
        return false;
    }

    @Override
    public boolean isHidden(Waypoint element, WaypointMapRenderContext context) {
        return false;
    }

    @Override
    public int getInteractionBoxLeft(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxRight(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxTop(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxBottom(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return 0;
    }

    @Override
    public int getRenderBoxLeft(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return -this.getRenderBoxRight(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxRight(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        int j = element.getPurpose() == WaypointPurpose.DEATH ? 4 : Minecraft.getInstance().font.width(element.getInitials()) / 2;
        int addedFrame = j > 4 ? j - 4 : 0;
        return 5 + addedFrame;
    }

    @Override
    public int getRenderBoxTop(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return this.getRenderBoxLeft(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxBottom(Waypoint element, WaypointMapRenderContext context, float partialTicks) {
        return this.getRenderBoxRight(element, context, partialTicks);
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
}

