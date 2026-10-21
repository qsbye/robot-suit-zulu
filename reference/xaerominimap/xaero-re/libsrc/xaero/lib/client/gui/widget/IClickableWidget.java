/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget;

import java.util.function.Supplier;
import xaero.lib.client.gui.widget.ITooltipHaver;
import xaero.lib.client.gui.widget.Tooltip;

public interface IClickableWidget
extends ITooltipHaver {
    public void setXaero_tooltip(Supplier<Tooltip> var1);
}

