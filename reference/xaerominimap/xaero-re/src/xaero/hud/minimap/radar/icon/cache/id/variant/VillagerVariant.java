/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;

public class VillagerVariant {
    private final Identifier texture;
    private final boolean baby;
    private final Identifier type;
    private final Identifier profession;
    private final int level;

    public VillagerVariant(Identifier texture, boolean baby, Identifier type, Identifier profession, int level) {
        this.texture = texture;
        this.baby = baby;
        this.type = type;
        this.profession = profession;
        this.level = level;
    }

    public String toString() {
        return this.texture + "%" + this.baby + "%" + this.type + "%" + this.profession + "%" + this.level;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VillagerVariant that = (VillagerVariant)o;
        return this.baby == that.baby && this.level == that.level && Objects.equals(this.texture, that.texture) && this.type.equals((Object)that.type) && this.profession.equals((Object)that.profession);
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.baby, this.type, this.profession, this.level);
    }
}

