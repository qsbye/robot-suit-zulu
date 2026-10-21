/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={ChatListener.class})
public class MixinFabricChatListener {
    @Inject(method={"showMessageToPlayer"}, cancellable=true, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/ChatComponent;addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V")})
    public void onShowMessageToPlayer(ChatType.Bound bound, PlayerChatMessage playerChatMessage, Component component, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (HudMod.INSTANCE.getEvents().handleClientPlayerChatReceivedEvent(bound, component, gameProfile)) {
            info.setReturnValue((Object)false);
        }
    }
}

