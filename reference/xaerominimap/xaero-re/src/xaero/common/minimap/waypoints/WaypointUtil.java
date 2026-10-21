/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.waypoints;

public class WaypointUtil {
    public static int getAddedMinimapIconFrame(int initialsWidth) {
        return WaypointUtil.getAddedMinimapIconFrame(0, initialsWidth);
    }

    public static int getAddedMinimapIconFrame(int addedFrame, int initialsWidth) {
        int totalToAdd;
        int frameToAdd;
        if (initialsWidth > 8 && (frameToAdd = (totalToAdd = initialsWidth - 8) - totalToAdd / 2) > addedFrame) {
            addedFrame = frameToAdd;
        }
        return addedFrame;
    }
}

