/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.xminimap.controls.key.function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.HudMod;
import xaero.common.gui.GuiMinimapMain;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;

public class MinimapSettingsFunction
extends KeyMappingFunction {
    private final boolean server;

    protected MinimapSettingsFunction(boolean server) {
        super(false);
        this.server = server;
    }

    @Override
    public void onPress() {
        Screen current = Minecraft.getInstance().gui.screen();
        Screen currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        Minecraft.getInstance().gui.setScreen((Screen)new GuiMinimapMain(HudMod.INSTANCE, current, currentEscScreen, true, this.server ? BuiltInEditConfigScreenContexts.SERVER : BuiltInEditConfigScreenContexts.CLIENT));
    }

    @Override
    public void onRelease() {
    }
}

