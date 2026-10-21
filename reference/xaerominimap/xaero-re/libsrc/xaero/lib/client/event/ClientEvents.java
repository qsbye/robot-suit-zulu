/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import xaero.lib.client.graphics.shader.CustomUniforms;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;
import xaero.lib.client.gui.GuiUpdateAll;
import xaero.lib.patreon.Patreon;

public class ClientEvents {
    public void handleDrawScreenEventPost(Screen gui) {
        if (Patreon.needsNotification() && gui instanceof TitleScreen) {
            Minecraft.getInstance().gui.setScreen((Screen)new GuiUpdateAll());
        }
    }

    public void onRenderTick() {
    }

    public void onPostRenderTick() {
        CustomUniforms.endFrame();
        ImmediateRenderUtil.endFrame();
    }
}

