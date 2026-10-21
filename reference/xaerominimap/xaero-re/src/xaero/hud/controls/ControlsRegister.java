/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.controls;

import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;

public abstract class ControlsRegister {
    protected final KeyMappingControllerManager keyMappingControllers = new KeyMappingControllerManager();

    protected ControlsRegister() {
    }

    public void registerKeybindings(Consumer<KeyMapping> registry, Consumer<KeyMapping.Category> categoryRegistry) {
        MinimapKeyMappings.registerAll(this.keyMappingControllers, registry, categoryRegistry);
    }

    public void onStage2() {
    }

    public KeyMappingControllerManager getKeyMappingControllers() {
        return this.keyMappingControllers;
    }
}

