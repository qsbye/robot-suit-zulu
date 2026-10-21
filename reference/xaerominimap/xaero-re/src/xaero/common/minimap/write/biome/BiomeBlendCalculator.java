/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.write.biome;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class BiomeBlendCalculator
implements BlockAndTintGetter {
    private ClientLevel original;
    private int startI;
    private int endI;
    private int startJ;
    private int endJ;

    public void prepare(ClientLevel original, boolean biomeBlending) {
        this.original = original;
        this.endJ = 0;
        this.startJ = 0;
        this.endI = 0;
        this.startI = 0;
        if (biomeBlending) {
            this.startI = -1;
            this.endI = 1;
            this.startJ = -1;
            this.endJ = 1;
        }
    }

    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.original.getBlockEntity(blockPos);
    }

    public BlockState getBlockState(BlockPos blockPos) {
        return this.original.getBlockState(blockPos);
    }

    public FluidState getFluidState(BlockPos blockPos) {
        return this.original.getFluidState(blockPos);
    }

    public LevelLightEngine getLightEngine() {
        return this.original.getLightEngine();
    }

    public int getHeight() {
        return this.original.getHeight();
    }

    public int getMinY() {
        return this.original.getMinY();
    }

    public CardinalLighting cardinalLighting() {
        return this.original.cardinalLighting();
    }

    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        int redAccumulator = 0;
        int greenAccumulator = 0;
        int blueAccumulator = 0;
        ClientLevel original = this.original;
        int total = 0;
        for (int i = this.startI; i <= this.endI; ++i) {
            for (int j = this.startJ; j <= this.endJ; ++j) {
                Biome biome;
                if (i != 0 && j != 0) continue;
                mutableBlockPos.set(x + i, y, z + j);
                Holder biomeHolder = original.getBiome((BlockPos)mutableBlockPos);
                Biome biome2 = biome = biomeHolder == null ? null : (Biome)biomeHolder.value();
                if (biome == null) continue;
                int colorSample = colorResolver.getColor(biome, (double)mutableBlockPos.getX(), (double)mutableBlockPos.getZ());
                redAccumulator += colorSample & 0xFF0000;
                greenAccumulator += colorSample & 0xFF00;
                blueAccumulator += colorSample & 0xFF;
                ++total;
            }
        }
        int red = redAccumulator / total;
        int green = greenAccumulator / total;
        int blue = blueAccumulator / total;
        return 0xFF000000 | red & 0xFF0000 | green & 0xFF00 | blue;
    }
}

