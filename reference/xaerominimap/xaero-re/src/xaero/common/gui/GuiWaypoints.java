/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import xaero.common.HudMod;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiClearSet;
import xaero.common.gui.GuiDeleteSet;
import xaero.common.gui.GuiNewSet;
import xaero.common.gui.GuiWaypointContainers;
import xaero.common.gui.GuiWaypointSets;
import xaero.common.gui.GuiWaypointWorlds;
import xaero.common.gui.GuiWaypointsOptions;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.waypoint.WaypointsSort;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypoints;
import xaero.hud.minimap.waypoint.util.WaypointUtils;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.controls.util.KeyMappingUtils;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.util.GuiUtils;
import xaero.lib.client.gui.widget.MyTinyButton;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.client.gui.widget.dropdown.IDropDownWidgetCallback;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.lib.common.util.KeySortableByOther;

public class GuiWaypoints
extends ScreenBase
implements IDropDownWidgetCallback {
    private static final int FRAME_TOP_SIZE = 58;
    private static final int FRAME_BOTTOM_SIZE = 61;
    public static double distanceDivided;
    public static boolean showingThirdPartyDeleted;
    private List list;
    private MinimapWorld displayedWorld;
    private ConcurrentSkipListSet<Integer> selectedListSet;
    private GuiWaypointContainers containers;
    private GuiWaypointWorlds worlds;
    private GuiWaypointSets sets;
    private DropDownWidget containersDD;
    private DropDownWidget worldsDD;
    private DropDownWidget setsDD;
    private MinimapSession session;
    private MinimapWorldManager manager;
    private int draggingFromX;
    private int draggingFromY;
    private int draggingFromSlot;
    private Waypoint draggingWaypoint;
    private boolean displayingTeleportableWorld;
    private int shiftSelectFirst;
    private ArrayList<Waypoint> waypointsSorted;
    private ArrayList<Waypoint> thirdPartyWaypointsSorted;
    private final XaeroPath frozenAutoWorldPath;
    private Button deleteButton;
    private Button editButton;
    private Button teleportButton;
    private Button disableEnableButton;
    private Button clearButton;
    private Button shareButton;
    private EditBox filterBox;
    private String filterValue;
    private boolean filterResponderPaused;
    private final HudMod modMain;
    private boolean leftMouseButtonDown;
    private String previousDistanceText;
    private Tooltip previousDistanceTooltip;

    public GuiWaypoints(HudMod modMain, MinimapSession session, Screen par1GuiScreen, Screen escapeScreen) {
        super(par1GuiScreen, escapeScreen, (Component)Component.translatable((String)"gui.xaero_waypoints"));
        this.modMain = modMain;
        this.session = session;
        this.manager = session.getWorldManager();
        this.frozenAutoWorldPath = session.getWorldState().getAutoWorldPath();
        this.displayedWorld = this.manager.getCurrentWorld(this.frozenAutoWorldPath);
        if (this.displayedWorld == null) {
            return;
        }
        this.selectedListSet = new ConcurrentSkipListSet();
        this.draggingFromX = -1;
        this.draggingFromY = -1;
        this.draggingFromSlot = -1;
        XaeroPath currentContainer = this.displayedWorld.getContainer().getRoot().getPath();
        this.containers = new GuiWaypointContainers(modMain, this.manager, currentContainer, this.frozenAutoWorldPath);
        this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()), session, this.displayedWorld.getFullPath(), this.frozenAutoWorldPath);
        this.displayingTeleportableWorld = session.getWaypointSession().getTeleport().isWorldTeleportable(this.displayedWorld);
        this.waypointsSorted = new ArrayList();
        this.filterValue = "";
    }

    public void init() {
        super.init();
        if (this.displayedWorld == null) {
            return;
        }
        this.updateSortedList();
        this.list = new List(this);
        this.sets = new GuiWaypointSets(true, this.displayedWorld, this.displayedWorld.getCurrentWaypointSetId());
        this.deleteButton = new MyTinyButton(this.width / 2 + 129, this.height - 53, (Component)Component.translatable((String)"gui.xaero_delete"), b -> {
            Waypoint w;
            if (!this.isSomethingSelected()) {
                return;
            }
            this.undrag();
            boolean shouldRestore = true;
            for (int i : this.selectedListSet) {
                w = this.list.getWaypoint(i);
                if (w.isEffectivelyDeleted()) continue;
                shouldRestore = false;
                w.setTemporary(true);
            }
            if (shouldRestore) {
                for (int i : this.selectedListSet) {
                    w = this.list.getWaypoint(i);
                    w.setTemporary(false);
                    if (!w.isThirdParty()) continue;
                    w.setThirdPartyDeleted(false);
                }
            }
            try {
                this.session.getWorldManagerIO().saveWorld(this.displayedWorld);
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            this.session.getWorldManagerIO().getRootConfigIO().save(this.displayedWorld.getContainer().getRoot());
        });
        this.addRenderableWidget((GuiEventListener)this.deleteButton);
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.done", (Object[])new Object[0]), b -> this.goBack()).bounds(this.width / 2 - 100, this.height - 29, 200, 20).build());
        this.editButton = new MyTinyButton(this.width / 2 - 203, this.height - 53, (Component)Component.translatable((String)"gui.xaero_add_edit", (Object[])new Object[0]), b -> {
            if (!this.isAddEditEnabled()) {
                return;
            }
            ArrayList selectedWaypoints = this.getSelectedWaypointsList().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            this.minecraft.gui.setScreen((Screen)new GuiAddWaypoint(this.modMain, this.session, (Screen)this, this.escape, selectedWaypoints, this.displayedWorld.getContainer().getRoot().getPath(), this.displayedWorld, this.displayedWorld.getCurrentWaypointSetId(), selectedWaypoints.isEmpty()));
            this.list.setSelected((List.WaypointEntry)null);
        });
        this.addRenderableWidget((GuiEventListener)this.editButton);
        this.teleportButton = new MyTinyButton(this.width / 2 - 120, this.height - 53, (Component)Component.literal((String)(I18n.get((String)"gui.xaero_waypoint_teleport", (Object[])new Object[0]) + " (T)")), b -> {
            if (!this.canTeleport()) {
                return;
            }
            this.displayingTeleportableWorld = this.session.getWaypointSession().getTeleport().isWorldTeleportable(this.displayedWorld);
            this.session.getWaypointSession().getTeleport().teleportToWaypoint(this.list.getWaypoint(this.selectedListSet.first()), this.displayedWorld, (Screen)this);
        });
        this.addRenderableWidget((GuiEventListener)this.teleportButton);
        this.disableEnableButton = new MyTinyButton(this.width / 2 + 46, this.height - 53, (Component)Component.translatable((String)"gui.xaero_disable_enable", (Object[])new Object[0]), b -> {
            if (!this.isSomethingSelected()) {
                return;
            }
            ArrayList<Waypoint> selectedWaypoints = this.getSelectedWaypointsList();
            boolean effectivelyDeleted = GuiWaypoints.allWaypointsAre(selectedWaypoints, Waypoint::isEffectivelyDeleted);
            if (effectivelyDeleted) {
                for (Waypoint selected : selectedWaypoints) {
                    if (selected.isThirdParty()) {
                        selected.getThirdPartyRenderOverride().clear();
                        selected.setThirdPartyDeleted(true);
                        continue;
                    }
                    this.displayedWorld.getCurrentWaypointSet().remove(selected);
                }
                this.selectedListSet.clear();
            } else if (GuiWaypoints.allWaypointsAre(selectedWaypoints, Waypoint::isDisabled)) {
                for (Waypoint selected : selectedWaypoints) {
                    this.setDisabled(selected, false);
                }
            } else {
                for (Waypoint selected : selectedWaypoints) {
                    this.setDisabled(selected, true);
                }
            }
            this.updateSortedList();
            if (!effectivelyDeleted && (this.isFiltering() || this.displayedWorld.getRootConfig().getSortType() != WaypointsSort.NONE)) {
                this.selectWaypoints(selectedWaypoints);
            }
            try {
                this.session.getWorldManagerIO().saveWorld(this.displayedWorld);
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            this.session.getWorldManagerIO().getRootConfigIO().save(this.displayedWorld.getContainer().getRoot());
        });
        this.addRenderableWidget((GuiEventListener)this.disableEnableButton);
        this.clearButton = new MyTinyButton(this.width / 2 + 130, 32, (Component)Component.translatable((String)"gui.xaero_clear", (Object[])new Object[0]), b -> {
            XaeroPath worldKeys = (XaeroPath)this.worlds.getCurrentKey();
            String name = this.sets.getOptions()[this.sets.getCurrentSet()];
            if (this.shouldDeleteSet()) {
                this.minecraft.gui.setScreen((Screen)new GuiDeleteSet(I18n.get((String)name, (Object[])new Object[0]), worldKeys, name, (Screen)this, this.escape, this.modMain, this.session));
            } else {
                this.minecraft.gui.setScreen((Screen)new GuiClearSet(I18n.get((String)name, (Object[])new Object[0]), worldKeys, name, this, this.escape, this.modMain, this.session));
            }
        });
        this.addRenderableWidget((GuiEventListener)this.clearButton);
        this.addRenderableWidget((GuiEventListener)new MyTinyButton(this.width / 2 - 203, 32, (Component)Component.translatable((String)"gui.xaero_options", (Object[])new Object[0]), b -> this.minecraft.gui.setScreen((Screen)new GuiWaypointsOptions(this.modMain, this.session, (Screen)this, this.escape, this.displayedWorld, this.frozenAutoWorldPath))));
        this.shareButton = new MyTinyButton(this.width / 2 - 37, this.height - 53, (Component)Component.translatable((String)"gui.xaero_share", (Object[])new Object[0]), b -> {
            Waypoint selected;
            if (!this.isOneSelected()) {
                return;
            }
            Waypoint waypoint = selected = this.selectedListSet.isEmpty() ? null : this.list.getWaypoint(this.selectedListSet.first());
            if (selected != null) {
                this.session.getWaypointSession().getSharing().shareWaypoint((Screen)this, selected, this.displayedWorld);
            }
        });
        this.addRenderableWidget((GuiEventListener)this.shareButton);
        this.containersDD = this.createContainersDropdown();
        this.addWidget((GuiEventListener)this.containersDD);
        this.worldsDD = this.createWorldsDropdown();
        this.addWidget((GuiEventListener)this.worldsDD);
        this.setsDD = this.createSetsDropdown();
        this.addWidget((GuiEventListener)this.setsDD);
        this.filterBox = new EditBox(this.font, this.width / 2 - 203, 60, 75, 20, (Component)Component.translatable((String)"gui.xaero_waypoint_filter_box"));
        this.addRenderableWidget((GuiEventListener)this.filterBox);
        this.filterBox.setValue(this.filterValue);
        this.filterBox.setResponder(this::onFilterTyped);
        this.addWidget((GuiEventListener)this.list);
    }

    private void setDisabled(Waypoint waypoint, boolean value) {
        WaypointRenderInfo renderInfoDest = waypoint.getRenderInfoEditDest();
        renderInfoDest.setDisabled(value);
        if (waypoint != renderInfoDest) {
            renderInfoDest.nullEverythingMatching(waypoint);
        }
    }

    private DropDownWidget createSetsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.sets.getOptions()).setX(this.width / 2 - 100).setY(33).setW(200).setSelected(Integer.valueOf(this.sets.getCurrentSet())).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_set")).build();
    }

    private DropDownWidget createContainersDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.containers.options).setX(this.width / 2 - 202).setY(17).setW(200).setSelected(Integer.valueOf(this.containers.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_container")).build();
    }

    private DropDownWidget createWorldsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.worlds.options).setX(this.width / 2 + 2).setY(17).setW(200).setSelected(Integer.valueOf(this.worlds.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_world")).build();
    }

    private ArrayList<Waypoint> getSelectedWaypointsList() {
        return this.selectedListSet.stream().map(i -> this.list.getWaypoint((int)i)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static boolean allWaypointsAre(ArrayList<Waypoint> waypoints, Predicate<Waypoint> predicate) {
        boolean allTrue = true;
        for (Waypoint w : waypoints) {
            if (predicate.test(w)) continue;
            allTrue = false;
            break;
        }
        return allTrue;
    }

    public boolean shouldDeleteSet() {
        return !this.sets.getOptions()[this.sets.getCurrentSet()].equals("gui.xaero_default") && this.displayedWorld.getCurrentWaypointSet().isEmpty();
    }

    private void undrag() {
        this.draggingFromX = -1;
        this.draggingFromY = -1;
        this.draggingFromSlot = -1;
        this.draggingWaypoint = null;
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1) {
            this.leftMouseButtonDown = true;
        }
        if (this.openDropdown == null) {
            if (KeyMappingUtils.inputMatches((InputConstants.Type)InputConstants.Type.MOUSE, (int)event.button(), (KeyMapping)MinimapKeyMappings.WAYPOINT_MENU, (int)0)) {
                this.goBack();
                return true;
            }
            if (event.button() == 1) {
                double par1 = event.x();
                double par2 = event.y();
                if (par2 >= 58.0 && par2 < (double)(this.height - 61) && !this.isFiltering() && this.displayedWorld.getRootConfig().getSortType() == WaypointsSort.NONE) {
                    this.draggingFromX = (int)par1;
                    this.draggingFromY = (int)par2;
                    this.draggingFromSlot = this.list.getEntryAt(par1, par2);
                    if (this.draggingFromSlot >= this.displayedWorld.getCurrentWaypointSet().size()) {
                        this.draggingFromSlot = -1;
                    }
                }
            } else {
                this.list.setSelected((List.WaypointEntry)null);
            }
        }
        if (!super.mouseClicked(event, doubleClick)) {
            if (this.getFocused() == this.filterBox) {
                this.setFocused(null);
            }
            return false;
        }
        return true;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 1) {
            this.leftMouseButtonDown = false;
        }
        try {
            if (this.draggingWaypoint != null) {
                this.session.getWorldManagerIO().saveWorld(this.displayedWorld);
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.undrag();
        if (!super.mouseReleased(event)) {
            return this.list.mouseReleased(event);
        }
        return true;
    }

    public boolean keyReleased(KeyEvent event) {
        if (this.filterBox.isFocused()) {
            return super.keyReleased(event);
        }
        switch (event.key()) {
            case 76: {
                if (this.disableEnableButton.active) {
                    for (int i : this.selectedListSet) {
                        this.list.getWaypoint(i).setTemporary(true);
                    }
                    this.disableEnableButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
                }
                return true;
            }
            case 23: {
                if (this.teleportButton.active) {
                    this.teleportButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
                }
                return true;
            }
        }
        return super.keyReleased(event);
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractBackground(guiGraphics, par1, par2, par3);
        this.list.extractRenderState(guiGraphics, par1, par2, par3);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        boolean renderingFilterBoxHint;
        if (this.displayedWorld == null) {
            this.minecraft.gui.setScreen(this.parent);
            return;
        }
        if (this.minecraft.player == null) {
            this.minecraft.gui.setScreen(null);
            return;
        }
        this.updateButtons();
        boolean bl = renderingFilterBoxHint = !this.filterBox.isFocused() && this.filterBox.getValue().isEmpty();
        if (renderingFilterBoxHint) {
            this.filterResponderPaused = true;
            GuiUtils.setFieldText((EditBox)this.filterBox, (String)Component.translatable((String)"gui.xaero_waypoint_filter_box_suggestion").getString(), (int)-11184811);
            this.filterBox.moveCursorTo(0, false);
            this.filterResponderPaused = false;
        }
        super.extractRenderState(guiGraphics, par1, par2, par3);
        if (renderingFilterBoxHint) {
            this.filterResponderPaused = true;
            GuiUtils.setFieldText((EditBox)this.filterBox, (String)"", (int)-1);
            this.filterResponderPaused = false;
        }
        this.renderHoveredWaypointDistance(guiGraphics, par1, par2);
    }

    private void renderHoveredWaypointDistance(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        boolean configValue = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_DISTANCE_IN_MENU);
        if (!configValue) {
            return;
        }
        int hoveredEntry = this.list.getEntryAt(mouseX, mouseY);
        if (hoveredEntry == -1 || this.leftMouseButtonDown) {
            return;
        }
        Entity renderEntity = this.minecraft.getCameraEntity();
        if (renderEntity == null) {
            return;
        }
        float partialTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (this.minecraft.isPaused()) {
            partialTicks = 1.0f;
        }
        Waypoint hoveredWaypoint = this.list.getWaypoint(hoveredEntry);
        double waypointCoordinateScale = this.session.getDimensionHelper().getDimCoordinateScale(this.displayedWorld);
        String distanceText = WaypointUtils.getDistanceTextForCurrentWorld(hoveredWaypoint, waypointCoordinateScale, EntityUtils.getEntityX(renderEntity, partialTicks), EntityUtils.getEntityY(renderEntity, partialTicks), EntityUtils.getEntityZ(renderEntity, partialTicks));
        Tooltip distanceTooltip = this.previousDistanceTooltip;
        if (!distanceText.equals(this.previousDistanceText)) {
            distanceTooltip = new Tooltip(distanceText);
            this.previousDistanceText = distanceText;
            this.previousDistanceTooltip = distanceTooltip;
        }
        if (this.openDropdown != null && this.openDropdown.isHovered()) {
            return;
        }
        distanceTooltip.drawBox(guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    protected void renderPreDropdown(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_world_server", (Object[])new Object[0]), this.width / 2 - 102, 5, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_subworld_dimension", (Object[])new Object[0]), this.width / 2 + 102, 5, -1);
        if (this.draggingFromSlot != -1) {
            int distance = (int)Math.sqrt(Math.pow(mouseX - this.draggingFromX, 2.0) + Math.pow(mouseY - this.draggingFromY, 2.0));
            int toSlot = Math.min(this.displayedWorld.getCurrentWaypointSet().size() - 1, this.list.getEntryAt(mouseX, mouseY));
            if (distance > 4 && this.draggingWaypoint == null) {
                this.draggingWaypoint = this.displayedWorld.getCurrentWaypointSet().get(this.draggingFromSlot);
                this.list.setSelected((List.WaypointEntry)null);
            }
            if (this.draggingWaypoint != null && this.draggingFromSlot != toSlot && toSlot != -1) {
                int direction = toSlot > this.draggingFromSlot ? 1 : -1;
                for (int i = this.draggingFromSlot; i != toSlot; i += direction) {
                    this.displayedWorld.getCurrentWaypointSet().set(i, this.displayedWorld.getCurrentWaypointSet().get(i + direction));
                }
                this.displayedWorld.getCurrentWaypointSet().set(toSlot, this.draggingWaypoint);
                this.draggingFromSlot = toSlot;
                this.updateSortedList();
            }
            int fromCenter = this.draggingFromX - this.list.getWidth() / 2;
            this.list.drawWaypointSlot(guiGraphics, this.draggingWaypoint, mouseX - 108 - fromCenter, mouseY - this.list.getItemHeight() / 4);
        }
    }

    private void updateButtons() {
        this.deleteButton.active = this.disableEnableButton.active = this.isSomethingSelected();
        this.shareButton.active = this.isOneSelected();
        this.teleportButton.active = this.canTeleport();
        this.editButton.active = this.isAddEditEnabled();
        this.clearButton.setMessage((Component)Component.translatable((String)(this.shouldDeleteSet() ? "gui.xaero_delete_set" : "gui.xaero_clear"), (Object[])new Object[0]));
        ArrayList<Waypoint> selectedWaypointsList = this.getSelectedWaypointsList();
        if (this.isSomethingSelected() && GuiWaypoints.allWaypointsAre(selectedWaypointsList, Waypoint::isEffectivelyDeleted)) {
            this.disableEnableButton.setMessage((Component)Component.translatable((String)"gui.xaero_delete"));
            this.disableEnableButton.active = !GuiWaypoints.allWaypointsAre(selectedWaypointsList, Waypoint::isThirdPartyDeleted);
            this.deleteButton.setMessage((Component)Component.translatable((String)"gui.xaero_restore"));
        } else {
            this.deleteButton.setMessage((Component)Component.translatable((String)"gui.xaero_delete"));
            String[] enabledisable = I18n.get((String)"gui.xaero_disable_enable", (Object[])new Object[0]).split("/");
            this.disableEnableButton.setMessage((Component)Component.literal((String)enabledisable[!GuiWaypoints.allWaypointsAre(selectedWaypointsList, Waypoint::isDisabled) ? 0 : 1]));
        }
    }

    private boolean isAddEditEnabled() {
        return true;
    }

    private boolean isSomethingSelected() {
        return !this.selectedListSet.isEmpty();
    }

    private boolean isOneSelected() {
        return this.selectedListSet.size() == 1;
    }

    public void clearSelection() {
        this.selectedListSet.clear();
    }

    private boolean canTeleport() {
        if (!this.isOneSelected()) {
            return false;
        }
        if (!this.displayedWorld.getRootConfig().isTeleportationEnabled()) {
            return false;
        }
        if (this.displayingTeleportableWorld) {
            return true;
        }
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        SingleConfigManager primaryConfigManager = configManager.getPrimaryConfigManager();
        return (Boolean)primaryConfigManager.getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.WRONG_WORLD_TELEPORT);
    }

    public boolean onSelected(DropDownWidget menu, int selectedIndex) {
        this.clearFilter(false);
        if (menu == this.containersDD || menu == this.worldsDD) {
            if (menu == this.containersDD) {
                this.containers.current = selectedIndex;
                if (this.containers.current != this.containers.auto) {
                    MinimapWorld firstWorld = this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()).getFirstWorld();
                    this.session.getWorldState().setCustomWorldPath(firstWorld.getFullPath());
                } else {
                    this.session.getWorldState().setCustomWorldPath(null);
                }
                this.displayedWorld = this.manager.getCurrentWorld(this.frozenAutoWorldPath);
                this.updateSortedList();
                this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()), this.session, this.displayedWorld.getFullPath(), this.frozenAutoWorldPath);
                this.worldsDD = this.createWorldsDropdown();
                this.replaceWidget((AbstractWidget)this.worldsDD, (AbstractWidget)this.worldsDD);
            } else {
                this.worlds.current = selectedIndex;
                if (this.worlds.current != this.worlds.auto) {
                    XaeroPath selectedWorldPath = (XaeroPath)this.worlds.getCurrentKey();
                    this.session.getWorldState().setCustomWorldPath(selectedWorldPath);
                } else {
                    this.session.getWorldState().setCustomWorldPath(null);
                }
                this.displayedWorld = this.manager.getCurrentWorld(this.frozenAutoWorldPath);
                this.updateSortedList();
            }
            this.displayingTeleportableWorld = this.session.getWaypointSession().getTeleport().isWorldTeleportable(this.displayedWorld);
            this.list.setSelected((List.WaypointEntry)null);
            this.sets = new GuiWaypointSets(true, this.displayedWorld, this.displayedWorld.getCurrentWaypointSetId());
            this.setsDD = this.createSetsDropdown();
            this.replaceWidget((AbstractWidget)this.setsDD, (AbstractWidget)this.setsDD);
            return true;
        }
        if (menu == this.setsDD) {
            this.list.setSelected((List.WaypointEntry)null);
            if (selectedIndex == menu.size() - 1) {
                MinimapLogs.LOGGER.info("New waypoint set gui");
                this.minecraft.gui.setScreen((Screen)new GuiNewSet(this.modMain, this.session, (Screen)this, this.escape, this.displayedWorld));
                return false;
            }
            this.sets.setCurrentSet(selectedIndex);
            this.displayedWorld.setCurrentWaypointSetId(this.sets.getCurrentSetKey());
            this.updateSortedList();
            try {
                this.session.getWorldManagerIO().saveWorld(this.displayedWorld);
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            return true;
        }
        return false;
    }

    private void updateSortedList() {
        WaypointsSort sortType = this.displayedWorld.getRootConfig().getSortType();
        this.waypointsSorted = new ArrayList();
        this.thirdPartyWaypointsSorted = new ArrayList();
        ThirdPartyWaypointManager thirdPartyWaypointManager = this.displayedWorld.getContainer().getThirdPartyWaypointManager();
        if (sortType == WaypointsSort.NONE && !this.isFiltering()) {
            for (Waypoint waypoint : this.displayedWorld.getCurrentWaypointSet().getWaypoints()) {
                this.waypointsSorted.add(waypoint);
            }
            if ("gui.xaero_default".equals(this.displayedWorld.getCurrentWaypointSetId())) {
                for (ThirdPartyWaypoints thirdPartyWaypoints : thirdPartyWaypointManager.getAll()) {
                    if (!thirdPartyWaypoints.isEnabled()) continue;
                    for (Waypoint waypoint : thirdPartyWaypoints.getWaypoints().values()) {
                        if (!showingThirdPartyDeleted && waypoint.isThirdPartyDeleted()) continue;
                        this.thirdPartyWaypointsSorted.add(waypoint);
                    }
                }
            }
            return;
        }
        distanceDivided = this.session.getDimensionHelper().getDimensionDivision(this.displayedWorld);
        boolean reversed = this.displayedWorld.getRootConfig().isSortReversed();
        ArrayList<KeySortableByOther> sortableKeys = new ArrayList<KeySortableByOther>();
        Camera camera = this.minecraft.gameRenderer.mainCamera();
        for (Waypoint w : this.displayedWorld.getCurrentWaypointSet().getWaypoints()) {
            int filterMatch = this.getWaypointFilterMatch(w);
            if (filterMatch == -1) continue;
            Comparable<?> sortObject = this.getSortObject(w, filterMatch, camera, sortType);
            sortableKeys.add(new KeySortableByOther((Object)w, new Comparable[]{Boolean.valueOf(reversed != w.isDisabled()), sortObject}));
        }
        Collections.sort(sortableKeys);
        for (KeySortableByOther k : sortableKeys) {
            this.waypointsSorted.add((Waypoint)k.getKey());
        }
        if (reversed) {
            Collections.reverse(this.waypointsSorted);
        }
        if (!"gui.xaero_default".equals(this.displayedWorld.getCurrentWaypointSetId())) {
            return;
        }
        for (ThirdPartyWaypoints thirdPartyWaypoints : thirdPartyWaypointManager.getAll()) {
            if (!thirdPartyWaypoints.isEnabled()) continue;
            sortableKeys.clear();
            for (Waypoint w : thirdPartyWaypoints.getWaypoints().values()) {
                int filterMatch;
                if (!showingThirdPartyDeleted && w.isThirdPartyDeleted() || (filterMatch = this.getWaypointFilterMatch(w)) == -1) continue;
                Comparable<?> sortObject = this.getSortObject(w, filterMatch, camera, sortType);
                sortableKeys.add(new KeySortableByOther((Object)w, new Comparable[]{Boolean.valueOf(reversed != w.isDisabled()), sortObject}));
            }
            Collections.sort(sortableKeys);
            if (reversed) {
                Collections.reverse(sortableKeys);
            }
            for (KeySortableByOther k : sortableKeys) {
                this.thirdPartyWaypointsSorted.add((Waypoint)k.getKey());
            }
        }
    }

    private Comparable<?> getSortObject(Waypoint w, int filterMatch, Camera camera, WaypointsSort sortType) {
        if (this.isFiltering()) {
            return filterMatch;
        }
        if (sortType == WaypointsSort.COLOR) {
            return w.getWaypointColor();
        }
        if (sortType == WaypointsSort.ANGLE) {
            return -w.getComparisonAngleCos(camera, distanceDivided);
        }
        if (sortType == WaypointsSort.NAME) {
            return w.getComparisonName();
        }
        if (sortType == WaypointsSort.SYMBOL) {
            return w.getInitials();
        }
        return w.getComparisonDistance(camera, distanceDivided);
    }

    private boolean isFiltering() {
        return this.filterValue != null && !this.filterValue.isEmpty();
    }

    private int getWaypointFilterMatch(Waypoint w) {
        String lowerCaseFilter;
        if (this.filterValue == null || this.filterValue.isEmpty()) {
            return 0;
        }
        String localizedName = w.getLocalizedName();
        if (localizedName == null) {
            return -1;
        }
        int filterValuePos = localizedName.indexOf(this.filterValue);
        if (filterValuePos == 0) {
            return 0;
        }
        String lowerCaseName = localizedName.toLowerCase();
        int caselessFilterValuePos = lowerCaseName.indexOf(lowerCaseFilter = this.filterValue.toLowerCase());
        if (caselessFilterValuePos == 0) {
            return 1;
        }
        if (filterValuePos != -1) {
            return 2 + filterValuePos;
        }
        if (caselessFilterValuePos != -1) {
            return 1000000 + caselessFilterValuePos;
        }
        return -1;
    }

    private void onFilterTyped(String s) {
        if (this.filterResponderPaused) {
            return;
        }
        if (s.equals(this.filterValue)) {
            return;
        }
        this.filterValue = s;
        this.updateSortedList();
        this.list.setScrollAmount(0.0);
        this.selectedListSet.clear();
    }

    public boolean keyPressed(KeyEvent event) {
        int par1 = event.key();
        if (!super.keyPressed(event)) {
            if (this.filterBox.isFocused()) {
                return false;
            }
            if (KeyMappingUtils.inputMatches((InputConstants.Type)InputConstants.Type.KEYBOARD, (int)par1, (KeyMapping)MinimapKeyMappings.WAYPOINT_MENU, (int)0)) {
                this.goBack();
                return true;
            }
            return false;
        }
        return true;
    }

    public void clearFilter(boolean callResponder) {
        this.filterValue = "";
        if (!callResponder) {
            this.filterResponderPaused = true;
        }
        this.filterBox.setValue(this.filterValue);
        if (!callResponder) {
            this.filterResponderPaused = false;
        }
    }

    private void selectWaypoints(ArrayList<Waypoint> ws) {
        this.selectedListSet.clear();
        boolean scrolled = false;
        for (Waypoint selectedWaypoint : ws) {
            int index = selectedWaypoint.isThirdParty() ? this.waypointsSorted.size() + this.thirdPartyWaypointsSorted.indexOf(selectedWaypoint) : this.waypointsSorted.indexOf(selectedWaypoint);
            this.selectedListSet.add(index);
            if (scrolled) continue;
            this.list.ensureVisible(index);
            scrolled = true;
        }
    }

    class List
    extends ObjectSelectionList<WaypointEntry> {
        private int createdCount;
        final /* synthetic */ GuiWaypoints this$0;

        public List(GuiWaypoints this$0) {
            GuiWaypoints guiWaypoints = this$0;
            Objects.requireNonNull(guiWaypoints);
            this.this$0 = guiWaypoints;
            super(this$0.minecraft, this$0.width, Math.max(4, this$0.height - 61 - 58), 58, 18);
            this.createEntries(this.getWaypointCount());
        }

        private int getThirdPartyCount() {
            return this.this$0.displayedWorld.getContainer().getThirdPartyWaypointManager().getCount();
        }

        private Waypoint getThirdPartyWaypoint(int index) {
            return this.this$0.thirdPartyWaypointsSorted.get(index);
        }

        protected int getWaypointCount() {
            int size = this.this$0.waypointsSorted.size();
            return size += this.this$0.thirdPartyWaypointsSorted.size();
        }

        private Waypoint getWaypoint(int slotIndex) {
            if (slotIndex < this.this$0.waypointsSorted.size()) {
                return this.this$0.waypointsSorted.get(slotIndex);
            }
            int thirdPartyWPIndex = slotIndex - this.this$0.waypointsSorted.size();
            if (thirdPartyWPIndex < this.getThirdPartyCount()) {
                return this.getThirdPartyWaypoint(thirdPartyWPIndex);
            }
            return null;
        }

        protected boolean entriesCanBeSelected() {
            return false;
        }

        protected boolean isSelectedItem(int p_148131_1_) {
            return !this.this$0.selectedListSet.isEmpty() && this.this$0.selectedListSet.contains(p_148131_1_);
        }

        private void createEntries(int count) {
            this.clearEntries();
            this.createdCount = count;
            for (int i = 0; i < count; ++i) {
                WaypointEntry entry = new WaypointEntry(this, i);
                this.addEntry((AbstractSelectionList.Entry)entry);
            }
        }

        public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
            int currentCount = this.getWaypointCount();
            if (currentCount != this.createdCount) {
                this.createEntries(currentCount);
                this.setScrollAmount(this.scrollAmount());
            }
            super.extractWidgetRenderState(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
        }

        protected void extractItem(GuiGraphicsExtractor $$0, int mouseX, int mouseY, float partialTicks, WaypointEntry entry) {
            if (this.isSelectedItem(entry.index)) {
                int selectionColor = this.isFocused() ? -1 : -8355712;
                this.extractSelection($$0, (AbstractSelectionList.Entry)entry, selectionColor);
            }
            super.extractItem($$0, mouseX, mouseY, partialTicks, (AbstractSelectionList.Entry)entry);
        }

        public boolean isFocused() {
            if (this.this$0.openDropdown != null || this.this$0.draggingWaypoint != null) {
                return false;
            }
            return this.this$0.getFocused() == this;
        }

        public void setSelected(WaypointEntry e) {
            if (e == null) {
                this.this$0.selectedListSet.clear();
                this.this$0.shiftSelectFirst = -1;
                return;
            }
            Waypoint waypoint = this.getWaypoint(e.index);
            int currentSize = this.this$0.selectedListSet.size();
            boolean shiftPressed = ScreenBase.hasShiftDown();
            if ((currentSize > 1 || currentSize == 1 && this.this$0.selectedListSet.first() != e.index) && !ScreenBase.hasControlDown() && !shiftPressed) {
                this.this$0.selectedListSet.clear();
            }
            if (currentSize > 0 && shiftPressed) {
                int direction = e.index > this.this$0.shiftSelectFirst ? 1 : -1;
                this.this$0.selectedListSet.clear();
                for (int i = this.this$0.shiftSelectFirst; i != e.index + direction; i += direction) {
                    this.this$0.selectedListSet.add(i);
                }
            } else if (this.this$0.selectedListSet.contains(e.index)) {
                this.this$0.selectedListSet.remove(e.index);
            } else {
                this.this$0.shiftSelectFirst = e.index;
                this.this$0.selectedListSet.add(e.index);
            }
            super.setSelected((AbstractSelectionList.Entry)(this.this$0.selectedListSet.isEmpty() ? null : e));
        }

        public int getItemHeight() {
            return this.defaultEntryHeight;
        }

        public void drawWaypointSlot(GuiGraphicsExtractor guiGraphics, Waypoint w, int p_180791_2_, int p_180791_3_) {
            if (w == null) {
                return;
            }
            String stateIndicator = w.isThirdPartyDeleted() ? " \u00a74" + I18n.get((String)"gui.xaero_deleted", (Object[])new Object[0]) : (w.isDisabled() ? " \u00a74" + I18n.get((String)"gui.xaero_disabled", (Object[])new Object[0]) : (w.isTemporary() ? " \u00a74" + I18n.get((String)"gui.xaero_temporary", (Object[])new Object[0]) : ""));
            guiGraphics.centeredText(this.this$0.font, w.getLocalizedName() + stateIndicator, p_180791_2_ + 110, p_180791_3_ + 1, -1);
            int rectX = p_180791_2_ + 8 + 4;
            int rectY = p_180791_3_ + 6;
            if (w.isGlobal()) {
                guiGraphics.centeredText(this.this$0.font, "*", rectX - 25, rectY - 3, -1);
            }
            if (w.isThirdParty()) {
                guiGraphics.text(this.this$0.font, (Component)Component.literal((String)w.getThirdPartyOrigin().toString()).withStyle(ChatFormatting.DARK_GRAY), p_180791_2_ + 250, p_180791_3_ + 1, -1);
            }
            int opacity = 90;
            this.this$0.modMain.getMinimap().getWaypointMapRenderer().drawIconGUI(guiGraphics, w, rectX, rectY, opacity);
        }

        public int getEntryAt(double x, double y) {
            WaypointEntry entry = (WaypointEntry)this.getEntryAtPosition(x, y);
            return entry == null ? -1 : entry.index;
        }

        public void ensureVisible(int index) {
            this.scrollToEntry((AbstractSelectionList.Entry)((WaypointEntry)((Object)this.children().get(index))));
        }

        public void recreateEntries() {
            this.createEntries(this.getWaypointCount());
        }

        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (this.this$0.getFocused() == this.this$0.filterBox) {
                this.this$0.setFocused(null);
            }
            return super.mouseClicked(event, doubleClick);
        }

        public class WaypointEntry
        extends ObjectSelectionList.Entry<WaypointEntry> {
            private int index;
            final /* synthetic */ List this$1;

            public WaypointEntry(List this$1, int index) {
                List list = this$1;
                Objects.requireNonNull(list);
                this.this$1 = list;
                this.index = index;
            }

            public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
                Waypoint w = this.this$1.getWaypoint(this.index);
                if (w == this.this$1.this$0.draggingWaypoint) {
                    return;
                }
                this.this$1.drawWaypointSlot(guiGraphics, w, this.getX(), this.getY() + 2);
            }

            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                return event.button() == 1;
            }

            public Component getNarration() {
                Waypoint w = this.this$1.getWaypoint(this.index);
                Object narration = "";
                if (w != null) {
                    String stateIndicators = (String)(w.isThirdPartyDeleted() ? " " + I18n.get((String)"gui.xaero_deleted", (Object[])new Object[0]) : "") + (String)(w.isDisabled() ? " " + I18n.get((String)"gui.xaero_disabled", (Object[])new Object[0]) : "") + (String)(w.isTemporary() ? " " + I18n.get((String)"gui.xaero_temporary", (Object[])new Object[0]) : "");
                    narration = (String)narration + I18n.get((String)"narrator.select", (Object[])new Object[]{w.getName()}) + stateIndicators + ", ";
                }
                if (this.this$1.this$0.selectedListSet.size() != 1) {
                    narration = (String)narration + I18n.get((String)"narrator.select", (Object[])new Object[]{I18n.get((String)"gui.xaero_waypoints", (Object[])new Object[0]) + " " + this.this$1.this$0.selectedListSet.size()});
                }
                return Component.literal((String)narration);
            }
        }
    }
}

