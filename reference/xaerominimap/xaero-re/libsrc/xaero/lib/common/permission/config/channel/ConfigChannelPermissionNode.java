/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission.config.channel;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.permission.PermissionNode;

public class ConfigChannelPermissionNode<T>
extends PermissionNode<T> {
    private final ConfigOption<String> actualPathOption;
    private final Supplier<ConfigChannel> configChannelSupplier;

    public ConfigChannelPermissionNode(String modId, Class<T> type, ConfigOption<String> actualPathOption, Supplier<ConfigChannel> configChannelSupplier, String defaultPath, Supplier<String> actualPathSupplier, Component displayName, Component comment) {
        super(modId, type, defaultPath, actualPathSupplier, displayName, comment);
        this.actualPathOption = actualPathOption;
        this.configChannelSupplier = configChannelSupplier;
    }

    public ConfigChannel getConfigChannel() {
        return this.configChannelSupplier.get();
    }

    public ConfigOption<String> getActualPathOption() {
        return this.actualPathOption;
    }

    public static final class Builder<T>
    extends PermissionNode.Builder<T, Builder<T>> {
        private ConfigOption<String> actualPathOption;
        private Supplier<ConfigChannel> configChannelSupplier;

        private Builder(Class<T> type) {
            super(type);
        }

        @Override
        public Builder<T> setDefault() {
            super.setDefault();
            this.setActualPathOption(null);
            this.setConfigChannelSupplier(null);
            return (Builder)this.self;
        }

        public Builder<T> setActualPathOption(ConfigOption<String> actualPathOption) {
            this.actualPathOption = actualPathOption;
            return (Builder)this.self;
        }

        public Builder<T> setConfigChannelSupplier(Supplier<ConfigChannel> configChannelSupplier) {
            this.configChannelSupplier = configChannelSupplier;
            return (Builder)this.self;
        }

        @Override
        public ConfigChannelPermissionNode<T> build(Set<PermissionNode<?>> destination) {
            if (this.actualPathOption == null || this.configChannelSupplier == null) {
                throw new IllegalStateException();
            }
            this.setDefaultPath(this.actualPathOption.getDefaultValue());
            Supplier<ConfigChannel> finalConfigChannelSupplier = this.configChannelSupplier;
            ConfigOption<String> finalActualPathOption = this.actualPathOption;
            this.setActualPathSupplier(() -> (String)((ConfigChannel)finalConfigChannelSupplier.get()).getPrimaryCommonConfigManager().getEffective(finalActualPathOption));
            return (ConfigChannelPermissionNode)super.build(destination);
        }

        @Override
        protected ConfigChannelPermissionNode<T> buildInternally() {
            return new ConfigChannelPermissionNode(this.modId, this.type, this.actualPathOption, this.configChannelSupplier, this.defaultPath, this.actualPathSupplier, this.displayName, this.comment);
        }

        public static <T> Builder<T> begin(Class<T> type) {
            return new Builder<T>(type).setDefault();
        }
    }
}

