/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import xaero.common.HudMod;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.config.InfoDisplayConfigClientUtils;

public final class InfoDisplayManager {
    private final Map<String, InfoDisplay<?>> displays;
    private final List<String> defaultOrder;
    private final List<String> unmodifiableDefaultOrder;
    private List<String> order;
    private final Supplier<InfoDisplayManagerConfigData> localConfigSupplier;
    private final Consumer<InfoDisplayManagerConfigData> localConfigSetter;
    private final Supplier<InfoDisplayManagerConfigData> enforcedConfigSupplier;
    private boolean configApplicationRecursion;

    private InfoDisplayManager(Map<String, InfoDisplay<?>> displays, List<String> defaultOrder, List<String> unmodifiableDefaultOrder, Supplier<InfoDisplayManagerConfigData> localConfigSupplier, Consumer<InfoDisplayManagerConfigData> localConfigSetter, Supplier<InfoDisplayManagerConfigData> enforcedConfigSupplier) {
        this.displays = displays;
        this.defaultOrder = defaultOrder;
        this.unmodifiableDefaultOrder = unmodifiableDefaultOrder;
        this.localConfigSupplier = localConfigSupplier;
        this.localConfigSetter = localConfigSetter;
        this.enforcedConfigSupplier = enforcedConfigSupplier;
    }

    public void add(InfoDisplay<?> infoDisplay) {
        if (this.displays.put(infoDisplay.getId(), infoDisplay) == null) {
            this.defaultOrder.add(infoDisplay.getId());
            infoDisplay.setManager(this);
        }
    }

    public void applyLocalConfig() {
        if (this.configApplicationRecursion) {
            return;
        }
        InfoDisplayManagerConfigData config = this.getLocalConfig();
        if (config == null || config == InfoDisplayManagerConfigData.EMPTY) {
            this.configApplicationRecursion = true;
            config = InfoDisplayConfigClientUtils.createDefaultConfig(this, HudMod.INSTANCE.getSettings(), true);
            this.localConfigSetter.accept(config);
            this.configApplicationRecursion = false;
        }
        this.order = this.adaptOrder(config.getOrderStream());
    }

    public List<String> adaptOrder(Stream<String> originalOrder) {
        ArrayList<String> order = new ArrayList<String>();
        originalOrder.filter(id -> this.get((String)id) != null).forEach(order::add);
        int lastDefaultOrderIdIndex = -1;
        for (int i = 0; i < this.defaultOrder.size(); ++i) {
            String defaultOrderId = this.defaultOrder.get(i);
            int defaultOrderIdIndex = order.indexOf(defaultOrderId);
            if (defaultOrderIdIndex == -1) {
                defaultOrderIdIndex = lastDefaultOrderIdIndex != -1 ? lastDefaultOrderIdIndex + 1 : 0;
                order.add(defaultOrderIdIndex, defaultOrderId);
            }
            lastDefaultOrderIdIndex = defaultOrderIdIndex;
        }
        return order;
    }

    public InfoDisplayManagerConfigData getLocalConfig() {
        return this.localConfigSupplier.get();
    }

    public InfoDisplayManagerConfigData getEnforcedConfig() {
        return this.enforcedConfigSupplier.get();
    }

    public InfoDisplay<?> get(String id) {
        return this.displays.get(id);
    }

    public Stream<InfoDisplay<?>> getOrderedStream() {
        Stream<InfoDisplay> unfilteredResult = this.order.stream().map(this.displays::get);
        return unfilteredResult.filter(Objects::nonNull);
    }

    public Stream<InfoDisplay<?>> getStream() {
        return this.displays.values().stream();
    }

    public int getCount() {
        return this.displays.size();
    }

    public List<String> getDefaultOrder() {
        return this.unmodifiableDefaultOrder;
    }

    public void clearStateCache() {
        this.displays.values().forEach(InfoDisplay::clearStateCache);
    }

    public static final class Builder {
        private Supplier<InfoDisplayManagerConfigData> localConfigSupplier;
        private Consumer<InfoDisplayManagerConfigData> localConfigSetter;
        private Supplier<InfoDisplayManagerConfigData> enforcedConfigSupplier;

        private Builder() {
        }

        private Builder setDefault() {
            this.setLocalConfigSupplier(null);
            this.setLocalConfigSetter(null);
            this.setEnforcedConfigSupplier(null);
            return this;
        }

        public Builder setLocalConfigSupplier(Supplier<InfoDisplayManagerConfigData> localConfigSupplier) {
            this.localConfigSupplier = localConfigSupplier;
            return this;
        }

        public Builder setLocalConfigSetter(Consumer<InfoDisplayManagerConfigData> localConfigSetter) {
            this.localConfigSetter = localConfigSetter;
            return this;
        }

        public Builder setEnforcedConfigSupplier(Supplier<InfoDisplayManagerConfigData> enforcedConfigSupplier) {
            this.enforcedConfigSupplier = enforcedConfigSupplier;
            return this;
        }

        public InfoDisplayManager build() {
            if (this.localConfigSupplier == null || this.localConfigSetter == null || this.enforcedConfigSupplier == null) {
                throw new IllegalStateException();
            }
            HashMap displays = new HashMap();
            ArrayList<String> defaultOrder = new ArrayList<String>();
            List<String> unmodifiableDefaultOrder = Collections.unmodifiableList(defaultOrder);
            return new InfoDisplayManager(displays, defaultOrder, unmodifiableDefaultOrder, this.localConfigSupplier, this.localConfigSetter, this.enforcedConfigSupplier);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

