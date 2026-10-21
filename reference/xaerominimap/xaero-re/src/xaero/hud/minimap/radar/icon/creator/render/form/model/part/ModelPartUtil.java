/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.part;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class ModelPartUtil {
    private static final Field CUBES_FIELD = ReflectionUtils.getFieldReflection(ModelPart.class, (String)"cubes", (String)"Ljava/util/List;");
    private static final Field CHILDREN_FIELD = ReflectionUtils.getFieldReflection(ModelPart.class, (String)"children", (String)"Ljava/util/Map;");

    public static List<ModelPart.Cube> getCubes(ModelPart modelRenderer) {
        return (List)ReflectionUtils.getReflectFieldValue((Object)modelRenderer, (Field)CUBES_FIELD);
    }

    public static Map<String, ModelPart> getChildren(ModelPart modelRenderer) {
        return (Map)ReflectionUtils.getReflectFieldValue((Object)modelRenderer, (Field)CHILDREN_FIELD);
    }

    public static boolean hasDirectCubes(ModelPart part) {
        List<ModelPart.Cube> cubes = ModelPartUtil.getCubes(part);
        return cubes != null && !cubes.isEmpty();
    }

    public static boolean hasCubes(ModelPart part) {
        if (ModelPartUtil.hasDirectCubes(part)) {
            return true;
        }
        Map<String, ModelPart> children = ModelPartUtil.getChildren(part);
        for (ModelPart child : children.values()) {
            if (!ModelPartUtil.hasCubes(child)) continue;
            return true;
        }
        return false;
    }

    public static ModelPart.Cube getBiggestCuboid(ModelPart part) {
        List<ModelPart.Cube> mainCubeList = ModelPartUtil.getCubes(part);
        if (mainCubeList == null) {
            return null;
        }
        if (mainCubeList.isEmpty()) {
            return null;
        }
        float biggestSize = 0.0f;
        ModelPart.Cube biggestCuboid = null;
        for (ModelPart.Cube cuboid : mainCubeList) {
            float size = Math.abs((cuboid.maxX - cuboid.minX) * (cuboid.maxY - cuboid.minY) * (cuboid.maxZ - cuboid.minZ));
            if (!(size >= biggestSize)) continue;
            biggestCuboid = cuboid;
            biggestSize = size;
        }
        return biggestCuboid;
    }
}

