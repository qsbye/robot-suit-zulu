/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker.system;

import java.util.Iterator;
import xaero.hud.minimap.player.tracker.system.ITrackedPlayerReader;

public interface IRenderedPlayerTracker<P> {
    public ITrackedPlayerReader<P> getReader();

    public Iterator<P> getTrackedPlayerIterator();
}

