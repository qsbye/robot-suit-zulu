/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.packet;

import java.util.function.Consumer;
import xaero.common.server.level.LevelMapProperties;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class LevelMapPropertiesConsumer
implements Consumer<LevelMapProperties> {
    @Override
    public void accept(LevelMapProperties t) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        minimapSession.getWorldStateUpdater().onServerLevelId(t.getId());
    }
}

