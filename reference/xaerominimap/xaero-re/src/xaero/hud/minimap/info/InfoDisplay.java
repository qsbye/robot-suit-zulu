/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.common.gui.GuiInfoDisplayEdit;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.info.config.InfoDisplayConfigData;
import xaero.hud.minimap.common.config.info.config.InfoDisplayManagerConfigData;
import xaero.hud.minimap.info.InfoDisplayManager;
import xaero.hud.minimap.info.codec.InfoDisplayStateCodec;
import xaero.hud.minimap.info.render.compile.InfoDisplayOnCompile;
import xaero.hud.minimap.info.widget.InfoDisplayWidgetFactory;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;

public final class InfoDisplay<T> {
    private final String id;
    private final Component name;
    private final T defaultState;
    private final ConfigValueIOCodec<T> codec;
    private final InfoDisplayWidgetFactory<T> widgetFactory;
    private final InfoDisplayOnCompile<T> compiler;
    private final Function<ModSettings, T> legacyGetter;
    private ConfigCache configCache;
    private InfoDisplayManager manager;

    private InfoDisplay(String id, Component name, T defaultState, ConfigValueIOCodec<T> codec, InfoDisplayWidgetFactory<T> widgetFactory, InfoDisplayOnCompile<T> compiler, Function<ModSettings, T> legacyGetter) {
        this.id = id;
        this.name = name;
        this.defaultState = defaultState;
        this.codec = codec;
        this.widgetFactory = widgetFactory;
        this.compiler = compiler;
        this.legacyGetter = legacyGetter;
    }

