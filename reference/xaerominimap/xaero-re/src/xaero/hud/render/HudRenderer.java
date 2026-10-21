/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.hud.Hud;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;
import xaero.hud.preset.HudPreset;
import xaero.hud.pushbox.PushboxHandler;
import xaero.hud.render.module.IModuleRenderer;
import xaero.hud.render.module.ModuleRenderContext;

public final class HudRenderer {
    private final PushboxHandler pushboxHandler;

    public HudRenderer(PushboxHandler pushboxHandler) {
        this.pushboxHandler = pushboxHandler;
    }

    public void render(Hud hud, GuiGraphicsExtractor guiGraphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        double screenScale = mc.getWindow().getGuiScale();
        ModuleRenderContext renderContext = new ModuleRenderContext(screenWidth, screenHeight, screenScale);
        this.pushboxHandler.updateAll(hud.getPushboxManager());
        if (mc.gui.screen() == null) {
            for (HudPreset hudPreset : hud.getPresetManager().getPresets()) {
                hudPreset.cancel();
            }
        }
        for (HudModule hudModule : hud.getModuleManager().getModules()) {
            this.renderModule(hudModule, hud, renderContext, guiGraphics, partialTicks);
        }
        this.pushboxHandler.postUpdateAll(hud.getPushboxManager());
    }

    private <MS extends ModuleSession<MS>> void renderModule(HudModule<MS> module, Hud hud, ModuleRenderContext c, GuiGraphicsExtractor guiGraphics, float partialTicks) {
        MS session = module.getCurrentSession();
        ((ModuleSession)session).prePotentialRender();
        if (!((ModuleSession)session).isActive()) {
            return;
        }
        if (module.getUsedTransform().fromOldSystem) {
            hud.getOldSystemCompatibility().convertTransform(module.getUsedTransform(), (ModuleSession<?>)session, c);
        }
        IModuleRenderer<MS> renderer = module.getRenderer();
        PushboxHandler.State currentPushState = module.getPushState();
        currentPushState.resetForModule((ModuleSession<?>)session, c.screenWidth, c.screenHeight, c.screenScale);
        this.pushboxHandler.applyScreenEdges(currentPushState, c.screenWidth, c.screenHeight, c.screenScale);
        this.pushboxHandler.applyPushboxes(hud.getPushboxManager(), currentPushState, c.screenWidth, c.screenHeight, c.screenScale);
        c.x = currentPushState.x;
        c.y = currentPushState.y;
        c.w = ((ModuleSession)session).getWidth(c.screenScale);
        c.h = ((ModuleSession)session).getHeight(c.screenScale);
        c.flippedVertically = ((ModuleSession)session).shouldFlipVertically(c.screenHeight, c.screenScale);
        c.flippedHorizontally = ((ModuleSession)session).shouldFlipHorizontally(c.screenWidth, c.screenScale);
        renderer.render(session, c, guiGraphics, partialTicks);
    }

    public PushboxHandler getPushboxHandler() {
        return this.pushboxHandler;
    }
}

