/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.value.redirect;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirector;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirector;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;

public class ClientOptionValueRedirectorManager
extends OptionValueRedirectorManager {
    protected ClientOptionValueRedirectorManager(Map<ConfigOption<?>, OptionValueRedirector<?>> redirectors, Config cacheConfig) {
        super(redirectors, cacheConfig);
    }

    public <T> void register(ConfigOption<T> option, Supplier<T> redirectSupplier, Predicate<ConfigChannel> condition, Function<Screen, Screen> screenRedirector, Component name, Supplier<Component> tooltip) {
        this.checkDuplicates(option);
        this.redirectors.put(option, ((ClientOptionValueRedirector.Builder)((ClientOptionValueRedirector.Builder)ClientOptionValueRedirector.Builder.begin().setScreenRedirection(screenRedirector).setSupplier(redirectSupplier)).setCondition(condition)).setName(name).setTooltip(tooltip).build());
    }

    @Override
    public <T> void register(ConfigOption<T> option, Supplier<T> redirectSupplier, Predicate<ConfigChannel> condition) {
        throw new IllegalArgumentException("Use the longer client register() method instead.");
    }

    protected <T> ClientOptionValueRedirector<T> get(ConfigOption<T> option) {
        return (ClientOptionValueRedirector)super.get(option);
    }

    public Screen redirectScreen(ConfigOption<?> option, Screen currentScreen) {
        OptionValueRedirector redirector = this.get((ConfigOption)option);
        if (redirector == null) {
            return currentScreen;
        }
        return ((ClientOptionValueRedirector)redirector).getScreenRedirection(currentScreen);
    }

    public Component getName(ConfigOption<?> option) {
        OptionValueRedirector redirector = this.get((ConfigOption)option);
        if (redirector == null) {
            return null;
        }
        return ((ClientOptionValueRedirector)redirector).getName();
    }

    public Component getTooltip(ConfigOption<?> option) {
        OptionValueRedirector redirector = this.get((ConfigOption)option);
        if (redirector == null) {
            return null;
        }
        return ((ClientOptionValueRedirector)redirector).getTooltip();
    }

    public boolean redirectScreen(ConfigOption<?> option) {
        Screen currentScreen = Minecraft.getInstance().gui.screen();
        if (!this.shouldRedirect(option)) {
            return false;
        }
        Screen redirectScreen = this.redirectScreen(option, currentScreen);
        if (redirectScreen == null) {
            return true;
        }
        if (redirectScreen == currentScreen) {
            return false;
        }
        Minecraft.getInstance().gui.setScreen(redirectScreen);
        return true;
    }

    public boolean shouldDeactivateWidget(ConfigOption<?> option) {
        if (!this.shouldRedirect(option)) {
            return false;
        }
        return this.redirectScreen(option, null) == null;
    }

    public static final class Builder
    extends OptionValueRedirectorManager.Builder<Builder> {
        private Builder() {
        }

        @Override
        protected ClientOptionValueRedirectorManager buildInternally(Map<ConfigOption<?>, OptionValueRedirector<?>> redirectors, Config cacheConfig) {
            return new ClientOptionValueRedirectorManager(redirectors, cacheConfig);
        }

        @Override
        public ClientOptionValueRedirectorManager build() {
            return (ClientOptionValueRedirectorManager)super.build();
        }

        public static Builder begin() {
            return (Builder)new Builder().setDefault();
        }
    }
}

