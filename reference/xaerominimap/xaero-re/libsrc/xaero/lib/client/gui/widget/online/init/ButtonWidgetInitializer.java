/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.init;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.widget.online.ButtonWidget;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetScreen;
import xaero.lib.client.gui.widget.online.init.WidgetInitializer;

public class ButtonWidgetInitializer
implements WidgetInitializer {
    @Override
    public void init(WidgetScreen screen, int width, int height, Widget widget) {
        ButtonWidget buttonWidget = (ButtonWidget)widget;
        screen.addButtonVisible((AbstractWidget)Button.builder((Component)Component.literal((String)buttonWidget.getButtonText()), b -> widget.getOnClick().clickHandler.onClick(this.toScreen(screen), widget)).bounds(widget.getX(width), widget.getY(height), buttonWidget.getW(), buttonWidget.getH()).build());
    }

    private Screen toScreen(WidgetScreen screen) {
        Object result = screen.getScreen();
        if (result == screen) {
            return result;
        }
        throw new RuntimeException("Incorrect usage of " + String.valueOf(this.getClass()));
    }
}

