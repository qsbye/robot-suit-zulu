/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={ChatListener.class})
public class MixinChatListener {
    @Inject(method={"handleDisguisedChatMessage"}, cancellable=true, at={@At(value="HEAD")})
    public void onHandleDisguisedChatMessag(Component component, ChatType.Bound bound, CallbackInfo info) {
        if (!XaeroMinimapCore.onHandleDisguisedChatMessage(bound, component)) {
            info.cancel();
        }
    }

    @Inject(method={"handleSystemMessage"}, cancellable=true, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/ChatComponent;addServerSystemMessage(Lnet/minecraft/network/chat/Component;)V")})
    public void onHandleSystemChat(Component component, boolean bl, CallbackInfo info) {
        if (XaeroMinimapCore.onSystemChat(component)) {
            info.cancel();
        }
    }
}

