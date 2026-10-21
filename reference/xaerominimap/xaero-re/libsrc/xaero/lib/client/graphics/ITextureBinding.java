/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;

public interface ITextureBinding {
    public Identifier xaero_lib_getLocation();

    public Supplier<GpuSampler> xaero_lib_getSampler();
}

