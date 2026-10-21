/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.controls.key;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class KeyMappingController
implements Iterable<KeyMappingFunction> {
    private final KeyMapping keyMapping;
    private final boolean xaeroKey;
    private final Set<KeyMappingFunction> functions;
    private boolean pressed;

    public KeyMappingController(KeyMapping keyMapping, boolean xaeroKey) {
        this.keyMapping = keyMapping;
        this.xaeroKey = xaeroKey;
        this.functions = new HashSet<KeyMappingFunction>();
    }

    public void add(KeyMappingFunction function) {
        this.functions.add(function);
    }

    public KeyMapping getKeyMapping() {
        return this.keyMapping;
    }

    public Iterable<KeyMappingFunction> getFunctions() {
        return this.functions;
    }

    public boolean isXaeroKey() {
        return this.xaeroKey;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public Iterator<KeyMappingFunction> iterator() {
        return this.functions.iterator();
    }
}

