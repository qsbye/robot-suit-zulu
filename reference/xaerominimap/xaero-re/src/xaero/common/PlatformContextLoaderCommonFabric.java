/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContextLoaderCommon;
import xaero.common.effect.EffectsRegister;
import xaero.common.events.CommonEventsFabric;
import xaero.hud.minimap.common.config.primary.option.MinimapPrimaryCommonConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;

public class PlatformContextLoaderCommonFabric
extends PlatformContextLoaderCommon {
    @Override
    public void setup(IXaeroMinimap modMain) {
        ((CommonEventsFabric)modMain.getCommonEvents()).register();
        modMain.getSupportServerMods().registerEvents();
        SingleConfigManager primaryConfig = HudMod.INSTANCE.getHudConfigs().getPrimaryCommonConfigManager();
        if (((Boolean)primaryConfig.getEffective((ConfigOption)MinimapPrimaryCommonConfigOptions.REGISTER_EFFECTS)).booleanValue()) {
            new EffectsRegister().registerEffects(effect -> Registry.registerForHolder((Registry)BuiltInRegistries.MOB_EFFECT, (Identifier)effect.getRegistryName(), (Object)effect));
        }
    }
}

