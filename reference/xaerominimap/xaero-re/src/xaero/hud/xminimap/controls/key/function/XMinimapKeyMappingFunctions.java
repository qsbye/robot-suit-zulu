/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.xminimap.controls.key.function;

import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.xminimap.controls.key.XMinimapKeyMappings;
import xaero.hud.xminimap.controls.key.function.MinimapSettingsFunction;

public class XMinimapKeyMappingFunctions {
    public static final KeyMappingFunction SETTINGS = new MinimapSettingsFunction(false);
    public static final KeyMappingFunction SERVER_PROFILES = new MinimapSettingsFunction(true);

    public static void registerAll(KeyMappingControllerManager controllerManager) {
        controllerManager.registerFunction(XMinimapKeyMappings.SETTINGS, SETTINGS);
        controllerManager.registerFunction(XMinimapKeyMappings.SERVER_PROFILES, SERVER_PROFILES);
    }
}

