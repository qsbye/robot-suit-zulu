/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public interface WidgetScreen {
    public <S extends Screen> S getScreen();

    public void addButtonVisible(AbstractWidget var1);
}

