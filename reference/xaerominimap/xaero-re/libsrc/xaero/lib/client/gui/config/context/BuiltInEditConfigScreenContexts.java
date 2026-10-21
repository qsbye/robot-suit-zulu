/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.config.context;

import xaero.lib.client.gui.config.context.EditConfigScreenClientContext;
import xaero.lib.client.gui.config.context.EditConfigScreenServerContext;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;

public class BuiltInEditConfigScreenContexts {
    public static final IEditConfigScreenContext CLIENT = new EditConfigScreenClientContext();
    public static final IEditConfigScreenContext SERVER = new EditConfigScreenServerContext();
}

