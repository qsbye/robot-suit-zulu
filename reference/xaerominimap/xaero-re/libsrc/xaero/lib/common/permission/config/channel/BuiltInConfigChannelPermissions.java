/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission.config.channel;

import java.util.Set;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.type.BuiltInConfigValueTypes;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.PermissionRegistry;
import xaero.lib.common.permission.config.channel.ConfigChannelPermissionNode;

public class BuiltInConfigChannelPermissions {
    private final ConfigChannel channel;
    private final ConfigChannelPermissionNode<String> enforcedServerProfileNode;

    private BuiltInConfigChannelPermissions(ConfigChannel channel, ConfigChannelPermissionNode<String> enforcedServerProfileNode) {
        this.channel = channel;
        this.enforcedServerProfileNode = enforcedServerProfileNode;
    }

    public PermissionNode<String> getEnforcedServerProfileNode() {
        return this.enforcedServerProfileNode;
    }

    public void register() {
        ConfigOptionManager options = this.channel.getPrimaryCommonConfigOptionManager();
        options.register(this.enforcedServerProfileNode.getActualPathOption());
        PermissionRegistry.INSTANCE.register(this.enforcedServerProfileNode);
    }

    public static final class Builder {
        private ConfigChannel channel;
        private String defaultEnforcedServerProfileNodePath;

        private Builder() {
        }

        public Builder setDefault() {
            this.setChannel(null);
            this.setDefaultEnforcedServerProfileNodePath(null);
            return this;
        }

        public Builder setChannel(ConfigChannel channel) {
            this.channel = channel;
            return this;
        }

        public Builder setDefaultEnforcedServerProfileNodePath(String defaultEnforcedServerProfileNodePath) {
            this.defaultEnforcedServerProfileNodePath = defaultEnforcedServerProfileNodePath;
            return this;
        }

        public BuiltInConfigChannelPermissions build() {
            if (this.channel == null || this.defaultEnforcedServerProfileNodePath == null) {
                throw new IllegalStateException();
            }
            ConfigChannel finalChannel = this.channel;
            PermissionNode enforcedServerProfileNode = ((ConfigChannelPermissionNode.Builder)((ConfigChannelPermissionNode.Builder)((ConfigChannelPermissionNode.Builder)ConfigChannelPermissionNode.Builder.begin(String.class).setModId(this.channel.getId().getNamespace())).setConfigChannelSupplier(() -> finalChannel).setDisplayName((Component)Component.translatable((String)"gui.xaero_permission_enforced_server_profile"))).setComment((Component)Component.translatable((String)"gui.xaero_permission_enforced_server_profile_tooltip"))).setActualPathOption(((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("enforced_profile_permission_node")).setDefaultValue(this.defaultEnforcedServerProfileNodePath)).setValueType(BuiltInConfigValueTypes.getString(500))).build(null)).build((Set)null);
            return new BuiltInConfigChannelPermissions(finalChannel, (ConfigChannelPermissionNode<String>)enforcedServerProfileNode);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

