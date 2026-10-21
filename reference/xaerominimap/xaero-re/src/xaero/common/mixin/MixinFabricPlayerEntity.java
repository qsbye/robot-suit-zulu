/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.server.core.XaeroMinimapServerCore;

@Mixin(value={Player.class})
public class MixinFabricPlayerEntity {
    @Inject(at={@At(value="HEAD")}, method={"tick"})
    public void onTickStart(CallbackInfo info) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getCommonEvents().handlePlayerTickStart((Player)this);
    }
}

