/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import xaero.common.gui.GuiDropdownHelper;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.lib.common.util.KeySortableByOther;

public class GuiWaypointWorlds
extends GuiDropdownHelper<XaeroPath> {
    public GuiWaypointWorlds(MinimapWorldRootContainer root, MinimapSession session, XaeroPath currentWorldPath, XaeroPath autoWorldPath) {
        HashMap<XaeroPath, String> nameMap = new HashMap<XaeroPath, String>();
        XaeroPath autoContainer = autoWorldPath == null ? null : autoWorldPath.getParent();
        boolean connections = autoContainer != null && MinimapWorldContainerUtil.isMultiplayer(autoContainer) && root.getPath().equals(session.getWorldState().getAutoRootContainerPath());
        MinimapWorld autoWorldObj = autoWorldPath == null ? null : session.getWorldManager().getWorld(autoWorldPath);
        ArrayList<KeySortableByOther<XaeroPath>> sortableKeyList = new ArrayList<KeySortableByOther<XaeroPath>>();
        this.addWorlds(root, root, autoWorldObj, autoWorldPath, sortableKeyList, nameMap, connections);
        Collections.sort(sortableKeyList);
        this.current = -1;
        this.auto = -1;
        ArrayList<XaeroPath> keyList = new ArrayList<XaeroPath>();
        ArrayList<String> optionList = new ArrayList<String>();
        for (int j = 0; j < sortableKeyList.size(); ++j) {
            KeySortableByOther keySortable = (KeySortableByOther)sortableKeyList.get(j);
            XaeroPath key = (XaeroPath)keySortable.getKey();
            if (this.current == -1 && key.equals(currentWorldPath)) {
                this.current = j;
            }
            Object option = "Error";
            try {
                if (this.auto == -1 && key.equals(autoWorldPath)) {
                    this.auto = j;
                }
                option = (String)nameMap.get(key);
                if (this.auto == j) {
                    option = (String)option + " (auto)";
                }
            }
            catch (Exception e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            keyList.add(key);
            optionList.add((String)option);
        }
        if (this.current == -1) {
            this.current = 0;
        }
        this.keys = keyList.toArray(new XaeroPath[0]);
        this.options = optionList.toArray(new String[0]);
    }

    private void addWorlds(MinimapWorldRootContainer root, MinimapWorldContainer container, MinimapWorld autoWorld, XaeroPath autoWorldPath, List<KeySortableByOther<XaeroPath>> sortableKeyList, Map<XaeroPath, String> nameMap, boolean connections) {
        String containerName = container.getSubName();
        for (MinimapWorld world : container.getWorlds()) {
            String worldNode = world.getNode();
            Object worldName = container.getFullWorldName(worldNode, containerName);
            XaeroPath fullKey = world.getFullPath();
            int firstNameWordAsInt = 0;
            int firstNameSpace = ((String)worldName).indexOf(32);
            if (firstNameSpace != -1) {
                String firstNameWord = ((String)worldName).substring(0, firstNameSpace);
                try {
                    firstNameWordAsInt = Integer.parseInt(firstNameWord);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            boolean connected = false;
            if (connections && root.getSubWorldConnections().isConnected(autoWorld, world)) {
                connected = true;
                if (!fullKey.equals(autoWorldPath)) {
                    worldName = (String)worldName + " *";
                }
            }
            sortableKeyList.add((KeySortableByOther<XaeroPath>)new KeySortableByOther((Object)fullKey, new Comparable[]{Boolean.valueOf(!connected), containerName.toLowerCase(), Integer.valueOf(firstNameWordAsInt), ((String)worldName).toLowerCase()}));
            nameMap.put(fullKey, (String)worldName);
        }
        for (MinimapWorldContainer subContainer : container.getSubContainers()) {
            this.addWorlds(root, subContainer, autoWorld, autoWorldPath, sortableKeyList, nameMap, connections);
        }
    }
}

