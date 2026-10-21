/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.cache.placeholder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PlaceholderBlockGetter
implements BlockGetter {
    private BlockState placeholderState;

    public void setPlaceholderState(BlockState placeholderState) {
        this.placeholderState = placeholderState;
    }

    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    public BlockState getBlockState(BlockPos blockPos) {
        return this.placeholderState;
    }

    public FluidState getFluidState(BlockPos blockPos) {
        return this.placeholderState == null ? null : this.placeholderState.getFluidState();
    }

    public int getHeight() {
        return 16;
    }

    public int getMinY() {
        return 0;
    }
}

