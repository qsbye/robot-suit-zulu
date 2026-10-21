/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;

public class EndermanVariant {
    private final Identifier texture;
    private final boolean angry;

    public EndermanVariant(Identifier texture, boolean angry) {
        this.texture = texture;
        this.angry = angry;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + this.angry;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EndermanVariant that = (EndermanVariant)o;
        return this.angry == that.angry && Objects.equals(this.texture, that.texture);
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.angry);
    }
}

