/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.primary.option;

import java.util.ArrayList;
import java.util.List;
import xaero.lib.client.config.option.ClientConfigOptionManager;
import xaero.lib.common.config.option.ConfigOption;

public class LibPrimaryClientConfigOptions {
    private static final List<ConfigOption<?>> ALL = new ArrayList();

    public static void registerAll(ClientConfigOptionManager manager) {
        for (ConfigOption<?> option : ALL) {
            manager.register(option);
        }
    }
}

