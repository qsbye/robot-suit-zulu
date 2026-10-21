/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

public interface ISettingEntry {
    public String getStringForSearch();

    public AbstractWidget createWidget(int var1, int var2, int var3);
}

