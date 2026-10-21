/*
 * Decompiled with CFR 0.152.
 */
package xaero.minimap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.common.HudClientOnlyBase;
import xaero.common.HudMod;
import xaero.common.XaeroMinimapSession;
import xaero.common.events.ClientEventsListener;
import xaero.common.gui.GuiHelper;
import xaero.common.mods.SupportMods;
import xaero.common.platform.Services;
import xaero.common.settings.ModSettings;
import xaero.hud.common.config.channel.register.HudModConfigCommonRegistryHandler;
import xaero.hud.config.channel.register.HudModConfigClientRegistryHandler;
import xaero.hud.controls.ControlsRegister;
import xaero.hud.xminimap.controls.XMinimapControlsRegister;
import xaero.hud.xminimap.controls.key.XMinimapKeyMappings;
import xaero.lib.client.config.channel.register.handler.IConfigChannelClientRegistryHandler;
import xaero.lib.patreon.Patreon;
import xaero.minimap.MinimapClientOnly;
import xaero.minimap.XaeroMinimapStandaloneSession;
import xaero.minimap.gui.MinimapGuiHelper;

public abstract class XaeroMinimap
extends HudMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static XaeroMinimap instance;
    public static final String MOD_ID = "xaerominimap";

    public XaeroMinimap() {
        instance = this;
    }

    @Override
    protected void loadClient() throws IOException {
        super.loadClient();
    }

    @Override
    protected void loadCommon() {
        SupportMods.checkForMinimapDuplicates("xaero.pvp.BetterPVP");
        super.loadCommon();
    }

    @Override
    protected String getCommonConfigFileName() {
        return "xaerominimap-common.txt";
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    protected ModSettings createModSettings() {
        return new ModSettings(this);
    }

    @Override
    protected GuiHelper createGuiHelper() {
        return new MinimapGuiHelper(this);
    }

    @Override
    public String getOldConfigFileName() {
        return "xaerominimap.txt";
    }

    @Override
    protected HudClientOnlyBase createClientOnly() {
        return new MinimapClientOnly();
    }

    @Override
    protected String getModName() {
        return "Xaero's Minimap";
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected ClientEventsListener createForgeEventHandlerListener() {
        return new ClientEventsListener();
    }

    @Override
    public String getVersionsURL() {
        return "http://data.chocolateminecraft.com/Versions_" + Patreon.getKEY_VERSION2() + "/Minimap.dat";
    }

    @Override
    public String getUpdateLink() {
        return "http://chocolateminecraft.com/update/minimap.html";
    }

    @Override
    protected ControlsRegister createControlsRegister() {
        return new XMinimapControlsRegister();
    }

    @Override
    public XaeroMinimapSession createSession() {
        return new XaeroMinimapStandaloneSession(this);
    }

    @Override
    public Object getSettingsKey() {
        return XMinimapKeyMappings.SETTINGS;
    }

    @Override
    public Object getServerSettingsKey() {
        return XMinimapKeyMappings.SERVER_PROFILES;
    }

    @Override
    public Path getConfigSubFolder() {
        return Services.PLATFORM.getConfigDir().resolve("xaero").resolve("minimap");
    }

    @Override
    public Path getDefaultConfigsSubFolder() {
        return Services.PLATFORM.getConfigDir().resolveSibling("defaultconfigs").resolve("xaero").resolve("minimap");
    }

    @Override
    protected HudModConfigCommonRegistryHandler createConfigCommonRegistryHandler() {
        return new HudModConfigCommonRegistryHandler();
    }

    @Override
    protected Supplier<IConfigChannelClientRegistryHandler> createConfigClientRegistryHandlerSupplier() {
        return HudModConfigClientRegistryHandler::new;
    }

    @Override
    public boolean isStandalone() {
        return true;
    }
}

