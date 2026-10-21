/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.Logger;

public class SupportAmecs {
    public SupportAmecs(Logger logger) {
    }

    public boolean modifiersArePressed(KeyMapping keyBinding) {
        KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((KeyMapping)keyBinding);
        return KeyModifiers.getCurrentlyPressed().contains(modifiers);
    }
}

