/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.HudMod;
import xaero.common.gui.GuiAddWaypoint;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class AddWaypointFunction
extends KeyMappingFunction {
    protected AddWaypointFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (!HudMod.INSTANCE.getSettings().waypointsGUI(session)) {
            return;
        }
        Minecraft.getInstance().gui.setScreen((Screen)new GuiAddWaypoint(HudMod.INSTANCE, session, null, Lists.newArrayList(), session.getWorldState().getCurrentWorldPath().getRoot(), session.getWorldManager().getCurrentWorld(), true));
    }

    @Override
    public void onRelease() {
    }
}

