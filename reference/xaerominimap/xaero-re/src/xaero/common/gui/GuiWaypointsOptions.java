/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.commons.io.FileUtils;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiTransfer;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.GuiWorldTpCommand;
import xaero.common.gui.MyBigButton;
import xaero.common.gui.TooltipButton;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.MyTinyButton;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.common.config.util.ConfigUtils;

public class GuiWaypointsOptions
extends ScreenBase {
    private MinimapSession session;
    private MinimapWorldManager manager;
    private Button automaticButton;
    private Button subAutomaticButton;
    private Button deleteButton;
    private Button subDeleteButton;
    private Button connectButton;
    private Button showDeletedThirdPartyButton;
    private boolean buttonTest;
    private MinimapWorld minimapWorld;
    private MinimapWorld automaticMinimapWorld;
    private MinimapWorldRootContainer rootContainer;
    private boolean teleportationOptionShown;
    private boolean selectedWorldIsConnected;
    public Tooltip mwTooltip = new Tooltip("gui.xaero_use_multiworld_tooltip");
    public Tooltip teleportationTooltip = new Tooltip("gui.xaero_teleportation_tooltip", Style.EMPTY.withColor(ChatFormatting.RED));
    public Tooltip connectionTooltip = new Tooltip("gui.xaero_world_connection_tooltip");
    private Tooltip showDeletedThirdPartyTooltip = new Tooltip("gui.xaero_box_showing_third_party_deleted_waypoints");
    private final IXaeroMinimap modMain;

    public GuiWaypointsOptions(IXaeroMinimap modMain, MinimapSession session, Screen parent, Screen escapeScreen, MinimapWorld minimapWorld, XaeroPath frozenAutoWorldPath) {
        super(parent, escapeScreen, (Component)Component.translatable((String)"gui.xaero_options"));
        this.modMain = modMain;
        this.session = session;
        this.manager = this.session.getWorldManager();
        this.minimapWorld = minimapWorld;
        this.rootContainer = minimapWorld.getContainer().getRoot();
        this.automaticMinimapWorld = this.manager.getWorld(frozenAutoWorldPath);
        this.teleportationOptionShown = this.rootContainer.getConfig().isTeleportationEnabled();
    }

    public void init() {
        super.init();
        this.parent.resize(this.width, this.height);
        this.selectedWorldIsConnected = this.rootContainer.getSubWorldConnections().isConnected(this.automaticMinimapWorld, this.minimapWorld);
        this.addRenderableWidget((GuiEventListener)new MyTinyButton(this.width / 2 - 203, 32, (Component)Component.translatable((String)"gui.xaero_close", (Object[])new Object[0]), b -> this.actionPerformed(b, 5)));
        this.addRenderableWidget((GuiEventListener)new MyBigButton(6, this.width / 2 - 203, 57, (Component)Component.translatable((String)"gui.xaero_transfer", (Object[])new Object[0]), b -> this.actionPerformed(b, 6)));
        this.automaticButton = new MyBigButton(7, this.width / 2 - 203, 82, (Component)Component.translatable((String)"gui.xaero_make_automatic", (Object[])new Object[0]), b -> this.actionPerformed(b, 7));
        this.addRenderableWidget((GuiEventListener)this.automaticButton);
        this.subAutomaticButton = new MyBigButton(8, this.width / 2 - 203, 107, (Component)Component.translatable((String)"gui.xaero_make_multi_automatic", (Object[])new Object[0]), b -> this.actionPerformed(b, 8));
        this.addRenderableWidget((GuiEventListener)this.subAutomaticButton);
        this.deleteButton = new MyBigButton(9, this.width / 2 - 203, 132, (Component)Component.translatable((String)"gui.xaero_delete_world", (Object[])new Object[0]), b -> this.actionPerformed(b, 9));
        this.addRenderableWidget((GuiEventListener)this.deleteButton);
        this.subDeleteButton = new MyBigButton(10, this.width / 2 - 203, 157, (Component)Component.translatable((String)"gui.xaero_delete_multi_world", (Object[])new Object[0]), b -> this.actionPerformed(b, 10));
        this.addRenderableWidget((GuiEventListener)this.subDeleteButton);
        this.addRenderableWidget((GuiEventListener)new TooltipButton(this.width / 2 + 3, 57, 200, 20, (Component)Component.literal((String)this.getConfigButtonName(0)), this::onMultiWorldButton, (Supplier<Tooltip>)this.mwTooltip));
        Button teleportationEnabledButton = (Button)this.addRenderableWidget((GuiEventListener)new TooltipButton(this.width / 2 + 3, 82, 200, 20, (Component)Component.literal((String)this.getConfigButtonName(1)), this::onTeleportationButton, (Supplier<Tooltip>)this.teleportationTooltip));
        teleportationEnabledButton.active = this.teleportationOptionShown;
        this.addRenderableWidget((GuiEventListener)new MyBigButton(13, this.width / 2 + 3, 107, (Component)Component.translatable((String)"gui.xaero_world_teleport_command"), b -> this.actionPerformed(b, 13)));
        this.connectButton = new TooltipButton(this.width / 2 + 3, 132, 200, 20, (Component)Component.literal((String)this.getConfigButtonName(4)), b -> this.actionPerformed(b, 14), (Supplier<Tooltip>)this.connectionTooltip);
        this.addRenderableWidget((GuiEventListener)this.connectButton);
        this.connectButton.active = MinimapWorldContainerUtil.isMultiplayer(this.rootContainer.getPath()) && this.rootContainer == this.automaticMinimapWorld.getContainer().getRoot();
        this.showDeletedThirdPartyButton = new TooltipButton(this.width / 2 + 3, 157, 200, 20, this.getThirdPartyDeletedMessage(), this::onShowThirdPartyDeleted, (Supplier<Tooltip>)this.showDeletedThirdPartyTooltip);
        this.addRenderableWidget((GuiEventListener)this.showDeletedThirdPartyButton);
        this.addRenderableWidget((GuiEventListener)new MyBigButton(202, this.width / 2 + 3, 182, (Component)Component.literal((String)this.getConfigButtonName(2)), this::onSortButton));
        this.addRenderableWidget((GuiEventListener)new MyBigButton(203, this.width / 2 + 3, 207, (Component)Component.literal((String)this.getConfigButtonName(3)), this::onSortReverseButton));
    }

    private String getConfigButtonName(int buttonId) {
        switch (buttonId) {
            case 0: {
                return I18n.get((String)"gui.xaero_use_multiworld", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isUsingMultiworldDetection());
            }
            case 1: {
                return I18n.get((String)"gui.xaero_teleportation", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isTeleportationEnabled());
            }
            case 2: {
                return I18n.get((String)"gui.xaero_sort", (Object[])new Object[0]) + ": " + I18n.get((String)this.rootContainer.getConfig().getSortType().optionName, (Object[])new Object[0]);
            }
            case 3: {
                return I18n.get((String)"gui.xaero_sort_reversed", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isSortReversed());
            }
            case 4: {
                return this.selectedWorldIsConnected ? I18n.get((String)"gui.xaero_disconnect_from_auto", (Object[])new Object[0]) : I18n.get((String)"gui.xaero_connect_with_auto", (Object[])new Object[0]);
            }
        }
        return "";
    }

    private void onMultiWorldButton(Button button) {
        this.buttonTest = true;
        MinimapWorldRootContainer wc = this.rootContainer;
        wc.getConfig().setUsingMultiworldDetection(!this.rootContainer.getConfig().isUsingMultiworldDetection());
        wc.getConfig().setDefaultMultiworldId(null);
        this.session.getWorldManagerIO().getRootConfigIO().save(wc);
        button.setMessage((Component)Component.literal((String)this.getConfigButtonName(0)));
    }

    private void onTeleportationButton(Button button) {
        this.buttonTest = true;
        MinimapWorldRootContainer wc = this.rootContainer;
        wc.getConfig().setTeleportationEnabled(!wc.getConfig().isTeleportationEnabled());
        this.session.getWorldManagerIO().getRootConfigIO().save(wc);
        button.setMessage((Component)Component.literal((String)this.getConfigButtonName(1)));
    }

    private void onSortButton(Button button) {
        this.buttonTest = true;
        MinimapWorldRootContainer wc = this.rootContainer;
        this.rootContainer.getConfig().toggleSortType();
        Screen screen = this.parent;
        if (screen instanceof GuiWaypoints) {
            GuiWaypoints guiWaypoints = (GuiWaypoints)screen;
            guiWaypoints.clearSelection();
        }
        this.parent.init(this.width, this.height);
        this.session.getWorldManagerIO().getRootConfigIO().save(wc);
        button.setMessage((Component)Component.literal((String)this.getConfigButtonName(2)));
    }

    private void onSortReverseButton(Button button) {
        this.buttonTest = true;
        MinimapWorldRootContainer wc = this.rootContainer;
        this.rootContainer.getConfig().toggleSortReversed();
        Screen screen = this.parent;
        if (screen instanceof GuiWaypoints) {
            GuiWaypoints guiWaypoints = (GuiWaypoints)screen;
            guiWaypoints.clearSelection();
        }
        this.parent.init(this.width, this.height);
        this.session.getWorldManagerIO().getRootConfigIO().save(wc);
        button.setMessage((Component)Component.literal((String)this.getConfigButtonName(3)));
    }

    private void onShowThirdPartyDeleted(Button button) {
        this.buttonTest = true;
        GuiWaypoints.showingThirdPartyDeleted = !GuiWaypoints.showingThirdPartyDeleted;
        button.setMessage(this.getThirdPartyDeletedMessage());
        Screen screen = this.parent;
        if (screen instanceof GuiWaypoints) {
            GuiWaypoints guiWaypoints = (GuiWaypoints)screen;
            guiWaypoints.clearSelection();
        }
        this.parent.init(this.width, this.height);
    }

    private Component getThirdPartyDeletedMessage() {
        return CommonComponents.optionNameValue((Component)Component.translatable((String)"gui.xaero_showing_third_party_deleted_waypoints"), (Component)ConfigUtils.getDisplayForBoolean(null, (Boolean)GuiWaypoints.showingThirdPartyDeleted));
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.buttonTest = false;
        boolean toReturn = super.mouseClicked(event, doubleClick);
        if (!this.buttonTest) {
            this.goBack();
        }
        return toReturn;
    }

    protected void actionPerformed(Button p_146284_1_, int id) {
        this.buttonTest = true;
        if (p_146284_1_.active) {
            switch (id) {
                case 5: {
                    this.goBack();
                    break;
                }
                case 6: {
                    this.minecraft.gui.setScreen((Screen)new GuiTransfer(this.modMain, this.session, this.parent, this.escape));
                    break;
                }
                case 7: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_make_automatic_msg1"), (Component)Component.translatable((String)"gui.xaero_make_automatic_msg2")));
                    break;
                }
                case 8: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_make_multi_automatic_msg1"), (Component)Component.translatable((String)"gui.xaero_make_multi_automatic_msg2")));
                    break;
                }
                case 9: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_delete_world_msg1"), (Component)Component.translatable((String)"gui.xaero_delete_world_msg2")));
                    break;
                }
                case 10: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_delete_multi_world_msg1"), (Component)Component.translatable((String)"gui.xaero_delete_multi_world_msg2")));
                    break;
                }
                case 11: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_multiply_msg1"), (Component)Component.translatable((String)"gui.xaero_multiply_msg2")));
                    break;
                }
                case 12: {
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_multiply_msg1"), (Component)Component.translatable((String)"gui.xaero_divide_msg2")));
                    break;
                }
                case 13: {
                    this.minecraft.gui.setScreen((Screen)new GuiWorldTpCommand(this.modMain, (Screen)this, this.escape, this.minimapWorld.getContainer().getRoot()));
                    break;
                }
                case 14: {
                    MinimapWorldContainer autoContainer = this.automaticMinimapWorld.getContainer();
                    MinimapWorldContainer selectedContainer = this.minimapWorld.getContainer();
                    String autoWorldName = autoContainer.getFullWorldName(this.automaticMinimapWorld.getNode(), autoContainer.getSubName()) + " (auto)";
                    String selectedWorldName = selectedContainer.getFullWorldName(this.minimapWorld.getNode(), selectedContainer.getSubName());
                    String connectionDisplayString = autoWorldName + "   \u00a7e<=>\u00a7r   " + selectedWorldName;
                    if (this.selectedWorldIsConnected) {
                        this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_disconnect_from_auto_msg"), (Component)Component.literal((String)connectionDisplayString)));
                        break;
                    }
                    this.minecraft.gui.setScreen((Screen)new ConfirmScreen(result -> this.confirmResult(result, id), (Component)Component.translatable((String)"gui.xaero_connect_with_auto_msg"), (Component)Component.literal((String)connectionDisplayString)));
                }
            }
        }
    }

    public void confirmResult(boolean result, int id) {
        block33: {
            block32: {
                boolean differentRoot = this.isDifferentRootContainer();
                boolean differentSub = this.isDifferentSubWorld(differentRoot);
                boolean exitOptions = true;
                if (!result) break block32;
                switch (id) {
                    case 7: {
                        if (!differentRoot) break;
                        MinimapWorldRootContainer selected = this.rootContainer;
                        MinimapWorldRootContainer auto = this.manager.getAutoRootContainer();
                        if (selected == null || auto == null) break;
                        XaeroPath buKey = selected.getPath();
                        this.manager.removeContainer(selected.getPath());
                        this.manager.removeContainer(auto.getPath());
                        selected.setPath(auto.getPath());
                        auto.setPath(buKey);
                        this.manager.addRootWorldContainer(selected);
                        this.manager.addRootWorldContainer(auto);
                        selected.updateConnectionsField(this.session.getWaypointSession());
                        auto.updateConnectionsField(this.session.getWaypointSession());
                        for (MinimapWorldContainer subContainer : auto.getSubContainers()) {
                            ThirdPartyWaypointManager oldThirdPartyWaypointManager = subContainer.getThirdPartyWaypointManager();
                            if (oldThirdPartyWaypointManager == null) continue;
                            ThirdPartyWaypointManager newThirdPartyWaypointManager = selected.addSubContainer(selected.getPath().resolve(subContainer.getLastNode())).getThirdPartyWaypointManager();
                            for (ThirdPartyWaypoints oldThirdPartyWaypoints : oldThirdPartyWaypointManager.getAll()) {
                                ThirdPartyWaypoints newThirdPartyWaypoints = newThirdPartyWaypointManager.get(oldThirdPartyWaypoints.getOriginId());
                                newThirdPartyWaypoints.setEnabledStateGetter(oldThirdPartyWaypoints.getEnabledStateGetter());
                                for (Map.Entry<String, Waypoint> oldWaypointEntry : oldThirdPartyWaypoints.getWaypoints().entrySet()) {
                                    newThirdPartyWaypoints.add(oldWaypointEntry.getKey(), oldWaypointEntry.getValue());
                                }
                            }
                            oldThirdPartyWaypointManager.clear();
                        }
                        Path selectedPath = selected.getDirectoryPath();
                        Path autoPath = auto.getDirectoryPath();
                        Path tempFolder = this.modMain.getMinimapFolder().resolve("temp_to_add");
                        try {
                            Files.createDirectories(tempFolder, new FileAttribute[0]);
                            Path selectedTemp = tempFolder.resolve(selectedPath.getFileName());
                            if (Files.exists(selectedPath, new LinkOption[0])) {
                                Files.move(selectedPath, selectedTemp, new CopyOption[0]);
                            }
                            if (Files.exists(autoPath, new LinkOption[0])) {
                                Files.move(autoPath, selectedPath, new CopyOption[0]);
                            }
                            if (Files.exists(selectedTemp, new LinkOption[0])) {
                                Files.move(selectedTemp, autoPath, new CopyOption[0]);
                            }
                            Files.deleteIfExists(tempFolder);
                            this.session.getWorldManagerIO().getRootConfigIO().load(selected);
                            this.session.getWorldManagerIO().getRootConfigIO().load(auto);
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                        }
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 8: {
                        if (!differentSub) break;
                        MinimapWorld autoWorld = this.automaticMinimapWorld;
                        MinimapWorld selectedWorld = this.minimapWorld;
                        try {
                            Path autoFile = this.session.getWorldManagerIO().getWorldFile(autoWorld);
                            Path selectedFile = this.session.getWorldManagerIO().getWorldFile(selectedWorld);
                            Path autoTempFile = autoFile.getParent().resolve("temp_to_add").resolve(autoFile.getFileName());
                            Files.createDirectories(autoTempFile.getParent(), new FileAttribute[0]);
                            if (!Files.exists(autoFile, new LinkOption[0])) {
                                Files.createFile(autoFile, new FileAttribute[0]);
                            }
                            Files.move(autoFile, autoTempFile, new CopyOption[0]);
                            if (!Files.exists(selectedFile, new LinkOption[0])) {
                                Files.createFile(selectedFile, new FileAttribute[0]);
                            }
                            Files.move(selectedFile, autoFile, new CopyOption[0]);
                            if (Files.exists(autoTempFile, new LinkOption[0])) {
                                Files.move(autoTempFile, selectedFile, new CopyOption[0]);
                            }
                            Files.deleteIfExists(autoTempFile.getParent());
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                            break;
                        }
                        MinimapWorldContainer autoWc = autoWorld.getContainer();
                        MinimapWorldContainer selectedWc = selectedWorld.getContainer();
                        autoWorld.setContainer(selectedWc);
                        selectedWorld.setContainer(autoWc);
                        selectedWc.removeWorld(selectedWorld.getNode());
                        autoWc.removeWorld(autoWorld.getNode());
                        String buSelected = selectedWorld.getNode();
                        selectedWorld.setNode(autoWorld.getNode());
                        autoWorld.setNode(buSelected);
                        selectedWc.addWorld(autoWorld);
                        autoWc.addWorld(selectedWorld);
                        ResourceKey<Level> buDimId = selectedWorld.getDimId();
                        selectedWorld.setDimId(autoWorld.getDimId());
                        autoWorld.setDimId(buDimId);
                        this.rootContainer.getSubWorldConnections().swapConnections(autoWorld, selectedWorld);
                        this.session.getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 9: {
                        if (!differentRoot) break;
                        XaeroPath selectedRootContainerId = this.rootContainer.getPath();
                        try {
                            File directory = selectedRootContainerId.applyToFilePath(this.modMain.getMinimapFolder()).toFile();
                            if (directory.exists()) {
                                FileUtils.deleteDirectory((File)directory);
                            }
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                            break;
                        }
                        this.manager.removeContainer(selectedRootContainerId);
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 10: {
                        if (!differentSub) break;
                        MinimapWorld selectedWorld = this.minimapWorld;
                        try {
                            Files.deleteIfExists(this.session.getWorldManagerIO().getWorldFile(selectedWorld));
                        }
                        catch (IOException e) {
                            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                        }
                        selectedWorld.getContainer().removeWorld(selectedWorld.getNode());
                        selectedWorld.getContainer().removeName(selectedWorld.getNode());
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 11: {
                        this.multiplyWaypoints(this.minimapWorld, 8.0);
                        break;
                    }
                    case 12: {
                        this.multiplyWaypoints(this.minimapWorld, 0.125);
                        break;
                    }
                    case 14: {
                        if (!this.selectedWorldIsConnected) {
                            this.rootContainer.getSubWorldConnections().addConnection(this.automaticMinimapWorld, this.minimapWorld);
                        } else {
                            this.rootContainer.getSubWorldConnections().removeConnection(this.automaticMinimapWorld, this.minimapWorld);
                        }
                        this.session.getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
                    }
                }
                if (exitOptions) {
                    if (this.parent instanceof GuiWaypoints) {
                        this.minecraft.gui.setScreen((Screen)new GuiWaypoints((HudMod)this.modMain, this.session, ((GuiWaypoints)this.parent).parent, this.escape));
                    } else {
                        this.goBack();
                    }
                }
                break block33;
            }
            this.minecraft.gui.setScreen((Screen)this);
        }
    }

    private void multiplyWaypoints(MinimapWorld world, double factor) {
        for (WaypointSet set : world.getIterableWaypointSets()) {
            for (Waypoint wp : set.getWaypoints()) {
                wp.setX((int)Math.floor((double)wp.getX() * factor));
                wp.setZ((int)Math.floor((double)wp.getZ() * factor));
            }
        }
        try {
            this.session.getWorldManagerIO().saveWorld(world);
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    private boolean isDifferentRootContainer() {
        return this.session.getWorldState().getAutoRootContainerPath() != null && !this.session.getWorldState().getAutoRootContainerPath().equals(this.rootContainer.getPath());
    }

    private boolean isDifferentSubWorld(boolean differentRoot) {
        return !differentRoot && this.minimapWorld != this.automaticMinimapWorld;
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        this.parent.extractRenderStateWithTooltipAndSubtitles(guiGraphics, 0, 0, par3);
        super.extractBackground(guiGraphics, par1, par2, par3);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        this.automaticButton.active = this.deleteButton.active = this.isDifferentRootContainer();
        this.subAutomaticButton.active = this.subDeleteButton.active = this.isDifferentSubWorld(this.automaticButton.active);
        super.extractRenderState(guiGraphics, par1, par2, par3);
        this.renderTooltips(guiGraphics, par1, par2, par3);
    }

    public boolean isInGameUi() {
        return true;
    }
}

