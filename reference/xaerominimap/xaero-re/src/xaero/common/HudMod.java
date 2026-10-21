/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.common.HudClientOnlyBase;
import xaero.common.HudCommonBase;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContext;
import xaero.common.XaeroMinimapSession;
import xaero.common.config.LegacyCommonConfigIO;
import xaero.common.config.LegacyCommonConfigInit;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.events.ClientEvents;
import xaero.common.events.ClientEventsListener;
import xaero.common.events.CommonEvents;
import xaero.common.events.ModClientEvents;
import xaero.common.events.ModCommonEvents;
import xaero.common.file.SimpleBackup;
import xaero.common.gui.GuiHelper;
import xaero.common.misc.Internet;
import xaero.common.mods.SupportMods;
import xaero.common.platform.Services;
import xaero.common.server.XaeroMinimapServer;
import xaero.common.server.core.XaeroMinimapServerCore;
import xaero.common.server.mods.SupportServerMods;
import xaero.common.server.player.ServerPlayerTickHandler;
import xaero.common.settings.ModSettings;
import xaero.common.validator.FieldValidatorHolder;
import xaero.common.validator.NumericFieldValidator;
import xaero.common.validator.WaypointCoordinateFieldValidator;
import xaero.hud.Hud;
import xaero.hud.common.config.channel.register.HudModConfigCommonRegistryHandler;
import xaero.hud.controls.ControlsRegister;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.io.HudIO;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.radar.category.EntityRadarCategorySerializers;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.info.InfoDisplayIO;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderer;
import xaero.hud.minimap.player.tracker.synced.SyncedRenderedPlayerTracker;
import xaero.hud.minimap.player.tracker.system.RenderedPlayerTrackerManager;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.render.HudRenderer;
import xaero.lib.client.config.channel.register.handler.IConfigChannelClientRegistryHandler;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.channel.register.ConfigChannelRegistry;
import xaero.lib.common.config.channel.register.handler.IConfigChannelCommonRegistryHandler;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.common.packet.PacketHandlerRegistry;
import xaero.lib.patreon.Patreon;
import xaero.lib.patreon.PatreonMod;

