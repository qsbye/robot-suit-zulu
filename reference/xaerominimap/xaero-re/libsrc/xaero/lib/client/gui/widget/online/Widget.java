/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.WidgetType;

public class Widget {
    private WidgetType type;
    private Class<? extends Screen> location;
    private float horizontalAnchor;
    private float verticalAnchor;
    private ClickAction onClick;
    private HoverAction onHover;
    private int x;
    private int y;
    private String url;
    private String tooltip;
    private Tooltip cursorBox;
    private final int minWidgetLevel;
    private final int maxWidgetLevel;

    public Widget(WidgetType type, Class<? extends Screen> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, String url, String tooltip, int minWidgetLevel, int maxWidgetLevel) {
        this.type = type;
        this.location = location;
        this.horizontalAnchor = horizontalAnchor;
        this.verticalAnchor = verticalAnchor;
        this.onClick = onClick;
        this.onHover = onHover;
        this.x = x;
        this.y = y;
        this.url = url;
        this.tooltip = tooltip;
        this.minWidgetLevel = minWidgetLevel;
        this.maxWidgetLevel = maxWidgetLevel;
        if (tooltip != null && !tooltip.isEmpty()) {
            this.cursorBox = new Tooltip(tooltip);
        }
    }

    public WidgetType getType() {
        return this.type;
    }

    public Class<? extends Screen> getLocation() {
        return this.location;
    }

    public float getHorizontalAnchor() {
        return this.horizontalAnchor;
    }

    public float getVerticalAnchor() {
        return this.verticalAnchor;
    }

    public ClickAction getOnClick() {
        return this.onClick;
    }

    public HoverAction getOnHover() {
        return this.onHover;
    }

    public int getX(int width) {
        return (int)((float)width * this.horizontalAnchor + (float)this.x);
    }

    public int getY(int height) {
        return (int)((float)height * this.verticalAnchor + (float)this.y);
    }

    public int getW() {
        return 1;
    }

    public int getH() {
        return 1;
    }

    public int getBoxX(int width, double guiScale) {
        return this.getX(width);
    }

    public int getBoxY(int height, double guiScale) {
        return this.getX(height);
    }

    public int getBoxW(double guiScale) {
        return this.getW();
    }

    public int getBoxH(double guiScale) {
        return this.getH();
    }

    public String getUrl() {
        return this.url;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public Tooltip getCursorBox() {
        return this.cursorBox;
    }

    public int getMinWidgetLevel() {
        return this.minWidgetLevel;
    }

    public int getMaxWidgetLevel() {
        return this.maxWidgetLevel;
    }
}

