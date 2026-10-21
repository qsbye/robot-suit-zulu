/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.event;

import net.minecraft.client.gui.screens.Screen;
import xaero.lib.client.event.ClientEvents;

public class ClientEventsFabric
extends ClientEvents {
    public void register() {
    }

    public void onRenderTickMixin() {
        super.onRenderTick();
    }

    public void onDrawScreenPostMixin(Screen screen) {
        super.handleDrawScreenEventPost(screen);
    }
}

