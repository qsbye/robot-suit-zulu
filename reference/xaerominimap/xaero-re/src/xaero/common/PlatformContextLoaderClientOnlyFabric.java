/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContextLoaderClientOnly;
import xaero.common.events.ModClientEventsFabric;
import xaero.hud.minimap.render.MinimapPipRenderState;
import xaero.hud.minimap.render.MinimapPipRenderer;

public class PlatformContextLoaderClientOnlyFabric
extends PlatformContextLoaderClientOnly {
    @Override
    public void preInit(String modId, IXaeroMinimap modMain) {
        ((ModClientEventsFabric)modMain.getModClientEvents()).register();
        modMain.getSupportMods().registerClientEvents();
        modMain.ensureControlsRegister();
        modMain.getControlsRegister().registerKeybindings(KeyMappingHelper::registerKeyMapping, c -> {});
        PictureInPictureRendererRegistry.register(ctx -> new MinimapPipRenderer<MinimapPipRenderState>(MinimapPipRenderState.class));
        PictureInPictureRendererRegistry.register(ctx -> new MinimapPipRenderer<MinimapPipRenderState.Enlarged>(MinimapPipRenderState.Enlarged.class));
    }
}

