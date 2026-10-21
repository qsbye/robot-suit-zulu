/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import xaero.lib.client.level.ClientLevelData;
import xaero.lib.client.level.IClientLevel;

@Mixin(value={ClientLevel.class})
public class MixinClientLevel
implements IClientLevel {
    private ClientLevelData xaerolib_data;

    @Override
    public ClientLevelData xaerolib_getData() {
        return this.xaerolib_data;
    }

    @Override
    public void xaerolib_setData(ClientLevelData data) {
        this.xaerolib_data = data;
    }
}

