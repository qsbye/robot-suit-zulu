/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id;

import java.util.Objects;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmor;

public class RadarIconKey {
    private final Object variant;
    private final RadarIconArmor armor;

    public RadarIconKey(Object variant, RadarIconArmor armor) {
        this.variant = variant;
        this.armor = armor;
    }

    public Object getVariant() {
        return this.variant;
    }

    public String toString() {
        return "RadarIconKey{" + String.valueOf(this.variant) + ", " + String.valueOf(this.armor) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RadarIconKey that = (RadarIconKey)o;
        return this.variant.equals(that.variant) && Objects.equals(this.armor, that.armor);
    }

    public int hashCode() {
        return Objects.hash(this.variant, this.armor);
    }
}

