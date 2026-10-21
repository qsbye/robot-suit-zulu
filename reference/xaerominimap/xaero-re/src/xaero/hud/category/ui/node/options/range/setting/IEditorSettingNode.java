/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.range.setting;

import xaero.hud.category.setting.ObjectCategorySetting;

public interface IEditorSettingNode<V> {
    public ObjectCategorySetting<V> getSetting();

    public V getSettingValue();

    public boolean isRootSettings();
}

