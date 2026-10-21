/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget;

import java.util.function.DoubleConsumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.widget.IClickableWidget;
import xaero.lib.client.gui.widget.Tooltip;

public class XaeroSliderWidget
extends AbstractSliderButton
implements IClickableWidget {
    private Supplier<Tooltip> tooltip;
    private final DoubleConsumer valueListener;
    private final Supplier<Component> labelGetter;

    public XaeroSliderWidget(int x, int y, int w, int h, Component label, double value, DoubleConsumer valueListener, Supplier<Component> labelGetter) {
        super(x, y, w, h, label, value);
        this.valueListener = valueListener;
        this.labelGetter = labelGetter;
    }

    protected void applyValue() {
        this.valueListener.accept(this.value);
    }

    protected void updateMessage() {
        this.setMessage(this.labelGetter.get());
    }

    @Override
    public void setXaero_tooltip(Supplier<Tooltip> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Supplier<Tooltip> getXaero_tooltip() {
        return this.tooltip;
    }
}

