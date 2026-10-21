/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client;

import xaero.lib.XaeroLib;
import xaero.lib.client.config.sync.ClientConfigSynchronizer;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.gui.widget.online.WidgetLoadingHandler;
import xaero.lib.client.gui.widget.online.WidgetScreenHandler;
import xaero.lib.patreon.Patreon;

public class XaeroLibClient {
    private Throwable delayedError;
    private ClientConfigSynchronizer configSynchronizer;
    private WidgetScreenHandler widgetScreenHandler;
    private WidgetLoadingHandler widgetLoader;
    private XaeroBufferProvider bufferProvider;

    public void load() {
        try {
            XaeroLib.LOGGER.info("Loading XaeroLib client 1/3!");
            this.bufferProvider = XaeroBufferProvider.Builder.begin().build();
            this.configSynchronizer = ClientConfigSynchronizer.Builder.begin().build();
            this.widgetScreenHandler = new WidgetScreenHandler();
            this.widgetLoader = new WidgetLoadingHandler(this.widgetScreenHandler);
            Patreon.checkPatreon();
        }
        catch (Throwable t) {
            this.delayedError = t;
        }
    }

    public void loadLater() {
        this.checkClientLoadingCrash();
        XaeroLib.LOGGER.info("Loading XaeroLib client 2/3!");
    }

    public void loadLaterRender() {
        if (this.delayedError != null) {
            return;
        }
        XaeroLib.LOGGER.info("Loading XaeroLib client 3/3!");
    }

    public ClientConfigSynchronizer getConfigSynchronizer() {
        return this.configSynchronizer;
    }

    public WidgetScreenHandler getWidgetScreenHandler() {
        return this.widgetScreenHandler;
    }

    public WidgetLoadingHandler getWidgetLoader() {
        return this.widgetLoader;
    }

    public XaeroBufferProvider getBufferProvider() {
        return this.bufferProvider;
    }

    public void checkClientLoadingCrash() {
        if (this.delayedError != null) {
            throw new RuntimeException(this.delayedError);
        }
    }
}

