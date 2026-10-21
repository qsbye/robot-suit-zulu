/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.color;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public enum RadarColor {
    BLACK((Component)Component.translatable((String)"gui.xaero_black"), '0', -16777216),
    DARK_BLUE((Component)Component.translatable((String)"gui.xaero_dark_blue"), '1', -16777046),
    DARK_GREEN((Component)Component.translatable((String)"gui.xaero_dark_green"), '2', -16733696),
    DARK_AQUA((Component)Component.translatable((String)"gui.xaero_dark_aqua"), '3', -16733526),
    DARK_RED((Component)Component.translatable((String)"gui.xaero_dark_red"), '4', -5636096),
    DARK_PURPLE((Component)Component.translatable((String)"gui.xaero_dark_purple"), '5', -5635926),
    GOLD((Component)Component.translatable((String)"gui.xaero_gold"), '6', -22016),
    GRAY((Component)Component.translatable((String)"gui.xaero_gray"), '7', -5592406),
    DARK_GRAY((Component)Component.translatable((String)"gui.xaero_dark_gray"), '8', -11184811),
    BLUE((Component)Component.translatable((String)"gui.xaero_blue"), '9', -11184641),
    GREEN((Component)Component.translatable((String)"gui.xaero_green"), 'a', -11141291),
    AQUA((Component)Component.translatable((String)"gui.xaero_aqua"), 'b', -11141121),
    RED((Component)Component.translatable((String)"gui.xaero_red"), 'c', -65536),
    PURPLE((Component)Component.translatable((String)"gui.xaero_purple"), 'd', -43521),
    YELLOW((Component)Component.translatable((String)"gui.xaero_yellow"), 'e', -171),
    WHITE((Component)Component.translatable((String)"gui.xaero_white"), 'f', -1);

    private final Component name;
    private final char format;
    private final int hex;

    private RadarColor(Component name, char format, int hex) {
        this.name = name;
        this.format = format;
        this.hex = hex;
    }

    public Component getName() {
        return this.name;
    }

    public char getFormat() {
        return this.format;
    }

    public int getHex() {
        return this.hex;
    }

    public static RadarColor fromIndex(int index) {
        if (index == -1) {
            return null;
        }
        return RadarColor.values()[Mth.clamp((int)index, (int)0, (int)(RadarColor.values().length - 1))];
    }

    public static RadarColor getRandom() {
        return RadarColor.fromIndex((int)(Math.random() * (double)RadarColor.values().length));
    }
}

