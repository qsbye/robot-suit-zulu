/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;

public class SaddleVariant {
    private final Identifier texture;
    private final boolean saddled;

    public SaddleVariant(Identifier texture, boolean saddled) {
        this.texture = texture;
        this.saddled = saddled;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + this.saddled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SaddleVariant that = (SaddleVariant)o;
        return this.saddled == that.saddled && Objects.equals(this.texture, that.texture);
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.saddled);
    }
}

