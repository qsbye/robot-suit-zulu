/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import java.util.Objects;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;

public class WaypointRenderInfo {
    protected String initials;
    protected WaypointColor color;
    protected WaypointVisibilityType visibility;
    protected Boolean disabled;
    @Deprecated
    protected int actualColor;
    protected boolean thirdPartyDeleted;

    public String getInitials() {
        return this.initials;
    }

    public String getInitialsSafe(String replacement) {
        return this.getInitials().replace(":", replacement);
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public WaypointColor getWaypointColor() {
        return this.color;
    }

    public void setWaypointColor(WaypointColor c) {
        this.color = c;
        this.actualColor = c == null ? -1 : c.ordinal();
    }

    public WaypointVisibilityType getVisibility() {
        return this.visibility;
    }

    public void setVisibility(WaypointVisibilityType visibility) {
        this.visibility = visibility;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isThirdPartyDeleted() {
        return this.thirdPartyDeleted;
    }

    public void setThirdPartyDeleted(boolean thirdPartyDeleted) {
        this.thirdPartyDeleted = thirdPartyDeleted;
    }

    public void nullEverythingMatching(Waypoint w) {
        if (Objects.equals(this.initials, w.initials)) {
            this.setInitials(null);
        }
        if (Objects.equals((Object)this.color, (Object)w.color)) {
            this.setWaypointColor(null);
        }
        if (Objects.equals((Object)this.visibility, (Object)w.visibility)) {
            this.setVisibility(null);
        }
        if (Objects.equals(this.disabled, w.disabled)) {
            this.setDisabled(null);
        }
    }

    public void clear() {
        this.initials = null;
        this.color = null;
        this.visibility = null;
        this.disabled = false;
        this.thirdPartyDeleted = false;
    }
}

