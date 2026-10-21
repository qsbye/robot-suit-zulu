/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import com.mojang.blaze3d.Blaze3D;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import xaero.lib.XaeroLib;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetClickHandler;

public class WidgetUrlClickHandler
implements WidgetClickHandler {
    private Screen clickedScreen;
    private URI clickedURL;

    @Override
    public void onClick(Screen screen, Widget widget) {
        this.clickedScreen = screen;
        try {
            this.clickedURL = new URI(widget.getUrl());
        }
        catch (URISyntaxException e) {
            XaeroLib.LOGGER.error("Invalid URL: {}", (Object)widget.getUrl());
            return;
        }
        Minecraft.getInstance().gui.setScreen((Screen)new ConfirmLinkScreen(this::confirmLink, this.clickedURL, true));
    }

    private void confirmLink(boolean confirmed) {
        if (confirmed) {
            Blaze3D.openUri((URI)this.clickedURL);
        }
        Minecraft.getInstance().gui.setScreen(this.clickedScreen);
    }
}

