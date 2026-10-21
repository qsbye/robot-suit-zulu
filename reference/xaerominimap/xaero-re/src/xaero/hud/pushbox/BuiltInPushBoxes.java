/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox;

import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.pushbox.PushboxManager;
import xaero.hud.pushbox.boss.BossHealthPushBox;
import xaero.hud.pushbox.boss.BossHealthShiftPushBox;
import xaero.hud.pushbox.boss.IBossHealthPushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;
import xaero.hud.pushbox.effect.PotionEffectsPushBox;
import xaero.hud.pushbox.effect.PotionEffectsShiftPushBox;
import xaero.lib.common.config.option.ConfigOption;

public class BuiltInPushBoxes {
    public static final PotionEffectsPushBox POTION_EFFECTS_PUSH_BOX = new PotionEffectsPushBox();
    public static final PotionEffectsShiftPushBox POTION_EFFECTS_SHIFT_PUSH_BOX = new PotionEffectsShiftPushBox();
    public static final BossHealthPushBox BOSS_HEALTH_PUSH_BOX = new BossHealthPushBox();
    public static final BossHealthShiftPushBox BOSS_HEALTH_SHIFT_PUSH_BOX = new BossHealthShiftPushBox();

    public static IPotionEffectsPushBox getPotionEffectPushBox(IXaeroMinimap modMain) {
        int boxType = (Integer)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.POTION_EFFECT_PUSH_BOX);
        return boxType == 0 ? null : (boxType == 2 ? POTION_EFFECTS_SHIFT_PUSH_BOX : POTION_EFFECTS_PUSH_BOX);
    }

    public static IBossHealthPushBox getBossHealthPushBox(IXaeroMinimap modMain) {
        int boxType = (Integer)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.BOSS_HEALTH_PUSH_BOX);
        return boxType == 0 ? null : (boxType == 2 ? BOSS_HEALTH_SHIFT_PUSH_BOX : BOSS_HEALTH_PUSH_BOX);
    }

    public static void addAll(PushboxManager manager) {
        manager.add(POTION_EFFECTS_PUSH_BOX);
        manager.add(POTION_EFFECTS_SHIFT_PUSH_BOX);
        manager.add(BOSS_HEALTH_PUSH_BOX);
        manager.add(BOSS_HEALTH_SHIFT_PUSH_BOX);
    }
}

