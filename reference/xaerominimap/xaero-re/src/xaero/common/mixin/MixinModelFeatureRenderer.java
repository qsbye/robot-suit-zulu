/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={ModelFeatureRenderer.class})
public class MixinModelFeatureRenderer {
    @ModifyVariable(index=1, method={"prepareModel"}, at={@At(value="HEAD")})
    public ModelFeatureRenderer.Submit<?> onPrepareModel(ModelFeatureRenderer.Submit<?> submit) {
        XaeroMinimapCore.onEntityIconsModelPrepareDetection(submit);
        return submit;
    }
}

