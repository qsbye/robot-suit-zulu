/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MyBigButton
extends Button.Plain {
    protected int id;

    public MyBigButton(int id, int par1, int par2, int par3, int par4, int par5, Component par6Str, Button.OnPress onPress) {
        super(par1, par2, par3, par4, par6Str, onPress, DEFAULT_NARRATION);
        this.id = id;
    }

    public MyBigButton(int id, int par1, int par2, Component par5Str, Button.OnPress onPress) {
        super(par1, par2, 200, 20, par5Str, onPress, DEFAULT_NARRATION);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

