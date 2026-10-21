/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.gui.TooltipButton;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.Tooltip;

public class ScreenSwitchSettingEntry
implements ISettingEntry {
    private String name;
    private BiFunction<Screen, Screen, Screen> screenFactory;
    private Supplier<Tooltip> tooltipSupplier;
    private BooleanSupplier activeSupplier;
    private final boolean consideredAnExit;

    public ScreenSwitchSettingEntry(String name, BiFunction<Screen, Screen, Screen> screenFactoryFromCurrentAndEscape, Tooltip tooltip, boolean active) {
        this(name, screenFactoryFromCurrentAndEscape, tooltip, active, true);
    }

    public ScreenSwitchSettingEntry(String name, BiFunction<Screen, Screen, Screen> screenFactoryFromCurrentAndEscape, Tooltip tooltip, BooleanSupplier activeSupplier) {
        this(name, screenFactoryFromCurrentAndEscape, tooltip, activeSupplier, true);
    }

    public ScreenSwitchSettingEntry(String name, BiFunction<Screen, Screen, Screen> screenFactoryFromCurrentAndEscape, Tooltip tooltip, boolean active, boolean consideredAnExit) {
        this(name, screenFactoryFromCurrentAndEscape, tooltip, () -> active, consideredAnExit);
    }

    public ScreenSwitchSettingEntry(String name, BiFunction<Screen, Screen, Screen> screenFactoryFromCurrentAndEscape, Tooltip tooltip, BooleanSupplier activeSupplier, boolean consideredAnExit) {
        this.name = name;
        this.screenFactory = screenFactoryFromCurrentAndEscape;
        this.tooltipSupplier = () -> tooltip;
        this.activeSupplier = activeSupplier;
        this.consideredAnExit = consideredAnExit;
    }

    public String getStringForSearch() {
        Tooltip entryTooltip = this.tooltipSupplier == null ? null : this.tooltipSupplier.get();
        String tooltipFullCode = entryTooltip == null ? null : entryTooltip.getFullCode();
        return I18n.get((String)this.name, (Object[])new Object[0]) + " " + this.name.replace("gui.xaero", "") + (String)(tooltipFullCode != null ? " " + tooltipFullCode.replace("gui.xaero", "") : "") + (String)(entryTooltip != null ? " " + entryTooltip.getPlainText() : "");
    }

    public AbstractWidget createWidget(int x, int y, int w) {
        TooltipButton button = new TooltipButton(x, y, w, 20, (Component)Component.translatable((String)this.name), b -> {
            Minecraft mc = Minecraft.getInstance();
            Screen current = mc.gui.screen();
            Screen currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
            Screen targetScreen = this.screenFactory.apply(current, currentEscScreen);
            if (this.consideredAnExit && current instanceof ScreenBase) {
                ((ScreenBase)current).onExit(targetScreen);
            } else {
                mc.gui.setScreen(targetScreen);
            }
        }, this.tooltipSupplier);
        button.active = this.activeSupplier.getAsBoolean();
        return button;
    }

    public BiFunction<Screen, Screen, Screen> getScreenFactory() {
        return this.screenFactory;
    }
}

