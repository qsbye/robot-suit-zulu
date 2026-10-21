/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.widget;

import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.common.gui.GuiInfoDisplayEdit;
import xaero.hud.minimap.info.widget.InfoDisplayCycleButton;
import xaero.hud.minimap.info.widget.InfoDisplayWidgetFactory;

public class InfoDisplayCycleWidgetFactory<T>
implements InfoDisplayWidgetFactory<T> {
    private final List<T> values;
    private final List<Component> valueNames;

    public InfoDisplayCycleWidgetFactory(List<T> values, List<Component> valueNames) {
        this.values = values;
        this.valueNames = valueNames;
    }

    @Override
    public AbstractWidget create(int x, int y, int w, int h, GuiInfoDisplayEdit.MoveableEntry<T> entry, Runnable onChange, boolean includeNull) {
        return InfoDisplayCycleButton.Builder.begin().setBounds(x, y, w, h).setEntry(entry).setValues(this.values, this.valueNames).setOnChange(onChange).setIncludeNull(includeNull).build();
    }
}

