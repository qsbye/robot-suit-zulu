/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import xaero.lib.client.graphics.ITextureBinding;

public class CustomTextureBinding
implements ITextureBinding {
    private final Identifier location;
    private final Supplier<GpuSampler> sampler;

    public CustomTextureBinding(Identifier location, Supplier<GpuSampler> sampler) {
        this.location = location;
        this.sampler = sampler;
    }

    @Override
    public Identifier xaero_lib_getLocation() {
        return this.location;
    }

    @Override
    public Supplier<GpuSampler> xaero_lib_getSampler() {
        return this.sampler;
    }
}

