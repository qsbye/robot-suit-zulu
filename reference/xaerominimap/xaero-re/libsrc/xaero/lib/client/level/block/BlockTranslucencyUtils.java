/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import xaero.lib.client.level.block.BlockModelUtils;
import xaero.lib.client.level.block.BlockUtils;

public abstract class BlockTranslucencyUtils {
    protected ThreadLocal<RandomSource> threadRandomSource;
    protected final BlockUtils blockUtils;

    public BlockTranslucencyUtils(BlockUtils blockUtils) {
        this.blockUtils = blockUtils;
        this.threadRandomSource = ThreadLocal.withInitial(() -> RandomSource.create((long)0L));
    }

    public boolean getModelBasedTranslucency(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        Iterator quadIterator;
        List<Object> upTouchingQuads;
        BlockStateModelSet bms = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        BlockStateModel model = bms.get(state);
        if (model == null) {
            return true;
        }
        float biggestUpQuadSize = 0.0f;
        boolean biggestUpQuadIsTranslucent = false;
        BlockModelUtils modelUtils = this.blockUtils.getModelUtils();
        List<Object> innerQuads = modelUtils.getQuads(model, level, pos, state, null);
        if (innerQuads == null) {
            innerQuads = Collections.emptyList();
        }
        if ((upTouchingQuads = modelUtils.getQuads(model, level, pos, state, Direction.UP)) == null) {
            upTouchingQuads = Collections.emptyList();
        }
        if (!(quadIterator = Iterators.concat(upTouchingQuads.iterator(), innerQuads.iterator())).hasNext()) {
            return false;
        }
        while (quadIterator.hasNext()) {
            float quadSize;
            BakedQuad quad = (BakedQuad)quadIterator.next();
            if (quad.direction() != Direction.UP || (quadSize = modelUtils.getQuadSize(quad)) < biggestUpQuadSize || !biggestUpQuadIsTranslucent && quadSize == biggestUpQuadSize) continue;
            biggestUpQuadIsTranslucent = quad.materialInfo().layer().translucent();
            biggestUpQuadSize = quadSize;
        }
        if (biggestUpQuadIsTranslucent) {
            ClientLevel world = level instanceof ClientLevel ? (ClientLevel)level : Minecraft.getInstance().level;
            Registry blockRegistry = world.registryAccess().lookupOrThrow(Registries.BLOCK);
            int textureColor = this.blockUtils.getTextureColorUtils().getBlockTextureColor(state, true, world, (Registry<Block>)blockRegistry, new BlockPos(0, 70, 0));
            int alpha = textureColor >> 24 & 0xFF;
            biggestUpQuadIsTranslucent = alpha <= 240;
        }
        return biggestUpQuadIsTranslucent;
    }

    public boolean shouldConsiderFluidTranslucent(FluidState fluidState) {
        FluidStateModelSet fms = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
        FluidModel model = fms.get(fluidState);
        return model.layer().translucent();
    }

    public abstract boolean shouldConsiderBlockTranslucent(BlockAndTintGetter var1, BlockPos var2, BlockState var3);
}

