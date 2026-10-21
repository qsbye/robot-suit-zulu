/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MinimapStatusEffect
extends MobEffect {
    private Identifier id;

    protected MinimapStatusEffect(MobEffectCategory type, int color, String idPrefix) {
        super(type, color);
        String suffix = type == MobEffectCategory.HARMFUL ? "_harmful" : (type == MobEffectCategory.BENEFICIAL ? "_beneficial" : "");
        this.setRegistryName(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)(idPrefix + suffix)));
    }

    protected void setRegistryName(Identifier id) {
        this.id = id;
    }

    public Identifier getRegistryName() {
        return this.id;
    }
}

