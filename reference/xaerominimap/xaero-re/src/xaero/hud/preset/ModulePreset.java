/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.preset;

import java.util.ArrayList;
import java.util.List;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;
import xaero.hud.preset.action.IPresetAction;
import xaero.hud.preset.action.PlacementPresetAction;

public final class ModulePreset<MS extends ModuleSession<MS>> {
    private final HudModule<MS> module;
    private final List<IPresetAction<? super HudModule<MS>>> actions;

    private ModulePreset(HudModule<MS> module, List<IPresetAction<? super HudModule<MS>>> actions) {
        this.module = module;
        this.actions = actions;
    }

    public void apply() {
        for (IPresetAction<HudModule<MS>> action : this.actions) {
            action.apply(this.module);
        }
    }

    public void confirm() {
        for (IPresetAction<HudModule<MS>> action : this.actions) {
            action.confirm(this.module);
        }
    }

    public void cancel() {
        for (IPresetAction<HudModule<MS>> action : this.actions) {
            action.cancel(this.module);
        }
    }

    public HudModule<MS> getModule() {
        return this.module;
    }

    public static final class Builder<MS extends ModuleSession<MS>> {
        private final HudModule<MS> module;
        private final List<IPresetAction<? super HudModule<MS>>> actions;
        private PlacementPresetAction<MS> placementAction;

        private Builder(HudModule<MS> module) {
            this.module = module;
            this.actions = new ArrayList<IPresetAction<? super HudModule<MS>>>();
        }

        public Builder<MS> setDefault() {
            this.actions.clear();
            this.setPlacement(0, 0, false, false, false, false, false);
            return this;
        }

        public Builder<MS> setPlacement(int x, int y, boolean centered, boolean fromRight, boolean fromBottom, boolean flippedHor, boolean flippedVer) {
            this.placementAction = new PlacementPresetAction(x, y, centered, fromRight, fromBottom, flippedHor, flippedVer);
            return this;
        }

        public Builder<MS> addAction(IPresetAction<? super HudModule<MS>> action) {
            this.actions.add(action);
            return this;
        }

        public ModulePreset<MS> build() {
            if (this.placementAction != null) {
                this.actions.add(0, this.placementAction);
            }
            return new ModulePreset<MS>(this.module, this.actions);
        }

        public static <MS extends ModuleSession<MS>, M extends HudModule<MS>> Builder<MS> begin(HudModule<MS> module) {
            return new Builder<MS>(module).setDefault();
        }
    }
}

