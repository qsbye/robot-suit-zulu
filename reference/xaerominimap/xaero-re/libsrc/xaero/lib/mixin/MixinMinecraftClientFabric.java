/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;

@Mixin(value={Minecraft.class})
public class MixinMinecraftClientFabric {
    @ModifyArg(method={"runTick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;renderFrame(Z)V")}, index=0)
    public boolean onRenderCall(boolean advanceGameTime) {
        if (XaeroLib.INSTANCE != null) {
            ((XaeroLibFabric)XaeroLib.INSTANCE).tryLoadLaterClient();
        }
        return advanceGameTime;
    }
}

