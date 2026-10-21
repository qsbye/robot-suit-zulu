/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.module;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.hud.module.HudModule;

public final class ModuleManager {
    private final Map<Identifier, HudModule<?>> modules = new LinkedHashMap();

    public void register(HudModule<?> hudModule) {
        this.modules.put(hudModule.getId(), hudModule);
    }

    public HudModule<?> get(Identifier id) {
        return this.modules.get(id);
    }

    public Iterable<HudModule<?>> getModules() {
        return this.modules.values();
    }
}

