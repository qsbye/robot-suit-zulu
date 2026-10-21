/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={LevelChunk.class})
public class MixinWorldChunk {
    public boolean xaero_chunkClean = false;
}

