/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import xaero.common.HudMod;
import xaero.common.gui.GuiSlimeSeed;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public class ToggleSlimeChunksFunction
extends KeyMappingFunction {
    protected ToggleSlimeChunksFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        ConfigProfile currentProfile;
        HudMod modMain = HudMod.INSTANCE;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        ClientConfigManager configManager = modMain.getHudConfigs().getClientConfigManager();
        if (Minecraft.getInstance().getSingleplayerServer() == null && ((Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPEN_SLIME_CHUNKS_SCREEN)).booleanValue()) {
            Screen current = Minecraft.getInstance().gui.screen();
            Screen currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
            Minecraft.getInstance().gui.setScreen((Screen)new GuiSlimeSeed(modMain, session, current, currentEscScreen, BuiltInEditConfigScreenContexts.CLIENT));
            return;
        }
        currentProfile.set((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS, (Object)((Boolean)(currentProfile = configManager.getCurrentProfile()).get((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS) == false ? 1 : 0));
        modMain.getHudConfigs().getClientConfigProfileIO().save(currentProfile);
    }

    @Override
    public void onRelease() {
    }
}

