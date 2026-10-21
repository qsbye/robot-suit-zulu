/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.HudMod;
import xaero.common.gui.GuiWaypoints;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.gui.ScreenBase;

public class WaypointMenuFunction
extends KeyMappingFunction {
    protected WaypointMenuFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (!HudMod.INSTANCE.getSettings().waypointsGUI(session)) {
            return;
        }
        Screen current = mc.gui.screen();
        Screen currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        mc.gui.setScreen((Screen)new GuiWaypoints(HudMod.INSTANCE, session, current, currentEscScreen));
    }

    @Override
    public void onRelease() {
    }
}

