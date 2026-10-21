/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.preset;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.hud.preset.HudPreset;

public class HudPresetManager {
    private final Map<Identifier, HudPreset> presets = new LinkedHashMap<Identifier, HudPreset>();

    public void register(HudPreset preset) {
        this.presets.put(preset.getId(), preset);
    }

    public Iterable<HudPreset> getPresets() {
        return this.presets.values();
    }
}

