/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={BossHealthOverlay.class})
public class MixinFabricBossBarHud {
    @Inject(at={@At(value="HEAD")}, method={"extractBar"})
    public void onRenderBossBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossBar, CallbackInfo info) {
        XaeroMinimapCore.onBossHealthRender(y + 19);
    }
}

