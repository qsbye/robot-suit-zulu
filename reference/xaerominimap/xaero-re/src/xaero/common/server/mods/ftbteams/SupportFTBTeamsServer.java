/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.mods.ftbteams;

import xaero.common.server.mods.ftbteams.FTBTeamsSyncedPlayerTrackerSystem;
import xaero.common.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class SupportFTBTeamsServer {
    private final ISyncedPlayerTrackerSystem syncedPlayerTrackerSystem = new FTBTeamsSyncedPlayerTrackerSystem();

    public ISyncedPlayerTrackerSystem getSyncedPlayerTrackerSystem() {
        return this.syncedPlayerTrackerSystem;
    }
}

