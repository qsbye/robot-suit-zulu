/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.listener.IConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirector;

public class OptionValueRedirectorManager {
    protected final Map<ConfigOption<?>, OptionValueRedirector<?>> redirectors;
    private final Config cacheConfig;
    protected ConfigChannel channel;
    protected IConfigChangeListener changeListener;
    private boolean frozen;

    protected OptionValueRedirectorManager(Map<ConfigOption<?>, OptionValueRedirector<?>> redirectors, Config cacheConfig) {
        this.redirectors = redirectors;
        this.cacheConfig = cacheConfig;
    }

    public void freeze() {
        this.frozen = true;
    }

    protected <T> void checkDuplicates(ConfigOption<T> option) {
        if (this.redirectors.containsKey(option)) {
            throw new IllegalArgumentException("Tried to register duplicate config option redirector for " + option.getId());
        }
    }

    public <T> void register(ConfigOption<T> option, Supplier<T> redirectSupplier, Predicate<ConfigChannel> condition) {
        if (this.frozen) {
            throw new IllegalStateException("The redirector manager is already frozen!");
        }
        this.checkDuplicates(option);
        this.redirectors.put(option, ((OptionValueRedirector.FinalBuilder)((OptionValueRedirector.FinalBuilder)OptionValueRedirector.FinalBuilder.begin().setSupplier(redirectSupplier)).setCondition(condition)).build());
    }

    public void setChannel(ConfigChannel channel) {
        if (this.channel != null) {
            throw new IllegalStateException();
        }
        this.channel = channel;
    }

    public void setChangeListener(IConfigChangeListener changeListener) {
        if (this.changeListener != null) {
            throw new IllegalStateException();
        }
        this.changeListener = changeListener;
        this.cacheConfig.setChangeListener(changeListener);
    }

    protected <T> OptionValueRedirector<T> get(ConfigOption<T> option) {
        return this.redirectors.get(option);
    }

    public <T> T getValue(ConfigOption<T> option) {
        OptionValueRedirector<T> redirector = this.get(option);
        if (redirector == null) {
            return null;
        }
        T value = redirector.getValue();
        if (value == null) {
            throw new IllegalArgumentException("Redirectors aren't allowed to return null!");
        }
        this.cacheConfig.set(option, value);
        return value;
    }

    public boolean shouldRedirect(ConfigOption<?> option) {
        if (!option.isOverridable()) {
            return false;
        }
        OptionValueRedirector<?> redirector = this.get(option);
        if (redirector == null) {
            return false;
        }
        boolean result = redirector.shouldRedirect(this.channel);
        if (!result) {
            this.cacheConfig.set(option, null);
        }
        return result;
    }

    public Config getCacheConfig() {
        return this.cacheConfig;
    }

    public static final class FinalBuilder
    extends Builder<FinalBuilder> {
        private FinalBuilder() {
        }

        @Override
        protected OptionValueRedirectorManager buildInternally(Map<ConfigOption<?>, OptionValueRedirector<?>> redirectors, Config cacheConfig) {
            return new OptionValueRedirectorManager(redirectors, cacheConfig);
        }

        public static FinalBuilder begin() {
            return (FinalBuilder)new FinalBuilder().setDefault();
        }
    }

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;

        protected Builder() {
        }

        public B setDefault() {
            return this.self;
        }

        public OptionValueRedirectorManager build() {
            Object cacheConfig = ((Config.FinalBuilder)Config.FinalBuilder.begin().setAllowNullValues(true)).build();
            return this.buildInternally(new HashMap(), (Config)cacheConfig);
        }

        protected abstract OptionValueRedirectorManager buildInternally(Map<ConfigOption<?>, OptionValueRedirector<?>> var1, Config var2);
    }
}

