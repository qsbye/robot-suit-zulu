/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.state;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import xaero.common.HudMod;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.minimap.world.state.MinimapWorldState;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.lib.common.util.IOUtils;

public class MinimapWorldStateUpdater {
    public static final int ROOT_CONTAINER_FORMAT = 4;
    private final HudMod modMain;
    private final MinimapSession session;
    private final ClientPacketListener connection;
    private BlockPos currentWorldSpawn;

    public MinimapWorldStateUpdater(HudMod modMain, MinimapSession session, ClientPacketListener connection) {
        this.modMain = modMain;
        this.session = session;
        this.connection = connection;
    }

    public void init() {
        this.session.getWorldState().setAutoRootContainerPath(this.getAutoRootContainerPath(4));
        for (int i = 0; i < 4; ++i) {
            this.session.getWorldState().setOutdatedAutoRootContainerPath(i, this.getAutoRootContainerPath(i));
        }
    }

    public void update() {
        MinimapWorldState state = this.session.getWorldState();
        XaeroPath oldAutoWorldPath = state.getAutoWorldPath();
        XaeroPath potentialAutoContainerPath = this.getPotentialContainerPath();
        state.setAutoContainerPathIgnoreCaseCache(potentialAutoContainerPath);
        boolean worldmap = this.modMain.getSupportMods().worldmap();
        String potentialAutoWorldNode = this.getPotentialWorldNode((ResourceKey<Level>)this.session.getMc().level.dimension(), worldmap);
        if (potentialAutoWorldNode == null) {
            return;
        }
        XaeroPath autoWorldPath = potentialAutoContainerPath.resolve(potentialAutoWorldNode);
        state.setAutoWorldPath(autoWorldPath);
        if (oldAutoWorldPath != null && potentialAutoContainerPath.equals(oldAutoWorldPath.getParent())) {
            return;
        }
        MinimapWorldRootContainer autoRootContainer = this.session.getWorldManager().getAutoRootContainer();
        autoRootContainer.renameOldContainer(potentialAutoContainerPath);
        autoRootContainer.updateDimensionType(this.session.getMc().level);
        if (oldAutoWorldPath == null) {
            return;
        }
        MinimapWorldContainer oldContainer = this.session.getWorldManager().getWorldContainer(oldAutoWorldPath.getParent());
        oldContainer.getServerWaypointManager().clear();
    }

    public XaeroPath getPotentialContainerPath() {
        String dimensionNode = this.session.getDimensionHelper().getDimensionDirectoryName((ResourceKey<Level>)this.session.getMc().level.dimension());
        XaeroPath potentialContainerPath = this.session.getWorldState().getAutoRootContainerPath().resolve(dimensionNode);
        return this.ignoreContainerCase(potentialContainerPath, this.session.getWorldState().getAutoContainerPathIgnoreCaseCache());
    }

    public XaeroPath ignoreContainerCase(XaeroPath potentialContainerPath, XaeroPath currentPath) {
        if (potentialContainerPath.equals(currentPath)) {
            return currentPath;
        }
        for (MinimapWorldRootContainer rootContainer : this.session.getWorldManager().getRootContainers()) {
            XaeroPath containerSearch = rootContainer.fixPathCharacterCases(potentialContainerPath);
            if (containerSearch == null) continue;
            return containerSearch;
        }
        return potentialContainerPath;
    }

