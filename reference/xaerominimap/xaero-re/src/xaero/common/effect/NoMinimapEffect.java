/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import xaero.common.effect.MinimapStatusEffect;

public class NoMinimapEffect
extends MinimapStatusEffect {
    public NoMinimapEffect(MobEffectCategory type) {
        super(type, -16777216, "no_minimap");
    }
}

