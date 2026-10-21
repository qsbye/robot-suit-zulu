/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RadarIconCustomPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.XaeroBufferProvider;

public class LayeredIconCustomPrerenderer
extends RadarIconCustomPrerenderer {
    private List<RadarIconCustomPrerenderer> layers;

    public LayeredIconCustomPrerenderer(List<RadarIconCustomPrerenderer> layers) {
        this.layers = layers;
    }

    @Override
    public <S extends EntityRenderState> ModelPart render(PoseStack matrixStack, XaeroBufferProvider bufferSource, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, Entity e, EntityModel<S> defaultModel, RadarIconModelPartPrerenderer partPrerenderer, List<ModelPart> rendered, ModelPart mainPart, RadarIconModelConfig modelConfig, ModelRenderTrace mrt) {
        for (RadarIconCustomPrerenderer layer : this.layers) {
            mainPart = layer.render(matrixStack, bufferSource, entityRenderer, entityRenderState, e, defaultModel, partPrerenderer, rendered, mainPart, modelConfig, mrt);
        }
        return mainPart;
    }
}

