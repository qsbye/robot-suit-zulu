/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
    public static double getEntityX(Entity e, float partial) {
        double xOld = e.tickCount > 0 ? e.xOld : e.getX();
        return xOld + (e.getX() - xOld) * (double)partial;
    }

    public static double getEntityY(Entity e, float partial) {
        double yOld = e.tickCount > 0 ? e.yOld : e.getY();
        return yOld + (e.getY() - yOld) * (double)partial;
    }

    public static double getEntityZ(Entity e, float partial) {
        double zOld = e.tickCount > 0 ? e.zOld : e.getZ();
        return zOld + (e.getZ() - zOld) * (double)partial;
    }

    public static Vec3 getEntityPos(Entity e, float partial) {
        return new Vec3(EntityUtils.getEntityX(e, partial), EntityUtils.getEntityY(e, partial), EntityUtils.getEntityZ(e, partial));
    }
}

