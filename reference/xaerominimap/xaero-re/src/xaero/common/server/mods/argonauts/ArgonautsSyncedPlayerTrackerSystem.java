/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.mods.argonauts;

import earth.terrarium.argonauts.api.guild.Guild;
import earth.terrarium.argonauts.api.guild.GuildApi;
import earth.terrarium.argonauts.api.party.Party;
import earth.terrarium.argonauts.api.party.PartyApi;
import net.minecraft.world.entity.player.Player;
import xaero.common.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class ArgonautsSyncedPlayerTrackerSystem
implements ISyncedPlayerTrackerSystem {
    @Override
    public int getTrackingLevel(Player tracker, Player tracked) {
        int partyTrackingLevel = this.getPartyTrackingLevel(tracker, tracked);
        int guildTrackingLevel = this.getGuildTrackingLevel(tracker, tracked);
        return Math.max(partyTrackingLevel, guildTrackingLevel);
    }

    @Override
    public boolean isPartySystem() {
        return true;
    }

    private int getPartyTrackingLevel(Player tracker, Player tracked) {
        Party trackerParty = PartyApi.API.get(tracker);
        if (trackerParty == null) {
            return 0;
        }
        Party trackedParty = PartyApi.API.get(tracked);
        if (trackerParty == trackedParty) {
            return 2;
        }
        return 0;
    }

    private int getGuildTrackingLevel(Player tracker, Player tracked) {
        Guild trackerGuild = GuildApi.API.getPlayerGuild(tracker.level().getServer(), tracker.getUUID());
        if (trackerGuild == null) {
            return 0;
        }
        Guild trackedGuild = GuildApi.API.getPlayerGuild(tracked.level().getServer(), tracked.getUUID());
        if (trackerGuild == trackedGuild) {
            return 2;
        }
        return 0;
    }
}

