/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox.effect;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import xaero.hud.pushbox.PushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;

public class PotionEffectsPushBox
extends PushBox
implements IPotionEffectsPushBox {
    private boolean hasNegative;

    public PotionEffectsPushBox() {
        super(0, 0, 0, 0, 1.0f, 0.0f, 53);
    }

    @Override
    public int getX(int width, int height) {
        return super.getX(width, height) - this.getW(width, height);
    }

    @Override
    public void update() {
        super.update();
        this.hasNegative = false;
        this.w = PotionEffectsPushBox.calculatePotionDisplayWidth(this);
        this.h = this.hasNegative ? 53 : 27;
    }

    @Override
    public void postUpdate() {
        super.postUpdate();
        this.active = false;
    }

    protected static int calculatePotionDisplayWidth(IPotionEffectsPushBox potionEffectBox) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Collection collection = player.getActiveEffects();
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        int positiveCount = 0;
        int negativeCount = 0;
        for (MobEffectInstance effectInstance : collection) {
            Holder effect;
            if (!effectInstance.showIcon() || (effect = effectInstance.getEffect()) == null) continue;
            if (!((MobEffect)effect.value()).isBeneficial()) {
                potionEffectBox.setHasNegative(true);
                ++negativeCount;
                continue;
            }
            ++positiveCount;
        }
        if (positiveCount + negativeCount == 0) {
            return 0;
        }
        return Math.max(positiveCount, negativeCount) * 25 + 1;
    }

    @Override
    public void setHasNegative(boolean b) {
        this.hasNegative = b;
    }
}

