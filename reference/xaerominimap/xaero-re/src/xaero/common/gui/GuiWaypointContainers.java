/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import xaero.common.HudMod;
import xaero.common.gui.GuiDropdownHelper;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.util.KeySortableByOther;

public class GuiWaypointContainers
extends GuiDropdownHelper<String> {
    public GuiWaypointContainers(HudMod modMain, MinimapWorldManager manager, XaeroPath currentContainer, XaeroPath autoWorldPath) {
        ArrayList<KeySortableByOther> sortableKeyList = new ArrayList<KeySortableByOther>();
        ClientConfigManager configManager = modMain.getHudConfigs().getClientConfigManager();
        int hideWorldNamesConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.HIDE_WORLD_NAMES);
        for (MinimapWorldRootContainer rootContainer : manager.getRootContainers()) {
            String rootContainerNode = rootContainer.getPath().getLastNode();
            String[] details = rootContainerNode.split("_");
            Object sortName = details.length > 1 && details[0].equals("Realms") ? "Realm ID " + details[1].substring(details[1].indexOf(".") + 1) : details[details.length - 1].replace("%us%", "_").replace("%fs%", "/").replace("%bs%", "\\").replace("\u00a7", ":").replace("%lb%", "[").replace("%rb%", "]").replace(",", ".");
            if (hideWorldNamesConfig == 1 && details.length > 1 && details[0].equals("Multiplayer")) {
                String[] dotSplit = ((String)sortName).split("(\\.|:+)");
                StringBuilder builder = new StringBuilder();
                for (int o = 0; o < dotSplit.length; ++o) {
                    if (o < dotSplit.length - 2) {
                        builder.append("-.");
                        continue;
                    }
                    if (o < dotSplit.length - 1) {
                        builder.append(dotSplit[o].isEmpty() ? "" : Character.valueOf(dotSplit[o].charAt(0))).append("-.");
                        continue;
                    }
                    builder.append(dotSplit[o]);
                }
                sortName = builder.toString();
            }
            sortableKeyList.add(new KeySortableByOther((Object)rootContainerNode, new Comparable[]{Integer.valueOf(rootContainerNode.startsWith("Multiplayer_") ? 1 : (rootContainerNode.startsWith("Realms_") ? 2 : 0)), ((String)sortName).toLowerCase(), sortName}));
        }
        Collections.sort(sortableKeyList);
        this.current = -1;
        this.auto = -1;
        ArrayList<String> keyList = new ArrayList<String>();
        ArrayList<Object> optionList = new ArrayList<Object>();
        String currentRoot = currentContainer == null ? null : currentContainer.getLastNode();
        String autoRoot = autoWorldPath == null ? null : autoWorldPath.getRoot().getLastNode();
        for (int i = 0; i < sortableKeyList.size(); ++i) {
            KeySortableByOther k = (KeySortableByOther)sortableKeyList.get(i);
            String containerKey = (String)k.getKey();
            if (this.current == -1 && containerKey.equals(currentRoot)) {
                this.current = i;
            }
            Object option = (String)((Object)k.getDataToSortBy()[2]);
            if (hideWorldNamesConfig == 2 || ((String)option).isEmpty()) {
                option = "hidden " + optionList.size();
            }
            if (this.auto == -1 && containerKey.equals(autoRoot)) {
                this.auto = i;
                option = (String)option + " (auto)";
            }
            keyList.add(containerKey);
            optionList.add(option);
        }
        this.keys = keyList.toArray(new String[0]);
        this.options = optionList.toArray(new String[0]);
    }
}