public abstract class HudMod
implements IXaeroMinimap {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean FAIRPLAY = false;
    public static final String MINIMAP_ID = "xaerominimap";
    public static HudMod INSTANCE;
    protected PlatformContext platformContext;
    protected boolean loadedClient;
    protected boolean loadedServer;
    protected boolean firstStageLoaded;
    protected static final String versionID_minecraft = "26.3";
    private String versionID;
    private int newestUpdateID;
    private boolean isOutdated;
    private String latestVersion;
    private String latestVersionMD5;
    private ConfigChannel hudConfigs;
    private ModSettings settings;
    private String message = "";
    private ControlsRegister controlsRegister;
    protected final ClientEvents events;
    protected final ModClientEvents modClientEvents;
    private GuiHelper guiHelper;
    private FieldValidatorHolder fieldValidators;
    private SupportMods supportMods;
    private SupportServerMods supportServerMods;
    private EntityRadarCategorySerializers entityRadarCategorySerializers;
    private EntityRadarCategoryManager entityRadarCategoryManager;
    private ServerPlayerTickHandler serverPlayerTickHandler;
    private RenderedPlayerTrackerManager renderedPlayerTrackerManager;
    private PlayerTrackerMinimapElementRenderer trackedPlayerRenderer;
    private IPacketHandler messageHandler;
    protected final CommonEvents commonEvents;
    protected final ModCommonEvents modCommonEvents;
    private LegacyCommonConfigIO commonConfigIO;
    private ClientEventsListener clientEventsListener;
    protected Hud hud;
    protected HudRenderer hudRenderer;
    protected HudIO hudIO;
    private HudCommonBase hudCommon;
    private HudClientOnlyBase clientOnlyBase;
    private Minimap minimap;
    public boolean shouldLoadLegacySettings;
    private final InfoDisplayIO infoDisplaysIO;
    private File modJAR = null;
    private Path configFile;
    public Path waypointsFile;
    public Path minimapFolder;
    private XaeroMinimapServer minimapServer;

    @Override
    public boolean isLoadedClient() {
        return this.loadedClient;
    }

    @Override
    public boolean isLoadedServer() {
        return this.loadedServer;
    }

    protected HudMod() {
        INSTANCE = this;
        this.platformContext = this.createPlatformContext();
        this.renderedPlayerTrackerManager = RenderedPlayerTrackerManager.Builder.begin().build();
        this.commonEvents = this.platformContext.createCommonEvents(this);
        this.modCommonEvents = this.platformContext.createModCommonEvents(this);
        this.supportServerMods = this.platformContext.createSupportServerMods(this);
        this.entityRadarCategorySerializers = EntityRadarCategorySerializers.Builder.begin().build();
        Path configSubFolder = this.getConfigSubFolder();
        Path defaultConfigsSubFolder = this.getDefaultConfigsSubFolder();
        boolean bl = this.shouldLoadLegacySettings = !Files.exists(configSubFolder, new LinkOption[0]);
        if (!Services.PLATFORM.isDedicatedServer()) {
            this.events = this.platformContext.createClientEvents(this);
            this.clientEventsListener = this.createForgeEventHandlerListener();
            this.modClientEvents = this.platformContext.createModClientEvents(this);
            this.supportMods = this.platformContext.createSupportMods(this);
            this.entityRadarCategoryManager = EntityRadarCategoryManager.Builder.begin().setDefaultClientFilePath(configSubFolder.resolve("default_radar_categories_client.json")).setDefaultServerFilePath(configSubFolder.resolve("default_radar_categories_server.json")).setSecondaryDefaultFolderPath(defaultConfigsSubFolder).setSerializers(this.entityRadarCategorySerializers).build();
        } else {
            this.events = null;
            this.modClientEvents = null;
        }
        this.infoDisplaysIO = new InfoDisplayIO();
        this.hudConfigs = ConfigChannel.Builder.begin().setId(Identifier.fromNamespaceAndPath((String)this.getModId(), (String)"main")).setCommonRegistryHandler((IConfigChannelCommonRegistryHandler)this.createConfigCommonRegistryHandler()).setClientRegistryHandlerSupplier(this.createConfigClientRegistryHandlerSupplier()).setLogger(LOGGER).setConfigPath(configSubFolder).setDefaultConfigsPath(defaultConfigsSubFolder).setDefaultEnforcedServerProfileNodePath("xaero." + configSubFolder.getFileName().toString() + ".enforced_server_profile").build();
        ConfigChannelRegistry.INSTANCE.register(this.hudConfigs);
        new LegacyCommonConfigInit().init(this, this.getCommonConfigFileName());
    }

    protected abstract PlatformContext createPlatformContext();

    protected abstract String getCommonConfigFileName();

    protected abstract ModSettings createModSettings();

    protected abstract ControlsRegister createControlsRegister();

    protected abstract GuiHelper createGuiHelper();

    public abstract String getOldConfigFileName();

    protected abstract HudClientOnlyBase createClientOnly();

    protected abstract String getModName();

    protected abstract Logger getLogger();

    protected abstract ClientEventsListener createForgeEventHandlerListener();

    protected abstract HudModConfigCommonRegistryHandler createConfigCommonRegistryHandler();

    protected abstract Supplier<IConfigChannelClientRegistryHandler> createConfigClientRegistryHandlerSupplier();

    protected void loadClient() throws IOException {
        Path evenOlderConfigFile;
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 1/3", (Object)this.getModName());
        String modId = this.getModId();
        Path modFile = Services.PLATFORM.getModFile(modId);
        this.clientOnlyBase = this.createClientOnly();
        this.clientOnlyBase.preInit(modId, this);
        String fileName = modFile.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            this.modJAR = modFile.toFile();
        }
        Path gameDir = Services.PLATFORM.getGameDir();
        Path config = Services.PLATFORM.getConfigDir();
        this.waypointsFile = config.resolve("xaerowaypoints.txt");
        Path wrongWaypointsFolder3 = config.resolve("XaeroWaypoints");
        Path wrongWaypointsFolder2 = this.modJAR != null ? this.modJAR.toPath().getParent().resolve("XaeroWaypoints") : config.getParent().resolve("mods").resolve("XaeroWaypoints");
        Path wrongWaypointsFolder4 = new File(config.toFile().getCanonicalPath()).toPath().getParent().resolve("XaeroWaypoints");
        Path wrongWaypointsFolder5 = config.getParent().resolve("XaeroWaypoints");
        Path preMinimapWorldsFolder = gameDir.resolve("XaeroWaypoints");
        Path xaeroFolder = gameDir.resolve("xaero");
        if (!Files.exists(xaeroFolder, new LinkOption[0])) {
            Files.createDirectories(xaeroFolder, new FileAttribute[0]);
        }
        this.minimapFolder = xaeroFolder.resolve("minimap");
        if (Files.exists(preMinimapWorldsFolder, new LinkOption[0]) && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(preMinimapWorldsFolder, this.minimapFolder, new CopyOption[0]);
        }
        if (wrongWaypointsFile.exists() && !Files.exists(this.waypointsFile, new LinkOption[0])) {
            Files.move(wrongWaypointsFile.toPath(), this.waypointsFile, new CopyOption[0]);
        }
        if (wrongWaypointsFolder.exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder.toPath(), this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder2.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder2, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder3.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder3, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder4.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder4, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder5.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder5, this.minimapFolder, new CopyOption[0]);
        }
        Path waypointsFolderBackup = gameDir.resolve("XaeroWaypoints_BACKUP240807");
        if (!Files.exists(waypointsFolderBackup, new LinkOption[0]) && Files.exists(this.minimapFolder, new LinkOption[0])) {
            LOGGER.info("Backing up Xaero's minimap data...");
            SimpleBackup.copyDirectoryWithContents(this.minimapFolder, waypointsFolderBackup, 32, new CopyOption[0]);
            LOGGER.info("Done backing up Xaero's minimap data!");
        }
        Path legacyConfigFile = config.resolve(this.getOldConfigFileName());
        this.configFile = config.resolve("xaerohud.txt");
        if (Files.exists(legacyConfigFile, new LinkOption[0]) && !Files.exists(this.configFile, new LinkOption[0])) {
            Files.move(legacyConfigFile, this.configFile, new CopyOption[0]);
        }
        if (Files.exists(evenOlderConfigFile = gameDir.resolve("config").resolve(this.getOldConfigFileName()), new LinkOption[0]) && !Files.exists(this.configFile, new LinkOption[0])) {
            Files.move(evenOlderConfigFile, this.configFile, new CopyOption[0]);
        }
        this.settings = this.createModSettings();
        this.ensureControlsRegister();
        this.guiHelper = this.createGuiHelper();
        this.fieldValidators = new FieldValidatorHolder(new NumericFieldValidator(), new WaypointCoordinateFieldValidator());
        this.minimap = new Minimap(this);
        Path old_optionsFile = gameDir.resolve(this.getOldConfigFileName());
        if (Files.exists(old_optionsFile, new LinkOption[0]) && !Files.exists(this.configFile, new LinkOption[0])) {
            Path configFileParent = this.configFile.getParent();
            if (!Files.exists(configFileParent, new LinkOption[0])) {
                Files.createDirectories(configFileParent, new FileAttribute[0]);
            }
            Files.move(old_optionsFile, this.configFile, new CopyOption[0]);
        }
        if (Files.exists(old_waypointsFile, new LinkOption[0]) && !Files.exists(this.waypointsFile, new LinkOption[0])) {
            Path waypointFileParent = this.waypointsFile.getParent();
            if (!Files.exists(waypointFileParent, new LinkOption[0])) {
                Files.createDirectories(waypointFileParent, new FileAttribute[0]);
            }
            Files.move(old_waypointsFile, this.waypointsFile, new CopyOption[0]);
        }
        this.trackedPlayerRenderer = PlayerTrackerMinimapElementRenderer.Builder.begin(this).build();
        XaeroMinimapCore.modMain = this;
        this.firstStageLoaded = true;
    }

    public void loadLater() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 2/3", (Object)this.getModName());
        try {
            PatreonMod patreonEntry;
            this.minimap.getInfoDisplays().getManager().applyLocalConfig();
            this.hudCommon.preLoadLater(this);
            this.clientOnlyBase.preLoadLater(this);
            this.controlsRegister.onStage2();
            this.settings.loadSettings(this.shouldLoadLegacySettings);
            if (this.shouldLoadLegacySettings) {
                this.hudConfigs.getClientConfigProfileIO().save(this.hudConfigs.getClientConfigManager().getCurrentProfile());
                this.hudConfigs.getPrimaryClientConfigManagerIO().save();
            }
            this.entityRadarCategoryManager.init();
            Patreon.checkPatreon();
            Internet.checkModVersion(this);
            if (this.isOutdated && (patreonEntry = this.getPatreon()) != null) {
                patreonEntry.modJar = this.modJAR;
                patreonEntry.currentVersion = this.getVersionID();
                patreonEntry.latestVersion = this.latestVersion;
                patreonEntry.md5 = this.latestVersionMD5;
                patreonEntry.onVersionIgnore = () -> {
                    this.getHudConfigs().getPrimaryClientConfigManager().getConfig().set(MinimapPrimaryClientConfigOptions.IGNORED_UPDATE, (Object)this.newestUpdateID);
                    this.getHudConfigs().getPrimaryClientConfigManagerIO().save();
                };
                Patreon.addOutdatedMod((Object)patreonEntry);
            }
            this.renderedPlayerTrackerManager.register("minimap_synced", new SyncedRenderedPlayerTracker());
            this.supportMods.checkLater();
            this.getMinimap().getOverMapRendererHandler().add(this.trackedPlayerRenderer);
            this.getMinimap().getWorldRendererHandler().add(this.trackedPlayerRenderer);
        }
        catch (Throwable e) {
            LOGGER.error("error", e);
            this.minimap.setCrashedWith(e);
        }
    }

    public void loadLaterClientRender() {
        if (this.minimap.getCrashedWith() != null) {
            return;
        }
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 3/3", (Object)this.getModName());
        try {
            this.clientOnlyBase.loadLaterRender();
        }
        catch (Throwable e) {
            LOGGER.error("error", e);
            this.minimap.setCrashedWith(e);
        }
        this.loadedClient = true;
    }

    public void loadServer() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 1/2 (Server)", (Object)this.getModName());
        this.minimapServer = new XaeroMinimapServer(this);
        this.minimapServer.load();
        this.firstStageLoaded = true;
    }

    protected void loadCommon() {
        this.versionID = "26.3_" + this.platformContext.getModInfoVersion();
        XaeroMinimapServerCore.modMain = this;
        this.messageHandler = PacketHandlerRegistry.INSTANCE.register(Identifier.fromNamespaceAndPath((String)this.getModId(), (String)"main"), 1000000, "1.0");
        this.hudCommon = new HudCommonBase();
        this.hudCommon.setup(this);
    }

    protected void loadLaterServer() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 2/2 (Server)", (Object)this.getModName());
        this.hudCommon.preLoadLater(this);
        this.minimapServer.loadLater();
        this.loadedServer = true;
    }

    @Override
    public Path getConfigFile() {
        return this.configFile;
    }

    @Override
    public File getModJAR() {
        return this.modJAR;
    }

    @Override
    public ModSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setSettings(ModSettings minimapSettings) {
        this.settings = minimapSettings;
    }

    @Override
    public void resetSettings() throws IOException {
    }

    @Override
    public boolean isOutdated() {
        return this.isOutdated;
    }

    @Override
    public void setOutdated(boolean value) {
        this.isOutdated = value;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String string) {
        this.message = string;
    }

    @Override
    public String getLatestVersion() {
        return this.latestVersion;
    }

    @Override
    public void setLatestVersion(String string) {
        this.latestVersion = string;
    }

    @Override
    public int getNewestUpdateID() {
        return this.newestUpdateID;
    }

    @Override
    public void setNewestUpdateID(int parseInt) {
        this.newestUpdateID = parseInt;
    }

    @Override
    public PatreonMod getPatreon() {
        return (PatreonMod)Patreon.getMods().get(this.getFileLayoutID());
    }

    @Override
    public Path getWaypointsFile() {
        return this.waypointsFile;
    }

    @Override
    public Path getMinimapFolder() {
        return this.minimapFolder;
    }

    @Override
    public SupportMods getSupportMods() {
        return this.supportMods;
    }

    @Override
    public ModClientEvents getModEvents() {
        return this.modClientEvents;
    }

    @Override
    public GuiHelper getGuiHelper() {
        return this.guiHelper;
    }

    @Override
    public FieldValidatorHolder getFieldValidators() {
        return this.fieldValidators;
    }

    @Override
    public ControlsRegister getControlsRegister() {
        return this.controlsRegister;
    }

    @Override
    public ClientEvents getEvents() {
        return this.events;
    }

    @Override
    public void setLatestVersionMD5(String string) {
        this.latestVersionMD5 = string;
    }

    @Override
    public String getLatestVersionMD5() {
        return this.latestVersionMD5;
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public EntityRadarCategoryManager getEntityRadarCategoryManager() {
        return this.entityRadarCategoryManager;
    }

    @Override
    public boolean isFairPlay() {
        if (this.loadedClient) {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            return session != null && session.getMinimapProcessor().getForcedFairPlay();
        }
        return false;
    }

    @Override
    public PlayerTrackerMinimapElementRenderer getTrackedPlayerRenderer() {
        return this.trackedPlayerRenderer;
    }

    @Override
    public RenderedPlayerTrackerManager getRenderedPlayerTrackerManager() {
        return this.renderedPlayerTrackerManager;
    }

    @Override
    public ServerPlayerTickHandler getServerPlayerTickHandler() {
        return this.serverPlayerTickHandler;
    }

    @Override
    public void setServerPlayerTickHandler(ServerPlayerTickHandler serverPlayerTickHandler) {
        this.serverPlayerTickHandler = serverPlayerTickHandler;
    }

    @Override
    public IPacketHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public CommonEvents getCommonEvents() {
        return this.commonEvents;
    }

    @Override
    public SupportServerMods getSupportServerMods() {
        return this.supportServerMods;
    }

    @Override
    public void setCommonConfigIO(LegacyCommonConfigIO serverConfigIO) {
        this.commonConfigIO = serverConfigIO;
    }

    @Override
    public LegacyCommonConfigIO getCommonConfigIO() {
        return this.commonConfigIO;
    }

    @Override
    public ClientEventsListener getClientEventsListener() {
        return this.clientEventsListener;
    }

    @Override
    public PlatformContext getPlatformContext() {
        return this.platformContext;
    }

    @Override
    public void ensureControlsRegister() {
        if (this.controlsRegister == null) {
            this.controlsRegister = this.createControlsRegister();
        }
    }

    @Override
    public ModClientEvents getModClientEvents() {
        return this.modClientEvents;
    }

    @Override
    public ModCommonEvents getModCommonEvents() {
        return this.modCommonEvents;
    }

    @Override
    public void tryLoadLater() {
    }

    @Override
    public void tryLoadLaterServer() {
    }

    @Override
    public String getVersionID() {
        return this.versionID;
    }

    @Override
    public Hud getHud() {
        return this.hud;
    }

    @Override
    public HudRenderer getHudRenderer() {
        return this.hudRenderer;
    }

    @Override
    public HudIO getHudIO() {
        return this.hudIO;
    }

    @Override
    public Minimap getMinimap() {
        return this.minimap;
    }

    @Override
    public boolean isFirstStageLoaded() {
        return this.firstStageLoaded;
    }

    public KeyMappingControllerManager getKeyMappingControllers() {
        return this.controlsRegister.getKeyMappingControllers();
    }

    public abstract Path getConfigSubFolder();

    public abstract Path getDefaultConfigsSubFolder();

    public ConfigChannel getHudConfigs() {
        return this.hudConfigs;
    }

    public InfoDisplayIO getInfoDisplaysIO() {
        return this.infoDisplaysIO;
    }

    public EntityRadarCategorySerializers getEntityRadarCategorySerializers() {
        return this.entityRadarCategorySerializers;
    }
}

