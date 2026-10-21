/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import xaero.common.icon.XaeroIcon;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.cache.RadarIconCache;
import xaero.hud.minimap.radar.icon.cache.RadarIconEntityCache;
import xaero.hud.minimap.radar.icon.cache.id.RadarIconKey;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmor;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmorHandler;
import xaero.hud.minimap.radar.icon.cache.id.variant.RadarIconVariantHandler;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinitionManager;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconBasicForms;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;

public class RadarIconManager {
    public static final XaeroIcon FAILED = new XaeroIcon(null, 0, 0);
    public static final XaeroIcon DOT = new XaeroIcon(null, 0, 0);
    private boolean canPrerender;
    private final RadarIconCreator iconCreator;
    private final RadarIconModelConfig defaultModelConfig;
    private final RadarIconDefinitionManager definitionManager;
    private final RadarIconVariantHandler variantHandler;
    private final RadarIconArmorHandler armorHandler;
    private final RadarIconCache iconCache;

    public RadarIconManager(RadarIconCreator iconCreator) {
        this.iconCreator = iconCreator;
        this.definitionManager = new RadarIconDefinitionManager();
        this.variantHandler = new RadarIconVariantHandler();
        this.iconCache = new RadarIconCache();
        this.definitionManager.reloadResources();
        this.defaultModelConfig = new RadarIconModelConfig();
        this.armorHandler = new RadarIconArmorHandler();
    }

    public <T extends Entity> XaeroIcon get(T entity, float scale, boolean debug, boolean debugEntityVariantIds, MinimapElementGraphics guiGraphics, RenderTarget defaultFramebuffer) {
        EntityType entityType = entity.getType();
        RadarIconDefinition iconDefinition = this.definitionManager.get(EntityType.getKey((EntityType)entityType));
        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer entityRenderer = renderManager.getRenderer(entity);
        return this.get(entity, entityType, iconDefinition, entityRenderer, scale, debug, debugEntityVariantIds, guiGraphics, defaultFramebuffer);
    }

    private <T extends Entity, S extends EntityRenderState> XaeroIcon get(T entity, EntityType<?> entityType, RadarIconDefinition iconDefinition, EntityRenderer<? super T, S> entityRenderer, float scale, boolean debug, boolean debugEntityVariantIds, MinimapElementGraphics guiGraphics, RenderTarget defaultFramebuffer) {
        RadarIconForm iconForm;
        String entityVariantString;
        EntityRenderState entityRenderState = entityRenderer.createRenderState(entity, 1.0f);
        BuiltInRadarIconDefinitions.prepareEntityRenderState(entityRenderState);
        Object variant = this.variantHandler.getEntityVariant(iconDefinition, entity, entityRenderer, entityRenderState);
        if (variant == null) {
            return null;
        }
        RadarIconArmor armor = null;
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            armor = this.armorHandler.getArmor((LivingEntity)entity);
        }
        RadarIconEntityCache entityIconCache = this.iconCache.getEntityCache(entityType);
        RadarIconKey iconKey = new RadarIconKey(variant, armor);
        XaeroIcon cachedValue = entityIconCache.get(iconKey);
        if (entityIconCache.isInvalidVariantClass()) {
            return FAILED;
        }
        if (cachedValue != null) {
            return cachedValue;
        }
        String variantMapKey = entityVariantString = entityIconCache.getVariantString(iconKey);
        if (iconDefinition != null) {
            RadarIconForm radarIconForm = iconForm = variantMapKey == null ? null : iconDefinition.getVariantForm(variantMapKey);
            if (iconForm == null) {
                variantMapKey = "default";
                iconForm = iconDefinition.getVariantForm(variantMapKey);
            }
        } else {
            RadarIconForm radarIconForm = iconForm = entity instanceof LivingEntity ? RadarIconBasicForms.DEFAULT_MODEL : RadarIconBasicForms.SELF_ITEM;
        }
        if (debugEntityVariantIds && entityVariantString != null && (this.canPrerender || iconForm == RadarIconBasicForms.DOT)) {
            Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage((Component)Component.literal((String)entityVariantString));
        }
        if (iconForm == RadarIconBasicForms.DOT) {
            entityIconCache.add(iconKey, DOT);
            return DOT;
        }
        if (!this.canPrerender) {
            return null;
        }
        RadarIconCreator.Parameters parameters = new RadarIconCreator.Parameters(variant, this.defaultModelConfig, iconForm, scale, debug);
        cachedValue = this.iconCreator.create(guiGraphics, entityRenderer, entityRenderState, entity, defaultFramebuffer, parameters);
        entityIconCache.add(iconKey, cachedValue);
        this.canPrerender = false;
        return cachedValue;
    }

    public void reset() {
        this.iconCreator.reset();
        this.iconCache.clear();
        MinimapLogs.LOGGER.info("Radar icon manager reset!");
    }

    public void resetResources() {
        this.definitionManager.reloadResources();
    }

    public void allowPrerender() {
        this.canPrerender = true;
    }

    public void onModelPrepareDetection(ModelFeatureRenderer.Submit<?> submit) {
        this.iconCreator.getRenderTracer().onModelPrepare(submit);
    }

    public void onModelRenderTrace(Model model, VertexConsumer vertexConsumer, int color) {
        this.iconCreator.getRenderTracer().onModelRender(model, vertexConsumer, color);
    }

    public void onModelPartRenderTrace(ModelPart modelRenderer, int color) {
        this.iconCreator.getRenderTracer().onModelPartRender(modelRenderer, color);
    }
}

