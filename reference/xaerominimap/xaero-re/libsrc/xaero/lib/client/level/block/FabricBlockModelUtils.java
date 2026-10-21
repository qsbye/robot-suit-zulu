/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import xaero.lib.client.level.block.BlockModelUtils;
import xaero.lib.client.level.block.BlockUtils;

public class FabricBlockModelUtils
extends BlockModelUtils {
    protected FabricBlockModelUtils(BlockUtils blockUtils) {
        super(blockUtils);
    }

    @Override
    public List<BakedQuad> getQuads(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, Direction direction) {
        ArrayList<BakedQuad> result = new ArrayList<BakedQuad>();
        RandomSource usedRandom = (RandomSource)this.threadRandomSource.get();
        List reusableBlockModelPartList = (List)this.threadReusableBlockModelPartList.get();
        reusableBlockModelPartList.clear();
        model.collectParts(usedRandom, reusableBlockModelPartList);
        for (BlockStateModelPart blockModelPart : reusableBlockModelPartList) {
            List partQuads = blockModelPart.getQuads(direction);
            result.addAll(partQuads);
        }
        return result;
    }
}

