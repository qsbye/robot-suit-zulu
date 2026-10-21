/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox.boss;

import xaero.hud.pushbox.PushBox;
import xaero.hud.pushbox.boss.IBossHealthPushBox;

public class BossHealthPushBox
extends PushBox
implements IBossHealthPushBox {
    public BossHealthPushBox() {
        super(-92, 0, 184, 0, 0.5f, 0.0f, 0);
    }

    @Override
    public void postUpdate() {
        super.postUpdate();
        this.h = 0;
        this.active = false;
    }

    @Override
    public void setLastBossHealthHeight(int h) {
        this.h = h;
    }
}

