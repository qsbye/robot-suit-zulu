/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={Hud.class})
public class MixinOptionalInGameHud {
    @Inject(at={@At(value="HEAD")}, method={"extractCrosshair"}, cancellable=true)
    public void onRenderCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (XaeroMinimapCore.onRenderCrosshair(guiGraphics)) {
            info.cancel();
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"extractEffects"})
    public void postRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        XaeroMinimapCore.onRenderStatusEffectOverlayPost(guiGraphics);
    }

    @Inject(at={@At(value="HEAD")}, method={"extractEffects"}, cancellable=true)
    public void onRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (XaeroMinimapCore.onRenderStatusEffectOverlay(guiGraphics)) {
            info.cancel();
        }
    }
}

