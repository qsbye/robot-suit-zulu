/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.factory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import xaero.lib.client.config.option.ui.factory.IConfigOptionWidgetFactory;
import xaero.lib.client.config.option.ui.factory.ICustomOptionEditScreenFactory;
import xaero.lib.client.config.option.ui.factory.ViewEnforcedCondition;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;
import xaero.lib.client.gui.IScreenBase;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.client.gui.config.EditStringConfigOptionScreen;
import xaero.lib.client.gui.widget.CycleButtonOption;
import xaero.lib.client.gui.widget.IClickableWidget;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.XaeroSliderWidget;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;
import xaero.lib.common.config.util.ConfigUtils;

public class StandardConfigWidgetFactories {
    public static <T, CT extends IndexedConfigOption<T>> IConfigOptionWidgetFactory<CT> getIndexedCycleButtonFactory() {
        return (option, config, enforced, x, y, w, includeNullValue, onChange, channel, clientSide) -> StandardConfigWidgetFactories.createCycleButton(option, config, enforced, x, y, w, includeNullValue, option.getValidValues(), onChange, channel, clientSide);
    }

    public static <T, CT extends IndexedConfigOption<T>> IConfigOptionWidgetFactory<CT> getIndexedSliderFactory() {
        return (option, config, enforced, x, y, w, includeNullValue, onChange, channel, clientSide) -> StandardConfigWidgetFactories.createSliderWithValues(option, config, enforced, x, y, w, includeNullValue, option.getValidValues(), onChange, channel, clientSide);
    }

    public static <CT extends ConfigOption<?>> IConfigOptionWidgetFactory<CT> getStringEditFactory() {
        return StandardConfigWidgetFactories::createStringEditButton;
    }

    public static <CT extends ConfigOption<?>> IConfigOptionWidgetFactory<CT> getOpenScreenFactory(ICustomOptionEditScreenFactory<CT> screenFactory, ViewEnforcedCondition viewEnforcedCondition) {
        return (option, config, enforced, x, y, w, includeNullValue, onChange, channel, clientSide) -> StandardConfigWidgetFactories.createOpenScreenButton(option, config, enforced, x, y, w, includeNullValue, onChange, channel, clientSide, screenFactory, viewEnforcedCondition);
    }

    private static <T, CT extends ConfigOption<T>> AbstractWidget createCycleButton(CT option, Config config, Config enforced, int x, int y, int w, boolean includeNullValue, List<T> values, Runnable onChange, ConfigChannel channel, boolean clientSide) {
        ArrayList<CycleButtonOption<Object>> allValues = new ArrayList<CycleButtonOption<Object>>();
        if (includeNullValue) {
            allValues.add(new CycleButtonOption<Object>(null));
        }
        values.stream().map(CycleButtonOption::new).forEach(allValues::add);
        ClientOptionValueRedirectorManager redirectorManager = clientSide ? channel.getClientConfigManager().getRedirectorManager() : null;
        CycleButton resultButton = CycleButton.builder(v -> ConfigUtils.getEffectiveValueName(redirectorManager, option, config, enforced), () -> new CycleButtonOption(config.get(option))).withValues(allValues).create(x, y, w, 20, option.getDisplayName(), (button, value) -> {
            if (clientSide && redirectorManager.redirectScreen(option)) {
                return;
            }
            config.set(option, value.get());
            button.setValue(value);
            onChange.run();
        });
        if (clientSide && redirectorManager.shouldDeactivateWidget(option)) {
            resultButton.active = false;
        }
        StandardConfigWidgetFactories.addTooltipToWidget((AbstractWidget)resultButton, option, enforced, redirectorManager, null);
        return resultButton;
    }

    private static <T, CT extends ConfigOption<T>> AbstractWidget createSliderWithValues(CT option, Config config, Config enforced, int x, int y, int w, boolean includeNullValue, List<T> values, Runnable onChange, ConfigChannel channel, boolean clientSide) {
        double sliderValue;
        ClientOptionValueRedirectorManager redirectorManager;
        List allValues = values;
        if (includeNullValue) {
            allValues = new ArrayList<T>();
            allValues.add(null);
            allValues.addAll(values);
        }
        List finalValues = allValues;
        Object currentValue = config.get(option);
        ClientOptionValueRedirectorManager clientOptionValueRedirectorManager = redirectorManager = clientSide ? channel.getClientConfigManager().getRedirectorManager() : null;
        if (finalValues.size() == 1 || clientSide && redirectorManager.shouldRedirect(option)) {
            sliderValue = 1.0;
        } else {
            sliderValue = (double)allValues.indexOf(currentValue) / (double)(finalValues.size() - 1);
            sliderValue = Mth.clamp((double)sliderValue, (double)0.0, (double)1.0);
        }
        XaeroSliderWidget resultSlider = new XaeroSliderWidget(x, y, w, 20, StandardConfigWidgetFactories.getSliderLabel(option, config, enforced, channel, clientSide), sliderValue, newSliderValue -> {
            if (clientSide && redirectorManager.redirectScreen(option)) {
                return;
            }
            int newIndex = (int)Math.round(newSliderValue * (double)(finalValues.size() - 1));
            Object newValue = finalValues.get(newIndex);
            if (newValue != config.get(option)) {
                config.set(option, newValue);
                if (Minecraft.getInstance().gui.screen() instanceof EditConfigScreen) {
                    ((EditConfigScreen)Minecraft.getInstance().gui.screen()).handleChangesOnExit();
                }
            }
        }, () -> StandardConfigWidgetFactories.getSliderLabel(option, config, enforced, channel, clientSide));
        if (clientSide && redirectorManager.shouldDeactivateWidget(option)) {
            resultSlider.active = false;
        }
        StandardConfigWidgetFactories.addTooltipToWidget((AbstractWidget)resultSlider, option, enforced, redirectorManager, null);
        return resultSlider;
    }

