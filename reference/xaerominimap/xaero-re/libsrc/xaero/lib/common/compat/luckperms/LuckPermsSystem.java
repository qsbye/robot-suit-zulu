/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.compat.luckperms;

import java.util.SortedSet;
import javax.annotation.Nullable;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.system.IPermissionSystem;
import xaero.lib.common.permission.util.PermissionUtils;

public class LuckPermsSystem
implements IPermissionSystem {
    @Nullable
    private User getUser(ServerPlayer player) {
        ServerLevel level = player.level();
        if (level == null) {
            return null;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return null;
        }
        ServerPlayer actualPlayer = server.getPlayerList().getPlayer(player.getUUID());
        if (actualPlayer == null || actualPlayer.getClass() != ServerPlayer.class) {
            return null;
        }
        PlayerAdapter playerAdapter = LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class);
        return playerAdapter.getUser((Object)actualPlayer);
    }

    @Override
    public boolean getBoolean(PermissionNode<Boolean> node, ServerPlayer player) {
        User user = this.getUser(player);
        if (user == null) {
            return false;
        }
        SortedSet nodes = user.resolveDistinctInheritedNodes(QueryOptions.defaultContextualOptions());
        String actualNodePath = node.getActualPath();
        for (Node nodeObject : nodes) {
            if (!nodeObject.getKey().equals(actualNodePath)) continue;
            return nodeObject.getValue();
        }
        return false;
    }

    @Override
    public boolean getBooleanFast(PermissionNode<Boolean> node, ServerPlayer player) {
        User user = this.getUser(player);
        if (user == null) {
            return false;
        }
        return user.getCachedData().getPermissionData().checkPermission(node.getActualPath()).asBoolean();
    }

    @Override
    public <T> T get(PermissionNode<T> node, ServerPlayer player) {
        User user = this.getUser(player);
        if (user == null) {
            return null;
        }
        SortedSet nodes = user.resolveDistinctInheritedNodes(QueryOptions.defaultContextualOptions());
        String actualNodePath = node.getActualPath();
        for (Node nodeObject : nodes) {
            if (nodeObject.getType() != NodeType.META || !nodeObject.getKey().equals(actualNodePath)) continue;
            MetaNode metaNode = (MetaNode)nodeObject;
            String valueString = metaNode.getMetaValue();
            if (valueString == null) {
                return null;
            }
            return PermissionUtils.parseString(node, valueString);
        }
        return null;
    }

    @Override
    public <T> T getFast(PermissionNode<T> node, ServerPlayer player) {
        User user = this.getUser(player);
        if (user == null) {
            return null;
        }
        String valueString = user.getCachedData().getMetaData().getMetaValue(node.getActualPath());
        if (valueString == null) {
            return null;
        }
        return PermissionUtils.parseString(node, valueString);
    }

    @Override
    public void handleModPresence(String modId) {
    }
}

