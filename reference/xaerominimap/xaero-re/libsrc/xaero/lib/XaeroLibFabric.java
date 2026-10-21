/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import xaero.lib.XaeroLib;
import xaero.lib.client.XaeroLibClientFabric;
import xaero.lib.common.event.CommonEventsFabric;

public class XaeroLibFabric
extends XaeroLib
implements ClientModInitializer,
DedicatedServerModInitializer {
    private boolean triedLoadingLater;
    private XaeroLibClientFabric libClientFabric;
    private CommonEventsFabric commonEvents;

    public void onInitializeClient() {
        this.loadCommon();
        this.loadClient();
        this.libClientFabric = new XaeroLibClientFabric();
        this.libClientFabric.load();
        this.registerEvents(true);
    }

    public void onInitializeServer() {
        this.loadCommon();
        this.loadServer();
        this.registerEvents(false);
    }

    public void tryLoadLaterClient() {
        if (this.triedLoadingLater) {
            return;
        }
        this.triedLoadingLater = true;
        this.loadCommonLater();
        this.loadClientLater();
        this.libClientFabric.loadLater();
        this.loadClientRenderLater();
    }

    public void tryLoadLaterServer() {
        if (this.triedLoadingLater) {
            return;
        }
        this.triedLoadingLater = true;
        this.loadCommonLater();
        this.loadServerLater();
    }

    private void registerEvents(boolean client) {
        this.commonEvents = new CommonEventsFabric();
        this.commonEvents.register();
        if (!client) {
            return;
        }
        this.libClientFabric.registerEvents();
    }

    public CommonEventsFabric getCommonEvents() {
        return this.commonEvents;
    }

    public XaeroLibClientFabric getClientFabric() {
        return this.libClientFabric;
    }
}

