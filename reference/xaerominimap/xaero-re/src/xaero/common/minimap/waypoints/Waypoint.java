/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.waypoints;

import net.minecraft.client.Camera;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;

public class Waypoint
extends WaypointRenderInfo
implements Comparable<Waypoint> {
    public static final int ONEOFF_DESTINATION_SAFE_FOR = 5000;
    public static final int ONEOFF_DESTINATION_REMOVE_DISTANCE = 4;
    public static Vec3 RENDER_SORTING_POS = new Vec3(0.0, 0.0, 0.0);
    private int x;
    private int y;
    private int z;
    private String name;
    protected WaypointPurpose purpose;
    private boolean rotation;
    private int yaw;
    private boolean temporary;
    private boolean yIncluded;
    private final long createdAt;
    private Identifier thirdPartyOrigin;
    private WaypointRenderInfo thirdPartyRenderOverride;

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color) {
        this(x, y, z, name, initials, color, 0, false);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type) {
        this(x, y, z, name, initials, color, type, false);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type, boolean temp) {
        this(x, y, z, name, initials, color, type, temp, true);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type, boolean temp, boolean yIncluded) {
        this(x, y, z, name, initials, WaypointColor.fromIndex(color), WaypointPurpose.values()[type], temp, yIncluded);
        this.actualColor = color;
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color) {
        this(x, y, z, name, initials, color, WaypointPurpose.NORMAL, false);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose) {
        this(x, y, z, name, initials, color, purpose, false);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose, boolean temp) {
        this(x, y, z, name, initials, color, purpose, temp, true);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose, boolean temp, boolean yIncluded) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.initials = initials;
        this.color = color;
        this.purpose = purpose;
        this.name = name;
        this.temporary = temp;
        this.visibility = WaypointVisibilityType.LOCAL;
        if (this.purpose.isDeath()) {
            this.visibility = WaypointVisibilityType.GLOBAL;
        }
        this.yIncluded = yIncluded;
        this.createdAt = System.currentTimeMillis();
        this.actualColor = color.ordinal();
        this.disabled = false;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX(double dimDiv) {
        if (dimDiv == 1.0) {
            return this.x;
        }
        return (int)Math.floor((double)this.x / dimDiv);
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ(double dimDiv) {
        if (dimDiv == 1.0) {
            return this.z;
        }
        return (int)Math.floor((double)this.z / dimDiv);
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return I18n.get((String)this.name, (Object[])new Object[0]);
    }

    @Override
    public String getInitials() {
        if (this.thirdPartyRenderOverride != null && this.thirdPartyRenderOverride.getInitials() != null) {
            return this.thirdPartyRenderOverride.getInitials();
        }
        return super.getInitials();
    }

    public String getNameSafe(String replacement) {
        return this.getName().replace(":", replacement);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public String getSymbol() {
        return this.getInitials();
    }

    @Deprecated
    public void setSymbol(String symbol) {
        this.setInitials(symbol);
    }

    @Deprecated
    public String getSymbolSafe(String replacement) {
        return this.getInitialsSafe(replacement);
    }

    @Deprecated
    public int getColor() {
        return this.getWaypointColor().ordinal();
    }

    @Deprecated
    public int getActualColor() {
        return this.actualColor;
    }

    @Deprecated
    public void setColor(int c) {
        this.setWaypointColor(WaypointColor.fromIndex(c));
        this.actualColor = c;
    }

    @Override
    public WaypointColor getWaypointColor() {
        if (this.thirdPartyRenderOverride != null && this.thirdPartyRenderOverride.getWaypointColor() != null) {
            return this.thirdPartyRenderOverride.getWaypointColor();
        }
        return super.getWaypointColor();
    }

    public boolean isGlobal() {
        return this.getVisibility().isGlobal();
    }

    @Deprecated
    public int getVisibilityType() {
        return this.getVisibility().ordinal();
    }

    @Override
    public WaypointVisibilityType getVisibility() {
        if (this.thirdPartyRenderOverride != null && this.thirdPartyRenderOverride.getVisibility() != null) {
            return this.thirdPartyRenderOverride.getVisibility();
        }
        return super.getVisibility();
    }

    @Deprecated
    public void setVisibilityType(int visibility) {
        this.setVisibility(WaypointVisibilityType.values()[visibility]);
    }

    @Override
    public void setVisibility(WaypointVisibilityType visibility) {
        if (this.purpose != WaypointPurpose.DEATH) {
            super.setVisibility(visibility);
        }
    }

    public boolean isDisabled() {
        return this.getDisabled();
    }

    @Override
    public Boolean getDisabled() {
        if (this.thirdPartyRenderOverride != null && this.thirdPartyRenderOverride.getDisabled() != null) {
            return this.thirdPartyRenderOverride.getDisabled();
        }
        return super.getDisabled();
    }

    public void setDisabled(boolean b) {
        this.setDisabled((Boolean)b);
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.temporary = false;
        super.setDisabled(disabled);
    }

    @Deprecated
    public int getWaypointType() {
        return this.purpose.ordinal();
    }

    @Deprecated
    public void setType(int type) {
        this.setPurpose(WaypointPurpose.values()[type]);
    }

    public WaypointPurpose getPurpose() {
        return this.purpose;
    }

    public void setPurpose(WaypointPurpose purpose) {
        this.purpose = purpose;
        if (this.purpose.isDeath()) {
            this.visibility = WaypointVisibilityType.GLOBAL;
        }
    }

    public boolean isRotation() {
        return this.rotation;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public int getYaw() {
        return this.yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
        this.disabled = false;
    }

    public boolean isYIncluded() {
        return this.yIncluded;
    }

    public void setYIncluded(boolean yIncluded) {
        this.yIncluded = yIncluded;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    @Deprecated
    public boolean isOneoffDestination() {
        return this.getPurpose().isDestination();
    }

    @Deprecated
    public void setOneoffDestination(boolean oneoffDestination) {
        if (oneoffDestination) {
            if (this.purpose == WaypointPurpose.NORMAL) {
                this.purpose = WaypointPurpose.DESTINATION;
            }
            return;
        }
        if (this.purpose != WaypointPurpose.DESTINATION) {
            return;
        }
        this.purpose = WaypointPurpose.NORMAL;
    }

    public boolean isDestination() {
        return this.getPurpose().isDestination();
    }

    public double getDistanceSq(double x, double y, double z) {
        double offX = (double)this.x - x;
        double offY = this.yIncluded ? (double)this.y - y : 0.0;
        double offZ = (double)this.z - z;
        return offX * offX + offY * offY + offZ * offZ;
    }

    public static String getStringFromStringSafe(String stringSafe, String replacement) {
        return stringSafe.replace(replacement, ":");
    }

    @Deprecated
    public boolean isServerWaypoint() {
        return this.isThirdParty();
    }

    public String getComparisonName() {
        String comparisonName = this.getLocalizedName().toLowerCase().trim();
        if (comparisonName.startsWith("the ")) {
            return comparisonName.substring(4);
        }
        if (comparisonName.startsWith("a ")) {
            return comparisonName.substring(2);
        }
        return comparisonName;
    }

    public double getComparisonDistance(Camera camera, double dimDiv) {
        Vec3 cameraPos = camera.position();
        double offX = (double)this.getX(dimDiv) - cameraPos.x;
        double offY = !this.isYIncluded() ? 0.0 : (double)this.getY() - cameraPos.y;
        double offZ = (double)this.getZ(dimDiv) - cameraPos.z;
        return offX * offX + offY * offY + offZ * offZ;
    }

    public double getComparisonAngleCos(Camera camera, double dimDiv) {
        Vector3fc lookVector = camera.forwardVector();
        Vec3 cameraPos = camera.position();
        double offX = (double)this.getX(dimDiv) - cameraPos.x;
        double offY = !this.isYIncluded() ? 0.0 : (double)this.getY() - cameraPos.y;
        double offZ = (double)this.getZ(dimDiv) - cameraPos.z;
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        return (offX * (double)lookVector.x() + offY * (double)lookVector.y() + offZ * (double)lookVector.z()) / distance;
    }

    private double getRenderSortingDistanceSquared() {
        double fromCameraX = (double)this.x - Waypoint.RENDER_SORTING_POS.x;
        double fromCameraY = this.yIncluded ? (double)this.y - Waypoint.RENDER_SORTING_POS.y : 0.0;
        double fromCameraZ = (double)this.z - Waypoint.RENDER_SORTING_POS.z;
        return fromCameraX * fromCameraX + fromCameraY * fromCameraY + fromCameraZ * fromCameraZ;
    }

    @Override
    public int compareTo(Waypoint other) {
        double otherRsds;
        boolean isDeath = this.purpose.isDeath();
        if (isDeath != other.purpose.isDeath()) {
            return isDeath ? 1 : -1;
        }
        double rsds = this.getRenderSortingDistanceSquared();
        return rsds > (otherRsds = other.getRenderSortingDistanceSquared()) ? -1 : (rsds == otherRsds ? 0 : 1);
    }

    public void setThirdPartyOrigin(Identifier thirdPartyOrigin) {
        this.thirdPartyOrigin = thirdPartyOrigin;
        this.thirdPartyRenderOverride = new WaypointRenderInfo();
    }

    public Identifier getThirdPartyOrigin() {
        return this.thirdPartyOrigin;
    }

    public boolean isThirdParty() {
        return this.thirdPartyOrigin != null;
    }

    public boolean isEffectivelyDeleted() {
        return this.isTemporary() || this.isThirdPartyDeleted();
    }

    public WaypointRenderInfo getThirdPartyRenderOverride() {
        return this.thirdPartyRenderOverride;
    }

    public void setThirdPartyRenderOverride(WaypointRenderInfo thirdPartyRenderOverride) {
        this.thirdPartyRenderOverride = thirdPartyRenderOverride;
    }

    @Override
    public boolean isThirdPartyDeleted() {
        return this.thirdPartyRenderOverride != null && this.thirdPartyRenderOverride.isThirdPartyDeleted();
    }

    @Override
    public void setThirdPartyDeleted(boolean thirdPartyDeleted) {
        if (this.thirdPartyRenderOverride == null) {
            throw new IllegalStateException();
        }
        this.thirdPartyRenderOverride.setThirdPartyDeleted(thirdPartyDeleted);
    }

    public WaypointRenderInfo getRenderInfoEditDest() {
        if (this.isThirdParty()) {
            return this.getThirdPartyRenderOverride();
        }
        return this;
    }
}

