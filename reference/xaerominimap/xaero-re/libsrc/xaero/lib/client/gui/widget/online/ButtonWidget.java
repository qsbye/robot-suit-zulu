/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetType;

public class ButtonWidget
extends Widget {
    private String buttonText;
    private int buttonW;
    private int buttonH;

    public ButtonWidget(Class<? extends Screen> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, String url, String tooltip, String buttonText, int buttonW, int buttonH, int minWidgetLevel, int maxWidgetLevel) {
        super(WidgetType.BUTTON, location, horizontalAnchor, verticalAnchor, onClick, onHover, x, y, url, tooltip, minWidgetLevel, maxWidgetLevel);
        this.buttonText = buttonText;
        this.buttonW = buttonW;
        this.buttonH = buttonH;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    @Override
    public int getW() {
        return this.buttonW;
    }

    @Override
    public int getH() {
        return this.buttonH;
    }
}

