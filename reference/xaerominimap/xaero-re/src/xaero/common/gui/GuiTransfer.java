/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiWaypointContainers;
import xaero.common.gui.GuiWaypointWorlds;
import xaero.common.gui.GuiWaypoints;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.client.gui.widget.dropdown.IDropDownWidgetCallback;

public class GuiTransfer
extends ScreenBase
implements IDropDownWidgetCallback {
    private MySmallButton transferButton;
    private GuiWaypointContainers containers1;
    private GuiWaypointWorlds worlds1;
    private GuiWaypointContainers containers2;
    private GuiWaypointWorlds worlds2;
    private DropDownWidget containers1DD;
    private DropDownWidget worlds1DD;
    private DropDownWidget containers2DD;
    private DropDownWidget worlds2DD;
    private MinimapSession session;
    private MinimapWorldManager manager;
    private final XaeroPath frozenAutoWorldPath;
    private boolean dropped = false;
    private final IXaeroMinimap modMain;

    public GuiTransfer(IXaeroMinimap modMain, MinimapSession session, Screen par1, Screen escapeScreen) {
        super(par1, escapeScreen, (Component)Component.translatable((String)"gui.xaero_transfer_all"));
        this.modMain = modMain;
        this.session = session;
        this.manager = session.getWorldManager();
        this.frozenAutoWorldPath = session.getWorldState().getAutoWorldPath();
        XaeroPath currentWorldPath = session.getWorldState().getCurrentWorldPath(this.frozenAutoWorldPath);
        XaeroPath currentContainerPath = currentWorldPath.getRoot();
        this.containers1 = new GuiWaypointContainers((HudMod)modMain, this.manager, currentContainerPath, this.frozenAutoWorldPath);
        this.containers2 = new GuiWaypointContainers((HudMod)modMain, this.manager, currentContainerPath, this.frozenAutoWorldPath);
        this.worlds1 = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers1.getCurrentKey()), session, currentWorldPath, this.frozenAutoWorldPath);
        this.worlds2 = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers2.getCurrentKey()), session, currentWorldPath, this.frozenAutoWorldPath);
    }

    public void init() {
        super.init();
        this.transferButton = new MySmallButton(5, this.width / 2 - 155, this.height / 7 + 120, (Component)Component.translatable((String)"gui.xaero_transfer", (Object[])new Object[0]), b -> this.transfer());
        this.addRenderableWidget((GuiEventListener)this.transferButton);
        this.transferButton.active = false;
        this.addRenderableWidget((GuiEventListener)new MySmallButton(6, this.width / 2 + 5, this.height / 7 + 120, (Component)Component.translatable((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.openParent()));
        this.worlds1DD = this.createWorlds1DD();
        this.addWidget((GuiEventListener)this.worlds1DD);
        this.worlds2DD = this.createWorlds2DD();
        this.addWidget((GuiEventListener)this.worlds2DD);
        this.containers1DD = this.createContainers1DD();
        this.addWidget((GuiEventListener)this.containers1DD);
        this.containers2DD = this.createContainers2DD();
        this.addWidget((GuiEventListener)this.containers2DD);
    }

    private DropDownWidget createWorlds1DD() {
        return DropDownWidget.Builder.begin().setOptions(this.worlds1.options).setX(this.width / 2 + 2).setY(this.height / 7 + 20).setW(200).setSelected(Integer.valueOf(this.worlds1.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_transfer_worlds1")).build();
    }

    private DropDownWidget createWorlds2DD() {
        return DropDownWidget.Builder.begin().setOptions(this.worlds2.options).setX(this.width / 2 + 2).setY(this.height / 7 + 50).setW(200).setSelected(Integer.valueOf(this.worlds2.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_transfer_worlds2")).build();
    }

    private DropDownWidget createContainers1DD() {
        return DropDownWidget.Builder.begin().setOptions(this.containers1.options).setX(this.width / 2 - 202).setY(this.height / 7 + 20).setW(200).setSelected(Integer.valueOf(this.containers1.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_transfer_containers1")).build();
    }

    private DropDownWidget createContainers2DD() {
        return DropDownWidget.Builder.begin().setOptions(this.containers2.options).setX(this.width / 2 - 202).setY(this.height / 7 + 50).setW(200).setSelected(Integer.valueOf(this.containers2.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_transfer_containers2")).build();
    }

    private void openParent() {
        if (this.parent instanceof GuiWaypoints) {
            this.minecraft.gui.setScreen((Screen)new GuiWaypoints((HudMod)this.modMain, this.session, ((GuiWaypoints)this.parent).parent, this.escape));
        } else {
            this.goBack();
        }
    }

    public void transfer() {
        try {
            XaeroPath keys1 = (XaeroPath)this.worlds1.getCurrentKey();
            XaeroPath keys2 = (XaeroPath)this.worlds2.getCurrentKey();
            MinimapWorld from = this.manager.getWorld(keys1);
            MinimapWorld to = this.manager.getWorld(keys2);
            for (WaypointSet fromSet : from.getIterableWaypointSets()) {
                WaypointSet toSet = to.getWaypointSet(fromSet.getName());
                if (toSet == null) {
                    toSet = WaypointSet.Builder.begin().setName(fromSet.getName()).build();
                }
                for (Waypoint w : fromSet.getWaypoints()) {
                    Waypoint copy = new Waypoint(w.getX(), w.getY(), w.getZ(), w.getName(), w.getInitials(), w.getWaypointColor(), w.getPurpose(), w.isTemporary(), w.isYIncluded());
                    copy.setRotation(w.isRotation());
                    copy.setDisabled(w.isDisabled());
                    copy.setYaw(w.getYaw());
                    copy.setVisibility(w.getVisibility());
                    toSet.add(copy);
                }
                to.addWaypointSet(toSet);
            }
            if (keys2 != null) {
                this.session.getWorldState().setCustomWorldPath(keys2);
            }
            this.openParent();
            this.session.getWorldManagerIO().saveWorld(to);
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    protected void renderPreDropdown(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 5, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_from", (Object[])new Object[0]).replace("\u00a7\u00a7", ":") + ":", this.width / 2, this.height / 7 + 10, -1);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_to", (Object[])new Object[0]).replace("\u00a7\u00a7", ":") + ":", this.width / 2, this.height / 7 + 40, -1);
    }

    public boolean onSelected(DropDownWidget menu, int selected) {
        if (menu == this.containers1DD) {
            this.containers1.current = selected;
            this.worlds1 = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers1.getCurrentKey()), this.session, this.session.getWorldState().getCurrentWorldPath(this.frozenAutoWorldPath), this.frozenAutoWorldPath);
            this.worlds1DD = this.createWorlds1DD();
            this.replaceWidget((AbstractWidget)this.worlds1DD, (AbstractWidget)this.worlds1DD);
        } else if (menu == this.containers2DD) {
            this.containers2.current = selected;
            this.worlds2 = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers2.getCurrentKey()), this.session, this.session.getWorldState().getCurrentWorldPath(this.frozenAutoWorldPath), this.frozenAutoWorldPath);
            this.worlds2DD = this.createWorlds2DD();
            this.replaceWidget((AbstractWidget)this.worlds2DD, (AbstractWidget)this.worlds2DD);
        } else if (menu == this.worlds1DD) {
            this.worlds1.current = selected;
        } else if (menu == this.worlds2DD) {
            this.worlds2.current = selected;
        }
        this.transferButton.active = this.containers1.current != this.containers2.current || this.worlds1.current != this.worlds2.current;
        return true;
    }
}

