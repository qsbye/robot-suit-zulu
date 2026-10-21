/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.util;

import net.minecraft.network.chat.Component;

public class CategoryConstants {
    public static Component ON = Component.translatable((String)"gui.xaero_on");
    public static Component OFF = Component.translatable((String)"gui.xaero_off");
    public static Component INHERIT = Component.translatable((String)"gui.xaero_category_setting_inherit");

    public static Component getBooleanComponent(boolean value) {
        return value ? ON : OFF;
    }
}

