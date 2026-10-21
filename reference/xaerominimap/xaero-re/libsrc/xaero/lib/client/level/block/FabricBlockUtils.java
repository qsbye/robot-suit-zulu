/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import xaero.lib.client.level.block.BlockModelUtils;
import xaero.lib.client.level.block.BlockTextureColorUtils;
import xaero.lib.client.level.block.BlockTranslucencyUtils;
import xaero.lib.client.level.block.BlockUtils;
import xaero.lib.client.level.block.FabricBlockModelUtils;
import xaero.lib.client.level.block.FabricBlockTextureColorUtils;
import xaero.lib.client.level.block.FabricBlockTranslucencyUtils;

public class FabricBlockUtils
extends BlockUtils {
    @Override
    BlockModelUtils createModelUtils() {
        return new FabricBlockModelUtils(this);
    }

    @Override
    BlockTextureColorUtils createTextureColorUtils() {
        return new FabricBlockTextureColorUtils(this);
    }

    @Override
    BlockTranslucencyUtils createTranslucencyUtils() {
        return new FabricBlockTranslucencyUtils(this);
    }
}

