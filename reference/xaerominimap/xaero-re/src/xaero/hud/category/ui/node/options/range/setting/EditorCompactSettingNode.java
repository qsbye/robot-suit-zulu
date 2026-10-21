/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.range.setting;

import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.options.range.EditorCompactRangeNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNodeBuilder;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.hud.category.util.CategoryConstants;

public final class EditorCompactSettingNode<V>
extends EditorCompactRangeNode<V>
implements IEditorSettingNode<V> {
    private final ObjectCategorySetting<V> setting;
    private final boolean rootSettings;

    private EditorCompactSettingNode(ObjectCategorySetting<V> setting, Component displayName, V settingValue, boolean rootSettings, boolean hasNullOption, int currentIndex, int optionCount, int minNumber, IntFunction<V> numberReader, Function<V, Component> valueNamer, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(displayName, settingValue, currentIndex, optionCount, minNumber, hasNullOption, numberReader, valueNamer, movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.setting = setting;
        this.rootSettings = rootSettings;
    }

    @Override
    public ObjectCategorySetting<V> getSetting() {
        return this.setting;
    }

    @Override
    public V getSettingValue() {
        return this.getCurrentRangeValue();
    }

    @Override
    public boolean isRootSettings() {
        return this.rootSettings;
    }

    public static <T> Component getValueName(ObjectCategorySetting<T> setting, Object value) {
        if (value == null) {
            return CategoryConstants.INHERIT;
        }
        return setting.getWidgetValueNameProvider().apply(value);
    }

    public static final class Builder<V>
    extends EditorCompactRangeNode.Builder<V, Builder<V>>
    implements IEditorSettingNodeBuilder<V, EditorCompactSettingNode<V>> {
        private ObjectCategorySetting<V> setting;
        private boolean rootSettings;

        private Builder() {
        }

        @Override
        public Builder<V> setDefault() {
            this.setSetting((ObjectCategorySetting)null);
            this.setRootSettings(false);
            return (Builder)super.setDefault();
        }

        public Builder<V> setSetting(ObjectCategorySetting<V> setting) {
            this.setting = setting;
            if (setting == null) {
                this.setValueNamer(null);
                this.setNumberReader(null);
                this.setNumberWriter(null);
                this.setMinNumber(0);
                this.setMaxNumber(0);
                this.setTooltipSupplier(null);
            } else {
                this.setValueNamer(v -> EditorCompactSettingNode.getValueName(setting, v));
                this.setNumberReader(setting.getIndexReader());
                this.setNumberWriter(setting.getIndexWriter());
                this.setMinNumber(setting.getUiFirstOption());
                this.setMaxNumber(setting.getUiLastOption());
                this.setTooltipInfoSupplier((parent, data) -> setting.getTooltip());
            }
            return this;
        }

        public Builder<V> setSettingValue(V settingValue) {
            this.setCurrentRangeValue(settingValue);
            return this;
        }

        public Builder<V> setRootSettings(boolean rootSettings) {
            this.rootSettings = rootSettings;
            this.setHasNullOption(!rootSettings);
            return this;
        }

        @Override
        public Builder<V> setSlider(boolean slider) {
            return (Builder)super.setSlider(slider);
        }

        @Override
        public EditorCompactSettingNode<V> build() {
            if (this.setting == null) {
                throw new IllegalStateException("required fields not set!");
            }
            if (this.displayName == null) {
                this.setDisplayName(this.setting.getDisplayName());
            }
            return (EditorCompactSettingNode)super.build();
        }

        public static <V> Builder<V> begin() {
            return new Builder<V>().setDefault();
        }

        @Override
        protected EditorCompactRangeNode<V> buildInternally(int currentIndex, int optionCount, EditorListRootEntryFactory listEntryFactory) {
            return new EditorCompactSettingNode<Object>(this.setting, this.displayName, this.currentRangeValue, this.rootSettings, this.hasNullOption, currentIndex, optionCount, this.minNumber, this.numberReader, this.valueNamer, this.movable, listEntryFactory, this.tooltipSupplier, this.isActiveSupplier);
        }
    }
}

