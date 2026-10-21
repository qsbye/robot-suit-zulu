/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.preset.action;

public interface IPresetAction<M> {
    public void apply(M var1);

    public void confirm(M var1);

    public void cancel(M var1);
}

