/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.setting;

import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNodeBuilder;

@FunctionalInterface
public interface SettingNodeBuilderFactory {
    public <V> IEditorSettingNodeBuilder<V, ?> apply(ListFactory var1);
}

