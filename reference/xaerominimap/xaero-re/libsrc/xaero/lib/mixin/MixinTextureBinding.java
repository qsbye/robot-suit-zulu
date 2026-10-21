/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.lib.client.graphics.ITextureBinding;

@Mixin(targets={"net.minecraft.client.renderer.rendertype.RenderSetup$TextureBinding"})
public class MixinTextureBinding
implements ITextureBinding {
    @Shadow
    private Identifier location;
    @Shadow
    private Supplier<GpuSampler> sampler;

    @Override
    public Identifier xaero_lib_getLocation() {
        return this.location;
    }

    @Override
    public Supplier<GpuSampler> xaero_lib_getSampler() {
        return this.sampler;
    }
}

