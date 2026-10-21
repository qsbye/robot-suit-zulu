/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import xaero.common.HudMod;
import xaero.common.file.SimpleBackup;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.server.ServerWaypointManager;
import xaero.hud.minimap.waypoint.thirdparty.ThirdPartyWaypointManager;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.connection.MinimapWorldConnectionManager;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.path.XaeroPath;

public class MinimapWorldRootContainer
extends MinimapWorldContainer {
    private final RootConfig config;
    private final Map<ResourceKey<Level>, Identifier> dimensionTypeIds;
    private final Map<ResourceKey<Level>, DimensionType> dimensionTypes;

    protected MinimapWorldRootContainer(HudMod modMain, MinimapSession session, Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager, XaeroPath path, RootConfig config, Map<ResourceKey<Level>, Identifier> dimensionTypeIds, Map<ResourceKey<Level>, DimensionType> dimensionTypes, ResourceKey<Level> equivalentDimId, ThirdPartyWaypointManager thirdPartyWaypointManager) {
        super(modMain, session, subContainers, worlds, worldNames, serverWaypointManager, path, null, equivalentDimId, thirdPartyWaypointManager);
        this.config = config;
        this.dimensionTypeIds = dimensionTypeIds;
        this.dimensionTypes = dimensionTypes;
    }

    public void updateConnectionsField(WaypointSession session) {
        this.config.resetSubWorldConnections(MinimapWorldContainerUtil.isMultiplayer(this.path));
    }

    public MinimapWorldConnectionManager getSubWorldConnections() {
        return this.config.getSubWorldConnections();
    }

    public DimensionType getDimensionType(ResourceKey<Level> dimId) {
        DimensionType dimensionType = this.dimensionTypes.get(dimId);
        if (dimensionType != null) {
            return dimensionType;
        }
        Identifier dimensionTypeId = this.dimensionTypeIds.get(dimId);
        if (dimensionTypeId == null) {
            if (dimId == Level.NETHER) {
                dimensionTypeId = BuiltinDimensionTypes.NETHER.identifier();
            } else if (dimId == Level.OVERWORLD) {
                dimensionTypeId = BuiltinDimensionTypes.OVERWORLD.identifier();
            } else if (dimId == Level.END) {
                dimensionTypeId = BuiltinDimensionTypes.END.identifier();
            } else {
                IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
                if (integratedServer == null) {
                    return null;
                }
                ServerLevel serverLevel = integratedServer.getLevel(dimId);
                if (serverLevel == null) {
                    return null;
                }
                this.dimensionTypes.put(dimId, serverLevel.dimensionType());
                return serverLevel.dimensionType();
            }
        }
        if ((dimensionType = (DimensionType)Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).getValue(dimensionTypeId)) != null) {
            this.dimensionTypes.put(dimId, dimensionType);
        }
        return dimensionType;
    }

    public double getDimensionScale(ResourceKey<Level> dimId) {
        DimensionType dimType = this.getDimensionType(dimId);
        if (dimType == null) {
            return 1.0;
        }
        return dimType.coordinateScale();
    }

    public void updateDimensionType(ClientLevel level) {
        ResourceKey dimId = level.dimension();
        ResourceKey dimTypeId = (ResourceKey)level.dimensionTypeRegistration().unwrapKey().get();
        DimensionType dimType = level.dimensionType();
        if (Objects.equals(this.dimensionTypeIds.get(dimId), dimTypeId.identifier())) {
            return;
        }
        this.dimensionTypes.put((ResourceKey<Level>)dimId, dimType);
        this.dimensionTypeIds.put((ResourceKey<Level>)dimId, dimTypeId.identifier());
        this.session.getWorldManagerIO().getRootConfigIO().save(this);
    }

    public void renameOldContainer(XaeroPath containerPath) {
        if (this.subContainers.isEmpty()) {
            return;
        }
        String dimensionPart = containerPath.getAtIndex(1).getLastNode();
        if (this.subContainers.containsKey(dimensionPart)) {
            return;
        }
        ResourceKey<Level> dimId = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(dimensionPart);
        if (dimId == null) {
            return;
        }
        Identifier dimKey = dimId.identifier();
        String dimKeyOldValidation = dimKey.getPath().replaceAll("[^a-zA-Z0-9_]+", "");
        XaeroPath customWorldPath = this.session.getWorldState().getCustomWorldPath();
        MinimapWorldContainer currentCustomContainer = customWorldPath == null ? null : this.session.getWorldManager().getWorld(customWorldPath).getContainer();
        for (Map.Entry subContainerEntry : this.subContainers.entrySet()) {
            String subKey = (String)subContainerEntry.getKey();
            if (!subKey.equals(dimKeyOldValidation)) continue;
            MinimapWorldContainer dimContainer = (MinimapWorldContainer)subContainerEntry.getValue();
            boolean currentlySelected = currentCustomContainer != null && currentCustomContainer.getPath().isSubOf(dimContainer.getPath());
            this.subContainers.put(dimensionPart, dimContainer);
            this.subContainers.remove(subKey);
            SimpleBackup.moveToBackup(dimContainer.getDirectoryPath());
            dimContainer.setPath(this.path.resolve(dimensionPart));
            if (currentlySelected) {
                this.session.getWorldState().setCustomWorldPath(dimContainer.getPath().resolve(customWorldPath.getSubPath(2)));
            }
            try {
                this.session.getWorldManagerIO().saveWorlds(this);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to rename a dimension! Can't continue.", e);
            }
            MinimapWorldConnectionManager connections = this.getSubWorldConnections();
            connections.renameDimension(subKey, dimensionPart);
            this.session.getWorldManagerIO().getRootConfigIO().save(this);
            return;
        }
    }

    public Iterable<Map.Entry<ResourceKey<Level>, Identifier>> getDimensionTypeIds() {
        return this.dimensionTypeIds.entrySet();
    }

    public void setDimensionTypeId(ResourceKey<Level> dim, Identifier dimType) {
        this.dimensionTypes.remove(dim);
        this.dimensionTypeIds.put(dim, dimType);
    }

    @Override
    public MinimapWorldRootContainer getRoot() {
        return this;
    }

    public boolean isConfigLoaded() {
        return this.config.isLoaded();
    }

    public RootConfig getConfig() {
        return this.config;
    }

    public static final class Builder
    extends MinimapWorldContainer.Builder<Builder> {
        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            return this;
        }

        @Override
        public MinimapWorldRootContainer build() {
            return (MinimapWorldRootContainer)super.build();
        }

        @Override
        protected MinimapWorldContainer buildInternally(Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager, ResourceKey<Level> equivalentDimId, ThirdPartyWaypointManager thirdPartyWaypointManager) {
            return new MinimapWorldRootContainer(this.modMain, this.session, subContainers, worlds, worldNames, serverWaypointManager, this.path, new RootConfig(MinimapWorldContainerUtil.isMultiplayer(this.path)), new HashMap<ResourceKey<Level>, Identifier>(), new HashMap<ResourceKey<Level>, DimensionType>(), equivalentDimId, thirdPartyWaypointManager);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

