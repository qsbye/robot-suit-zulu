/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import java.util.Iterator;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementCollector;

public class PlayerTrackerMinimapElementRenderProvider<C>
extends MinimapElementRenderProvider<PlayerTrackerMinimapElement<?>, C> {
    private PlayerTrackerMinimapElementCollector collector;
    private Iterator<PlayerTrackerMinimapElement<?>> iterator;

    public PlayerTrackerMinimapElementRenderProvider(PlayerTrackerMinimapElementCollector collector) {
        this.collector = collector;
    }

    @Override
    public void begin(MinimapElementRenderLocation location, C context) {
        this.iterator = this.collector.getElements().iterator();
    }

    @Override
    public boolean hasNext(MinimapElementRenderLocation location, C context) {
        return this.iterator != null && this.iterator.hasNext();
    }

    @Override
    public PlayerTrackerMinimapElement<?> getNext(MinimapElementRenderLocation location, C context) {
        return this.iterator.next();
    }

    @Override
    public void end(MinimapElementRenderLocation location, C context) {
        this.iterator = null;
    }
}

