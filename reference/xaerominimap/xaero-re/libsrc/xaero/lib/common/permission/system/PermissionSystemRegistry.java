/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.system.IPermissionSystem;

public class PermissionSystemRegistry
implements IPermissionSystem {
    public static final PermissionSystemRegistry INSTANCE = Builder.begin().build();
    private final List<IPermissionSystem> systems;
    private boolean frozen;
    private final Set<String> presentMods;

    private PermissionSystemRegistry(List<IPermissionSystem> systems, Set<String> presentMods) {
        this.systems = systems;
        this.presentMods = presentMods;
    }

    public synchronized void register(IPermissionSystem system) {
        if (this.frozen) {
            throw new IllegalStateException();
        }
        this.systems.add(system);
        for (String presentMod : this.presentMods) {
            system.handleModPresence(presentMod);
        }
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean getBoolean(PermissionNode<Boolean> node, ServerPlayer player) {
        for (IPermissionSystem system : this.systems) {
            if (!system.getBoolean(node, player)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean getBooleanFast(PermissionNode<Boolean> node, ServerPlayer player) {
        for (IPermissionSystem system : this.systems) {
            if (!system.getBooleanFast(node, player)) continue;
            return true;
        }
        return false;
    }

    @Override
    public <T> T get(PermissionNode<T> node, ServerPlayer player) {
        for (IPermissionSystem system : this.systems) {
            T value = system.get(node, player);
            if (value == null) continue;
            return value;
        }
        return null;
    }

    @Override
    public <T> T getFast(PermissionNode<T> node, ServerPlayer player) {
        for (IPermissionSystem system : this.systems) {
            T value = system.getFast(node, player);
            if (value == null) continue;
            return value;
        }
        return null;
    }

    @Override
    public void handleModPresence(String modId) {
        if (this.frozen) {
            throw new IllegalStateException();
        }
        this.presentMods.add(modId);
        for (IPermissionSystem system : this.systems) {
            system.handleModPresence(modId);
        }
    }

    private static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public PermissionSystemRegistry build() {
            return new PermissionSystemRegistry(new ArrayList<IPermissionSystem>(), new HashSet<String>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

