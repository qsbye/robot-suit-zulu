/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud;

import xaero.hud.compat.OldSystemCompatibility;
import xaero.hud.event.HudEventHandler;
import xaero.hud.module.ModuleManager;
import xaero.hud.module.ModuleSessionHandler;
import xaero.hud.preset.HudPresetManager;
import xaero.hud.pushbox.PushboxManager;

public class Hud {
    private final ModuleManager moduleManager;
    private final PushboxManager pushboxManager;
    private final HudPresetManager presetManager;
    private final HudEventHandler eventHandler;
    private final ModuleSessionHandler sessionHandler;
    private final OldSystemCompatibility oldSystemCompatibility;

    public Hud(ModuleManager moduleManager, PushboxManager pushboxManager, HudPresetManager presetManager, HudEventHandler eventHandler, ModuleSessionHandler sessionHandler, OldSystemCompatibility oldSystemCompatibility) {
        this.moduleManager = moduleManager;
        this.pushboxManager = pushboxManager;
        this.presetManager = presetManager;
        this.eventHandler = eventHandler;
        this.sessionHandler = sessionHandler;
        this.oldSystemCompatibility = oldSystemCompatibility;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public PushboxManager getPushboxManager() {
        return this.pushboxManager;
    }

    public HudPresetManager getPresetManager() {
        return this.presetManager;
    }

    public HudEventHandler getEventHandler() {
        return this.eventHandler;
    }

    public OldSystemCompatibility getOldSystemCompatibility() {
        return this.oldSystemCompatibility;
    }

    public ModuleSessionHandler getSessionHandler() {
        return this.sessionHandler;
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public Hud build() {
            ModuleManager moduleManager = new ModuleManager();
            PushboxManager pushboxManager = new PushboxManager();
            HudPresetManager presetManager = new HudPresetManager();
            HudEventHandler eventHandler = new HudEventHandler();
            ModuleSessionHandler sessionHandler = new ModuleSessionHandler(moduleManager);
            OldSystemCompatibility oldSystemCompatibility = new OldSystemCompatibility();
            Hud hud = new Hud(moduleManager, pushboxManager, presetManager, eventHandler, sessionHandler, oldSystemCompatibility);
            eventHandler.setHud(hud);
            return hud;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

