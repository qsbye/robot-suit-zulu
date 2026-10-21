/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.event;

import xaero.common.XaeroMinimapSession;
import xaero.hud.Hud;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;

public class HudEventHandler {
    private Hud hud;

    public void setHud(Hud hud) {
        if (this.hud != null) {
            throw new IllegalStateException();
        }
        this.hud = hud;
    }

    public void handleRenderGameOverlayEventPost() {
        if (XaeroMinimapSession.getCurrentSession() == null) {
            return;
        }
        for (HudModule<?> module : this.hud.getModuleManager().getModules()) {
            ((ModuleSession)module.getCurrentSession()).onPostGameOverlay();
        }
    }
}

