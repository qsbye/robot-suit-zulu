/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.setting;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.setting.EditorSettingType;
import xaero.lib.common.gui.widget.TooltipInfo;

public final class ObjectCategorySetting<T> {
    private final String id;
    private final Component displayName;
    private final T defaultValue;
    private final TooltipInfo tooltip;
    private final EditorSettingType settingUIType;
    private final int uiFirstOption;
    private final int uiLastOption;
    private final IntFunction<T> indexReader;
    private final Function<T, Integer> indexWriter;
    private final Function<T, Component> uiValueNameProvider;

    private ObjectCategorySetting(@Nonnull String id, @Nonnull Component displayName, @Nonnull T defaultValue, @Nonnull EditorSettingType settingUIType, int uiFirstOption, int uiLastOption, IntFunction<T> indexReader, Function<T, Integer> indexWriter, Function<T, Component> uiValueNameProvider, TooltipInfo tooltip) {
        this.id = id;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.settingUIType = settingUIType;
        this.tooltip = tooltip;
        this.uiFirstOption = uiFirstOption;
        this.uiLastOption = uiLastOption;
        this.indexReader = indexReader;
        this.indexWriter = indexWriter;
        this.uiValueNameProvider = uiValueNameProvider;
    }

    public EditorSettingType getSettingUIType() {
        return this.settingUIType;
    }

    public String getId() {
        return this.id;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public TooltipInfo getTooltip() {
        return this.tooltip;
    }

    public int getUiFirstOption() {
        return this.uiFirstOption;
    }

    public int getUiLastOption() {
        return this.uiLastOption;
    }

    public IntFunction<T> getIndexReader() {
        return this.indexReader;
    }

    public Function<T, Integer> getIndexWriter() {
        return this.indexWriter;
    }

    public Function<T, Component> getWidgetValueNameProvider() {
        return this.uiValueNameProvider;
    }

    public static final class Builder<T> {
        private String id;
        private Component displayName;
        private T defaultValue;
        private EditorSettingType settingUIType;
        private TooltipInfo tooltip;
        private int uiFirstOption;
        private int uiLastOption;
        private IntFunction<T> indexReader;
        private Function<T, Integer> indexWriter;
        private Function<T, Component> uiValueNameProvider;

        private Builder() {
        }

        public Builder<T> setDefault() {
            this.setId(null);
            this.setDisplayName(null);
            this.setSettingUIType(null);
            this.setTooltip(null);
            this.setUiFirstOption(0);
            this.setUiLastOption(0);
            this.setIndexReader(null);
            this.setIndexWriter(null);
            this.setUiValueNameProvider(null);
            return this;
        }

        public Builder<T> setId(String id) {
            this.id = id;
            return this;
        }

        public Builder<T> setDisplayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder<T> setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> setSettingUIType(EditorSettingType settingUIType) {
            this.settingUIType = settingUIType;
            return this;
        }

        public Builder<T> setTooltip(TooltipInfo tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder<T> setUiFirstOption(int widgetFirstOption) {
            this.uiFirstOption = widgetFirstOption;
            return this;
        }

        public Builder<T> setUiLastOption(int widgetLastOption) {
            this.uiLastOption = widgetLastOption;
            return this;
        }

        public Builder<T> setIndexReader(IntFunction<T> widgetReader) {
            this.indexReader = widgetReader;
            return this;
        }

        public Builder<T> setIndexWriter(Function<T, Integer> widgetWriter) {
            this.indexWriter = widgetWriter;
            return this;
        }

        public Builder<T> setUiValueNameProvider(Function<T, Component> widgetValueNameProvider) {
            this.uiValueNameProvider = widgetValueNameProvider;
            return this;
        }

        public ObjectCategorySetting<T> build(Map<String, ObjectCategorySetting<?>> destination, List<ObjectCategorySetting<?>> destinationList) {
            if (this.id == null || this.displayName == null || this.defaultValue == null || this.settingUIType == null) {
                throw new IllegalStateException("required fields not set!");
            }
            if (this.settingUIType.isUsingIndices() && (this.indexReader == null || this.indexWriter == null || this.uiValueNameProvider == null)) {
                throw new IllegalStateException("required index usage related fields not set!");
            }
            ObjectCategorySetting<T> result = new ObjectCategorySetting<T>(this.id, this.displayName, this.defaultValue, this.settingUIType, this.uiFirstOption, this.uiLastOption, this.indexReader, this.indexWriter, this.uiValueNameProvider, this.tooltip);
            if (destination == null) {
                return result;
            }
            destination.put(result.getId(), result);
            destinationList.add(result);
            return result;
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

