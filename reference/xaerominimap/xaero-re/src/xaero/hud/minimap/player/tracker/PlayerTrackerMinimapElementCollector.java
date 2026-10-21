/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.system.IRenderedPlayerTracker;
import xaero.hud.minimap.player.tracker.system.ITrackedPlayerReader;
import xaero.hud.minimap.player.tracker.system.RenderedPlayerTrackerManager;

public class PlayerTrackerMinimapElementCollector {
    private Map<UUID, PlayerTrackerMinimapElement<?>> elements = new HashMap();
    private final RenderedPlayerTrackerManager systemManager;

    public PlayerTrackerMinimapElementCollector(RenderedPlayerTrackerManager systemManager) {
        this.systemManager = systemManager;
    }

    public void update(Minecraft mc) {
        if (this.elements == null) {
            this.elements = new HashMap();
        }
        HashMap updatedMap = new HashMap();
        boolean hasNewPlayer = false;
        for (IRenderedPlayerTracker<?> system : this.systemManager.getAllSystems()) {
            hasNewPlayer = this.updateForSystem(system, updatedMap, this.elements) || hasNewPlayer;
        }
        if (hasNewPlayer || updatedMap.size() != this.elements.size()) {
            this.elements = updatedMap;
        }
    }

    private <P> boolean updateForSystem(IRenderedPlayerTracker<P> system, Map<UUID, PlayerTrackerMinimapElement<?>> destination, Map<UUID, PlayerTrackerMinimapElement<?>> current) {
        Iterator<P> playerIterator = system.getTrackedPlayerIterator();
        if (playerIterator == null) {
            return false;
        }
        ITrackedPlayerReader<P> reader = system.getReader();
        boolean hasNewPlayer = false;
        while (playerIterator.hasNext()) {
            P player = playerIterator.next();
            UUID playerId = reader.getId(player);
            PlayerTrackerMinimapElement<Object> element = current.get(playerId);
            if (destination.containsKey(playerId)) continue;
            if (element == null || element.getSystem() != system || element.getPlayer() != player) {
                element = new PlayerTrackerMinimapElement<P>(player, system);
                hasNewPlayer = true;
            }
            destination.put(element.getPlayerId(), element);
        }
        return hasNewPlayer;
    }

    public boolean playerExists(UUID id) {
        return this.elements != null && this.elements.containsKey(id);
    }

    public Iterable<PlayerTrackerMinimapElement<?>> getElements() {
        return this.elements.values();
    }

    public void resetRenderedOnRadarFlags() {
        for (PlayerTrackerMinimapElement<?> e : this.elements.values()) {
            e.setRenderedOnRadar(false);
        }
    }

    public void confirmPlayerRadarRender(Player p) {
        this.elements.get(p.getUUID()).setRenderedOnRadar(true);
    }
}

