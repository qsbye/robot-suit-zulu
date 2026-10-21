/*
 * Decompiled with CFR 0.152.
 */
package xaero.minimap;

import xaero.common.HudClientOnlyBase;
import xaero.common.HudMod;
import xaero.hud.xminimap.preset.BuiltInHudPresets;

public class MinimapClientOnly
extends HudClientOnlyBase {
    @Override
    public void preInit(String modId, HudMod modMain) {
        super.preInit(modId, modMain);
    }

    @Override
    public void preLoadLater(HudMod modMain) {
        super.preLoadLater(modMain);
        BuiltInHudPresets.addAll(modMain.getHud().getPresetManager());
        BuiltInHudPresets.TOP_LEFT.apply();
    }
}

