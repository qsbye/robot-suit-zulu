/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={Model.class})
public class MixinModel {
    @Inject(at={@At(value="HEAD")}, method={"renderToBuffer"})
    public void onRender(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo info) {
        XaeroMinimapCore.onEntityIconsModelRenderDetection((Model)this, vertices, color);
    }
}

