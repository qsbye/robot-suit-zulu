/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.widget.online.Alignment;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.ScalableWidget;
import xaero.lib.client.gui.widget.online.WidgetType;

public class TextWidget
extends ScalableWidget {
    private String text;
    private Alignment alignment;

    public TextWidget(Class<? extends Screen> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, int scaledOffsetX, int scaledOffsetY, String url, String tooltip, String text, Alignment alignment, boolean noGuiScale, double scale, int minWidgetLevel, int maxWidgetLevel) {
        super(WidgetType.TEXT, location, horizontalAnchor, verticalAnchor, onClick, onHover, x, y, scaledOffsetX, scaledOffsetY, url, tooltip, noGuiScale, scale, minWidgetLevel, maxWidgetLevel);
        this.text = text;
        this.alignment = alignment;
    }

    public String getText() {
        return this.text;
    }

    public Alignment getAlignment() {
        return this.alignment;
    }

    @Override
    public int getW() {
        return Minecraft.getInstance().font.width(this.text);
    }

    @Override
    public int getH() {
        return 10;
    }

    @Override
    public int getScaledOffsetX() {
        int pos = super.getScaledOffsetX();
        if (this.alignment == Alignment.RIGHT) {
            pos -= Minecraft.getInstance().font.width(this.text);
        } else if (this.alignment == Alignment.CENTER) {
            pos -= Minecraft.getInstance().font.width(this.text) / 2;
        }
        return pos;
    }
}

