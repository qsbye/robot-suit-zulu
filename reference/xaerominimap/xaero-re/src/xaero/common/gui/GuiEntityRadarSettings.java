/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapSettings;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.RootCategorySettingEntry;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;

public class GuiEntityRadarSettings
extends GuiMinimapSettings {
    public GuiEntityRadarSettings(IXaeroMinimap modMain, Screen backScreen, Screen escScreen, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_entity_radar_settings"), backScreen, escScreen, context);
        BooleanSupplier allowNullSupplier = () -> HudMod.INSTANCE.getEntityRadarCategoryManager().getEditedCategoryConfig().isAllowNullValues();
        Supplier<ObjectCategory<?, ?>> categorySupplier = () -> HudMod.INSTANCE.getEntityRadarCategoryManager().getEditedCategory();
        BiConsumer<Object, Object> defaultValueChangeListener = (o, n) -> HudMod.INSTANCE.getEntityRadarCategoryManager().setEditedCategoryNeedsSaving(true);
        RootCategorySettingEntry<Double> entityNumberEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.ENTITY_NUMBER, (Component)Component.translatable((String)"gui.xaero_entity_amount"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Boolean> entityFadeEntry = new RootCategorySettingEntry<Boolean>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.HEIGHT_FADE, (Component)Component.translatable((String)"gui.xaero_entity_depth"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Boolean> alwaysNametagsEntry = new RootCategorySettingEntry<Boolean>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.ALWAYS_NAMETAGS, (Component)Component.translatable((String)"gui.xaero_always_entity_nametags"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> displayYEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.DISPLAY_Y, (Component)Component.translatable((String)"gui.xaero_entity_display_height_full"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Boolean> iconNameFallbackEntry = new RootCategorySettingEntry<Boolean>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.ICON_NAME_FALLBACK, (Component)Component.translatable((String)"gui.xaero_entity_icon_name_fallback"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> iconsEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.ICONS, (Component)Component.translatable((String)"gui.xaero_radar_setting_icons"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> namesEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.NAMES, (Component)Component.translatable((String)"gui.xaero_radar_setting_names"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> renderOverMinimapEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.RENDER_OVER_MINIMAP, (Component)Component.translatable((String)"gui.xaero_radar_render_radar_over_frame"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> dotSizeEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.DOT_SIZE, (Component)Component.translatable((String)"gui.xaero_dots_size"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> iconScaleEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.ICON_SCALE, (Component)Component.translatable((String)"gui.xaero_entity_heads_scale"), (currentScale, newScale) -> {
            defaultValueChangeListener.accept(currentScale, newScale);
            if (context.isClientSide() && (newScale < 1.0 || newScale < 1.0 != currentScale < 1.0)) {
                modMain.getMinimap().getMinimapFBORenderer().resetEntityIcons();
            }
        });
        RootCategorySettingEntry<Double> heightLimitEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.HEIGHT_LIMIT, (Component)Component.translatable((String)"gui.xaero_height_limit"), defaultValueChangeListener::accept);
        RootCategorySettingEntry<Double> startFadingEntry = new RootCategorySettingEntry<Double>(categorySupplier, allowNullSupplier, EntityRadarCategorySettings.START_FADING_AT, (Component)Component.translatable((String)"gui.xaero_start_fading_at"), defaultValueChangeListener::accept);
        this.entries = new ISettingEntry[]{this.optionEntry(MinimapProfiledConfigOptions.RADAR_CATEGORIES), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR), dotSizeEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_DOTS_STYLE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_SMOOTH_DOTS), iconsEntry, namesEntry, iconScaleEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_NAME_SCALE), alwaysNametagsEntry, iconNameFallbackEntry, entityNumberEntry, heightLimitEntry, entityFadeEntry, startFadingEntry, displayYEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_ENTITY), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_DOT_SIZE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ARROW_SCALE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ARROW_COLOR), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.ARROW_OPACITY), renderOverMinimapEntry, this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.RADAR_HIDE_INVISIBLE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_ON_MINIMAP), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYERS_IN_WORLD), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_ICON_SCALE), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.TRACKED_PLAYER_WORLD_NAME_SCALE)};
    }
}

