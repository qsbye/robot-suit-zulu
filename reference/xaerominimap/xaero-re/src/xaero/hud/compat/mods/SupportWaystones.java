/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.compat.mods;

import java.util.UUID;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.api.event.WaystoneRemoveReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;

public class SupportWaystones {
    public void registerClientEvents() {
        WaystonesListReceivedEvent.EVENT.register(event -> {
            if (!Minecraft.getInstance().isSameThread()) {
                return;
            }
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (session == null) {
                return;
            }
            MinimapWorldRootContainer rootContainer = session.getWorldManager().getAutoRootContainer();
            this.removeAllOfOrigin(event.waystoneType(), rootContainer);
            for (Waystone waystone : event.waystones()) {
                this.addWaypoint(waystone, rootContainer);
            }
        });
        WaystoneUpdatedEvent.EVENT.register(event -> {
            if (!Minecraft.getInstance().isSameThread()) {
                return;
            }
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (session == null) {
                return;
            }
            MinimapWorldRootContainer rootContainer = session.getWorldManager().getAutoRootContainer();
            this.addWaypoint(event.waystone(), rootContainer);
        });
        WaystoneRemoveReceivedEvent.EVENT.register(event -> {
            if (!Minecraft.getInstance().isSameThread()) {
                return;
            }
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (session == null) {
                return;
            }
            MinimapWorldRootContainer rootContainer = session.getWorldManager().getAutoRootContainer();
            Identifier originId = this.getDiscoveredOriginId(event.waystoneType());
            Identifier globalOriginId = this.getGlobalOriginId(event.waystoneType());
            this.removeWaypoint(event.waystoneId(), originId, globalOriginId, rootContainer);
        });
    }

