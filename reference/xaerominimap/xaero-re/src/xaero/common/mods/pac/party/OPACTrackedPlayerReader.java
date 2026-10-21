/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods.pac.party;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.hud.minimap.player.tracker.system.ITrackedPlayerReader;
import xaero.pac.common.parties.party.api.IPartyMemberDynamicInfoSyncableAPI;

public class OPACTrackedPlayerReader
implements ITrackedPlayerReader<IPartyMemberDynamicInfoSyncableAPI> {
    private final Map<Identifier, ResourceKey<Level>> dimensionKeyCache = new HashMap<Identifier, ResourceKey<Level>>();

    @Override
    public UUID getId(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getPlayerId();
    }

    @Override
    public double getX(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getX();
    }

    @Override
    public double getY(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getY();
    }

    @Override
    public double getZ(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getZ();
    }

    @Override
    public ResourceKey<Level> getDimension(IPartyMemberDynamicInfoSyncableAPI player) {
        if (player.getDimension() == null) {
            return null;
        }
        ResourceKey result = this.dimensionKeyCache.get(player.getDimension());
        if (result == null) {
            result = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)player.getDimension());
            this.dimensionKeyCache.put(player.getDimension(), (ResourceKey<Level>)result);
        }
        return result;
    }
}

