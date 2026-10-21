/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import xaero.common.HudMod;
import xaero.common.effect.Effects;
import xaero.common.misc.Misc;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public class ToggleMapFunction
extends KeyMappingFunction {
    protected ToggleMapFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        if (Misc.hasEffect((Player)mc.player, Effects.NO_MINIMAP) || Misc.hasEffect((Player)mc.player, Effects.NO_MINIMAP_HARMFUL)) {
            return;
        }
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean currentDisplayMinimap = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_MINIMAP);
        configManager.getCurrentProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_MINIMAP, (Object)(!currentDisplayMinimap ? 1 : 0));
        HudMod.INSTANCE.getHudConfigs().getClientConfigProfileIO().save(configManager.getCurrentProfile());
    }

    @Override
    public void onRelease() {
    }
}

