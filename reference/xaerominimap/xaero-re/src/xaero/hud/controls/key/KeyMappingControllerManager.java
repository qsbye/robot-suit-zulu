/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.controls.key;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import xaero.hud.controls.key.KeyMappingController;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class KeyMappingControllerManager
implements Iterable<KeyMappingController> {
    private final Map<KeyMapping, KeyMappingController> controllers = new HashMap<KeyMapping, KeyMappingController>();

    public KeyMappingController getController(KeyMapping keyMapping) {
        return this.controllers.get(keyMapping);
    }

    public void registerController(KeyMapping keyMapping, boolean xaeroKey) {
        this.registerController(keyMapping, xaeroKey, null);
    }

    public void registerController(KeyMapping keyMapping, boolean xaeroKey, Consumer<KeyMapping> then) {
        if (this.controllers.containsKey(keyMapping)) {
            throw new IllegalArgumentException("The key mapping is already registered!");
        }
        this.controllers.put(keyMapping, new KeyMappingController(keyMapping, xaeroKey));
        if (then != null) {
            then.accept(keyMapping);
        }
    }

    public void registerFunction(KeyMapping keyMapping, KeyMappingFunction function) {
        KeyMappingController functionSet = this.getController(keyMapping);
        if (functionSet == null) {
            throw new IllegalArgumentException("The key mapping needs to be registered with registerController first!");
        }
        functionSet.add(function);
    }

    @Override
    public Iterator<KeyMappingController> iterator() {
        return this.controllers.values().iterator();
    }
}

