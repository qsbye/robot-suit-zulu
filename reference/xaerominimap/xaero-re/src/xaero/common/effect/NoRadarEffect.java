/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import xaero.common.effect.MinimapStatusEffect;

public class NoRadarEffect
extends MinimapStatusEffect {
    public NoRadarEffect(MobEffectCategory type) {
        super(type, -16777216, "no_entity_radar");
    }
}

