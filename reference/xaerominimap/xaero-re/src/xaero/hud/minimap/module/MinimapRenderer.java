/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.entity.player.Player;
import xaero.common.HudMod;
import xaero.common.core.IGuiGraphics;
import xaero.common.effect.Effects;
import xaero.common.misc.Misc;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.render.MinimapPipRenderState;
import xaero.hud.render.module.IModuleRenderer;
import xaero.hud.render.module.ModuleRenderContext;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.IScreenBase;
import xaero.lib.common.config.option.ConfigOption;

public class MinimapRenderer
implements IModuleRenderer<MinimapSession> {
    @Override
    public void render(MinimapSession session, ModuleRenderContext c, GuiGraphicsExtractor guiGraphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (Misc.hasEffect((Player)mc.player, Effects.NO_MINIMAP) || Misc.hasEffect((Player)mc.player, Effects.NO_MINIMAP_HARMFUL) || session.getProcessor().getNoMinimapMessageReceived()) {
            return;
        }
        if (session.getHideMinimapUnderScreen() && mc.gui.screen() != null && !(mc.gui.screen() instanceof IScreenBase) && !(mc.gui.screen() instanceof ChatScreen) && !(mc.gui.screen() instanceof DeathScreen) || session.getHideMinimapUnderF3() && mc.debugEntries.isOverlayVisible()) {
            return;
        }
        int renderX = c.x;
        int renderY = c.y;
        MinimapPipRenderState renderState = session.getProcessor().getRenderState();
        if (session.getProcessor().isEnlargedMap()) {
            renderState = session.getProcessor().getEnlargedRenderState();
            if (((Boolean)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.CENTERED_ENLARGED)).booleanValue()) {
                renderX = (c.screenWidth - c.w) / 2;
                renderY = (c.screenHeight - c.w) / 2;
            }
        }
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        float minimapScale = GuiUtils.getMinimapScale(configManager);
        renderState.update(renderX, renderY, c.screenWidth, c.screenHeight, c.screenScale, minimapScale, session.getConfiguredWidth(), c.w, partialTicks, XaeroLib.INSTANCE.getClient().getBufferProvider());
        ((IGuiGraphics)guiGraphics).xaero_mm_getGuiRenderState().addPicturesInPictureState((PictureInPictureRenderState)renderState);
        session.getProcessor().getRenderer().renderOutsidePip(session, renderX, renderY, c.screenWidth, c.screenHeight, c.screenScale, minimapScale, session.getConfiguredWidth(), partialTicks, guiGraphics);
    }
}

