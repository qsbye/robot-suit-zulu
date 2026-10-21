/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.factory;

import net.minecraft.client.gui.components.AbstractWidget;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;

@FunctionalInterface
public interface IConfigOptionWidgetFactory<CT extends ConfigOption<?>> {
    public AbstractWidget create(CT var1, Config var2, Config var3, int var4, int var5, int var6, boolean var7, Runnable var8, ConfigChannel var9, boolean var10);
}

