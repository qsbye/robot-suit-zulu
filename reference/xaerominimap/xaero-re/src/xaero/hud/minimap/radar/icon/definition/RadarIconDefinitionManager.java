/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinitionReloader;

public class RadarIconDefinitionManager {
    private final Map<Identifier, RadarIconDefinition> definitions = new HashMap<Identifier, RadarIconDefinition>();
    private final RadarIconDefinitionReloader reloader = new RadarIconDefinitionReloader();

    public RadarIconDefinition get(Identifier key) {
        return this.definitions.get(key);
    }

    public void reloadResources() {
        this.reloader.reloadResources(this.definitions);
    }
}

