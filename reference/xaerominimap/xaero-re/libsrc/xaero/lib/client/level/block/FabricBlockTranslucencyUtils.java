/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xaero.lib.client.level.block.BlockTranslucencyUtils;
import xaero.lib.client.level.block.BlockUtils;

public class FabricBlockTranslucencyUtils
extends BlockTranslucencyUtils {
    public FabricBlockTranslucencyUtils(BlockUtils blockUtils) {
        super(blockUtils);
    }

    @Override
    public boolean shouldConsiderBlockTranslucent(BlockAndTintGetter level, BlockPos pos, BlockState blockState) {
        return this.getModelBasedTranslucency(level, pos, blockState);
    }
}

