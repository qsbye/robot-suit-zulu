/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.ISettingEntry;

public class TextSettingEntry
implements ISettingEntry {
    private final Supplier<Component> text;

    public TextSettingEntry(Supplier<Component> text) {
        this.text = text;
    }

    @Override
    public String getStringForSearch() {
        return this.text.get().getString();
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int w) {
        Button result = Button.builder((Component)this.text.get(), b -> {}).bounds(x, y, w, 20).build();
        result.active = false;
        return result;
    }
}

