/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config;

import net.minecraft.network.chat.Component;

public class MinimapConfigConstants {
    public static final Component[] DOTS_STYLES = new Component[]{Component.translatable((String)"gui.xaero_dots_style_default"), Component.translatable((String)"gui.xaero_dots_style_legacy")};
    public static final Component[] MAIN_ENTITY_TYPES = new Component[]{Component.translatable((String)"gui.xaero_crosshair"), Component.translatable((String)"gui.xaero_dot"), Component.translatable((String)"gui.xaero_arrow")};
    public static Component[] ARROW_COLOR_NAMES = new Component[]{Component.translatable((String)"gui.xaero_red"), Component.translatable((String)"gui.xaero_green"), Component.translatable((String)"gui.xaero_blue"), Component.translatable((String)"gui.xaero_yellow"), Component.translatable((String)"gui.xaero_purple"), Component.translatable((String)"gui.xaero_white"), Component.translatable((String)"gui.xaero_black"), Component.translatable((String)"gui.xaero_legacy_color")};
    public static float[][] ARROW_COLORS = new float[][]{{0.8f, 0.1f, 0.1f, 1.0f}, {0.09f, 0.57f, 0.0f, 1.0f}, {0.0f, 0.55f, 1.0f, 1.0f}, {1.0f, 0.93f, 0.0f, 1.0f}, {0.73f, 0.33f, 0.83f, 1.0f}, {1.0f, 1.0f, 1.0f, 1.0f}, {0.0f, 0.0f, 0.0f, 1.0f}, {0.4588f, 0.0f, 0.0f, 1.0f}};
    public static final Component AUTO_SCALE_COMPONENT = Component.translatable((String)"gui.xaero_ui_scale_auto");
    public static final Component MINECRAFT_SCALE_COMPONENT = Component.translatable((String)"gui.xaero_ui_scale_mc");
    public static final float DEFAULT_WORLD_WAYPOINT_SCALE = 0.8f;
    public static final float WORLD_WAYPOINT_MINECRAFT_SCALE = 0.02666667f;
    public static final double WAYPOINT_ICON_WORLD_SCALE = 0.02133333496749401;
    public static final Component[] MULTIPLE_WAYPOINTS_INFO_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_while_sneaking"), Component.translatable((String)"gui.xaero_multiple_waypoints_always")};
    public static final Component[] WAYPOINT_DISTANCE_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_looking_at"), Component.translatable((String)"gui.xaero_all")};
    public static final Component UNLIMITED_COMPONENT = Component.translatable((String)"gui.xaero_unlimited");
    public static final Component CONVERT_DISTANCE_NEVER_COMPONENT = Component.translatable((String)"gui.xaero_auto_convert_wp_distance_km_never");
    public static final Component[] HIDE_WORLD_NAMES_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_partial"), Component.translatable((String)"gui.xaero_full")};
    public static final Component[] COMPASS_LOCATION_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_minimap_compass_inside_frame"), Component.translatable((String)"gui.xaero_minimap_compass_on_frame")};
    public static final Component[] COLOR_NAMES = new Component[]{Component.translatable((String)"gui.xaero_black"), Component.translatable((String)"gui.xaero_dark_blue"), Component.translatable((String)"gui.xaero_dark_green"), Component.translatable((String)"gui.xaero_dark_aqua"), Component.translatable((String)"gui.xaero_dark_red"), Component.translatable((String)"gui.xaero_dark_purple"), Component.translatable((String)"gui.xaero_gold"), Component.translatable((String)"gui.xaero_gray"), Component.translatable((String)"gui.xaero_dark_gray"), Component.translatable((String)"gui.xaero_blue"), Component.translatable((String)"gui.xaero_green"), Component.translatable((String)"gui.xaero_aqua"), Component.translatable((String)"gui.xaero_red"), Component.translatable((String)"gui.xaero_purple"), Component.translatable((String)"gui.xaero_yellow"), Component.translatable((String)"gui.xaero_white")};
    public static final int[] COLORS = new int[]{-16777216, -16777046, -16733696, -16733526, -5636096, -5635926, -22016, -5592406, -11184811, -11184641, -11141291, -11141121, -65536, -43521, -171, -1};
    public static final String[] COLOR_CODES = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    public static final Component[] BLOCK_COLOR_NAMES = new Component[]{Component.translatable((String)"gui.xaero_accurate"), Component.translatable((String)"gui.xaero_vanilla")};
    public static final Component[] TERRAIN_SLOPES_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_slopes_legacy"), Component.translatable((String)"gui.xaero_slopes_default_3d"), Component.translatable((String)"gui.xaero_slopes_default_2d")};
    public static final Component[] MINIMAP_SHAPE_NAMES = new Component[]{Component.translatable((String)"gui.xaero_minimap_shape_square"), Component.translatable((String)"gui.xaero_minimap_shape_circle")};
    public static final int MAX_ZOOM = 6;
    public static final Component[] FRAME_NAMES = new Component[]{Component.translatable((String)"gui.xaero_minimap_frame_default"), Component.translatable((String)"gui.xaero_minimap_frame_colored_thick"), Component.translatable((String)"gui.xaero_minimap_frame_colored_thin"), Component.translatable((String)"gui.xaero_off")};
    public static final Component[] PUSH_BOX_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_pushbox_normal"), Component.translatable((String)"gui.xaero_pushbox_screen_height")};
    public static final Component[] LIGHT_NAMES = new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_light_block"), Component.translatable((String)"gui.xaero_light_sky"), Component.translatable((String)"gui.xaero_light_all"), Component.translatable((String)"gui.xaero_light_both2")};
    public static final Component[] INFO_DISPLAY_ALIGNMENT_NAMES = new Component[]{Component.translatable((String)"gui.xaero_center"), Component.translatable((String)"gui.xaero_left"), Component.translatable((String)"gui.xaero_right")};
}

