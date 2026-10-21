/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.factory;

import net.minecraft.network.chat.Component;
import xaero.lib.client.config.option.ui.factory.ViewEnforcedCondition;
import xaero.lib.client.gui.ScreenBase;

public class StandardViewEnforcedConditions {
    public static final ViewEnforcedCondition SHIFT_PRESSED = new ViewEnforcedCondition(ScreenBase::hasShiftDown, (Component)Component.translatable((String)"gui.xaero_config_value_enforced_tooltip_shift_condition_prefix"));
}

