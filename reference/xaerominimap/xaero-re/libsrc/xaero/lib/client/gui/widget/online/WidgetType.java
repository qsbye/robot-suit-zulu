/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import xaero.lib.client.gui.widget.online.init.ButtonWidgetInitializer;
import xaero.lib.client.gui.widget.online.init.WidgetInitializer;
import xaero.lib.client.gui.widget.online.loader.ButtonWidgetLoader;
import xaero.lib.client.gui.widget.online.loader.ImageWidgetLoader;
import xaero.lib.client.gui.widget.online.loader.TextWidgetLoader;
import xaero.lib.client.gui.widget.online.loader.WidgetLoader;
import xaero.lib.client.gui.widget.online.render.ImageWidgetRenderer;
import xaero.lib.client.gui.widget.online.render.TextWidgetRenderer;
import xaero.lib.client.gui.widget.online.render.WidgetRenderer;

public enum WidgetType {
    IMAGE(new ImageWidgetLoader(), new ImageWidgetRenderer(), null),
    BUTTON(new ButtonWidgetLoader(), null, new ButtonWidgetInitializer()),
    TEXT(new TextWidgetLoader(), new TextWidgetRenderer(), null);

    public final WidgetLoader widgetLoader;
    public final WidgetRenderer widgetRenderer;
    public final WidgetInitializer widgetInit;

    private WidgetType(WidgetLoader widgetLoader, WidgetRenderer widgetRenderer, WidgetInitializer widgetInit) {
        this.widgetLoader = widgetLoader;
        this.widgetRenderer = widgetRenderer;
        this.widgetInit = widgetInit;
    }
}

