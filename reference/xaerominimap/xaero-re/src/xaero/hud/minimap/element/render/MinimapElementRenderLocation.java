/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.element.render;

public class MinimapElementRenderLocation {
    public static final MinimapElementRenderLocation UNKNOWN = new MinimapElementRenderLocation("unknown");
    public static final MinimapElementRenderLocation IN_MINIMAP = new MinimapElementRenderLocation("in_minimap");
    public static final MinimapElementRenderLocation OVER_MINIMAP = new MinimapElementRenderLocation("over_minimap");
    public static final MinimapElementRenderLocation IN_WORLD = new MinimapElementRenderLocation("in_world");
    public static final MinimapElementRenderLocation WORLD_MAP = new MinimapElementRenderLocation("world_map");
    public static final MinimapElementRenderLocation WORLD_MAP_MENU = new MinimapElementRenderLocation("world_map_menu");
    private final String name;

    public MinimapElementRenderLocation(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

