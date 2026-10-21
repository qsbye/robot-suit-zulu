/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.widget.CycleButtonOption;
import xaero.lib.client.gui.widget.IClickableWidget;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.XaeroSliderWidget;
import xaero.lib.common.config.util.ConfigConstants;
import xaero.lib.common.gui.widget.TooltipInfo;

public class CustomSettingEntry<T>
implements ISettingEntry {
    private final BooleanSupplier allowNull;
    private final Component name;
    private final TooltipInfo tooltipInfo;
    private final boolean slider;
    private Supplier<T> currentValueSupplier;
    private final int minIndex;
    private final int maxIndex;
    private final IntFunction<T> indexReader;
    private final Function<T, Component> valueNamer;
    private final BiConsumer<T, T> onValueChange;
    private final BooleanSupplier activeSupplier;

    public CustomSettingEntry(BooleanSupplier allowNull, Component name, TooltipInfo tooltipInfo, boolean slider, Supplier<T> currentValueSupplier, int minIndex, int maxIndex, IntFunction<T> indexReader, Function<T, Component> valueNamer, BiConsumer<T, T> onValueChange, BooleanSupplier activeSupplier) {
        this.allowNull = allowNull;
        this.name = name;
        this.tooltipInfo = tooltipInfo;
        this.slider = slider;
        this.currentValueSupplier = currentValueSupplier;
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.indexReader = indexReader;
        this.valueNamer = valueNamer;
        this.onValueChange = onValueChange;
        this.activeSupplier = activeSupplier;
    }

    @Override
    public String getStringForSearch() {
        Component displayName = this.name;
        T currentValue = this.currentValueSupplier.get();
        Object result = CommonComponents.optionNameValue((Component)displayName, (Component)(currentValue == null ? ConfigConstants.UNSPECIFIED : this.valueNamer.apply(currentValue))).getString();
        if (displayName.getContents() instanceof TranslatableContents) {
            result = (String)result + " " + ((TranslatableContents)displayName.getContents()).getKey().replace("gui.xaero", "");
        }
        if (this.tooltipInfo != null) {
            result = (String)result + " " + this.tooltipInfo.text.getString();
            if (this.tooltipInfo.text.getContents() instanceof TranslatableContents) {
                result = (String)result + " " + ((TranslatableContents)this.tooltipInfo.text.getContents()).getKey().replace("gui.xaero", "");
            }
        }
        return result;
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int w) {
        Object result;
        ArrayList<CycleButtonOption<T>> values = new ArrayList<CycleButtonOption<T>>();
        CycleButtonOption initialOption = null;
        int initialOptionIndex = -1;
        if (this.allowNull.getAsBoolean()) {
            values.add(new CycleButtonOption<Object>(null));
        }
        T currentValue = this.currentValueSupplier.get();
        for (int i = this.minIndex; i <= this.maxIndex; ++i) {
            T value = this.indexReader.apply(i);
            CycleButtonOption option = new CycleButtonOption(value);
            values.add(option);
            if (!Objects.equals(value, currentValue)) continue;
            initialOption = option;
            initialOptionIndex = values.size() - 1;
        }
        if (initialOption == null) {
            initialOption = (CycleButtonOption)values.get(0);
            initialOptionIndex = 0;
        }
        CycleButtonOption finalInitialOption = initialOption;
        Component displayName = this.name;
        if (!this.slider) {
            result = CycleButton.builder(co -> this.getValueName(co.get()), () -> finalInitialOption).withValues(values).create(x, y, w, 20, displayName, (b, o) -> {
                T oldValue = this.currentValueSupplier.get();
                if (this.onValueChange != null) {
                    this.onValueChange.accept(oldValue, o.get());
                }
            });
        } else {
            Supplier<Component> labelGetter = () -> CommonComponents.optionNameValue((Component)displayName, (Component)this.getValueName(this.currentValueSupplier.get()));
            double initialSliderValue = (double)initialOptionIndex / (double)(values.size() - 1);
            result = new XaeroSliderWidget(x, y, w, 20, labelGetter.get(), initialSliderValue, newSliderValue -> {
                int selectedOptionIndex = (int)Math.round(newSliderValue * (double)(values.size() - 1));
                CycleButtonOption selectedOption = (CycleButtonOption)values.get(selectedOptionIndex);
                T oldValue = this.currentValueSupplier.get();
                if (this.onValueChange != null) {
                    this.onValueChange.accept(oldValue, selectedOption.get());
                }
            }, labelGetter);
        }
        result.active = this.activeSupplier.getAsBoolean();
        if (this.tooltipInfo != null) {
            ((IClickableWidget)result).setXaero_tooltip(new Tooltip(this.tooltipInfo));
        }
        return result;
    }

    public Component getValueName(T value) {
        if (value == null) {
            return ConfigConstants.UNSPECIFIED;
        }
        return this.valueNamer.apply(value);
    }
}

