/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xaero.common.IXaeroMinimap;

public class XaeroMinimapServerCore {
    public static IXaeroMinimap modMain;

    public static void onServerWorldInfo(Player player) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        modMain.getCommonEvents().onPlayerWorldJoin((ServerPlayer)player);
    }

    public static boolean isModLoaded() {
        return modMain != null && (modMain.isLoadedServer() || modMain.isLoadedClient());
    }
}

