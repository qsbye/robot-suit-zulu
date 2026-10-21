/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.DestinationHandler;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;
import xaero.hud.minimap.waypoint.render.AbstractWaypointRenderContext;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public abstract class AbstractWaypointRenderProvider<C extends AbstractWaypointRenderContext>
extends MinimapElementRenderProvider<Waypoint, C> {
    private final List<Waypoint> collectingList = new ArrayList<Waypoint>();
    private Iterator<Waypoint> iterator;
    private boolean deathpoints;
    private DestinationHandler destinationHandler;
    public final Predicate<Waypoint> filter = w -> {
        if (w.isDisabled()) {
            return false;
        }
        if (w.getVisibility() == WaypointVisibilityType.WORLD_MAP_LOCAL) {
            return false;
        }
        if (w.getVisibility() == WaypointVisibilityType.WORLD_MAP_GLOBAL) {
            return false;
        }
        return this.deathpoints || !w.getPurpose().isDeath();
    };

    @Override
    public void begin(MinimapElementRenderLocation location, C context) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        this.collectingList.clear();
        session.getWaypointSession().getCollector().collect(this.collectingList);
        ModSettings settings = HudMod.INSTANCE.getSettings();
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean renderAllSets = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS);
        MinimapWorld currentWorld = session.getWorldManager().getCurrentWorld();
        this.destinationHandler = session.getWaypointSession().getDestinationHandler();
        Entity renderEntity = Minecraft.getInstance().getCameraEntity();
        boolean deleteReachedDeathpoints = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DELETE_REACHED_DEATHPOINTS);
        this.destinationHandler.begin(renderEntity, currentWorld, renderAllSets, deleteReachedDeathpoints);
        this.deathpoints = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DEATHPOINTS);
        Camera activeRender = Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 cameraPos = activeRender.position();
        ((AbstractWaypointRenderContext)context).dimCoordinateScale = session.getDimensionHelper().getDimCoordinateScale(currentWorld);
        double cameraPosMultiplier = Minecraft.getInstance().level.dimensionType().coordinateScale() / ((AbstractWaypointRenderContext)context).dimCoordinateScale;
        Waypoint.RENDER_SORTING_POS = new Vec3(cameraPos.x * cameraPosMultiplier, cameraPos.y, cameraPos.z * cameraPosMultiplier);
        this.iterator = this.collectingList.stream().filter(this.filter).sorted().iterator();
    }

    @Override
    public boolean hasNext(MinimapElementRenderLocation location, C context) {
        return this.iterator.hasNext();
    }

    @Override
    public Waypoint getNext(MinimapElementRenderLocation location, C context) {
        Waypoint result = this.iterator.next();
        this.destinationHandler.handle(result);
        return result;
    }

    @Override
    public void end(MinimapElementRenderLocation location, C context) {
        this.iterator = null;
        this.deathpoints = false;
        this.destinationHandler.end();
        this.destinationHandler = null;
    }
}

