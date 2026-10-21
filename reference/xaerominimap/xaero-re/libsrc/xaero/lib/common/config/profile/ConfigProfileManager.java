/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.profile;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.listener.IConfigChangeListener;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public final class ConfigProfileManager
implements Iterable<ConfigProfile> {
    public final Logger logger;
    private final Map<String, ConfigProfile> profiles;
    private final List<String> order;
    private final Object2IntMap<String> orderLookup;
    private final String configType;
    private final ConfigListener configListener;
    private IConfigChangeListener changeListener;

    private ConfigProfileManager(Logger logger, Map<String, ConfigProfile> profiles, List<String> order, Object2IntMap<String> orderLookup, String configType) {
        this.logger = logger;
        this.profiles = profiles;
        this.order = order;
        this.orderLookup = orderLookup;
        this.configType = configType;
        this.configListener = new ConfigListener(this);
    }

    public void add(ConfigProfile profile) {
        this.add(this.order.size(), profile);
    }

    public void add(int index, ConfigProfile profile) {
        if (this.profiles.containsKey(profile.getId())) {
            throw new IllegalArgumentException();
        }
        this.profiles.put(profile.getId(), profile);
        this.order.add(index, profile.getId());
        this.updateOrderLookup(index);
        profile.setChangeListener(this.configListener);
    }

    public ConfigProfile get(String id) {
        return this.profiles.get(id);
    }

    public void remove(String id) {
        if (!this.profiles.containsKey(id)) {
            throw new IllegalArgumentException();
        }
        int index = this.orderLookup.getInt((Object)id);
        ConfigProfile removed = this.profiles.remove(id);
        this.order.remove(index);
        this.orderLookup.removeInt((Object)id);
        this.updateOrderLookup(index);
        if (removed.getChangeListener() == this.configListener) {
            removed.setChangeListener(null);
        }
        if (this.changeListener == null) {
            return;
        }
        this.changeListener.onRemoved(removed);
    }

    public int getIndex(String id) {
        return this.orderLookup.getInt((Object)id);
    }

    private void updateOrderLookup(int startIndex) {
        for (int i = startIndex; i < this.order.size(); ++i) {
            String otherId = this.order.get(i);
            this.orderLookup.put((Object)otherId, i);
        }
    }

    @Override
    public Iterator<ConfigProfile> iterator() {
        return this.order.stream().map(this.profiles::get).iterator();
    }

    public String getConfigType() {
        return this.configType;
    }

    public void setChangeListener(IConfigChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void reset() {
        this.profiles.clear();
        this.orderLookup.clear();
        this.order.clear();
    }

    private final class ConfigListener
    implements IConfigChangeListener {
        final /* synthetic */ ConfigProfileManager this$0;

        private ConfigListener(ConfigProfileManager configProfileManager) {
            ConfigProfileManager configProfileManager2 = configProfileManager;
            Objects.requireNonNull(configProfileManager2);
            this.this$0 = configProfileManager2;
        }

        @Override
        public void onChange(Config config, ConfigOption<?> option) {
            if (this.this$0.changeListener == null) {
                return;
            }
            this.this$0.changeListener.onChange(config, option);
        }

        @Override
        public void onFullChange(Config config) {
            if (this.this$0.changeListener == null) {
                return;
            }
            this.this$0.changeListener.onFullChange(config);
        }

        @Override
        public void onRemoved(Config config) {
            if (this.this$0.changeListener == null) {
                return;
            }
            this.this$0.changeListener.onRemoved(config);
        }
    }

    public static final class Builder {
        private String configType;
        private Logger logger;

        private Builder() {
        }

        public Builder setDefault() {
            this.setConfigType(null);
            this.setLogger(null);
            return this;
        }

        public Builder setConfigType(String configType) {
            this.configType = configType;
            return this;
        }

        public Builder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public ConfigProfileManager build() {
            if (this.configType == null || this.logger == null) {
                throw new IllegalStateException();
            }
            return new ConfigProfileManager(this.logger, new HashMap<String, ConfigProfile>(), new ArrayList<String>(), (Object2IntMap<String>)new Object2IntOpenHashMap(), this.configType);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

