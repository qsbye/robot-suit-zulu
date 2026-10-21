/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={LevelStorageSource.LevelStorageAccess.class})
public class MixinLevelStorageAccess {
    @Inject(at={@At(value="RETURN")}, method={"deleteLevel"}, cancellable=false)
    public void onDeleteLevel(CallbackInfo info) {
        XaeroMinimapCore.onDeleteWorld((LevelStorageSource.LevelStorageAccess)this);
    }
}

