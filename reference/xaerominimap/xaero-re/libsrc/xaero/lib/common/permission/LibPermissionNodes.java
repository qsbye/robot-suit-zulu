/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.chat.Component;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.PermissionRegistry;
import xaero.lib.common.permission.config.channel.ConfigChannelPermissionNode;

public class LibPermissionNodes {
    private static final Set<PermissionNode<?>> ALL = new HashSet();
    public static final PermissionNode<Boolean> EDIT_SERVER_PROFILES = ((ConfigChannelPermissionNode.Builder)((ConfigChannelPermissionNode.Builder)ConfigChannelPermissionNode.Builder.begin(Boolean.class).setModId("xaerolib")).setDisplayName((Component)Component.translatable((String)"gui.xaero_permission_edit_server_profiles"))).setActualPathOption(LibPrimaryCommonConfigOptions.EDIT_SERVER_PROFILES_PERMISSION).setConfigChannelSupplier(() -> XaeroLib.INSTANCE.getLibConfigChannel()).build((Set)ALL);

    public static void registerAll() {
        for (PermissionNode<?> node : ALL) {
            PermissionRegistry.INSTANCE.register(node);
        }
    }
}

