/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.type;

import xaero.lib.client.config.option.ui.factory.StandardConfigWidgetFactories;
import xaero.lib.client.config.option.ui.type.ConfigOptionUIType;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;

public class BuiltInConfigOptionUITypes {
    public static ConfigOptionUIType<IndexedConfigOption<Boolean>> TOGGLE = BuiltInConfigOptionUITypes.getIndexedButton();
    public static ConfigOptionUIType<IndexedConfigOption<Integer>> INT_INDEXED_BUTTON = BuiltInConfigOptionUITypes.getIndexedButton();
    public static ConfigOptionUIType<IndexedConfigOption<Double>> DOUBLE_INDEXED_BUTTON = BuiltInConfigOptionUITypes.getIndexedButton();
    public static ConfigOptionUIType<IndexedConfigOption<Integer>> INT_INDEXED_SLIDER = BuiltInConfigOptionUITypes.getIndexedSlider();
    public static ConfigOptionUIType<IndexedConfigOption<Double>> DOUBLE_INDEXED_SLIDER = BuiltInConfigOptionUITypes.getIndexedSlider();
    public static ConfigOptionUIType<ConfigOption<String>> STRING_STRING_EDIT = BuiltInConfigOptionUITypes.getStringEdit();
    public static ConfigOptionUIType<ConfigOption<Integer>> INT_STRING_EDIT = BuiltInConfigOptionUITypes.getStringEdit();
    public static ConfigOptionUIType<ConfigOption<Double>> DOUBLE_STRING_EDIT = BuiltInConfigOptionUITypes.getStringEdit();

    public static <T, CT extends IndexedConfigOption<T>> ConfigOptionUIType<CT> getIndexedButton() {
        return ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getIndexedCycleButtonFactory()).build();
    }

    public static <T, CT extends IndexedConfigOption<T>> ConfigOptionUIType<CT> getIndexedSlider() {
        return ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getIndexedSliderFactory()).build();
    }

    public static <CT extends ConfigOption<?>> ConfigOptionUIType<CT> getStringEdit() {
        return ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getStringEditFactory()).build();
    }
}

