/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.lang.reflect.Method;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;

public class RadarIconVariantHandler {
    private final StringBuilder legacyEntityStringBuilder = new StringBuilder();

    public <T extends Entity> Object getEntityVariant(RadarIconDefinition iconDefinition, T entity, EntityRenderer<? super T, ?> entityRenderer, EntityRenderState entityRenderState) {
        Object variant = null;
        Identifier entityTexture = null;
        try {
            Identifier entityTextureUnchecked;
            Identifier identifier;
            if (entityRenderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer livingEntityRenderer = (LivingEntityRenderer)entityRenderer;
                identifier = livingEntityRenderer.getTextureLocation((LivingEntityRenderState)entityRenderState);
            } else {
                identifier = null;
            }
            entityTexture = entityTextureUnchecked = identifier;
        }
        catch (Throwable e) {
            MinimapLogs.LOGGER.error("Exception while fetching entity texture to build its variant ID for " + String.valueOf(EntityType.getKey((EntityType)entity.getType())));
            MinimapLogs.LOGGER.error("The exception is most likely on another mod's end and suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
        }
        if (iconDefinition != null) {
            Method variantMethod = iconDefinition.getVariantMethod();
            if (variantMethod != null) {
                try {
                    variant = variantMethod.invoke(null, entityTexture, entityRenderer, entity);
                }
                catch (Throwable e) {
                    Identifier entityId = EntityType.getKey((EntityType)entity.getType());
                    MinimapLogs.LOGGER.error("Exception while using the variant ID method " + iconDefinition.getVariantMethodString() + " defined for " + String.valueOf(entityId));
                    MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
                    iconDefinition.setVariantMethod(null);
                }
            } else {
                variant = this.getLegacyVariantId(iconDefinition, entity, entityRenderer);
            }
        }
        if (variant == null) {
            variant = BuiltInRadarIconDefinitions.getVariant(entityTexture, entityRenderer, entity);
        }
        return variant;
    }

    private <T extends Entity> String getLegacyVariantId(RadarIconDefinition iconDefinition, T entity, EntityRenderer<? super T, ?> entityRenderer) {
        Method variantIdBuilderMethod = iconDefinition.getVariantIdBuilderMethod();
        if (variantIdBuilderMethod != null && !variantIdBuilderMethod.equals(BuiltInRadarIconDefinitions.BUILD_VARIANT_ID_STRING_METHOD)) {
            this.legacyEntityStringBuilder.setLength(0);
            try {
                variantIdBuilderMethod.invoke(null, this.legacyEntityStringBuilder, entityRenderer, entity);
                return this.legacyEntityStringBuilder.toString();
            }
            catch (Throwable e) {
                Identifier entityId = EntityType.getKey((EntityType)entity.getType());
                MinimapLogs.LOGGER.error("Exception while using the variant builder ID method " + iconDefinition.getVariantIdBuilderMethodString() + " defined for " + String.valueOf(entityId));
                MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
                iconDefinition.setVariantIdBuilderMethod(null);
                return null;
            }
        }
        Method variantOldIdMethod = iconDefinition.getOldVariantIdMethod();
        if (variantOldIdMethod == null || variantOldIdMethod.equals(BuiltInRadarIconDefinitions.GET_VARIANT_ID_STRING_METHOD)) {
            return null;
        }
        try {
            return (String)variantOldIdMethod.invoke(null, entityRenderer, entity);
        }
        catch (Throwable e) {
            Identifier entityId = EntityType.getKey((EntityType)entity.getType());
            MinimapLogs.LOGGER.error("Exception while using the variant ID method " + iconDefinition.getOldVariantIdMethodString() + " defined for " + String.valueOf(entityId));
            MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
            iconDefinition.setOldVariantIdMethod(null);
            return null;
        }
    }
}

