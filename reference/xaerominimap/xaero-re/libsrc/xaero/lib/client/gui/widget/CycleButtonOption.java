/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget;

import java.util.Objects;

public class CycleButtonOption<T> {
    private final T value;

    public CycleButtonOption(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CycleButtonOption that = (CycleButtonOption)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}

