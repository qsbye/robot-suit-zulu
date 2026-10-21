/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.highlight.AbstractHighlighter;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;

public class WorldMapHighlighter
extends AbstractHighlighter {
    private final xaero.map.highlight.AbstractHighlighter highlighter;
    private List<Component> tooltips;

    public WorldMapHighlighter(xaero.map.highlight.AbstractHighlighter highlighter) {
        super(highlighter.isCoveringOutsideDiscovered());
        this.highlighter = highlighter;
        this.tooltips = new ArrayList<Component>();
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return this.highlighter.regionHasHighlights(dimension, regionX, regionZ);
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return this.highlighter.chunkIsHighlit(dimension, chunkX, chunkZ);
    }

    @Override
    public int[] getChunkHighlitColor(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return this.highlighter.getChunkHighlitColor(dimension, chunkX, chunkZ);
    }

    @Override
    public void addBlockHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int blockX, int blockZ, int width) {
        this.tooltips.clear();
        this.highlighter.addMinimapBlockHighlightTooltips(this.tooltips, dimension, blockX, blockZ, width);
        for (Component tooltipLine : this.tooltips) {
            compiler.addLine(tooltipLine);
        }
    }

    @Override
    public boolean isCoveringOutsideDiscovered() {
        return this.highlighter.isCoveringOutsideDiscovered();
    }
}

