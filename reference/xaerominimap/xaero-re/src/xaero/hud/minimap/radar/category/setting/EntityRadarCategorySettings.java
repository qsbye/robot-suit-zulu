/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category.setting;

import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.setting.EditorSettingType;
import xaero.hud.category.util.CategoryConstants;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.lib.common.gui.widget.TooltipInfo;

public class EntityRadarCategorySettings {
    public static final String[] ENTITY_ICONS_OPTIONS = new String[]{"gui.xaero_icons_off", "gui.xaero_icons_list", "gui.xaero_icons_always", "-"};
    public static final String[] ENTITY_NAMES_OPTIONS = new String[]{"gui.xaero_names_off", "gui.xaero_names_list", "gui.xaero_names_always", "-"};
    public static final String[] RADAR_OVER_MAP_OPTIONS = new String[]{"gui.xaero_radar_over_map_never", "gui.xaero_radar_over_map_list", "gui.xaero_radar_over_map_always", "-"};
    public static final Map<String, ObjectCategorySetting<?>> SETTINGS = EntityRadarCategoryConstants.MAP_FACTORY.get();
    public static final List<ObjectCategorySetting<?>> SETTINGS_LIST = EntityRadarCategoryConstants.LIST_FACTORY.get();
    public static final ObjectCategorySetting<Boolean> DISPLAYED = ObjectCategorySetting.Builder.begin().setId("displayed").setDefaultValue(true).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_displayed")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(1).setIndexReader(x -> x == 1).setIndexWriter(x -> x != false ? 1 : 0).setUiValueNameProvider(CategoryConstants::getBooleanComponent).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> COLOR = ObjectCategorySetting.Builder.begin().setId("color").setDefaultValue(13.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_dots_color")).setSettingUIType(EditorSettingType.EXPANDING).setUiFirstOption(-1).setUiLastOption(15).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> {
        RadarColor color = RadarColor.fromIndex(x.intValue());
        if (color == null) {
            MutableComponent teamColorComponent = Component.literal((String)"# ");
            teamColorComponent.getSiblings().add(Component.translatable((String)"gui.xaero_radar_dots_color_team_colour"));
            return teamColorComponent;
        }
        MutableComponent colorComponent = Component.literal((String)("\u00a7" + color.getFormat() + "#\u00a7r "));
        colorComponent.getSiblings().add(color.getName());
        return colorComponent;
    }).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> ICONS = ObjectCategorySetting.Builder.begin().setId("icons").setDefaultValue(1.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_icons_displayed")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(2).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> Component.translatable((String)ENTITY_ICONS_OPTIONS[x.intValue()])).setTooltip(new TooltipInfo("gui.xaero_box_entity_radar_icons")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> NAMES = ObjectCategorySetting.Builder.begin().setId("names").setDefaultValue(0.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_names_displayed")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(2).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> Component.translatable((String)ENTITY_NAMES_OPTIONS[x.intValue()])).setTooltip(new TooltipInfo("gui.xaero_box_entity_radar_names")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> DOT_SIZE = ObjectCategorySetting.Builder.begin().setId("dotSize").setDefaultValue(2.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_dots_size")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(1).setUiLastOption(4).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> Component.literal((String)("" + x.intValue()))).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> ICON_SCALE = ObjectCategorySetting.Builder.begin().setId("iconScale").setDefaultValue(1.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_entity_heads_scale")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(5).setUiLastOption(40).setIndexReader(x -> (double)x * 0.05).setIndexWriter(x -> (int)(x / 0.05)).setUiValueNameProvider(x -> Component.literal((String)String.format("%.2f", x))).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> HEIGHT_LIMIT = ObjectCategorySetting.Builder.begin().setId("heightLimit").setDefaultValue(20.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_height_limit")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(2).setUiLastOption(410).setIndexReader(x -> (double)x * 5.0).setIndexWriter(x -> (int)(x / 5.0)).setUiValueNameProvider(x -> Component.literal((String)("" + x.intValue()))).setTooltip(new TooltipInfo("gui.xaero_box_height_limit")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Boolean> HEIGHT_FADE = ObjectCategorySetting.Builder.begin().setId("heightBasedFade").setDefaultValue(true).setDisplayName((Component)Component.translatable((String)"gui.xaero_entity_depth")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(1).setIndexReader(x -> x == 1).setIndexWriter(x -> x != false ? 1 : 0).setUiValueNameProvider(CategoryConstants::getBooleanComponent).setTooltip(new TooltipInfo("gui.xaero_box_entity_depth")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> DISPLAY_Y = ObjectCategorySetting.Builder.begin().setId("displayHeight").setDefaultValue(0.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_entity_display_height")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(3).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> {
        if (x.intValue() == 0) {
            return Component.translatable((String)"gui.xaero_off");
        }
        if (x.intValue() == 1) {
            return Component.translatable((String)"gui.xaero_entity_display_height_actual");
        }
        if (x.intValue() == 2) {
            return Component.translatable((String)"gui.xaero_entity_display_height_relative");
        }
        return Component.translatable((String)"gui.xaero_entity_display_height_direction");
    }).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> START_FADING_AT = ObjectCategorySetting.Builder.begin().setId("startFadingAt").setDefaultValue(0.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_start_fading_at")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(0).setUiLastOption(256).setIndexReader(x -> x).setIndexWriter(x -> (int)x.doubleValue()).setUiValueNameProvider(x -> {
        if (x.intValue() == 0) {
            return Component.translatable((String)"gui.xaero_start_fading_at_auto");
        }
        return Component.literal((String)("" + x.intValue()));
    }).setTooltip(new TooltipInfo("gui.xaero_box_start_fading_at")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> ENTITY_NUMBER = ObjectCategorySetting.Builder.begin().setId("entityNumber").setDefaultValue(1000.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_entity_amount")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(0).setUiLastOption(10).setIndexReader(x -> (double)x * 100.0).setIndexWriter(x -> (int)(x / 100.0)).setUiValueNameProvider(x -> {
        if (x.intValue() == 0) {
            return Component.translatable((String)"gui.xaero_unlimited");
        }
        return Component.literal((String)("" + x.intValue()));
    }).setTooltip(new TooltipInfo("gui.xaero_box_entity_amount")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Boolean> ALWAYS_NAMETAGS = ObjectCategorySetting.Builder.begin().setId("alwaysDisplayNametags").setDefaultValue(false).setDisplayName((Component)Component.translatable((String)"gui.xaero_always_entity_nametags")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(1).setIndexReader(x -> x == 1).setIndexWriter(x -> x != false ? 1 : 0).setUiValueNameProvider(CategoryConstants::getBooleanComponent).setTooltip(new TooltipInfo("gui.xaero_box_always_entity_nametags2")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Boolean> ICON_NAME_FALLBACK = ObjectCategorySetting.Builder.begin().setId("displayNameWhenIconFails").setDefaultValue(true).setDisplayName((Component)Component.translatable((String)"gui.xaero_entity_icon_name_fallback")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(1).setIndexReader(x -> x == 1).setIndexWriter(x -> x != false ? 1 : 0).setUiValueNameProvider(CategoryConstants::getBooleanComponent).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> RENDER_OVER_MINIMAP = ObjectCategorySetting.Builder.begin().setId("renderOverMinimapFrame").setDefaultValue(1.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_render_over_minimap")).setSettingUIType(EditorSettingType.ITERATION_BUTTON).setUiFirstOption(0).setUiLastOption(2).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> Component.translatable((String)RADAR_OVER_MAP_OPTIONS[x.intValue()])).setTooltip(new TooltipInfo("gui.xaero_box_radar_render_over_minimap")).build(SETTINGS, SETTINGS_LIST);
    public static final ObjectCategorySetting<Double> RENDER_ORDER = ObjectCategorySetting.Builder.begin().setId("renderOrder").setDefaultValue(0.0).setDisplayName((Component)Component.translatable((String)"gui.xaero_radar_render_order")).setSettingUIType(EditorSettingType.SLIDER).setUiFirstOption(0).setUiLastOption(1000).setIndexReader(x -> x).setIndexWriter(x -> x.intValue()).setUiValueNameProvider(x -> Component.literal((String)("" + x.intValue()))).setTooltip(new TooltipInfo("gui.xaero_box_radar_render_order")).build(SETTINGS, SETTINGS_LIST);
}

