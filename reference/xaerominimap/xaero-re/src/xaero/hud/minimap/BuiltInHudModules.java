/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xaero.common.gui.GuiMinimapMain;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleManager;

public class BuiltInHudModules {
    public static final HudModule<MinimapSession> MINIMAP = new HudModule(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"minimap"), (Component)Component.translatable((String)"gui.xaero_minimap"), MinimapSession::new, MinimapRenderer::new, GuiMinimapMain::new, MinimapProfiledConfigOptions.DISPLAY_MINIMAP);

    public static void addAll(ModuleManager manager) {
        manager.register(MINIMAP);
    }
}

