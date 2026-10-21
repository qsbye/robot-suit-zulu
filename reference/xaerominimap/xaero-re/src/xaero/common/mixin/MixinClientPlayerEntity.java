/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={LocalPlayer.class})
public class MixinClientPlayerEntity {
    @Inject(at={@At(value="HEAD")}, method={"respawn"})
    public void onTickStart(CallbackInfo info) {
        XaeroMinimapCore.beforeRespawn((Player)this);
    }
}

