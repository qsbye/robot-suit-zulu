/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={ClientPacketListener.class})
public class MixinFabricClientPacketListener {
    @Inject(at={@At(value="HEAD")}, method={"sendChat(Ljava/lang/String;)V"}, cancellable=true)
    public void onSendChat(String string_1, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (HudMod.INSTANCE.getEvents().handleClientSendChatEvent(string_1)) {
            info.cancel();
        }
    }
}

