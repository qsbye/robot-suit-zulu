/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.lib.client.graphics.IRenderType;

@Mixin(value={RenderType.class})
public class MixinRenderType
implements IRenderType {
    @Shadow
    private RenderSetup state;

    @Override
    public RenderSetup xaero_lib_getSetup() {
        return this.state;
    }
}

