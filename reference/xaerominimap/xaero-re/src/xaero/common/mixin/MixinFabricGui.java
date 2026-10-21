/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.events.ClientEvents;

@Mixin(value={Gui.class})
public class MixinFabricGui {
    @Shadow
    private Minecraft minecraft;

    @Inject(at={@At(value="RETURN")}, method={"extractRenderState"})
    public void onRenderGuiEnd(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo info) {
        ClientEvents events;
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (shouldRenderLevel && this.minecraft.gui.overlay() == null && this.minecraft.gui.screen() != null && (events = HudMod.INSTANCE.getEvents()) != null) {
            events.handleDrawScreenEventPost(this.minecraft.gui.screen());
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"setScreen"}, cancellable=true)
    public void onOpenScreen(Screen screen_1, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        Screen resultScreen = HudMod.INSTANCE.getEvents().handleGuiOpen(screen_1);
        if (screen_1 != resultScreen) {
            ((Minecraft)this).gui.setScreen(resultScreen);
            info.cancel();
        }
    }
}