    public void onWaystoneDeactivation(Waystone waystone) {
        if (!Minecraft.getInstance().isSameThread()) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null) {
            return;
        }
        MinimapWorldRootContainer rootContainer = session.getWorldManager().getAutoRootContainer();
        Identifier originId = this.getDiscoveredOriginId(waystone.getWaystoneKind());
        Identifier globalOriginId = this.getGlobalOriginId(waystone.getWaystoneKind());
        this.removeWaypoint(waystone.getWaystoneUid(), originId, globalOriginId, rootContainer);
    }

    private void addWaypoint(Waystone waystone, MinimapWorldRootContainer rootContainer) {
        boolean isTeam;
        Identifier waystoneType = waystone.getWaystoneKind();
        Identifier originId = this.getDiscoveredOriginId(waystoneType);
        Identifier globalOriginId = this.getGlobalOriginId(waystoneType);
        if (!waystone.isValid()) {
            this.removeWaypoint(waystone.getWaystoneUid(), originId, globalOriginId, rootContainer);
            return;
        }
        boolean activationType = waystone.getVisibility() == WaystoneVisibility.ACTIVATION;
        boolean isSharestone = WaystoneKinds.isSharestone((Identifier)waystone.getWaystoneKind());
        if (activationType && !isSharestone && !WaystonesAPI.isWaystoneActivated((Player)Minecraft.getInstance().player, (Waystone)waystone)) {
            this.removeWaypoint(waystone.getWaystoneUid(), originId, globalOriginId, rootContainer);
            return;
        }
        boolean isGlobal = waystone.getVisibility() == WaystoneVisibility.GLOBAL;
        boolean bl = isTeam = waystone.getVisibility() == WaystoneVisibility.TEAM;
        if (!(isSharestone || activationType || isGlobal || isTeam)) {
            return;
        }
        ResourceKey dimension = waystone.getDimension();
        String subContainerNode = rootContainer.getSession().getDimensionHelper().getDimensionDirectoryName((ResourceKey<Level>)dimension);
        MinimapWorldContainer dimensionContainer = rootContainer.addSubContainer(rootContainer.getPath().resolve(subContainerNode));
        ThirdPartyWaypoints waystoneWaypoints = this.getWithConfigCheck(dimensionContainer.getThirdPartyWaypointManager(), activationType ? originId : globalOriginId);
        String waystoneName = waystone.hasName() ? waystone.getName().getString() : I18n.get((String)"gui.xaero_default_waystone_name", (Object[])new Object[0]);
        String initial = waystoneName.isEmpty() ? "X" : ("" + waystoneName.charAt(0)).toUpperCase();
        WaypointColor waypointColor = null;
        if (isSharestone) {
            String waystoneTypePath = waystone.getWaystoneKind().getPath();
            String kindPrefix = waystoneTypePath.substring(0, waystoneTypePath.length() - 11);
            waypointColor = this.getSharestoneColor(kindPrefix);
        }
        if (waypointColor == null) {
            waypointColor = WaypointColor.fromIndex((int)((waystone.getWaystoneUid().getLeastSignificantBits() & Integer.MAX_VALUE) % (long)WaypointColor.values().length));
        }
        Waypoint waypoint = new Waypoint(waystone.getPos().getX(), waystone.getPos().getY(), waystone.getPos().getZ(), waystoneName, initial, waypointColor, WaypointPurpose.NORMAL);
        waystoneWaypoints.add(waystone.getWaystoneUid().toString(), waypoint);
    }

    private void removeAllOfOrigin(Identifier originId, MinimapWorldRootContainer rootContainer) {
        for (MinimapWorldContainer subContainer : rootContainer.getSubContainers()) {
            subContainer.getThirdPartyWaypointManager().clearOrigin(originId);
        }
    }

    private void removeWaypoint(UUID waystoneId, Identifier originId, Identifier globalOriginId, MinimapWorldRootContainer rootContainer) {
        for (MinimapWorldContainer subContainer : rootContainer.getSubContainers()) {
            this.getWithConfigCheck(subContainer.getThirdPartyWaypointManager(), originId).remove(waystoneId.toString());
        }
    }

    private void removeWaypointFromAllOrigins(UUID waystoneId, MinimapWorldRootContainer rootContainer) {
        for (MinimapWorldContainer subContainer : rootContainer.getSubContainers()) {
            for (ThirdPartyWaypoints thirdPartyWaypoints : subContainer.getThirdPartyWaypointManager().getAll()) {
                thirdPartyWaypoints.remove(waystoneId.toString());
            }
        }
    }

    private ThirdPartyWaypoints getWithConfigCheck(ThirdPartyWaypointManager manager, Identifier originId) {
        ThirdPartyWaypoints result = manager.get(originId);
        if (!result.hasEnabledStateGetter()) {
            if (WaystoneKinds.isSharestone((Identifier)originId)) {
                result.setEnabledStateGetter(MinimapConfigClientUtils::areOtherWaystonesEnabled);
            } else {
                result.setEnabledStateGetter(MinimapConfigClientUtils::areDiscoveredWaystonesEnabled);
            }
        }
        return result;
    }

    private Identifier getDiscoveredOriginId(Identifier waystoneType) {
        return waystoneType;
    }

    private Identifier getGlobalOriginId(Identifier waystoneType) {
        return waystoneType;
    }

    private WaypointColor getSharestoneColor(String kindPrefix) {
        if (kindPrefix.equals("copper")) {
            return WaypointColor.GOLD;
        }
        if (kindPrefix.equals("prismarine")) {
            return WaypointColor.LIGHT_BLUE;
        }
        if (kindPrefix.equals("gold")) {
            return WaypointColor.YELLOW;
        }
        if (kindPrefix.equals("redstone")) {
            return WaypointColor.RED;
        }
        if (kindPrefix.equals("diamond")) {
            return WaypointColor.AQUA;
        }
        if (kindPrefix.equals("amethyst")) {
            return WaypointColor.MAGENTA;
        }
        if (kindPrefix.equals("emerald")) {
            return WaypointColor.GREEN;
        }
        if (kindPrefix.equals("lapis")) {
            return WaypointColor.BLUE;
        }
        return null;
    }
}

