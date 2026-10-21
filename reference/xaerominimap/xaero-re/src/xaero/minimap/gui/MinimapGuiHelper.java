/*
 * Decompiled with CFR 0.152.
 */
package xaero.minimap.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiHelper;
import xaero.common.gui.GuiMinimapMain;
import xaero.lib.client.gui.GuiSettings;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;

public class MinimapGuiHelper
extends GuiHelper {
    public MinimapGuiHelper(IXaeroMinimap modMain) {
        super(modMain);
    }

    @Override
    public GuiSettings getMainSettingsScreen(Screen parent) {
        return new GuiMinimapMain(this.modMain, parent, null, true, BuiltInEditConfigScreenContexts.CLIENT);
    }

    @Override
    public void openMainSettingsFromScreen(Screen screen) {
        Minecraft.getInstance().gui.setScreen((Screen)this.getMinimapSettingsFromScreen(screen));
    }
}

