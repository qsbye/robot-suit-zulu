/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;

public interface IScreenBase
extends IDropDownContainer {
    public boolean shouldSkipWorldRender();

    public Screen getEscape();
}