    public void setManager(InfoDisplayManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException();
        }
        this.manager = manager;
    }

    @Deprecated
    public T getState() {
        return this.getEffectiveState();
    }

    public T getEffectiveState() {
        if (this.manager == null) {
            throw new IllegalStateException("The info display must be added to a manager first!");
        }
        this.updateConfigCache();
        return this.configCache.state;
    }

    public int getTextColor() {
        if (this.manager == null) {
            throw new IllegalStateException("The info display must be added to a manager first!");
        }
        this.updateConfigCache();
        return this.configCache.textColor;
    }

    public int getBackgroundColor() {
        if (this.manager == null) {
            throw new IllegalStateException("The info display must be added to a manager first!");
        }
        this.updateConfigCache();
        return this.configCache.backgroundColor;
    }

    private void updateConfigCache() {
        if (this.configCache == null) {
            this.configCache = new ConfigCache(this);
        }
    }

    public String getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public T getDefaultState() {
        return this.defaultState;
    }

    public ConfigValueIOCodec<T> getCodec() {
        return this.codec;
    }

    public AbstractWidget createWidget(int x, int y, int w, int h, GuiInfoDisplayEdit.MoveableEntry<T> entry, Runnable onChange, boolean includeNull) {
        return this.widgetFactory.create(x, y, w, h, entry, onChange, includeNull);
    }

    public InfoDisplayOnCompile<T> getCompiler() {
        return this.compiler;
    }

    public void clearStateCache() {
        this.configCache = null;
    }

    public T getLegacyValue(ModSettings legacySettings) {
        if (this.legacyGetter == null || legacySettings == null) {
            return null;
        }
        return this.legacyGetter.apply(legacySettings);
    }

    private class ConfigCache {
        private static final Function<InfoDisplayConfigData, Integer> BG_COLOR_GETTER = InfoDisplayConfigData::getBackgroundColor;
        private static final Function<InfoDisplayConfigData, Integer> TEXT_COLOR_GETTER = InfoDisplayConfigData::getTextColor;
        private T state;
        private int backgroundColor;
        private int textColor;
        final /* synthetic */ InfoDisplay this$0;

        public ConfigCache(InfoDisplay infoDisplay) {
            InfoDisplay infoDisplay2 = infoDisplay;
            Objects.requireNonNull(infoDisplay2);
            this.this$0 = infoDisplay2;
            this.state = this.fetchStateFromConfig();
            this.backgroundColor = this.fetchBackgroundColorFromConfig();
            this.textColor = this.fetchTextColorFromConfig();
        }

        private T fetchStateFromConfig() {
            InfoDisplayManagerConfigData localManagerConfig;
            InfoDisplayConfigData localDisplayConfig;
            String localStateString;
            String enforcedStateString;
            InfoDisplayManagerConfigData enforcedManagerConfig = this.this$0.manager.getEnforcedConfig();
            InfoDisplayConfigData enforcedDisplayConfig = enforcedManagerConfig == null ? null : enforcedManagerConfig.get(this.this$0.id);
            String string = enforcedStateString = enforcedDisplayConfig == null ? null : enforcedDisplayConfig.getState();
            if (enforcedStateString != null) {
                try {
                    return this.this$0.codec.decode(enforcedStateString, null, null);
                }
                catch (Throwable t) {
                    MinimapLogs.LOGGER.error("Error trying to decode info display state from enforced config: {}", (Object)enforcedStateString, (Object)t);
                }
            }
            String string2 = localStateString = (localDisplayConfig = (localManagerConfig = this.this$0.manager.getLocalConfig()).get(this.this$0.id)) == null ? null : localDisplayConfig.getState();
            if (localStateString == null) {
                return this.this$0.getDefaultState();
            }
            try {
                return this.this$0.codec.decode(localStateString, null, null);
            }
            catch (Throwable t) {
                MinimapLogs.LOGGER.error("Error trying to decode info display state from current config profile: {}", (Object)localStateString, (Object)t);
                return this.this$0.getDefaultState();
            }
        }

        private int fetchBackgroundColorFromConfig() {
            return this.fetchColorFromConfig(BG_COLOR_GETTER, -1);
        }

        private int fetchTextColorFromConfig() {
            return this.fetchColorFromConfig(TEXT_COLOR_GETTER, 15);
        }

        private int fetchColorFromConfig(Function<InfoDisplayConfigData, Integer> colorGetter, int defaultColor) {
            Integer localValue;
            Integer enforcedValue;
            InfoDisplayManagerConfigData enforcedManagerConfig = this.this$0.manager.getEnforcedConfig();
            InfoDisplayConfigData enforcedDisplayConfig = enforcedManagerConfig == null ? null : enforcedManagerConfig.get(this.this$0.id);
            Integer n = enforcedValue = enforcedDisplayConfig == null ? null : colorGetter.apply(enforcedDisplayConfig);
            if (enforcedValue != null) {
                return enforcedValue;
            }
            InfoDisplayManagerConfigData localManagerConfig = this.this$0.manager.getLocalConfig();
            InfoDisplayConfigData localDisplayConfig = localManagerConfig.get(this.this$0.id);
            Integer n2 = localValue = localDisplayConfig == null ? null : colorGetter.apply(localDisplayConfig);
            if (localValue == null) {
                return defaultColor;
            }
            return localValue;
        }
    }

    public static final class Builder<T> {
        private String id;
        private Component name;
        private T defaultState;
        private ConfigValueIOCodec<T> codec;
        private InfoDisplayWidgetFactory<T> widgetFactory;
        private InfoDisplayOnCompile<T> compiler;
        private Consumer<InfoDisplay<?>> destination;
        private Function<ModSettings, T> legacyGetter;

        private Builder() {
        }

        public Builder<T> setDefault() {
            this.setId(null);
            this.setName(null);
            this.setDefaultState(null);
            this.setCodec((InfoDisplayStateCodec<T>)null);
            this.setWidgetFactory(null);
            this.setCompiler(null);
            this.setDestination(null);
            this.setLegacyGetter(null);
            return this;
        }

        public Builder<T> setId(String id) {
            this.id = id;
            return this;
        }

        public Builder<T> setName(Component name) {
            this.name = name;
            return this;
        }

        public Builder<T> setDefaultState(T defaultState) {
            this.defaultState = defaultState;
            return this;
        }

        @Deprecated
        public Builder<T> setCodec(InfoDisplayStateCodec<T> codec) {
            return this.setCodec((ConfigValueIOCodec<T>)codec);
        }

        public Builder<T> setCodec(ConfigValueIOCodec<T> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<T> setWidgetFactory(InfoDisplayWidgetFactory<T> widgetFactory) {
            this.widgetFactory = widgetFactory;
            return this;
        }

        public Builder<T> setCompiler(InfoDisplayOnCompile<T> compiler) {
            this.compiler = compiler;
            return this;
        }

        public Builder<T> setDestination(Consumer<InfoDisplay<?>> destination) {
            this.destination = destination;
            return this;
        }

        public Builder<T> setLegacyGetter(Function<ModSettings, T> legacyGetter) {
            this.legacyGetter = legacyGetter;
            return this;
        }

        public InfoDisplay<T> build() {
            if (this.id == null || this.name == null || this.defaultState == null || this.codec == null || this.widgetFactory == null || this.compiler == null) {
                throw new IllegalStateException();
            }
            InfoDisplay<T> result = new InfoDisplay<T>(this.id, this.name, this.defaultState, this.codec, this.widgetFactory, this.compiler, this.legacyGetter);
            if (this.destination != null) {
                this.destination.accept(result);
            }
            return result;
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

