/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;
import xaero.lib.client.event.ClientEventsFabric;

@Mixin(value={Gui.class})
public class MixinGuiFabric {
    @Shadow
    private Minecraft minecraft;

    @Inject(at={@At(value="RETURN")}, method={"extractRenderState"})
    public void onRenderEnd(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo info) {
        if (!XaeroLib.isLoaded()) {
            return;
        }
        ClientEventsFabric clientEvents = ((XaeroLibFabric)XaeroLibFabric.INSTANCE).getClientFabric().getEvents();
        if (shouldRenderLevel && this.minecraft.gui.overlay() == null && this.minecraft.gui.screen() != null) {
            clientEvents.onDrawScreenPostMixin(this.minecraft.gui.screen());
        }
    }
}

