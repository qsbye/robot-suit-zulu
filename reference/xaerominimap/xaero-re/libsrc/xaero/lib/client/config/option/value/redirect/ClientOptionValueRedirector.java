/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.value.redirect;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirector;

public final class ClientOptionValueRedirector<T>
extends OptionValueRedirector<T> {
    private final Function<Screen, Screen> screenRedirection;
    private final Component name;
    private final Supplier<Component> tooltipSupplier;

    private ClientOptionValueRedirector(Supplier<T> supplier, Predicate<ConfigChannel> condition, Function<Screen, Screen> screenRedirection, Component name, Supplier<Component> tooltipSupplier) {
        super(supplier, condition);
        this.screenRedirection = screenRedirection;
        this.name = name;
        this.tooltipSupplier = tooltipSupplier;
    }

    public Screen getScreenRedirection(Screen currentScreen) {
        if (this.screenRedirection == null) {
            return null;
        }
        return this.screenRedirection.apply(currentScreen);
    }

    public Component getName() {
        return this.name;
    }

    public Component getTooltip() {
        return this.tooltipSupplier.get();
    }

    public static final class Builder<T>
    extends OptionValueRedirector.Builder<T, Builder<T>> {
        private Function<Screen, Screen> screenRedirection;
        private Component name;
        private Supplier<Component> tooltip;

        private Builder() {
        }

        @Override
        public Builder<T> setDefault() {
            super.setDefault();
            this.setScreenRedirection(null);
            this.setName(null);
            this.setTooltip(null);
            return (Builder)this.self;
        }

        public Builder<T> setScreenRedirection(Function<Screen, Screen> screenRedirection) {
            this.screenRedirection = screenRedirection;
            return (Builder)this.self;
        }

        public Builder<T> setName(Component name) {
            this.name = name;
            return (Builder)this.self;
        }

        public Builder<T> setTooltip(Supplier<Component> tooltip) {
            this.tooltip = tooltip;
            return (Builder)this.self;
        }

        @Override
        public ClientOptionValueRedirector<T> build() {
            return (ClientOptionValueRedirector)super.build();
        }

        @Override
        protected ClientOptionValueRedirector<T> buildInternally() {
            return new ClientOptionValueRedirector(this.supplier, this.condition, this.screenRedirection, this.name, this.tooltip);
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

