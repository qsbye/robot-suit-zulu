/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.mcworld;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import xaero.common.minimap.mcworld.IXaeroMinimapClientWorld;
import xaero.common.minimap.mcworld.MinimapClientWorldData;

public class MinimapClientWorldDataHelper {
    public static MinimapClientWorldData getCurrentWorldData() {
        return MinimapClientWorldDataHelper.getWorldData(Minecraft.getInstance().level);
    }

    public static MinimapClientWorldData getWorldData(ClientLevel clientWorld) {
        IXaeroMinimapClientWorld inter = (IXaeroMinimapClientWorld)clientWorld;
        MinimapClientWorldData minimapWorldData = inter.getXaero_minimapData();
        if (minimapWorldData == null) {
            minimapWorldData = new MinimapClientWorldData(clientWorld);
            inter.setXaero_minimapData(minimapWorldData);
        }
        return minimapWorldData;
    }
}

