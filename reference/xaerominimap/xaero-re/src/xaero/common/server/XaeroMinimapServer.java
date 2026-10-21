/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.common.IXaeroMinimap;
import xaero.common.server.events.ServerEvents;

public class XaeroMinimapServer {
    public static int SERVER_COMPATIBILITY = 1;
    private final Logger LOGGER = LogManager.getLogger();
    private IXaeroMinimap modMain;
    private ServerEvents serverEvents;

    public XaeroMinimapServer(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void load() {
        this.serverEvents = new ServerEvents(this);
        this.serverEvents.register();
    }

    public void loadLater() {
    }

    public IXaeroMinimap getModMain() {
        return this.modMain;
    }

    public ServerEvents getServerEvents() {
        return this.serverEvents;
    }
}

