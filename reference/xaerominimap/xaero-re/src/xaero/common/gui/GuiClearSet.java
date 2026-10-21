/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiWaypoints;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.path.XaeroPath;

public class GuiClearSet
extends ConfirmScreen {
    public GuiClearSet(String setName, XaeroPath worldPath, String name, GuiWaypoints parent, Screen escapeScreen, IXaeroMinimap modMain, MinimapSession session) {
        super(result -> GuiClearSet.confirmClearSet(result, worldPath, name, parent, escapeScreen, modMain, session), (Component)Component.literal((String)(I18n.get((String)"gui.xaero_clear_set_message", (Object[])new Object[0]) + ": " + setName.replace("\u00a7\u00a7", ":") + "?")), (Component)Component.translatable((String)"gui.xaero_clear_set_message2"));
    }

    private static void confirmClearSet(boolean p_confirmResult_1_, XaeroPath worldPath, String name, GuiWaypoints parent, Screen escapeScreen, IXaeroMinimap modMain, MinimapSession session) {
        if (p_confirmResult_1_) {
            WaypointSet set = session.getWorldManager().getWorld(worldPath).getWaypointSet(name);
            if (set != null) {
                set.clear();
            }
            try {
                session.getWorldManagerIO().saveWorld(session.getWorldManager().getWorld(worldPath));
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
        Minecraft.getInstance().gui.setScreen((Screen)new GuiWaypoints((HudMod)modMain, session, parent.parent, escapeScreen));
    }
}

