/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.render.radar;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;

@Deprecated
public class EntityIconDefinitions {
    public static <E extends Entity> Object getVariant(Identifier entityTexture, EntityRenderer<? super E, ?> entityRenderer, E entity) {
        return BuiltInRadarIconDefinitions.getVariant(entityTexture, entityRenderer, entity);
    }

    public static void buildVariantIdString(StringBuilder stringBuilder, EntityRenderer entityRenderer, Entity entity) {
    }

    public static String getVariantString(EntityRenderer entityRenderer, Entity entity) {
        return null;
    }
}

