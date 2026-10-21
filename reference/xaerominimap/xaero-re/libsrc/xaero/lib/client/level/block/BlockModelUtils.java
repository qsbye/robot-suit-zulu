/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import xaero.lib.client.level.block.BlockUtils;

public abstract class BlockModelUtils {
    protected ThreadLocal<RandomSource> threadRandomSource;
    protected ThreadLocal<List<BlockStateModelPart>> threadReusableBlockModelPartList;
    protected final BlockUtils blockUtils;

    protected BlockModelUtils(BlockUtils blockUtils) {
        this.blockUtils = blockUtils;
        this.threadRandomSource = ThreadLocal.withInitial(() -> RandomSource.create((long)0L));
        this.threadReusableBlockModelPartList = ThreadLocal.withInitial(ArrayList::new);
    }

    public abstract List<BakedQuad> getQuads(BlockStateModel var1, BlockAndTintGetter var2, BlockPos var3, BlockState var4, Direction var5);

    public float getQuadSize(BakedQuad quad) {
        float pos0X = quad.position0().x();
        float pos0Z = quad.position0().z();
        float pos1X = quad.position1().x();
        float pos1Z = quad.position1().z();
        float pos2X = quad.position2().x();
        float pos2Z = quad.position2().z();
        float pos3X = quad.position3().x();
        float pos3Z = quad.position3().z();
        float minX = Math.min(Math.min(Math.min(pos0X, pos1X), pos2X), pos3X);
        float maxX = Math.max(Math.max(Math.max(pos0X, pos1X), pos2X), pos3X);
        float minZ = Math.min(Math.min(Math.min(pos0Z, pos1Z), pos2Z), pos3Z);
        float maxZ = Math.max(Math.max(Math.max(pos0Z, pos1Z), pos2Z), pos3Z);
        return (maxX - minX) * (maxZ - minZ);
    }

    public BakedQuad getBiggestQuad(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, Direction direction) {
        Iterator quadIterator;
        List<Object> innerQuads;
        List<Object> touchingQuads = this.getQuads(model, level, pos, state, direction);
        if (touchingQuads == null) {
            touchingQuads = Collections.emptyList();
        }
        if ((innerQuads = this.getQuads(model, level, pos, state, null)) == null) {
            innerQuads = Collections.emptyList();
        }
        if (!(quadIterator = Iterators.concat(touchingQuads.iterator(), innerQuads.iterator())).hasNext()) {
            return null;
        }
        float biggestQuadSize = 0.0f;
        BakedQuad biggestQuad = null;
        while (quadIterator.hasNext()) {
            float quadSize;
            BakedQuad quad = (BakedQuad)quadIterator.next();
            if (quad.direction() != direction || (quadSize = this.getQuadSize(quad)) <= biggestQuadSize) continue;
            biggestQuadSize = quadSize;
            biggestQuad = quad;
        }
        return biggestQuad;
    }
}

