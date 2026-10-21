/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.compat.amecs;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.KeyMapping;

public class AmecsCompatibility {
    public boolean modifiersArePressed(KeyMapping keyBinding) {
        KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((KeyMapping)keyBinding);
        return KeyModifiers.getCurrentlyPressed().contains(modifiers);
    }
}

