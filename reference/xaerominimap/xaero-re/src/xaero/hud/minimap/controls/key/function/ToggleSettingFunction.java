/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import java.util.function.Supplier;
import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.BooleanConfigOption;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;

public class ToggleSettingFunction
extends KeyMappingFunction {
    private final Supplier<BooleanConfigOption> settingSupplier;

    protected ToggleSettingFunction(Supplier<BooleanConfigOption> settingSupplier) {
        super(false);
        this.settingSupplier = settingSupplier;
    }

    @Override
    public void onPress() {
        BooleanConfigOption option;
        ConfigProfile currentProfile;
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        currentProfile.set((ConfigOption)option, (Object)((Boolean)(currentProfile = configManager.getCurrentProfile()).get((ConfigOption)(option = this.settingSupplier.get())) == false ? 1 : 0));
        HudMod.INSTANCE.getHudConfigs().getClientConfigProfileIO().save(currentProfile);
    }

    @Override
    public void onRelease() {
    }
}

