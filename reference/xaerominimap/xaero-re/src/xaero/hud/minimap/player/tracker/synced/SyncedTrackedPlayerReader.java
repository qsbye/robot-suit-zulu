/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker.synced;

import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.server.radar.tracker.SyncedTrackedPlayer;
import xaero.hud.minimap.player.tracker.system.ITrackedPlayerReader;

public class SyncedTrackedPlayerReader
implements ITrackedPlayerReader<SyncedTrackedPlayer> {
    @Override
    public UUID getId(SyncedTrackedPlayer player) {
        return player.getId();
    }

    @Override
    public double getX(SyncedTrackedPlayer player) {
        return player.getX();
    }

    @Override
    public double getY(SyncedTrackedPlayer player) {
        return player.getY();
    }

    @Override
    public double getZ(SyncedTrackedPlayer player) {
        return player.getZ();
    }

    @Override
    public ResourceKey<Level> getDimension(SyncedTrackedPlayer player) {
        return player.getDimensionKey();
    }
}

