/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.widget;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import xaero.hud.minimap.info.widget.InfoDisplayCycleWidgetFactory;
import xaero.hud.minimap.info.widget.InfoDisplayWidgetFactory;

public class InfoDisplayCommonWidgetFactories {
    public static final InfoDisplayCycleWidgetFactory<Boolean> OFF_ON = new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Boolean[]{false, true}), Lists.newArrayList((Object[])new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_on")}));
    public static final InfoDisplayWidgetFactory<Boolean> ALWAYS_ON = (x, y, w, h, entry, onChange, includeNull) -> null;
}

