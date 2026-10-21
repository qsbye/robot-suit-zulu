/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import xaero.lib.client.gui.widget.IClickableWidget;
import xaero.lib.client.gui.widget.Tooltip;

@Mixin(value={AbstractButton.class})
public class MixinAbstractButton
implements IClickableWidget {
    private Supplier<Tooltip> xaero_tooltip;

    @Override
    public Supplier<Tooltip> getXaero_tooltip() {
        return this.xaero_tooltip;
    }

    @Override
    public void setXaero_tooltip(Supplier<Tooltip> tooltip) {
        this.xaero_tooltip = tooltip;
    }
}

