/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

public enum WaypointColor {
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
    WHITE((Component)Component.translatable((String)"gui.xaero_white"), 'f', -1),
    MAGENTA((Component)Component.translatable((String)"gui.xaero_magenta"), 'f', DyeColor.MAGENTA.getTextColor() | 0xFF000000),
    LIGHT_BLUE((Component)Component.translatable((String)"gui.xaero_light_blue"), 'f', DyeColor.LIGHT_BLUE.getTextColor() | 0xFF000000),
    LIME((Component)Component.translatable((String)"gui.xaero_lime"), 'f', DyeColor.LIME.getTextColor() | 0xFF000000),
    PINK((Component)Component.translatable((String)"gui.xaero_pink"), 'f', DyeColor.PINK.getTextColor() | 0xFF000000),
    BROWN((Component)Component.translatable((String)"gui.xaero_brown"), 'f', DyeColor.BROWN.getTextColor() | 0xFF000000);

    private final Component name;
    private final char format;
    private final int hex;

    private WaypointColor(Component name, char format, int hex) {
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

    public static WaypointColor fromIndex(int index) {
        return WaypointColor.values()[Mth.clamp((int)index, (int)0, (int)(WaypointColor.values().length - 1))];
    }

    public static WaypointColor getRandom() {
        return WaypointColor.fromIndex((int)(Math.random() * (double)WaypointColor.values().length));
    }

    public static WaypointColor fromDye(DyeColor dyeColor) {
        switch (dyeColor) {
            case ORANGE: {
                return GOLD;
            }
            case MAGENTA: {
                return MAGENTA;
            }
            case LIGHT_BLUE: {
                return LIGHT_BLUE;
            }
            case YELLOW: {
                return YELLOW;
            }
            case LIME: {
                return LIME;
            }
            case PINK: {
                return PINK;
            }
            case GRAY: {
                return DARK_GRAY;
            }
            case LIGHT_GRAY: {
                return GRAY;
            }
            case CYAN: {
                return AQUA;
            }
            case PURPLE: {
                return PURPLE;
            }
            case BLUE: {
                return BLUE;
            }
            case BROWN: {
                return BROWN;
            }
            case GREEN: {
                return GREEN;
            }
            case RED: {
                return RED;
            }
            case BLACK: {
                return BLACK;
            }
        }
        return WHITE;
    }
}

