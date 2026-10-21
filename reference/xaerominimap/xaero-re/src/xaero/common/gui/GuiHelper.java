/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapMain;
import xaero.lib.client.gui.GuiSettings;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;

public abstract class GuiHelper {
    protected IXaeroMinimap modMain;

    public GuiHelper(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public ScreenBase getMinimapSettingsFromScreen(Screen currentScreen) {
        IEditConfigScreenContext iEditConfigScreenContext;
        Screen escScreen = ScreenBase.tryToGetEscape((Screen)currentScreen);
        if (currentScreen instanceof EditConfigScreen) {
            EditConfigScreen ecs = (EditConfigScreen)currentScreen;
            iEditConfigScreenContext = ecs.getContext();
        } else {
            iEditConfigScreenContext = BuiltInEditConfigScreenContexts.CLIENT;
        }
        IEditConfigScreenContext context = iEditConfigScreenContext;
        return new GuiMinimapMain(this.modMain, currentScreen, escScreen, true, context);
    }

    @Deprecated
    public void openMinimapSettingsFromScreen(Screen parent, Screen escScreen) {
        Minecraft.getInstance().gui.setScreen((Screen)new GuiMinimapMain(this.modMain, parent, escScreen, true, BuiltInEditConfigScreenContexts.CLIENT));
    }

    public abstract GuiSettings getMainSettingsScreen(Screen var1);

    public abstract void openMainSettingsFromScreen(Screen var1);
}