    public XaeroPath getAutoRootContainerPath(int version) {
        Object potentialContainerID;
        ServerData serverData = this.connection.getServerData();
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null) {
            potentialContainerID = MinimapWorldContainerUtil.convertWorldFolderToContainerNode(mc.getSingleplayerServer().getWorldPath(LevelResource.ROOT).getParent().getFileName().toString(), version);
        } else if (serverData != null && serverData.isRealm() && this.modMain.getEvents().latestRealm != null) {
            potentialContainerID = "Realms_" + String.valueOf(this.modMain.getEvents().latestRealm.ownerUUID) + "." + this.modMain.getEvents().latestRealm.id;
        } else if (serverData != null) {
            ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
            SingleConfigManager primaryConfigManager = configManager.getPrimaryConfigManager();
            boolean differentiateByServerAddress = (Boolean)primaryConfigManager.getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.DIFFERENTIATE_BY_SERVER_ADDRESS);
            String serverIP = differentiateByServerAddress ? serverData.ip : "Any Address";
            int portDivider = version >= 1 && serverIP.indexOf(":") != serverIP.lastIndexOf(":") ? serverIP.lastIndexOf("]:") + 1 : serverIP.indexOf(":");
            if (portDivider > 0) {
                serverIP = serverIP.substring(0, portDivider);
            }
            while (serverIP.endsWith(".")) {
                serverIP = serverIP.substring(0, serverIP.length() - 1);
            }
            if (version >= 2) {
                serverIP = serverIP.replace("[", "").replace("]", "");
            }
            serverIP = serverIP.replace(":", version < 3 ? "\u00a7" : ".").replace("_", "%us%").replace("/", "%fs%").replace("\\", "%bs%");
            if (version >= 4) {
                serverIP = serverIP.trim();
                serverIP = IOUtils.replaceTrailingDots((String)serverIP, (char)',');
            }
            if (serverIP.isEmpty()) {
                serverIP = "Empty Address";
            }
            potentialContainerID = "Multiplayer_" + serverIP;
        } else {
            potentialContainerID = "Unknown";
        }
        XaeroPath potentialContainerPath = XaeroPath.root((String)potentialContainerID);
        potentialContainerPath = this.ignoreContainerCase(potentialContainerPath, null);
        return potentialContainerPath;
    }

    public String getPotentialWorldNode(ResourceKey<Level> dimId, boolean useWorldmap) {
        Object actualWorldNode;
        String worldmapWorldNode;
        if (this.session.getMc().getSingleplayerServer() != null) {
            return "waypoints";
        }
        MinimapWorldState state = this.session.getWorldState();
        MinimapWorldRootContainer rootContainer = this.session.getWorldManager().getRootWorldContainer(state.getAutoRootContainerPath());
        Object autoNodeBase = this.getAutoWorldNodeBase(rootContainer);
        if (autoNodeBase == null) {
            return null;
        }
        String string = worldmapWorldNode = useWorldmap ? this.modMain.getSupportMods().worldmapSupport.tryToGetMultiworldId(dimId) : null;
        if (useWorldmap && worldmapWorldNode == null) {
            return null;
        }
        if (autoNodeBase instanceof BlockPos) {
            BlockPos pos = (BlockPos)autoNodeBase;
            actualWorldNode = "mw" + (pos.getX() >> 6) + "," + (pos.getY() >> 6) + "," + (pos.getZ() >> 6);
            RootConfig config = rootContainer.getConfig();
            if (!config.isUsingMultiworldDetection()) {
                String defaultMultiworldId = config.getDefaultMultiworldId();
                if (defaultMultiworldId == null) {
                    config.setDefaultMultiworldId((String)actualWorldNode);
                    this.session.getWorldManagerIO().getRootConfigIO().save(rootContainer);
                } else {
                    actualWorldNode = defaultMultiworldId;
                }
            }
        } else {
            actualWorldNode = "mw$" + String.valueOf(autoNodeBase);
        }
        if (useWorldmap && worldmapWorldNode != "minimap") {
            actualWorldNode = worldmapWorldNode;
        }
        return actualWorldNode;
    }

    public boolean hasServerLevelId(MinimapWorldRootContainer rootContainer) {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        return worldData.serverLevelId != null && !rootContainer.getConfig().isIgnoreServerLevelId();
    }

    public Object getAutoWorldNodeBase(MinimapWorldRootContainer rootContainer) {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        return this.hasServerLevelId(rootContainer) ? worldData.serverLevelId : this.currentWorldSpawn;
    }

    public void onServerLevelId(int id) {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        worldData.serverLevelId = id;
        MinimapLogs.LOGGER.info("Minimap updated server level id: " + id + " for world " + String.valueOf(Minecraft.getInstance().level.dimension()));
    }

    public void setCurrentWorldSpawn(BlockPos currentWorldSpawn) {
        this.currentWorldSpawn = currentWorldSpawn;
    }
}

