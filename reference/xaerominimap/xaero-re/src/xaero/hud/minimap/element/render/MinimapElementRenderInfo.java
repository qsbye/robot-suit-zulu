/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;

public class MinimapElementRenderInfo {
    public final MinimapElementRenderLocation location;
    public final Entity renderEntity;
    public final Vec3 renderEntityPos;
    public final Player player;
    public final Vec3 renderPos;
    public final boolean cave;
    public final float partialTicks;
    public final RenderTarget framebuffer;
    public final double renderEntityDimensionScale;
    public final ResourceKey<Level> renderEntityDimension;
    public final double backgroundCoordinateScale;
    public final ResourceKey<Level> mapDimension;

    public MinimapElementRenderInfo(MinimapElementRenderLocation location, Entity renderEntity, Player player, Vec3 renderPos, boolean cave, float partialTicks, RenderTarget framebuffer, double backgroundCoordinateScale, ResourceKey<Level> mapDimension) {
        this.location = location;
        this.renderEntity = renderEntity;
        this.renderEntityPos = EntityUtils.getEntityPos(renderEntity, partialTicks);
        this.player = player;
        this.renderPos = renderPos;
        this.cave = cave;
        this.partialTicks = partialTicks;
        this.framebuffer = framebuffer;
        this.renderEntityDimensionScale = Minecraft.getInstance().level.dimensionType().coordinateScale();
        this.renderEntityDimension = Minecraft.getInstance().level.dimension();
        this.backgroundCoordinateScale = backgroundCoordinateScale;
        this.mapDimension = mapDimension;
    }
}

