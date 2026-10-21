/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

public class EntityServerUtils {
    public static MinecraftServer getServer(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.level() == null) {
            return null;
        }
        return entity.level().getServer();
    }
}

