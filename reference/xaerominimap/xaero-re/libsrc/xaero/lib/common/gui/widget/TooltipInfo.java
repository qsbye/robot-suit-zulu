/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.gui.widget;

import net.minecraft.network.chat.Component;

public class TooltipInfo {
    public final Component text;
    public final boolean flippedByDefault;
    public final boolean autoLineBreak;

    public TooltipInfo(Component text, boolean flippedByDefault, boolean autoLineBreak) {
        this.text = text;
        this.flippedByDefault = flippedByDefault;
        this.autoLineBreak = autoLineBreak;
    }

    public TooltipInfo(String translationCode, boolean autoLineBreak, boolean flippedByDefault) {
        this((Component)Component.translatable((String)translationCode), flippedByDefault, autoLineBreak);
    }

    public TooltipInfo(String translationCode) {
        this(translationCode, true, false);
    }
}

