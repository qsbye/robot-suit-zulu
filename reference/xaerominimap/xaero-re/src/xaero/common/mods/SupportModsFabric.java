/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import xaero.common.IXaeroMinimap;
import xaero.common.mods.SupportAmecs;
import xaero.common.mods.SupportMods;
import xaero.common.mods.SupportWraithWaystones;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;

public class SupportModsFabric
extends SupportMods {
    public SupportAmecs amecs = null;
    public SupportWraithWaystones wraithWaystones = null;

    public SupportModsFabric(IXaeroMinimap modMain) {
        super(modMain);
        try {
            Class<?> mmClassTest = Class.forName("de.siphalor.amecs.api.KeyModifiers");
            this.amecs = new SupportAmecs(MinimapLogs.LOGGER);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public boolean amecs() {
        return this.amecs != null;
    }

    public boolean wraithWaystones() {
        return this.wraithWaystones != null;
    }

    @Override
    public void registerClientEvents() {
        super.registerClientEvents();
    }

    @Override
    public void onClientTick() {
        super.onClientTick();
    }

    @Override
    public void onMinimapSessionStarted(MinimapSession minimapSession) {
        super.onMinimapSessionStarted(minimapSession);
    }
}

