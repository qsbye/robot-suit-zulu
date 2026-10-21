/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;

@Mixin(value={Player.class})
public class MixinPlayerFabric {
    @Inject(at={@At(value="HEAD")}, method={"tick"})
    public void onTickHead(CallbackInfo info) {
        ((XaeroLibFabric)XaeroLib.INSTANCE).getCommonEvents().onPlayerTick(true, (Player)this);
    }

    @Inject(at={@At(value="TAIL")}, method={"tick"})
    public void onTickTail(CallbackInfo info) {
        ((XaeroLibFabric)XaeroLib.INSTANCE).getCommonEvents().onPlayerTick(false, (Player)this);
    }
}