    private static <T> Component getSliderLabel(ConfigOption<T> option, Config config, Config enforced, ConfigChannel channel, boolean clientSide) {
        ClientOptionValueRedirectorManager redirectorManager = clientSide ? channel.getClientConfigManager().getRedirectorManager() : null;
        return CommonComponents.optionNameValue((Component)option.getDisplayName(), (Component)ConfigUtils.getEffectiveValueName(redirectorManager, option, config, enforced));
    }

    private static AbstractWidget createStringEditButton(ConfigOption<?> option, Config config, Config enforced, int x, int y, int w, boolean includeNullValue, Runnable onChange, ConfigChannel channel, boolean clientSide) {
        return StandardConfigWidgetFactories.createOpenScreenButton(option, config, enforced, x, y, w, includeNullValue, onChange, channel, clientSide, (parent, escape, config1, enforced1, option1, onChange1, readOnly, includeNullValue1) -> new EditStringConfigOptionScreen(parent, escape, config1, enforced1, option1, includeNullValue1, includeNullValue1, onChange1), null);
    }

    private static <CT extends ConfigOption<?>> AbstractWidget createOpenScreenButton(CT option, Config config, Config enforced, int x, int y, int w, boolean includeNullValue, Runnable onChange, ConfigChannel channel, boolean clientSide, ICustomOptionEditScreenFactory<CT> screenFactory, ViewEnforcedCondition viewEnforcedCondition) {
        ClientOptionValueRedirectorManager redirectorManager = clientSide ? channel.getClientConfigManager().getRedirectorManager() : null;
        Component displayName = option.getDisplayName();
        boolean needsEnforcedIndicator = StandardConfigWidgetFactories.widgetNeedsEnforcedTooltipPrefix(option, enforced);
        if (needsEnforcedIndicator) {
            displayName = Component.translatable((String)"gui.xaero_config_value_enforced", (Object[])new Object[]{displayName.copy().withStyle(ChatFormatting.WHITE), Component.translatable((String)"gui.xaero_config_value_enforced_indicator").withStyle(ChatFormatting.YELLOW)}).withStyle(ChatFormatting.YELLOW);
        }
        Button resultButton = Button.builder((Component)displayName, b -> {
            if (clientSide && redirectorManager.redirectScreen(option)) {
                return;
            }
            Screen parent = Minecraft.getInstance().gui.screen();
            if (!(parent instanceof EditConfigScreen)) {
                return;
            }
            Screen escape = null;
            if (parent instanceof IScreenBase) {
                escape = ((IScreenBase)parent).getEscape();
            }
            boolean readOnly = needsEnforcedIndicator && viewEnforcedCondition != null && viewEnforcedCondition.getCondition().getAsBoolean();
            Config screenConfig = readOnly ? enforced : config;
            Minecraft.getInstance().gui.setScreen(screenFactory.get((EditConfigScreen)parent, escape, screenConfig, enforced, option, onChange, readOnly, includeNullValue));
        }).bounds(x, y, w, 20).build();
        if (clientSide && redirectorManager.shouldDeactivateWidget(option)) {
            resultButton.active = false;
        }
        StandardConfigWidgetFactories.addTooltipToWidget((AbstractWidget)resultButton, option, enforced, redirectorManager, viewEnforcedCondition);
        return resultButton;
    }

    private static <CT extends ConfigOption<?>> void addTooltipToWidget(AbstractWidget widget, CT option, Config enforced, ClientOptionValueRedirectorManager redirectorManager, ViewEnforcedCondition viewEnforcedCondition) {
        if (!(widget instanceof IClickableWidget)) {
            return;
        }
        if (redirectorManager != null && redirectorManager.shouldRedirect(option)) {
            ((IClickableWidget)widget).setXaero_tooltip(new Tooltip(redirectorManager.getTooltip(option)));
            return;
        }
        MutableComponent tooltipComponent = null;
        if (StandardConfigWidgetFactories.widgetNeedsEnforcedTooltipPrefix(option, enforced)) {
            tooltipComponent = viewEnforcedCondition != null ? viewEnforcedCondition.getTooltip().copy() : Component.translatable((String)"gui.xaero_config_value_enforced_tooltip_prefix");
            tooltipComponent = tooltipComponent.withStyle(ChatFormatting.YELLOW);
        }
        if (option.getTooltip() != null) {
            MutableComponent mainTooltip = option.getTooltip().copy().withStyle(ChatFormatting.WHITE);
            if (tooltipComponent == null) {
                tooltipComponent = mainTooltip.copy();
            } else {
                tooltipComponent.getSiblings().add(Component.literal((String)" \n \n "));
                tooltipComponent.getSiblings().add(mainTooltip);
            }
        }
        if (tooltipComponent == null) {
            return;
        }
        ((IClickableWidget)widget).setXaero_tooltip(new Tooltip((Component)tooltipComponent));
    }

    public static boolean widgetNeedsEnforcedTooltipPrefix(ConfigOption<?> option, Config enforced) {
        return enforced != null && option != BuiltInProfiledConfigOptions.PROFILE_NAME && enforced.get(option) != null;
    }
}

