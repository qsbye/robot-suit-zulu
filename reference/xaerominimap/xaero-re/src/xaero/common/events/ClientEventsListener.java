/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.hud.HudSession;

public class ClientEventsListener {
    public void playerTickPost(HudSession hudSession) {
    }

    public void clientTickPost(HudSession hudSession) {
    }

    public boolean handleRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphics) {
        return false;
    }

    public boolean handleForceToggleKeyMapping(ToggleKeyMapping keyMapping) {
        return false;
    }
}

