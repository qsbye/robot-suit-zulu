/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.module;

import java.util.function.BiConsumer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import xaero.common.HudMod;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleManager;
import xaero.hud.module.ModuleSession;

public class ModuleSessionHandler {
    private final ModuleManager manager;

    public ModuleSessionHandler(ModuleManager manager) {
        this.manager = manager;
    }

    public void resetSessions(HudMod modMain, ClientPacketListener packetListener, BiConsumer<HudModule<?>, ModuleSession<?>> sessionDest) {
        for (HudModule<?> module : this.manager.getModules()) {
            this.resetSession(module, modMain, packetListener, sessionDest);
        }
    }

    public void closeSessions(HudMod modMain) {
        for (HudModule<?> module : this.manager.getModules()) {
            this.closeSession(module, modMain);
        }
    }

    private <MS extends ModuleSession<MS>> void resetSession(HudModule<MS> module, HudMod modMain, ClientPacketListener packetListener, BiConsumer<HudModule<?>, ModuleSession<?>> sessionDest) {
        this.closeSession(module, modMain);
        sessionDest.accept(module, (ModuleSession)module.getSessionFactory().apply((Object)modMain, module, (Object)packetListener));
        HudMod.LOGGER.debug("Initialized new session for module {}!", (Object)module.getId());
    }

    private <MS extends ModuleSession<MS>> void closeSession(HudModule<MS> module, HudMod modMain) {
        if (module.getCurrentSession() != null) {
            try {
                ((ModuleSession)module.getCurrentSession()).close();
                HudMod.LOGGER.debug("Finalized session for module {}!", (Object)module.getId());
            }
            catch (Throwable t) {
                HudMod.LOGGER.error("Failed to finalize session for module {}!", (Object)module.getId(), (Object)t);
            }
        }
        module.setRenderer(null);
    }
}

