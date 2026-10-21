/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import xaero.lib.common.config.option.ConfigOption;

public class ConfigOptionManager
implements Iterable<ConfigOption<?>> {
    public final Logger logger;
    private final Map<String, ConfigOption<?>> options;
    private boolean frozen;

    protected ConfigOptionManager(Logger logger, Map<String, ConfigOption<?>> options) {
        this.logger = logger;
        this.options = options;
    }

    public ConfigOption<?> get(String id) {
        return this.options.get(id);
    }

    public void register(ConfigOption<?> option) {
        if (this.frozen) {
            throw new IllegalStateException("The config option manager is read-only!");
        }
        if (this.options.containsKey(option.getId())) {
            throw new IllegalArgumentException("Duplicate config option id!");
        }
        this.options.put(option.getId(), option);
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public Iterator<ConfigOption<?>> iterator() {
        return this.options.values().iterator();
    }

    public static final class FinalBuilder
    extends Builder<FinalBuilder> {
        private FinalBuilder() {
        }

        @Override
        protected ConfigOptionManager buildInternally(Map<String, ConfigOption<?>> options) {
            return new ConfigOptionManager(this.logger, options);
        }

        public static FinalBuilder begin() {
            return (FinalBuilder)new FinalBuilder().setDefault();
        }
    }

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;
        protected Logger logger;

        protected Builder() {
        }

        public B setDefault() {
            this.setLogger(null);
            return this.self;
        }

        public B setLogger(Logger logger) {
            this.logger = logger;
            return this.self;
        }

        public ConfigOptionManager build() {
            if (this.logger == null) {
                throw new IllegalStateException();
            }
            LinkedHashMap options = new LinkedHashMap();
            return this.buildInternally(options);
        }

        protected abstract ConfigOptionManager buildInternally(Map<String, ConfigOption<?>> var1);
    }
}

