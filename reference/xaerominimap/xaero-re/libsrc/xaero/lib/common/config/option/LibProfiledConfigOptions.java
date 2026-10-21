/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option;

import java.util.ArrayList;
import java.util.List;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

public class LibProfiledConfigOptions {
    private static final List<ConfigOption<?>> ALL = new ArrayList();

    public static void registerAll(ConfigOptionManager manager) {
        for (ConfigOption<?> option : ALL) {
            manager.register(option);
        }
    }
}

