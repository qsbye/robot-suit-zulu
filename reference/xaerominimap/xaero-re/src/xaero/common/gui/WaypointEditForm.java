/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;

public class WaypointEditForm {
    public String name = "";
    public String xText = "";
    public String yText = "";
    public String zText = "";
    public String yawText = "";
    public String initial = "";
    public boolean autoInitial;
    public int disabledOrTemporary;
    public WaypointVisibilityType visibilityType = WaypointVisibilityType.LOCAL;
    public WaypointColor color;
    public boolean thirdParty;
    public boolean keepName;
    public boolean keepXText;
    public boolean keepYText;
    public boolean keepZText;
    public boolean keepYawText;
    public boolean keepInitial;
    public boolean keepDisabledOrTemporary;
    public boolean keepVisibilityType;
    public boolean defaultKeepYawText;
    public boolean defaultKeepDisabledOrTemporary;
    public boolean defaultKeepVisibilityType;
    public boolean defaultKeepColor;

    public String getName() {
        return this.name;
    }

    public String getxText() {
        return this.xText;
    }

    public String getyText() {
        return this.yText;
    }

    public String getzText() {
        return this.zText;
    }

    public String getYawText() {
        return this.yawText;
    }

    public String getInitial() {
        return this.initial;
    }

    public WaypointVisibilityType getVisibilityType() {
        return this.visibilityType;
    }

    public int getDisabledOrTemporary() {
        return this.disabledOrTemporary;
    }

    public WaypointColor getColor() {
        return this.color;
    }

    public boolean isThirdParty() {
        return this.thirdParty;
    }
}

