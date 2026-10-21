/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.DyeColor;

public class TropicalFishVariant {
    private final Identifier texture;
    private final TropicalFish.Pattern pattern;
    private final DyeColor baseColor;
    private final DyeColor patternColor;

    public TropicalFishVariant(Identifier texture, TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
        this.texture = texture;
        this.pattern = pattern;
        this.baseColor = baseColor;
        this.patternColor = patternColor;
    }

    public String toString() {
        return this.texture + "%" + this.pattern + "%" + this.baseColor + "%" + this.patternColor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TropicalFishVariant that = (TropicalFishVariant)o;
        return Objects.equals(this.texture, that.texture) && this.pattern == that.pattern && this.baseColor == that.baseColor && this.patternColor == that.patternColor;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.pattern, this.baseColor, this.patternColor);
    }
}

