/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import xaero.common.effect.MinimapStatusEffect;

public class NoCaveMapsEffect
extends MinimapStatusEffect {
    public NoCaveMapsEffect(MobEffectCategory type) {
        super(type, -16777216, "no_cave_maps");
    }
}

