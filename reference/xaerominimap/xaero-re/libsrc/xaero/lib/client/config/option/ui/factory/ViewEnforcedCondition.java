/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.factory;

import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.Component;

public class ViewEnforcedCondition {
    private final BooleanSupplier condition;
    private final Component tooltip;

    public ViewEnforcedCondition(BooleanSupplier condition, Component tooltip) {
        this.condition = condition;
        this.tooltip = tooltip;
    }

    public BooleanSupplier getCondition() {
        return this.condition;
    }

    public Component getTooltip() {
        return this.tooltip;
    }
}

