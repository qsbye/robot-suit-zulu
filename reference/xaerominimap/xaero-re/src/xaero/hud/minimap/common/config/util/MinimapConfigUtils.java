/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config.util;

import java.util.function.BiFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.common.platform.Services;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapConfigUtils {
    public static BiFunction<ConfigOption<Integer>, Integer, Component> getUIScaleDisplayGetter(int auto, int max, boolean includeAutoValue) {
        return MinimapConfigUtils.getUIScaleDisplayGetter(auto, max, 1.0, includeAutoValue);
    }

    public static BiFunction<ConfigOption<Integer>, Integer, Component> getUIScaleDisplayGetter(int auto, int max, double autoScale, boolean includeAutoValue) {
        return (o, v) -> {
            if (v == auto) {
                MutableComponent result = MinimapConfigConstants.AUTO_SCALE_COMPONENT.copy();
                if (includeAutoValue && !Services.PLATFORM.isDedicatedServer()) {
                    MinimapConfigClientUtils.addAutoUIScaleValueToComponent(result, autoScale);
                }
                return result;
            }
            if (v == max) {
                MutableComponent result = MinimapConfigConstants.MINECRAFT_SCALE_COMPONENT.copy();
                if (!Services.PLATFORM.isDedicatedServer()) {
                    MinimapConfigClientUtils.addAutoMCScaleValueToComponent(result);
                }
                return result;
            }
            return Component.literal((String)v.toString());
        };
    }

    public static BiFunction<ConfigOption<Integer>, Integer, Component> getUIScaleDisplayGetter(int auto, int max) {
        return MinimapConfigUtils.getUIScaleDisplayGetter(auto, max, 1.0);
    }

    public static BiFunction<ConfigOption<Integer>, Integer, Component> getUIScaleDisplayGetter(int auto, int max, double autoScale) {
        return MinimapConfigUtils.getUIScaleDisplayGetter(auto, max, autoScale, true);
    }

    public static Component getAutoMinimapSizeName() {
        MutableComponent component = Component.translatable((String)"gui.xaero_auto_map_size");
        if (!Services.PLATFORM.isDedicatedServer()) {
            component.getSiblings().add(Component.literal((String)(" (" + MinimapConfigClientUtils.getAutoMinimapSize() + ")")));
        }
        return component;
    }
}

