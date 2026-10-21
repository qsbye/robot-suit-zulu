/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.gui.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.common.config.option.ConfigOption;

public class GuiUtils {
    public static float getMinimapScale(ClientConfigManager configManager) {
        int uiScaleConfigValue = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.UI_SCALE);
        List validValues = MinimapProfiledConfigOptions.UI_SCALE.getValidValues();
        int min = (Integer)validValues.get(0);
        int max = (Integer)validValues.get(validValues.size() - 1);
        return GuiUtils.getUIScale(uiScaleConfigValue, min, max);
    }

    public static float getUIScale(int optionValue, int min, int max) {
        if (optionValue <= min) {
            return GuiUtils.getAutoUIScale();
        }
        if (optionValue == max) {
            return Minecraft.getInstance().getWindow().getGuiScale();
        }
        return optionValue;
    }

    public static int getAutoUIScale() {
        int width;
        int size;
        int height = Minecraft.getInstance().getWindow().getHeight();
        int n = size = height <= (width = Minecraft.getInstance().getWindow().getWidth()) ? height : width;
        if (size >= 1500) {
            int steps = size / 500;
            return steps;
        }
        return 2;
    }

    public static void refreshScreenBase() {
        ScreenBase screenBase;
        Screen currentScreen = Minecraft.getInstance().gui.screen();
        ScreenBase screenBase2 = screenBase = currentScreen instanceof ScreenBase ? (ScreenBase)currentScreen : null;
        if (screenBase == null) {
            return;
        }
        screenBase.refresh();
    }
}

