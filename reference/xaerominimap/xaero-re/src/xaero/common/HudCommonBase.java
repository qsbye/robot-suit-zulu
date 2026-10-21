/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import xaero.common.HudMod;
import xaero.common.server.player.ServerPlayerTickHandler;
import xaero.hud.packet.MinimapPacketRegister;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;

public class HudCommonBase {
    public void setup(HudMod modMain) {
        modMain.getPlatformContext().getLoaderCommon().setup(modMain);
        new MinimapPacketRegister().register(modMain.getMessageHandler());
        modMain.setServerPlayerTickHandler(new ServerPlayerTickHandler());
        modMain.getSupportServerMods().check(modMain);
    }

    public void preLoadLater(HudMod modMain) {
        if (modMain.getCommonConfigIO().shouldEnableEveryoneTracksEveryone()) {
            XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)LibPrimaryCommonConfigOptions.EVERYONE_TRACKS_EVERYONE, (Object)true);
        }
    }
}

