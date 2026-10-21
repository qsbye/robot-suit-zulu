/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={KeyMapping.class})
public class MixinFabricKeyMapping {
    @ModifyReturnValue(method={"isDown"}, at={@At(value="RETURN")})
    public boolean onIsDown(boolean original) throws Exception {
        if (!(this instanceof ToggleKeyMapping)) {
            return original;
        }
        if (XaeroMinimapCore.onToggleKeyIsDown((ToggleKeyMapping)this)) {
            return true;
        }
        return original;
    }
}

