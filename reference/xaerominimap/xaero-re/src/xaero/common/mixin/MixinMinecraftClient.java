/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IXaeroMinimapMinecraftClient;

@Mixin(value={Minecraft.class})
public class MixinMinecraftClient
implements IXaeroMinimapMinecraftClient {
    @Shadow
    private static int fps;

    @Override
    public int getXaeroMinimap_fps() {
        return fps;
    }
}

