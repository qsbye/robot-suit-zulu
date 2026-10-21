/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.highlight;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;

public class TestHighlighter
extends ChunkHighlighter {
    public TestHighlighter() {
        super(true);
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return true;
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!this.chunkIsHighlit(dimension, chunkX, chunkZ)) {
            return null;
        }
        int centerColor = -11184777;
        int sideColor = -11184692;
        this.resultStore[0] = centerColor;
        this.resultStore[1] = (chunkZ & 3) == 0 ? sideColor : centerColor;
        this.resultStore[2] = (chunkX & 3) == 3 ? sideColor : centerColor;
        this.resultStore[3] = (chunkZ & 3) == 3 ? sideColor : centerColor;
        this.resultStore[4] = (chunkX & 3) == 0 ? sideColor : centerColor;
        return this.resultStore;
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return (chunkX >> 2 & 1) == (chunkZ >> 2 & 1);
    }

    @Override
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int chunkX, int chunkZ, int width) {
        compiler.addLine((Component)Component.literal((String)"subtle!"));
    }
}

