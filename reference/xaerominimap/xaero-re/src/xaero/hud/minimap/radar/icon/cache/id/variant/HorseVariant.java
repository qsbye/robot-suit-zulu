/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.animal.equine.Markings;

public class HorseVariant {
    public static final Map<Markings, Identifier> HORSE_MARKINGS = (Map)Util.make((Object)Maps.newEnumMap(Markings.class), map -> {
        map.put(Markings.NONE, null);
        map.put(Markings.WHITE, Identifier.parse((String)"textures/entity/horse/horse_markings_white.png"));
        map.put(Markings.WHITE_FIELD, Identifier.parse((String)"textures/entity/horse/horse_markings_whitefield.png"));
        map.put(Markings.WHITE_DOTS, Identifier.parse((String)"textures/entity/horse/horse_markings_whitedots.png"));
        map.put(Markings.BLACK_DOTS, Identifier.parse((String)"textures/entity/horse/horse_markings_blackdots.png"));
    });
    private final Identifier texture;
    private final Markings markings;

    public HorseVariant(Identifier texture, Markings markings) {
        this.texture = texture;
        this.markings = markings;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + String.valueOf(HORSE_MARKINGS.get(this.markings));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HorseVariant that = (HorseVariant)o;
        return Objects.equals(this.texture, that.texture) && this.markings == that.markings;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.markings);
    }
}

