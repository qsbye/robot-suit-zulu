/*
 * Decompiled with CFR 0.152.
 */
package xaero.minimap;

import java.io.IOException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import xaero.common.PlatformContext;
import xaero.common.PlatformContextFabric;
import xaero.minimap.XaeroMinimap;

public class XaeroMinimapFabric
extends XaeroMinimap
implements ClientModInitializer,
DedicatedServerModInitializer {
    public static String fileLayoutID = "minimap_fabric";
    private final PlatformContextFabric xaeroHudFabric;

    public XaeroMinimapFabric() {
        this.xaeroHudFabric = (PlatformContextFabric)this.platformContext;
    }

    public void onInitializeClient() {
        try {
            this.loadCommon();
            this.loadClient();
        }
        catch (Throwable e) {
            this.xaeroHudFabric.firstStageError = e;
        }
    }

    public void onInitializeServer() {
        try {
            this.loadCommon();
            this.loadServer();
        }
        catch (Throwable e) {
            this.xaeroHudFabric.firstStageError = e;
        }
    }

    @Override
    protected void loadClient() throws IOException {
        super.loadClient();
        this.xaeroHudFabric.postLoadClient();
    }

    @Override
    protected void loadCommon() {
        super.loadCommon();
        this.xaeroHudFabric.postLoadCommon();
    }

    @Override
    public void loadServer() {
        super.loadServer();
        this.xaeroHudFabric.postLoadServer();
    }

    @Override
    public void tryLoadLater() {
        if (this.xaeroHudFabric.preTryLoadLater()) {
            return;
        }
        this.loadLater();
        this.loadLaterClientRender();
    }

    @Override
    public void tryLoadLaterServer() {
        if (this.xaeroHudFabric.preTryLoadLaterServer()) {
            return;
        }
        this.loadLaterServer();
    }

    @Override
    protected PlatformContext createPlatformContext() {
        return new PlatformContextFabric(this);
    }

    @Override
    public String getFileLayoutID() {
        return fileLayoutID;
    }
}

