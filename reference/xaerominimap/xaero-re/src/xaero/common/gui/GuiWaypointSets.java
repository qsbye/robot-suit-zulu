/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.resources.language.I18n;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.common.util.KeySortableByOther;

public class GuiWaypointSets {
    private int currentSet;
    private String[] options;

    public GuiWaypointSets(boolean canCreate, MinimapWorld currentWorld) {
        this(canCreate, currentWorld, currentWorld.getCurrentWaypointSetId());
    }

    public GuiWaypointSets(boolean canCreate, MinimapWorld currentWorld, String currentSetName) {
        int size = currentWorld.getSetCount() + (canCreate ? 1 : 0);
        ArrayList<KeySortableByOther> keysList = new ArrayList<KeySortableByOther>();
        for (WaypointSet set : currentWorld.getIterableWaypointSets()) {
            String key = set.getName();
            keysList.add(new KeySortableByOther((Object)key, new Comparable[]{I18n.get((String)key, (Object[])new Object[0]).toLowerCase()}));
        }
        Collections.sort(keysList);
        this.options = new String[size];
        for (int i = 0; i < keysList.size(); ++i) {
            this.options[i] = (String)((KeySortableByOther)keysList.get(i)).getKey();
            if (!this.options[i].equals(currentSetName)) continue;
            this.currentSet = i;
        }
        if (canCreate) {
            this.options[this.options.length - 1] = "\u00a78" + I18n.get((String)"gui.xaero_create_set", (Object[])new Object[0]);
        }
    }

    public int getCurrentSet() {
        return this.currentSet;
    }

    public String getCurrentSetKey() {
        return this.options[this.currentSet];
    }

    public void setCurrentSet(int currentSet) {
        this.currentSet = currentSet;
    }

    public String[] getOptions() {
        return this.options;
    }
}

