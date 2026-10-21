/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;

public class ManualCaveModeFunction
extends KeyMappingFunction {
    protected ManualCaveModeFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        BuiltInHudModules.MINIMAP.getCurrentSession().getProcessor().toggleManualCaveMode();
    }

    @Override
    public void onRelease() {
    }
}

