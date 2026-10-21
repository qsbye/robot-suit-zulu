/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.xminimap.controls.key;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.xminimap.controls.key.function.XMinimapKeyMappingFunctions;

public class XMinimapKeyMappings {
    public static KeyMapping SETTINGS = new KeyMapping("gui.xaero_minimap_settings", 28, MinimapKeyMappings.CATEGORY);
    public static KeyMapping SERVER_PROFILES = new KeyMapping("gui.xaero_minimap_server_profiles", InputConstants.UNKNOWN.getValue(), MinimapKeyMappings.CATEGORY);

    public static void registerAll(KeyMappingControllerManager controllerManager, Consumer<KeyMapping> registry, Consumer<KeyMapping.Category> categoryRegistry) {
        controllerManager.registerController(SETTINGS, true, registry);
        controllerManager.registerController(SERVER_PROFILES, true, registry);
        XMinimapKeyMappingFunctions.registerAll(controllerManager);
    }
}

