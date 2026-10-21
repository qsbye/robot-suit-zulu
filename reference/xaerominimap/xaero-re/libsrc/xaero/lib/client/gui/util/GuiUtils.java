/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.util;

import net.minecraft.client.gui.components.EditBox;

public class GuiUtils {
    public static void setFieldText(EditBox field, String text) {
        GuiUtils.setFieldText(field, text, -1);
    }

    public static void setFieldText(EditBox field, String text, int color) {
        field.setTextColor(color);
        if (field.getValue().equals(text)) {
            return;
        }
        field.setValue(text);
    }
}

