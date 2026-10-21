/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.xminimap.preset;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.preset.HudPreset;
import xaero.hud.preset.HudPresetManager;
import xaero.hud.preset.ModulePreset;

public class BuiltInHudPresets {
    public static final HudPreset TOP_LEFT = HudPreset.Builder.begin().setId(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"top_left")).setName((Component)Component.translatable((String)"gui.xaero_preset_topleft")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, false, false, false, false).build()).build();
    public static final HudPreset TOP_RIGHT = HudPreset.Builder.begin().setId(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"top_right")).setName((Component)Component.translatable((String)"gui.xaero_preset_topright")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, true, false, false, false).build()).build();
    public static final HudPreset BOTTOM_LEFT = HudPreset.Builder.begin().setId(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"bottom_left")).setName((Component)Component.translatable((String)"gui.xaero_preset_bottom_left")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, false, true, false, false).build()).build();
    public static final HudPreset BOTTOM_RIGHT = HudPreset.Builder.begin().setId(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"bottom_right")).setName((Component)Component.translatable((String)"gui.xaero_preset_bottom_right")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, true, true, false, false).build()).build();

    public static void addAll(HudPresetManager manager) {
        manager.register(TOP_LEFT);
        manager.register(TOP_RIGHT);
        manager.register(BOTTOM_LEFT);
        manager.register(BOTTOM_RIGHT);
    }
}

