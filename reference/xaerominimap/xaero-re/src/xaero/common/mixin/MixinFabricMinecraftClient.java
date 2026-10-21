/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={Minecraft.class})
public class MixinFabricMinecraftClient {
    @Shadow
    public ClientLevel level;

    @Inject(at={@At(value="HEAD")}, method={"tick"})
    public void onTickStart(CallbackInfo info) {
        if (HudMod.INSTANCE != null) {
            HudMod.INSTANCE.tryLoadLater();
        }
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleClientTickStart();
    }

    @Inject(at={@At(value="HEAD")}, method={"disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V"})
    public void onDisconnect(Screen screen_1, boolean b, CallbackInfo info) {
        if (this.level != null) {
            if (!XaeroMinimapCore.isModLoaded()) {
                return;
            }
            HudMod.INSTANCE.getEvents().worldUnload((LevelAccessor)this.level);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"setLevel"})
    public void onJoinWorld(ClientLevel newWorld, CallbackInfo info) {
        if (this.level != null) {
            if (!XaeroMinimapCore.isModLoaded()) {
                return;
            }
            HudMod.INSTANCE.getEvents().worldUnload((LevelAccessor)this.level);
        }
    }
}

