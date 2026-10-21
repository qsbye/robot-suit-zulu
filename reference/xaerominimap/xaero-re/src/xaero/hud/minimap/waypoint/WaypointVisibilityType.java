/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import net.minecraft.network.chat.Component;

public enum WaypointVisibilityType {
    LOCAL((Component)Component.translatable((String)"gui.xaero_waypoints_local"), false),
    GLOBAL((Component)Component.translatable((String)"gui.xaero_waypoints_global"), true),
    WORLD_MAP_LOCAL((Component)Component.translatable((String)"gui.xaero_waypoint_visibility_type_world_map_local"), false),
    WORLD_MAP_GLOBAL((Component)Component.translatable((String)"gui.xaero_waypoint_visibility_type_world_map_global"), true);

    private final Component translation;
    private final boolean global;

    private WaypointVisibilityType(Component translation, boolean global) {
        this.translation = translation;
        this.global = global;
    }

    public Component getTranslation() {
        return this.translation;
    }

    public boolean isGlobal() {
        return this.global;
    }
}

