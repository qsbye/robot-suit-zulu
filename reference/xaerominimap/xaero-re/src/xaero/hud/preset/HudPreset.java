/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.preset;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xaero.hud.preset.ModulePreset;

public final class HudPreset {
    private final Identifier id;
    private final Component name;
    private final Set<ModulePreset<?>> modulePresets;
    private boolean applied;

    private HudPreset(Identifier id, Component name, Set<ModulePreset<?>> modulePresets) {
        this.id = id;
        this.name = name;
        this.modulePresets = modulePresets;
    }

    public Identifier getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public void apply() {
        this.applied = true;
        for (ModulePreset<?> modulePreset : this.modulePresets) {
            modulePreset.apply();
        }
    }

    public void confirm() {
        if (!this.applied) {
            return;
        }
        this.applied = false;
        for (ModulePreset<?> modulePreset : this.modulePresets) {
            modulePreset.confirm();
        }
    }

    public void cancel() {
        if (!this.applied) {
            return;
        }
        this.applied = false;
        for (ModulePreset<?> modulePreset : this.modulePresets) {
            modulePreset.cancel();
        }
    }

    public void applyAndConfirm() {
        this.apply();
        this.confirm();
        for (ModulePreset<?> modulePreset : this.modulePresets) {
            modulePreset.getModule().confirmTransform();
        }
    }

    public static final class Builder {
        private Identifier id;
        private Component name;
        private final Set<ModulePreset<?>> modulePresets = new HashSet();

        private Builder() {
        }

        public Builder setDefault() {
            this.modulePresets.clear();
            return this;
        }

        public Builder setId(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder setName(Component name) {
            this.name = name;
            return this;
        }

        public Builder addModulePreset(ModulePreset<?> modulePreset) {
            this.modulePresets.add(modulePreset);
            return this;
        }

        public HudPreset build() {
            if (this.id == null || this.name == null) {
                throw new IllegalStateException();
            }
            return new HudPreset(this.id, this.name, this.modulePresets);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

