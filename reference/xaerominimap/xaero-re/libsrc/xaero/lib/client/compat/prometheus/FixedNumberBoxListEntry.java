/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.compat.prometheus;

import earth.terrarium.prometheus.client.screens.roles.options.entries.NumberBoxListEntry;
import java.util.OptionalInt;
import java.util.OptionalLong;
import net.minecraft.network.chat.Component;

public class FixedNumberBoxListEntry
extends NumberBoxListEntry {
    public FixedNumberBoxListEntry(Number amount, Component component, Component tooltip) {
        super(0, amount instanceof Double, component, tooltip);
        this.setText(String.valueOf(amount != null ? amount : ""));
    }

    public FixedNumberBoxListEntry(Number amount, Component component) {
        super(0, amount instanceof Double, component);
        this.setText(String.valueOf(amount != null ? amount : ""));
    }

    private void setText(String text) {
        this.text = text;
    }

    public OptionalLong getLongValue() {
        try {
            return OptionalLong.of(Long.parseLong(this.getText()));
        }
        catch (NumberFormatException var2) {
            return OptionalLong.empty();
        }
    }

    public OptionalInt getByteValue() {
        try {
            return OptionalInt.of(0xFF & Integer.parseInt(this.getText()));
        }
        catch (NumberFormatException var2) {
            return OptionalInt.empty();
        }
    }
}

