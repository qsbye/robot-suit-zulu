/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.module;

import xaero.common.HudMod;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleTransform;

public abstract class ModuleSession<MS extends ModuleSession<MS>> {
    protected final HudMod modMain;
    protected final HudModule<MS> module;

    public ModuleSession(HudMod modMain, HudModule<MS> module) {
        this.modMain = modMain;
        this.module = module;
    }

    public HudModule<MS> getModule() {
        return this.module;
    }

    public HudMod getModMain() {
        return this.modMain;
    }

    public boolean isActive() {
        return this.module.isActive(this.modMain.getHudConfigs().getClientConfigManager());
    }

    public int getEffectiveX(int screenWidth, double screenScale) {
        ModuleTransform transform = this.module.getUsedTransform();
        if (!transform.centered && !transform.fromRight) {
            return transform.x;
        }
        int width = this.getWidth(screenScale);
        if (transform.centered) {
            return screenWidth / 2 - width / 2;
        }
        return screenWidth - transform.x - width;
    }

    public int getEffectiveY(int screenHeight, double screenScale) {
        ModuleTransform transform = this.module.getUsedTransform();
        if (!transform.fromBottom) {
            return transform.y;
        }
        int height = this.getHeight(screenScale);
        return screenHeight - transform.y - height;
    }

    public boolean isFlippedHor() {
        return this.module.getUsedTransform().flippedHor;
    }

    public boolean isFlippedVer() {
        return this.module.getUsedTransform().flippedVer;
    }

    public boolean isCentered() {
        return this.module.getUsedTransform().centered;
    }

    public boolean shouldFlipHorizontally(int screenWidth, double screenScale) {
        boolean flipped = this.isFlippedHor();
        int x = this.getEffectiveX(screenWidth, screenScale);
        int w = this.getWidth(screenScale);
        if (this.isCentered()) {
            return flipped;
        }
        if (flipped) {
            return x + w / 2 < screenWidth / 2;
        }
        return x + w / 2 > screenWidth / 2;
    }

    public boolean shouldFlipVertically(int screenHeight, double screenScale) {
        boolean flipped = this.isFlippedVer();
        int y = this.getEffectiveY(screenHeight, screenScale);
        int h = this.getHeight(screenScale);
        if (flipped) {
            return y + h / 2 < screenHeight / 2;
        }
        return y + h / 2 > screenHeight / 2;
    }

    public void prePotentialRender() {
    }

    public void onPostGameOverlay() {
    }

    public abstract int getWidth(double var1);

    public abstract int getHeight(double var1);

    public abstract void close();
}

