/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import xaero.common.PlatformContext;
import xaero.common.XaeroMinimapSession;
import xaero.common.config.LegacyCommonConfigIO;
import xaero.common.events.ClientEvents;
import xaero.common.events.ClientEventsListener;
import xaero.common.events.CommonEvents;
import xaero.common.events.ModClientEvents;
import xaero.common.events.ModCommonEvents;
import xaero.common.gui.GuiHelper;
import xaero.common.mods.SupportMods;
import xaero.common.platform.Services;
import xaero.common.server.mods.SupportServerMods;
import xaero.common.server.player.ServerPlayerTickHandler;
import xaero.common.settings.ModSettings;
import xaero.common.validator.FieldValidatorHolder;
import xaero.hud.Hud;
import xaero.hud.controls.ControlsRegister;
import xaero.hud.io.HudIO;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderer;
import xaero.hud.minimap.player.tracker.system.RenderedPlayerTrackerManager;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.render.HudRenderer;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.patreon.PatreonMod;

public interface IXaeroMinimap {
    public static final Path old_waypointsFile = Services.PLATFORM.getGameDir().resolve("xaerowaypoints.txt");
    public static final File wrongWaypointsFile = Services.PLATFORM.getGameDir().resolve("config").resolve("xaerowaypoints.txt").toFile();
    public static final File wrongWaypointsFolder = Services.PLATFORM.getGameDir().resolve("mods").resolve("XaeroWaypoints").toFile();

    public String getVersionID();

    public String getFileLayoutID();

    public Path getConfigFile();

    public File getModJAR();

    public ModSettings getSettings();

    public void setSettings(ModSettings var1);

    public boolean isOutdated();

    public void setOutdated(boolean var1);

    public String getMessage();

    public void setMessage(String var1);

    public String getLatestVersion();

    public String getLatestVersionMD5();

    public void setLatestVersion(String var1);

    public void setLatestVersionMD5(String var1);

    public int getNewestUpdateID();

    public void setNewestUpdateID(int var1);

    public PatreonMod getPatreon();

    public String getVersionsURL();

    public void resetSettings() throws IOException;

    public String getUpdateLink();

    public Object getSettingsKey();

    public Object getServerSettingsKey();

    public Path getWaypointsFile();

    public Path getMinimapFolder();

    public XaeroMinimapSession createSession();

    public SupportMods getSupportMods();

    public SupportServerMods getSupportServerMods();

    public GuiHelper getGuiHelper();

    public FieldValidatorHolder getFieldValidators();

    public ControlsRegister getControlsRegister();

    public ClientEvents getEvents();

    public void tryLoadLater();

    public void tryLoadLaterServer();

    public ModClientEvents getModEvents();

    public boolean isStandalone();

    public EntityRadarCategoryManager getEntityRadarCategoryManager();

    public boolean isFairPlay();

    public PlayerTrackerMinimapElementRenderer getTrackedPlayerRenderer();

    public RenderedPlayerTrackerManager getRenderedPlayerTrackerManager();

    public ServerPlayerTickHandler getServerPlayerTickHandler();

    public void setServerPlayerTickHandler(ServerPlayerTickHandler var1);

    public IPacketHandler getMessageHandler();

    public CommonEvents getCommonEvents();

    public void setCommonConfigIO(LegacyCommonConfigIO var1);

    public LegacyCommonConfigIO getCommonConfigIO();

    public ClientEventsListener getClientEventsListener();

    public PlatformContext getPlatformContext();

    public void ensureControlsRegister();

    public ModClientEvents getModClientEvents();

    public ModCommonEvents getModCommonEvents();

    public String getModId();

    public boolean isLoadedClient();

    public boolean isLoadedServer();

    public Hud getHud();

    public HudRenderer getHudRenderer();

    public HudIO getHudIO();

    public Minimap getMinimap();

    public boolean isFirstStageLoaded();
}

