/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.minimap.mcworld.IXaeroMinimapClientWorld;
import xaero.common.minimap.mcworld.MinimapClientWorldData;

@Mixin(value={ClientLevel.class})
public class MixinClientWorld
implements IXaeroMinimapClientWorld {
    private MinimapClientWorldData xaero_minimapData;

    @Override
    public MinimapClientWorldData getXaero_minimapData() {
        return this.xaero_minimapData;
    }

    @Override
    public void setXaero_minimapData(MinimapClientWorldData xaero_minimapData) {
        this.xaero_minimapData = xaero_minimapData;
    }
}

