/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @ModifyArg(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;")})
    public Matrix4f renderLevel(Matrix4f perspectiveMatrix) {
        XaeroMinimapCore.onProjectionMatrix(perspectiveMatrix);
        return perspectiveMatrix;
    }
}

