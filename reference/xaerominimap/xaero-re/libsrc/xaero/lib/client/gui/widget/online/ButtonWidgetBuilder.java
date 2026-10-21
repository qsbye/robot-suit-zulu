/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import xaero.lib.client.gui.widget.online.ButtonWidget;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetBuilder;

public class ButtonWidgetBuilder
extends WidgetBuilder {
    protected String buttonText;
    protected int buttonW;
    protected int buttonH;

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public void setButtonW(int buttonW) {
        this.buttonW = buttonW;
    }

    public void setButtonH(int buttonH) {
        this.buttonH = buttonH;
    }

    @Override
    public boolean validate() {
        return super.validate() && this.buttonText != null && this.buttonW > 0 && this.buttonH > 0;
    }

    @Override
    public Widget build() {
        return new ButtonWidget(this.location, this.horizontalAnchor, this.verticalAnchor, this.onClick, this.onHover, this.x, this.y, this.url, this.tooltip, this.buttonText, this.buttonW, this.buttonH, this.minWidgetLevel, this.maxWidgetLevel);
    }
}

