/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import java.util.Map;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.lib.client.graphics.IRenderSetup;

@Mixin(value={RenderSetup.class})
public class MixinRenderSetup
implements IRenderSetup {
    @Shadow
    Map<String, ?> textures;
    @Shadow
    TextureTransform textureTransform;
    @Shadow
    boolean useOverlay;
    @Shadow
    LayeringTransform layeringTransform;

    @Override
    public Map<String, ?> xaero_lib_getTextures() {
        return this.textures;
    }

    @Override
    public TextureTransform xaero_lib_getTextureTransform() {
        return this.textureTransform;
    }

    @Override
    public boolean xaero_lib_getUseOverlay() {
        return this.useOverlay;
    }

    @Override
    public LayeringTransform xaero_lib_getLayeringTransform() {
        return this.layeringTransform;
    }
}

