/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world;

import java.util.Set;
import java.util.function.Function;
import net.minecraft.IdentifierException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.lib.common.util.IOUtils;

public class MinimapDimensionHelper {
    private final Function<ResourceKey<Level>, String> convertToDimensionDirectoryNameCache = Util.memoize(this::convertToDimensionDirectoryName);

    public double getDimensionDivision(MinimapWorld minimapWorld) {
        if (Minecraft.getInstance().level == null) {
            return 1.0;
        }
        double dimCoordinateScale = this.getDimCoordinateScale(minimapWorld);
        return Minecraft.getInstance().level.dimensionType().coordinateScale() / dimCoordinateScale;
    }

    public double getDimCoordinateScale(MinimapWorld minimapWorld) {
        if (minimapWorld == null) {
            return 1.0;
        }
        MinimapWorldRootContainer rootContainer = minimapWorld.getContainer().getRoot();
        ResourceKey<Level> dimKey = minimapWorld.getDimId();
        if (dimKey == null) {
            return 1.0;
        }
        return rootContainer.getDimensionScale(dimKey);
    }

    public String getDimensionDirectoryName(ResourceKey<Level> dimKey) {
        if (dimKey == Level.OVERWORLD) {
            return "dim%0";
        }
        if (dimKey == Level.NETHER) {
            return "dim%-1";
        }
        if (dimKey == Level.END) {
            return "dim%1";
        }
        return this.convertToDimensionDirectoryNameCache.apply(dimKey);
    }

    private String convertToDimensionDirectoryName(ResourceKey<Level> dimKey) {
        Identifier identifier = dimKey.identifier();
        String path = identifier.getPath().replace('/', '%');
        path = IOUtils.replaceTrailingDots((String)path, (char)',');
        return "dim%" + identifier.getNamespace() + "$" + path;
    }

    public ResourceKey<Level> findDimensionKeyForOldName(LocalPlayer player, String oldName) {
        Set allDimensions = player.connection.levels();
        for (ResourceKey dk : allDimensions) {
            if (!oldName.equals(dk.identifier().getPath().replaceAll("[^a-zA-Z0-9_]+", ""))) continue;
            return dk;
        }
        return null;
    }

    public ResourceKey<Level> getDimensionKeyForDirectoryName(String dirName) {
        String dimIdPart = dirName.substring(4);
        if (dimIdPart.equals("0")) {
            return Level.OVERWORLD;
        }
        if (dimIdPart.equals("1")) {
            return Level.END;
        }
        if (dimIdPart.equals("-1")) {
            return Level.NETHER;
        }
        String[] idArgs = dimIdPart.split("\\$");
        if (idArgs.length < 2) {
            return null;
        }
        String path = idArgs[1].replace('%', '/');
        path = path.replace(',', '.');
        try {
            return ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)Identifier.fromNamespaceAndPath((String)idArgs[0], (String)path));
        }
        catch (IdentifierException rle) {
            return null;
        }
    }
}

