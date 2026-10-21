/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MySmallButton
extends Button.Plain {
    protected int id;

    public MySmallButton(int id, int par1, int par2, Component par5Str, Button.OnPress onPress) {
        super(par1, par2, 150, 20, par5Str, onPress, DEFAULT_NARRATION);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

