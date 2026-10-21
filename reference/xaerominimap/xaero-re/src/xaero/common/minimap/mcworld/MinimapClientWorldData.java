/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.mcworld;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import xaero.hud.packet.basic.ClientboundRulesPacket;

public class MinimapClientWorldData {
    private int serverModNetworkVersion;
    public Integer serverLevelId;
    public float shadowR = 1.0f;
    public float shadowG = 1.0f;
    public float shadowB = 1.0f;
    private ClientboundRulesPacket syncedRules;
    private EnvironmentAttributeSystem attributeSystem;

    public MinimapClientWorldData(ClientLevel world) {
        if (world.dimension() == ClientLevel.OVERWORLD || world.dimensionTypeRegistration().is(BuiltinDimensionTypes.OVERWORLD.identifier())) {
            this.shadowR = 0.518f;
            this.shadowG = 0.678f;
            this.shadowB = 1.0f;
        } else if (world.dimension() == ClientLevel.NETHER || world.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER.identifier())) {
            this.shadowR = 1.0f;
            this.shadowG = 0.0f;
            this.shadowB = 0.0f;
        }
        EnvironmentAttributeSystem.Builder builder = EnvironmentAttributeSystem.builder();
        ClockManager clockManager = definition -> world.clockManager().getInstance(definition);
        DimensionType dimType = world.dimensionType();
        builder.addConstantLayer(dimType.attributes());
        dimType.timelines().forEach(holder -> builder.addTimelineLayer(holder, clockManager));
        this.attributeSystem = builder.build();
    }

    public void setServerModNetworkVersion(int serverModNetworkVersion) {
        this.serverModNetworkVersion = serverModNetworkVersion;
    }

    public int getServerModNetworkVersion() {
        return this.serverModNetworkVersion;
    }

    public void setSyncedRules(ClientboundRulesPacket syncedRules) {
        this.syncedRules = syncedRules;
    }

    public ClientboundRulesPacket getSyncedRules() {
        if (this.syncedRules == null) {
            this.syncedRules = new ClientboundRulesPacket(true, true, true);
        }
        return this.syncedRules;
    }

    public EnvironmentAttributeSystem getAttributeSystem() {
        return this.attributeSystem;
    }
}

