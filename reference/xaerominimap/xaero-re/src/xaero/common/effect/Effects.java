/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import xaero.common.effect.NoCaveMapsEffect;
import xaero.common.effect.NoMinimapEffect;
import xaero.common.effect.NoRadarEffect;
import xaero.common.effect.NoWaypointsEffect;

public class Effects {
    public static MobEffect NO_MINIMAP_UNHELD = null;
    public static MobEffect NO_MINIMAP_HARMFUL_UNHELD = null;
    public static MobEffect NO_RADAR_UNHELD = null;
    public static MobEffect NO_RADAR_HARMFUL_UNHELD = null;
    public static MobEffect NO_WAYPOINTS_UNHELD = null;
    public static MobEffect NO_WAYPOINTS_HARMFUL_UNHELD = null;
    public static MobEffect NO_CAVE_MAPS_UNHELD = null;
    public static MobEffect NO_CAVE_MAPS_HARMFUL_UNHELD = null;
    public static Holder<MobEffect> NO_MINIMAP = null;
    public static Holder<MobEffect> NO_MINIMAP_HARMFUL = null;
    public static Holder<MobEffect> NO_RADAR = null;
    public static Holder<MobEffect> NO_RADAR_HARMFUL = null;
    public static Holder<MobEffect> NO_WAYPOINTS = null;
    public static Holder<MobEffect> NO_WAYPOINTS_HARMFUL = null;
    public static Holder<MobEffect> NO_CAVE_MAPS = null;
    public static Holder<MobEffect> NO_CAVE_MAPS_HARMFUL = null;

    public static void init() {
        if (NO_MINIMAP != null) {
            return;
        }
        NO_MINIMAP_UNHELD = new NoMinimapEffect(MobEffectCategory.NEUTRAL);
        NO_MINIMAP_HARMFUL_UNHELD = new NoMinimapEffect(MobEffectCategory.HARMFUL);
        NO_RADAR_UNHELD = new NoRadarEffect(MobEffectCategory.NEUTRAL);
        NO_RADAR_HARMFUL_UNHELD = new NoRadarEffect(MobEffectCategory.HARMFUL);
        NO_WAYPOINTS_UNHELD = new NoWaypointsEffect(MobEffectCategory.NEUTRAL);
        NO_WAYPOINTS_HARMFUL_UNHELD = new NoWaypointsEffect(MobEffectCategory.HARMFUL);
        NO_CAVE_MAPS_UNHELD = new NoCaveMapsEffect(MobEffectCategory.NEUTRAL);
        NO_CAVE_MAPS_HARMFUL_UNHELD = new NoCaveMapsEffect(MobEffectCategory.HARMFUL);
    }
}

