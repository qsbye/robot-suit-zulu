/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.write;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xaero.common.IXaeroMinimap;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.write.MinimapWriter;
import xaero.hud.minimap.module.MinimapSession;

public class MinimapWriterFabric
extends MinimapWriter {
    public MinimapWriterFabric(IXaeroMinimap modMain, MinimapSession minimapSession, BlockStateShortShapeCache blockStateShortShapeCache, HighlighterRegistry highlighterRegistry) {
        super(modMain, minimapSession, blockStateShortShapeCache, highlighterRegistry);
    }

    @Override
    protected int getBlockStateLightEmission(BlockState state, Level world, BlockPos pos) {
        return state.getLightEmission();
    }
}

