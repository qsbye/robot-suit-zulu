/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

public class GuiDropdownHelper<T> {
    protected int current;
    protected int auto;
    protected T[] keys;
    protected String[] options;

    public T getCurrentKey() {
        return this.keys[this.current];
    }

    public String getCurrentName() {
        return this.options[this.current];
    }
}

