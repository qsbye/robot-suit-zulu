/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.config.option.ui.type;

import net.minecraft.client.gui.screens.Screen;
import xaero.common.HudMod;
import xaero.common.gui.GuiEntityRadarCategoryEditor;
import xaero.common.gui.GuiInfoDisplayEdit;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.lib.client.config.option.ui.factory.StandardConfigWidgetFactories;
import xaero.lib.client.config.option.ui.factory.StandardViewEnforcedConditions;
import xaero.lib.client.config.option.ui.factory.ViewEnforcedCondition;
import xaero.lib.client.config.option.ui.type.ConfigOptionUIType;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapConfigOptionUITypes {
    public static final ConfigOptionUIType<ConfigOption<EntityRadarCategoryData>> RADAR_CATEGORIES_EDITOR = ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getOpenScreenFactory((parent, escape, config, enforced, option, onChange, readOnly, includeNullValue) -> HudMod.INSTANCE.getEntityRadarCategoryManager().getEditedCategory() == null ? null : new GuiEntityRadarCategoryEditor(HudMod.INSTANCE, parent, (Screen)parent, onChange, readOnly), (ViewEnforcedCondition)StandardViewEnforcedConditions.SHIFT_PRESSED)).build();
    public static final ConfigOptionUIType<ConfigOption<InfoDisplayManagerConfigData>> INFO_DISPLAY_CONFIG_EDITOR = ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getOpenScreenFactory((parent, escape, config, enforced, option, onChange, readOnly, includeNullValue) -> new GuiInfoDisplayEdit(parent, (Screen)parent, config, onChange, readOnly), (ViewEnforcedCondition)StandardViewEnforcedConditions.SHIFT_PRESSED)).build();
}

