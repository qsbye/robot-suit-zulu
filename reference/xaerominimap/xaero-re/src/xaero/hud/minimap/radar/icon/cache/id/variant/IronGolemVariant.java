/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Crackiness;

public class IronGolemVariant {
    private final Identifier texture;
    private final Crackiness.Level cracks;

    public IronGolemVariant(Identifier texture, Crackiness.Level cracks) {
        this.texture = texture;
        this.cracks = cracks;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + String.valueOf(this.cracks);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IronGolemVariant that = (IronGolemVariant)o;
        return Objects.equals(this.texture, that.texture) && this.cracks == that.cracks;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.cracks);
    }
}

