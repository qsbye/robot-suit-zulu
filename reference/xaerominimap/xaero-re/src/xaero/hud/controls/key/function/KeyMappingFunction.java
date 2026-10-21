/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.controls.key.function;

public abstract class KeyMappingFunction {
    private final boolean held;

    protected KeyMappingFunction(boolean held) {
        this.held = held;
    }

    public abstract void onPress();

    public abstract void onRelease();

    public boolean isHeld() {
        return this.held;
    }
}

