/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.single;

import org.apache.logging.log4j.Logger;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.listener.IConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.redirect.OptionValueRedirectorManager;

public class SingleConfigManager<C extends Config> {
    public final Logger logger;
    private C config;
    private final String configId;
    private OptionValueRedirectorManager redirectorManager;
    private ConfigChannel channel;
    private IConfigChangeListener changeListener;

    public SingleConfigManager(Logger logger, String configId) {
        this.logger = logger;
        this.configId = configId;
    }

    public C getConfig() {
        return this.config;
    }

    public void setConfig(C config) {
        this.config = config;
        ((Config)config).setChangeListener(this.changeListener);
    }

    public <T> T getEffective(ConfigOption<T> option) {
        if (this.redirectorManager != null && this.redirectorManager.shouldRedirect(option)) {
            return this.redirectorManager.getValue(option);
        }
        return ((Config)this.getConfig()).get(option);
    }

    public String getConfigId() {
        return this.configId;
    }

    public void setChangeListener(IConfigChangeListener changeListener) {
        this.changeListener = changeListener;
        if (this.config == null) {
            return;
        }
        ((Config)this.config).setChangeListener(changeListener);
    }

    public void setChannel(ConfigChannel channel) {
        if (this.channel != null) {
            throw new IllegalStateException();
        }
        this.channel = channel;
    }

    public ConfigChannel getChannel() {
        return this.channel;
    }

    public void setRedirectorManager(OptionValueRedirectorManager redirectorManager) {
        if (this.redirectorManager != null) {
            throw new IllegalStateException();
        }
        this.redirectorManager = redirectorManager;
    }

    public OptionValueRedirectorManager getRedirectorManager() {
        return this.redirectorManager;
    }

    public static final class FinalBuilder<C extends Config>
    extends Builder<C, FinalBuilder<C>> {
        private FinalBuilder() {
        }

        @Override
        protected SingleConfigManager<C> buildInternally() {
            return new SingleConfigManager(this.logger, this.configId);
        }

        public static <C extends Config> FinalBuilder<C> begin() {
            return (FinalBuilder)new FinalBuilder<C>().setDefault();
        }
    }

    public static abstract class Builder<C extends Config, B extends Builder<C, B>> {
        protected String configId;
        protected Logger logger;
        protected final B self = this;

        protected Builder() {
        }

        public B setDefault() {
            this.setConfigId(null);
            this.setLogger(XaeroLib.LOGGER);
            return this.self;
        }

        public B setConfigId(String configId) {
            this.configId = configId;
            return this.self;
        }

        public B setLogger(Logger logger) {
            this.logger = logger;
            return this.self;
        }

        public SingleConfigManager<C> build() {
            if (this.configId == null || this.logger == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally();
        }

        protected abstract SingleConfigManager<C> buildInternally();
    }
}

