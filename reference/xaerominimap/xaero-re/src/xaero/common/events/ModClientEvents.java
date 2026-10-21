/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.common.IXaeroMinimap;
import xaero.common.anim.MultiplyAnimationHelper;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public abstract class ModClientEvents {
    protected IXaeroMinimap modMain;

    public ModClientEvents(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void handleRenderModOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        MultiplyAnimationHelper.tick();
        if (Minecraft.getInstance().gui.hud.isHidden()) {
            return;
        }
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            this.modMain.getHudRenderer().render(this.modMain.getHud(), guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
            this.modMain.getMinimap().getWaypointMapRenderer().drawSetChange(minimapSession, guiGraphics, Minecraft.getInstance().getWindow());
        }
    }
}

