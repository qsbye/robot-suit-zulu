/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.gui.IXaeroConfirmScreenCallback;

public class GuiReset
extends ConfirmScreen {
    public GuiReset(IXaeroConfirmScreenCallback callback, Screen parent, Screen escScreen) {
        super(callback::accept, (Component)Component.translatable((String)"gui.xaero_reset_config_profile_default_message"), (Component)Component.translatable((String)"gui.xaero_reset_config_profile_default_message2"));
    }
}

