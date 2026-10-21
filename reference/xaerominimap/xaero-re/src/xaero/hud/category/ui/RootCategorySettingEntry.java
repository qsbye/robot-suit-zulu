/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.node.options.range.setting.EditorCompactSettingNode;
import xaero.hud.category.ui.setting.EditorSettingType;
import xaero.lib.client.gui.CustomSettingEntry;

public class RootCategorySettingEntry<T>
extends CustomSettingEntry<T> {
    private final Supplier<ObjectCategory<?, ?>> rootCategorySupplier;

    public RootCategorySettingEntry(ObjectCategorySetting<T> setting, Supplier<ObjectCategory<?, ?>> rootCategorySupplier, BooleanSupplier allowNull) {
        this(rootCategorySupplier, allowNull, setting, null);
    }

    public RootCategorySettingEntry(Supplier<ObjectCategory<?, ?>> rootCategorySupplier, BooleanSupplier allowNull, ObjectCategorySetting<T> setting, Component customName) {
        this(rootCategorySupplier, allowNull, setting, customName, null);
    }

    public RootCategorySettingEntry(Supplier<ObjectCategory<?, ?>> rootCategorySupplier, BooleanSupplier allowNull, ObjectCategorySetting<T> setting, Component customName, BiConsumer<T, T> onValueChange) {
        super(allowNull, customName == null ? setting.getDisplayName() : customName, setting.getTooltip(), setting.getSettingUIType() == EditorSettingType.SLIDER, () -> {
            ObjectCategory editedCategory = (ObjectCategory)rootCategorySupplier.get();
            if (editedCategory == null) {
                return null;
            }
            return editedCategory.getSettingValue(setting);
        }, setting.getUiFirstOption(), setting.getUiLastOption(), setting.getIndexReader(), v -> EditorCompactSettingNode.getValueName(setting, v), (oldValue, newValue) -> {
            ObjectCategory editedCategory = (ObjectCategory)rootCategorySupplier.get();
            editedCategory.setSettingValue(setting, newValue);
            if (onValueChange != null) {
                onValueChange.accept(oldValue, newValue);
            }
        }, () -> rootCategorySupplier.get() != null);
        this.rootCategorySupplier = rootCategorySupplier;
    }

    public String getStringForSearch() {
        ObjectCategory<?, ?> editedCategory = this.rootCategorySupplier.get();
        if (editedCategory == null) {
            return "";
        }
        return super.getStringForSearch();
    }

    public AbstractWidget createWidget(int x, int y, int w) {
        ObjectCategory<?, ?> editedCategory = this.rootCategorySupplier.get();
        if (editedCategory == null) {
            return null;
        }
        return super.createWidget(x, y, w);
    }
}

