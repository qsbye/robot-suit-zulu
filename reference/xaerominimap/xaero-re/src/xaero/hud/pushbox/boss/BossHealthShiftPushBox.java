/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox.boss;

import xaero.hud.pushbox.FullHeightShiftPushBox;
import xaero.hud.pushbox.boss.IBossHealthPushBox;

public class BossHealthShiftPushBox
extends FullHeightShiftPushBox
implements IBossHealthPushBox {
    public int lastBossHealthHeight;

    public BossHealthShiftPushBox() {
        super(-92, 184, 0.5f);
    }

    @Override
    protected int getShift() {
        return this.lastBossHealthHeight;
    }

    @Override
    public void postUpdate() {
        super.postUpdate();
        this.lastBossHealthHeight = 0;
        this.active = false;
    }

    @Override
    public void setLastBossHealthHeight(int h) {
        this.lastBossHealthHeight = h;
    }
}

