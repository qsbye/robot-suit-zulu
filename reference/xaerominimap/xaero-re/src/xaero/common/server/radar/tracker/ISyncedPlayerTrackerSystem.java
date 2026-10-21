/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.radar.tracker;

import net.minecraft.world.entity.player.Player;

public interface ISyncedPlayerTrackerSystem {
    public int getTrackingLevel(Player var1, Player var2);

    public boolean isPartySystem();
}

