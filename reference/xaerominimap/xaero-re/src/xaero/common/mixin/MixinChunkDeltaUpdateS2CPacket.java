/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IXaeroMinimapSMultiBlockChangePacket;

@Mixin(value={ClientboundSectionBlocksUpdatePacket.class})
public class MixinChunkDeltaUpdateS2CPacket
implements IXaeroMinimapSMultiBlockChangePacket {
    @Shadow
    SectionPos sectionPos;

    @Override
    public SectionPos xaero_mm_getSectionPos() {
        return this.sectionPos;
    }
}

