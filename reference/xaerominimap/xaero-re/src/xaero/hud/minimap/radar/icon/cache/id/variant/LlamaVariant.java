/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class LlamaVariant {
    private final Identifier texture;
    private final boolean trader;
    private final Item swag;

    public LlamaVariant(Identifier texture, boolean trader, Item swag) {
        this.texture = texture;
        this.trader = trader;
        this.swag = swag;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + this.trader + "%" + String.valueOf(this.swag);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LlamaVariant that = (LlamaVariant)o;
        return this.trader == that.trader && Objects.equals(this.texture, that.texture) && this.swag == that.swag;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.trader, this.swag);
    }
}

