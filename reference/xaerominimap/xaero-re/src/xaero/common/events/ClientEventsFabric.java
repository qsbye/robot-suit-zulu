/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import xaero.common.HudMod;
import xaero.common.events.ClientEvents;

public class ClientEventsFabric
extends ClientEvents {
    public ClientEventsFabric(HudMod modMain) {
        super(modMain);
    }

    @Override
    protected void handleTextureStitchEventPost_onReset() {
        super.handleTextureStitchEventPost_onReset();
        this.modMain.getMinimap().getMinimapFBORenderer().resetEntityIconsResources();
    }
}

