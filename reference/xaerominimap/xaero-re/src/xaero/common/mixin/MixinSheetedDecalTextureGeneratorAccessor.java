/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SheetedDecalTextureGenerator.class})
public interface MixinSheetedDecalTextureGeneratorAccessor {
    @Accessor
    public float getTextureScale();
}

