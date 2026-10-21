/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.widget.ITooltipHaver;
import xaero.lib.client.gui.widget.Tooltip;

public class TooltipButton
extends Button.Plain
implements ITooltipHaver {
    private Supplier<Tooltip> tooltipSupplier;

    public TooltipButton(int x, int y, int w, int h, Component text, Button.OnPress onPress, Supplier<Tooltip> tooltipSupplier) {
        super(x, y, w, h, text, onPress, DEFAULT_NARRATION);
        this.tooltipSupplier = tooltipSupplier;
    }

    public Supplier<Tooltip> getXaero_tooltip() {
        return this.tooltipSupplier;
    }
}

