/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

import xaero.hud.minimap.element.render.MinimapElementRenderLocation;

public abstract class MinimapElementRenderProvider<E, RC> {
    public abstract void begin(MinimapElementRenderLocation var1, RC var2);

    public abstract boolean hasNext(MinimapElementRenderLocation var1, RC var2);

    public abstract E getNext(MinimapElementRenderLocation var1, RC var2);

    public E setupContextAndGetNext(MinimapElementRenderLocation location, RC context) {
        return this.getNext(location, context);
    }

    public abstract void end(MinimapElementRenderLocation var1, RC var2);
}

