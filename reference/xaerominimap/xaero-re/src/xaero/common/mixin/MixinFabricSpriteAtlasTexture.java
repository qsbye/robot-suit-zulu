/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={TextureAtlas.class})
public class MixinFabricSpriteAtlasTexture {
    @Inject(at={@At(value="RETURN")}, method={"upload"})
    public void onUpload(SpriteLoader.Preparations spriteAtlasTexture$Data_1, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleTextureStitchEventPost((TextureAtlas)this);
    }
}

