/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import xaero.lib.client.gui.widget.online.WidgetClickHandler;
import xaero.lib.client.gui.widget.online.WidgetUrlClickHandler;

public enum ClickAction {
    NOTHING(null),
    URL(new WidgetUrlClickHandler());

    public final WidgetClickHandler clickHandler;

    private ClickAction(WidgetClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
}

