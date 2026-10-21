/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui.type;

import xaero.lib.client.config.option.ui.factory.IConfigOptionWidgetFactory;
import xaero.lib.common.config.option.ConfigOption;

public final class ConfigOptionUIType<CT extends ConfigOption<?>> {
    private final IConfigOptionWidgetFactory<CT> widgetFactory;

    public ConfigOptionUIType(IConfigOptionWidgetFactory<CT> widgetFactory) {
        this.widgetFactory = widgetFactory;
    }

    public IConfigOptionWidgetFactory<CT> getWidgetFactory() {
        return this.widgetFactory;
    }

    public static final class Builder<CT extends ConfigOption<?>> {
        private IConfigOptionWidgetFactory<CT> widgetFactory;

        private Builder() {
        }

        public Builder<CT> setDefault() {
            this.setWidgetFactory(null);
            return this;
        }

        public Builder<CT> setWidgetFactory(IConfigOptionWidgetFactory<CT> widgetFactory) {
            this.widgetFactory = widgetFactory;
            return this;
        }

        public ConfigOptionUIType<CT> build() {
            if (this.widgetFactory == null) {
                throw new IllegalStateException();
            }
            return new ConfigOptionUIType<CT>(this.widgetFactory);
        }

        public static <CT extends ConfigOption<?>> Builder<CT> begin() {
            return new Builder<CT>().setDefault();
        }
    }
}

