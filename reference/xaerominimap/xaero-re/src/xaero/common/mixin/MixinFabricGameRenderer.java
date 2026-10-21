/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.events.ClientEvents;

@Mixin(value={GameRenderer.class})
public class MixinFabricGameRenderer {
    @Shadow
    private Minecraft minecraft;

    @Inject(at={@At(value="HEAD")}, method={"extract"})
    public void onRenderStart(DeltaTracker deltaTracker, boolean boolean_1, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        ClientEvents fmlEvents = HudMod.INSTANCE.getEvents();
        if (fmlEvents != null) {
            fmlEvents.handleRenderTickStart();
        }
    }
}

