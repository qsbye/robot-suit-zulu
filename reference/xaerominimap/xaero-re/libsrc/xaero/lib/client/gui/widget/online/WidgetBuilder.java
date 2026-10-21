/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.Widget;

public abstract class WidgetBuilder {
    protected Class<? extends Screen> location;
    protected float horizontalAnchor;
    protected float verticalAnchor;
    protected ClickAction onClick = ClickAction.NOTHING;
    protected HoverAction onHover = HoverAction.NOTHING;
    protected int x;
    protected int y;
    protected String url;
    protected String tooltip;
    protected int minWidgetLevel = Integer.MIN_VALUE;
    protected int maxWidgetLevel = Integer.MAX_VALUE;

    public void setLocation(Class<? extends Screen> location) {
        this.location = location;
    }

    public void setHorizontalAnchor(float horizontalAnchor) {
        this.horizontalAnchor = horizontalAnchor;
    }

    public void setVerticalAnchor(float verticalAnchor) {
        this.verticalAnchor = verticalAnchor;
    }

    public void setOnClick(ClickAction onClick) {
        this.onClick = onClick;
    }

    public void setOnHover(HoverAction onHover) {
        this.onHover = onHover;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setMinWidgetLevel(int minWidgetLevel) {
        this.minWidgetLevel = minWidgetLevel;
    }

    public void setMaxWidgetLevel(int maxWidgetLevel) {
        this.maxWidgetLevel = maxWidgetLevel;
    }

    public boolean validate() {
        return !(this.location == null || this.onHover == HoverAction.TOOLTIP && this.tooltip == null || this.onClick == ClickAction.URL && this.url == null);
    }

    public abstract Widget build();
}

