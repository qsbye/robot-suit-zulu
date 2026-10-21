/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.render.element;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;

public final class RadarElementReader
extends MinimapElementReader<Entity, RadarRenderContext> {
    @Override
    public double getRenderX(Entity element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityX(element, partialTicks);
    }

    @Override
    public double getRenderY(Entity element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityY(element, partialTicks);
    }

    @Override
    public double getRenderZ(Entity element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityZ(element, partialTicks);
    }

    @Override
    public boolean isHidden(Entity element, RadarRenderContext context) {
        return false;
    }

    @Override
    public int getInteractionBoxLeft(Entity element, RadarRenderContext context, float partialTicks) {
        return context.icon ? -16 : -6;
    }

    @Override
    public int getInteractionBoxRight(Entity element, RadarRenderContext context, float partialTicks) {
        return context.icon ? 16 : 6;
    }

    @Override
    public int getInteractionBoxTop(Entity element, RadarRenderContext context, float partialTicks) {
        return context.icon ? -16 : -6;
    }

    @Override
    public int getInteractionBoxBottom(Entity element, RadarRenderContext context, float partialTicks) {
        return context.icon ? 16 : 6;
    }

    @Override
    public int getRenderBoxLeft(Entity element, RadarRenderContext context, float partialTicks) {
        return -64;
    }

    @Override
    public int getRenderBoxRight(Entity element, RadarRenderContext context, float partialTicks) {
        return 64;
    }

    @Override
    public int getRenderBoxTop(Entity element, RadarRenderContext context, float partialTicks) {
        return -32;
    }

    @Override
    public int getRenderBoxBottom(Entity element, RadarRenderContext context, float partialTicks) {
        return 32;
    }

    @Override
    public int getLeftSideLength(Entity element, Minecraft mc) {
        return 0;
    }

    @Override
    public String getMenuName(Entity element) {
        return "n/a";
    }

    @Override
    public String getFilterName(Entity element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(Entity element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(Entity element) {
        return 0;
    }

    @Override
    public float getBoxScale(MinimapElementRenderLocation location, Entity element, RadarRenderContext context) {
        return (location == MinimapElementRenderLocation.OVER_MINIMAP ? 0.5f : 1.0f) * (float)(context.icon ? context.iconScale : context.dotScale);
    }

    @Override
    public boolean isInteractable(MinimapElementRenderLocation location, Entity element) {
        return true;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }
}

