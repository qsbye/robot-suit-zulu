/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.controls.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import xaero.lib.client.controls.util.IKeyMappingHelper;
import xaero.lib.client.controls.util.MouseUtils;
import xaero.lib.platform.Services;

public class KeyMappingUtils {
    public static final int UNKNOWN_KEY = 0;

    public static boolean inputMatches(InputConstants.Type type, int code, KeyMapping kb, int keyConflictContext) {
        IKeyMappingHelper keyBindingHelper = Services.PLATFORM.getKeyMappingHelper();
        return kb != null && code != 0 && keyBindingHelper.getBoundKeyOf(kb).getType() == type && keyBindingHelper.getBoundKeyOf(kb).getValue() == code && keyBindingHelper.modifiersAreActive(kb, keyConflictContext);
    }

    public static void setKeyState(KeyMapping keyMapping, boolean pressed) {
        KeyMapping.set((InputConstants.Key)Services.PLATFORM.getKeyMappingHelper().getBoundKeyOf(keyMapping), (boolean)pressed);
    }

    public static boolean isPhysicallyDown(KeyMapping keyMapping) {
        IKeyMappingHelper keyBindingHelper = Services.PLATFORM.getKeyMappingHelper();
        if (keyBindingHelper.getBoundKeyOf(keyMapping).getValue() == 0) {
            return false;
        }
        if (keyBindingHelper.getBoundKeyOf(keyMapping).getType() == InputConstants.Type.MOUSE) {
            return MouseUtils.isMouseButtonDown(keyBindingHelper.getBoundKeyOf(keyMapping).getValue());
        }
        if (keyBindingHelper.getBoundKeyOf(keyMapping).getType() == InputConstants.Type.KEYBOARD) {
            return InputConstants.isKeyDown((int)keyBindingHelper.getBoundKeyOf(keyMapping).getValue());
        }
        return false;
    }

    public static String getKeyName(KeyMapping kb) {
        if (kb == null || Services.PLATFORM.getKeyMappingHelper().getBoundKeyOf(kb).getValue() == 0) {
            return "(unset)";
        }
        return kb.getTranslatedKeyMessage().getString().toUpperCase();
    }
}

