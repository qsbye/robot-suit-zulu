/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.compass.render;

import net.minecraft.network.chat.Component;

public enum CardinalDirection {
    NORTH((Component)Component.translatable((String)"gui.xaero_compass_north")),
    EAST((Component)Component.translatable((String)"gui.xaero_compass_east")),
    SOUTH((Component)Component.translatable((String)"gui.xaero_compass_south")),
    WEST((Component)Component.translatable((String)"gui.xaero_compass_west"));

    private final Component initials;

    private CardinalDirection(Component initials) {
        this.initials = initials;
    }

    public Component getInitials() {
        return this.initials;
    }
}

