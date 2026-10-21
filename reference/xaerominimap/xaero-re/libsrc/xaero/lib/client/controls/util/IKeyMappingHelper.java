/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.controls.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public interface IKeyMappingHelper {
    public InputConstants.Key getBoundKeyOf(KeyMapping var1);

    public boolean modifiersAreActive(KeyMapping var1, int var2);
}

