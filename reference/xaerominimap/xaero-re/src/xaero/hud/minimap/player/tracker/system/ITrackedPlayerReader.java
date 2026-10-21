/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker.system;

import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ITrackedPlayerReader<P> {
    public UUID getId(P var1);

    public double getX(P var1);

    public double getY(P var1);

    public double getZ(P var1);

    public ResourceKey<Level> getDimension(P var1);
}

