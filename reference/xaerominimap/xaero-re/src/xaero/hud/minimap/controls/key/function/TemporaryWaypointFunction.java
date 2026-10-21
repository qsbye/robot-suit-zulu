/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import xaero.common.effect.Effects;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class TemporaryWaypointFunction
extends KeyMappingFunction {
    protected TemporaryWaypointFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        if (Misc.hasEffect((Player)mc.player, Effects.NO_WAYPOINTS) || Misc.hasEffect((Player)mc.player, Effects.NO_WAYPOINTS_HARMFUL)) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        session.getWaypointSession().getTemporaryHandler().createTemporaryWaypoint(session.getWorldManager().getCurrentWorld(), OptimizedMath.myFloor(mc.getCameraEntity().getX()), OptimizedMath.myFloor(mc.getCameraEntity().getY() + 0.0625), OptimizedMath.myFloor(mc.getCameraEntity().getZ()));
    }

    @Override
    public void onRelease() {
    }
}

