/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.cache;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xaero.common.IXaeroMinimap;
import xaero.common.cache.placeholder.PlaceholderBlockGetter;
import xaero.hud.minimap.MinimapLogs;

public class BlockStateShortShapeCache {
    private IXaeroMinimap modMain;
    private Map<BlockState, Boolean> shortBlockStates;
    private BlockState lastShortChecked = null;
    private boolean lastShortCheckedResult = false;
    private PlaceholderBlockGetter placeholderBlockGetter;

    public BlockStateShortShapeCache(IXaeroMinimap modMain) {
        this.modMain = modMain;
        this.shortBlockStates = new HashMap<BlockState, Boolean>();
        this.placeholderBlockGetter = new PlaceholderBlockGetter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isShort(BlockState state) {
        Boolean cached;
        if (state == null || state.getBlock() instanceof AirBlock || state.getBlock() instanceof LiquidBlock) {
            return false;
        }
        Map<BlockState, Boolean> map = this.shortBlockStates;
        synchronized (map) {
            if (state == this.lastShortChecked) {
                return this.lastShortCheckedResult;
            }
            cached = this.shortBlockStates.get(state);
        }
        if (cached == null) {
            if (!Minecraft.getInstance().isSameThread()) {
                return (Boolean)Minecraft.getInstance().submit(() -> this.isShort(state)).join();
            }
            try {
                this.placeholderBlockGetter.setPlaceholderState(state);
                VoxelShape shape = state.getShape((BlockGetter)this.placeholderBlockGetter, BlockPos.ZERO);
                cached = shape.max(Direction.Axis.Y) < 0.25;
            }
            catch (Throwable t) {
                MinimapLogs.LOGGER.info("Defaulting world-dependent block state shape to not short: " + String.valueOf(state));
                cached = false;
            }
            map = this.shortBlockStates;
            synchronized (map) {
                this.shortBlockStates.put(state, cached);
                this.lastShortChecked = state;
                this.lastShortCheckedResult = cached;
            }
        }
        return cached;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Map<BlockState, Boolean> map = this.shortBlockStates;
        synchronized (map) {
            this.shortBlockStates.clear();
            this.lastShortChecked = null;
            this.lastShortCheckedResult = false;
        }
    }
}

