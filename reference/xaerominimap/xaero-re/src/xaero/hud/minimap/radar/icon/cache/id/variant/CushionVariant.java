/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.world.item.DyeColor;

public class CushionVariant {
    private final DyeColor color;

    public CushionVariant(DyeColor color) {
        this.color = color;
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CushionVariant that = (CushionVariant)o;
        return this.color == that.color;
    }

    public int hashCode() {
        return Objects.hashCode(this.color);
    }

    public String toString() {
        if (this.color == null) {
            return "default";
        }
        return this.color.toString();
    }
}

