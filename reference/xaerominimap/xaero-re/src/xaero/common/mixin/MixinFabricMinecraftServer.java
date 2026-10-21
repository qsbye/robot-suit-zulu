/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;

@Mixin(value={MinecraftServer.class})
public class MixinFabricMinecraftServer {
    @Inject(at={@At(value="HEAD")}, method={"tickServer"})
    public void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        if (!(this instanceof DedicatedServer)) {
            return;
        }
        if (HudMod.INSTANCE != null) {
            HudMod.INSTANCE.tryLoadLaterServer();
        }
    }
}

