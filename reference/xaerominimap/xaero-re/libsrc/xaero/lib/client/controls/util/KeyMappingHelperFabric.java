/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.controls.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import xaero.lib.XaeroLib;
import xaero.lib.XaeroLibFabric;
import xaero.lib.client.compat.ClientModCompatibilityFabric;
import xaero.lib.client.controls.util.IKeyMappingHelper;

public class KeyMappingHelperFabric
implements IKeyMappingHelper {
    @Override
    public InputConstants.Key getBoundKeyOf(KeyMapping km) {
        return KeyMappingHelper.getBoundKeyOf((KeyMapping)km);
    }

    @Override
    public boolean modifiersAreActive(KeyMapping km, int keyConflictContext) {
        ClientModCompatibilityFabric modCompatibility = ((XaeroLibFabric)XaeroLib.INSTANCE).getClientFabric().getModCompatibility();
        return !modCompatibility.hasAmecs() || modCompatibility.getAmecs().modifiersArePressed(km);
    }
}

