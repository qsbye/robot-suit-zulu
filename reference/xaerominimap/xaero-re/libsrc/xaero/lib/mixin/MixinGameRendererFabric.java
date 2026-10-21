/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;
import xaero.lib.client.event.ClientEventsFabric;

@Mixin(value={GameRenderer.class})
public class MixinGameRendererFabric {
    @Inject(at={@At(value="HEAD")}, method={"extract"})
    public void onRenderStart(DeltaTracker deltaTracker, boolean boolean_1, CallbackInfo info) {
        if (XaeroLib.INSTANCE != null) {
            XaeroLib.INSTANCE.checkClientLoadingCrash();
        }
        if (!XaeroLib.isLoaded()) {
            return;
        }
        ClientEventsFabric clientEvents = ((XaeroLibFabric)XaeroLibFabric.INSTANCE).getClientFabric().getEvents();
        clientEvents.onRenderTickMixin();
    }

    @Inject(at={@At(value="TAIL")}, method={"extract"})
    public void onRenderEnd(DeltaTracker deltaTracker, boolean boolean_1, CallbackInfo info) {
        if (!XaeroLib.isLoaded()) {
            return;
        }
        ClientEventsFabric clientEvents = ((XaeroLibFabric)XaeroLibFabric.INSTANCE).getClientFabric().getEvents();
        clientEvents.onPostRenderTick();
    }
}

