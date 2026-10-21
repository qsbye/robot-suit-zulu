/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.listener.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.listener.IConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

public class HandlerBasedConfigChangeListener
implements IConfigChangeListener {
    protected final Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers;
    protected final ConfigOptionManager options;
    protected boolean enabled;

    protected HandlerBasedConfigChangeListener(Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers, ConfigOptionManager options) {
        this.changeHandlers = changeHandlers;
        this.options = options;
    }

    public void register(ConfigOption<?> option, Consumer<Config> handler) {
        Set handlers = this.changeHandlers.computeIfAbsent(option, k -> new HashSet());
        handlers.add(handler);
    }

    @Override
    public void onChange(Config config, ConfigOption<?> option) {
        if (!this.enabled) {
            return;
        }
        this.changeHandlers.computeIfAbsent(option, k -> new HashSet()).forEach(c -> c.accept(config));
    }

    @Override
    public void onFullChange(Config config) {
        if (!this.enabled) {
            return;
        }
        for (ConfigOption<?> option : this.options) {
            this.onChange(config, option);
        }
    }

    @Override
    public void onRemoved(Config config) {
    }

    public void postLoad() {
        this.enabled = true;
    }

    public static final class FinalBuilder
    extends Builder<FinalBuilder> {
        private FinalBuilder() {
        }

        @Override
        protected HandlerBasedConfigChangeListener buildInternally(Map<ConfigOption<?>, Set<Consumer<Config>>> changeHandlers) {
            return new HandlerBasedConfigChangeListener(changeHandlers, this.options);
        }

        public static FinalBuilder begin() {
            return (FinalBuilder)new FinalBuilder().setDefault();
        }
    }

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;
        protected ConfigOptionManager options;

        protected Builder() {
        }

        public B setDefault() {
            this.setOptions(null);
            return this.self;
        }

        public B setOptions(ConfigOptionManager options) {
            this.options = options;
            return this.self;
        }

        public HandlerBasedConfigChangeListener build() {
            if (this.options == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally(new HashMap());
        }

        protected abstract HandlerBasedConfigChangeListener buildInternally(Map<ConfigOption<?>, Set<Consumer<Config>>> var1);
    }
}

