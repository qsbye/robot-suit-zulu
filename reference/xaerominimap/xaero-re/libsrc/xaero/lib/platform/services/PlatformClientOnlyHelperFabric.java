/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.platform.services;

import xaero.lib.client.level.block.FabricBlockUtils;
import xaero.lib.platform.services.IPlatformClientOnlyHelper;

public class PlatformClientOnlyHelperFabric
implements IPlatformClientOnlyHelper {
    private final FabricBlockUtils blockUtils = new FabricBlockUtils();

    @Override
    public FabricBlockUtils getBlockUtils() {
        return this.blockUtils;
    }
}

