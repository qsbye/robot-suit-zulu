/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.factory;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;

@FunctionalInterface
public interface ICustomOptionEditScreenFactory<CT extends ConfigOption<?>> {
    public Screen get(EditConfigScreen var1, Screen var2, Config var3, Config var4, CT var5, Runnable var6, boolean var7, boolean var8);
}

