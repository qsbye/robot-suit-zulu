/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox.effect;

import xaero.hud.pushbox.FullHeightShiftPushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;
import xaero.hud.pushbox.effect.PotionEffectsPushBox;

public class PotionEffectsShiftPushBox
extends FullHeightShiftPushBox
implements IPotionEffectsPushBox {
    private boolean hasNegative;

    public PotionEffectsShiftPushBox() {
        super(0, 0, 1.0f);
    }

    @Override
    public int getX(int width, int height) {
        return super.getX(width, height) - this.getW(width, height);
    }

    @Override
    protected int getShift() {
        return this.hasNegative ? 53 : 27;
    }

    @Override
    public void update() {
        super.update();
        this.hasNegative = false;
        this.w = PotionEffectsPushBox.calculatePotionDisplayWidth(this);
    }

    @Override
    public void postUpdate() {
        super.postUpdate();
        this.active = false;
    }

    @Override
    public void setHasNegative(boolean b) {
        this.hasNegative = b;
    }
}

