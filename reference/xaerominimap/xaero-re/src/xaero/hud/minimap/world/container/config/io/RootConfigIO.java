/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.container.config.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;
import xaero.hud.minimap.waypoint.WaypointsSort;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.path.XaeroPath;
import xaero.hud.path.XaeroPathReader;

public class RootConfigIO {
    private final HudMod modMain;

    public RootConfigIO(HudMod modMain) {
        this.modMain = modMain;
    }

    private File getFile(MinimapWorldRootContainer rootContainer) {
        Path directoryPath = rootContainer.getDirectoryPath();
        try {
            if (!Files.exists(directoryPath, new LinkOption[0])) {
                Files.createDirectories(directoryPath, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        return directoryPath.resolve("config.txt").toFile();
    }

    public void save(MinimapWorldRootContainer rootContainer) {
        File configFile = this.getFile(rootContainer);
        PrintWriter writer = null;
        RootConfig config = rootContainer.getConfig();
        try {
            writer = new PrintWriter(new FileWriter(configFile, StandardCharsets.UTF_8));
            writer.println("//waypoints config options");
            writer.println("usingMultiworldDetection:" + config.isUsingMultiworldDetection());
            writer.println("ignoreServerLevelId:" + config.isIgnoreServerLevelId());
            if (config.getDefaultMultiworldId() != null) {
                writer.println("defaultMultiworldId:" + config.getDefaultMultiworldId());
            }
            writer.println("teleportationEnabled:" + config.isTeleportationEnabled());
            writer.println("usingDefaultTeleportCommand:" + config.isUsingDefaultTeleportCommand());
            if (config.getServerTeleportCommandFormat() != null) {
                writer.println("serverTeleportCommandFormat:" + config.getServerTeleportCommandFormat().replace(":", "^col^"));
            }
            if (config.getServerTeleportCommandRotationFormat() != null) {
                writer.println("serverTeleportCommandRotationFormat:" + config.getServerTeleportCommandRotationFormat().replace(":", "^col^"));
            }
            writer.println("sortType:" + config.getSortType().name());
            writer.println("sortReversed:" + config.isSortReversed());
            writer.println("");
            writer.println("//other config options");
            writer.println("ignoreHeightmaps:" + config.isIgnoreHeightmaps());
            rootContainer.getSubWorldConnections().save(writer);
            writer.println("");
            writer.println("//dimension types (DO NOT EDIT)");
            for (Map.Entry<ResourceKey<Level>, Identifier> entry : rootContainer.getDimensionTypeIds()) {
                writer.println("dimensionType:" + entry.getKey().identifier().toString().replace(':', '$') + ":" + entry.getValue().toString().replace(':', '$'));
            }
            writer.println("//third-party waypoints");
            for (MinimapWorldContainer dimContainer : rootContainer.getSubContainers()) {
                ThirdPartyWaypointManager thirdPartyWaypointManager = dimContainer.getThirdPartyWaypointManager();
                if (thirdPartyWaypointManager == null) continue;
                for (ThirdPartyWaypoints thirdPartyWaypoints : thirdPartyWaypointManager.getAll()) {
                    for (Map.Entry<String, WaypointRenderInfo> entry : thirdPartyWaypoints.getRenderInfoOverrides().entrySet()) {
                        String waypointId = entry.getKey();
                        WaypointRenderInfo renderInfoOverride = entry.getValue();
                        writer.print("third-party-waypoint");
                        writer.print(":");
                        writer.print(dimContainer.getLastNode());
                        writer.print(":");
                        writer.print(thirdPartyWaypoints.getOriginId().getNamespace());
                        writer.print(":");
                        writer.print(thirdPartyWaypoints.getOriginId().getPath());
                        writer.print(":");
                        writer.print(waypointId.replace(":", "\u00a7\u00a7"));
                        writer.print(":");
                        if (renderInfoOverride.getDisabled() != null) {
                            writer.print(renderInfoOverride.getDisabled());
                        }
                        writer.print(":");
                        writer.print(renderInfoOverride.isThirdPartyDeleted());
                        writer.print(":");
                        if (renderInfoOverride.getInitials() != null) {
                            writer.print(renderInfoOverride.getInitialsSafe("\u00a7\u00a7"));
                        }
                        writer.print(":");
                        if (renderInfoOverride.getWaypointColor() != null) {
                            writer.print((Object)renderInfoOverride.getWaypointColor());
                        }
                        writer.print(":");
                        if (renderInfoOverride.getVisibility() != null) {
                            writer.print((Object)renderInfoOverride.getVisibility());
                        }
                        writer.println(":;");
                    }
                }
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        if (writer != null) {
            writer.close();
        }
    }

    public void load(MinimapWorldRootContainer rootContainer) {
        RootConfig config = rootContainer.getConfig();
        config.setLoaded(true);
        File configFile = this.getFile(rootContainer);
        if (!configFile.exists()) {
            this.save(rootContainer);
            return;
        }
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                String valueString;
                String[] args = line.split(":");
                String string = valueString = args.length < 2 ? "" : args[1];
                if (args[0].equals("usingMultiworldDetection")) {
                    config.setUsingMultiworldDetection(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("ignoreServerLevelId")) {
                    config.setIgnoreServerLevelId(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("defaultMultiworldId")) {
                    if (valueString.matches("[a-zA-Z,$0-9-]+")) {
                        config.setDefaultMultiworldId(valueString);
                        continue;
                    }
                    MinimapLogs.LOGGER.warn("Ignoring invalid defaultMultiworldId in {}", (Object)configFile);
                    continue;
                }
                if (args[0].equals("teleportationEnabled")) {
                    config.setTeleportationEnabled(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("usingDefaultTeleportCommand")) {
                    config.setUsingDefaultTeleportCommand(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("teleportCommand")) {
                    config.setServerTeleportCommandFormat("/" + valueString.replace("^col^", ":") + " {x} {y} {z}");
                    config.setServerTeleportCommandRotationFormat("/" + valueString.replace("^col^", ":") + " {x} {y} {z} {yaw} ~");
                    continue;
                }
                if (args[0].equals("serverTeleportCommand")) {
                    config.setServerTeleportCommandFormat(valueString.replace("^col^", ":") + " {x} {y} {z}");
                    config.setServerTeleportCommandRotationFormat(valueString.replace("^col^", ":") + " {x} {y} {z} {yaw} ~");
                    continue;
                }
                if (args[0].equals("serverTeleportCommandFormat")) {
                    config.setServerTeleportCommandFormat(valueString.replace("^col^", ":"));
                    continue;
                }
                if (args[0].equals("serverTeleportCommandRotationFormat")) {
                    config.setServerTeleportCommandRotationFormat(valueString.replace("^col^", ":"));
                    continue;
                }
                if (args[0].equals("sortType")) {
                    config.setSortType(WaypointsSort.valueOf(valueString));
                    continue;
                }
                if (args[0].equals("sortReversed")) {
                    config.setSortReversed(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("ignoreHeightmaps")) {
                    config.setIgnoreHeightmaps(valueString.equals("true"));
                    continue;
                }
                if (args[0].equals("connection")) {
                    XaeroPath worldKey1 = new XaeroPathReader().read(valueString);
                    if (args.length <= 2) continue;
                    XaeroPath worldKey2 = new XaeroPathReader().read(args[2]);
                    config.getSubWorldConnections().addConnection(worldKey1, worldKey2);
                    continue;
                }
                if (args[0].equals("dimensionType")) {
                    try {
                        rootContainer.setDimensionTypeId((ResourceKey<Level>)ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)Identifier.parse((String)args[1].replace('$', ':'))), Identifier.parse((String)args[2].replace('$', ':')));
                    }
                    catch (Throwable worldKey1) {}
                    continue;
                }
                if (!args[0].equals("third-party-waypoint") || args.length < 11) continue;
                String dimensionNode = args[1];
                String originNamespace = args[2];
                String originPath = args[3];
                Identifier originId = Identifier.fromNamespaceAndPath((String)originNamespace, (String)originPath);
                MinimapWorldContainer dimContainer = rootContainer.addSubContainer(rootContainer.getPath().resolve(dimensionNode));
                String id = Waypoint.getStringFromStringSafe(args[4], "\u00a7\u00a7");
                WaypointRenderInfo defaultRenderInfoOverride = new WaypointRenderInfo();
                defaultRenderInfoOverride.setDisabled(args[5].isEmpty() ? null : Boolean.valueOf(args[5].equals("true")));
                defaultRenderInfoOverride.setThirdPartyDeleted(args[6].equals("true"));
                defaultRenderInfoOverride.setInitials(args[7].isEmpty() ? null : Waypoint.getStringFromStringSafe(args[7], "\u00a7\u00a7"));
                defaultRenderInfoOverride.setWaypointColor(args[8].isEmpty() ? null : WaypointColor.valueOf(args[8]));
                defaultRenderInfoOverride.setVisibility(args[9].isEmpty() ? null : WaypointVisibilityType.valueOf(args[9]));
                dimContainer.getThirdPartyWaypointManager().get(originId).addRenderInfoOverride(id, defaultRenderInfoOverride);
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
    }
}

