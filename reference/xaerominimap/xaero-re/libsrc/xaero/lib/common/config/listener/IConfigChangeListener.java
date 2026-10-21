/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.listener;

import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;

public interface IConfigChangeListener {
    public void onChange(Config var1, ConfigOption<?> var2);

    public void onFullChange(Config var1);

    public void onRemoved(Config var1);
}

