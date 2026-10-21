/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xaero.common.HudMod;
import xaero.lib.client.gui.GuiSettings;

public class XaeroHudModMenu
implements ModMenuApi {
    public ConfigScreenFactory<GuiSettings> getModConfigScreenFactory() {
        return screen -> HudMod.INSTANCE.getGuiHelper().getMainSettingsScreen(screen);
    }
}

