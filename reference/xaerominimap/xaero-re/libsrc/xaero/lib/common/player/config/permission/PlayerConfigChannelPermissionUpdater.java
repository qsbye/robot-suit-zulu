/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.player.config.permission;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.ConfigChannelRegistry;
import xaero.lib.common.permission.LibPermissionNodes;
import xaero.lib.common.permission.system.PermissionSystemRegistry;
import xaero.lib.common.player.ServerPlayerData;
import xaero.lib.common.player.config.ServerPlayerConfigChannelData;

public class PlayerConfigChannelPermissionUpdater {
    public void handle(ServerPlayer player, boolean resync) {
        ServerPlayerData playerData = ServerPlayerData.get(player);
        boolean isOpped = Commands.LEVEL_GAMEMASTERS.check(playerData.getPlayer().permissions());
        PermissionSystemRegistry permissions = PermissionSystemRegistry.INSTANCE;
        boolean hasServerProfileEditPerm = isOpped || permissions.getBoolean(LibPermissionNodes.EDIT_SERVER_PROFILES, player);
        for (ConfigChannel configChannel : ConfigChannelRegistry.INSTANCE) {
            String enforcedProfilePermission = permissions.get(configChannel.getBuiltInPermissions().getEnforcedServerProfileNode(), player);
            if (enforcedProfilePermission != null && configChannel.getServerConfigManager().getProfileManager().get(enforcedProfilePermission) == null) {
                XaeroLib.LOGGER.error("Player {} has an invalid (permission-based) enforced profile ID: {}. Using default.", (Object)player.getGameProfile().name(), (Object)enforcedProfilePermission);
                enforcedProfilePermission = null;
            }
            ServerPlayerConfigChannelData configChannelData = playerData.getConfigChannelData(configChannel);
            configChannelData.setCachedUsingConfigProfilePermission(enforcedProfilePermission != null);
            configChannelData.setCachedHasServerProfileEditPermission(hasServerProfileEditPerm);
            configChannelData.setCachedEnforcedConfigProfilePermission(enforcedProfilePermission);
            if (!resync) continue;
            configChannel.getServerConfigSynchronizer().syncServerProfileEditingInfo(playerData.getConfigSynchronizer());
        }
    }
}

