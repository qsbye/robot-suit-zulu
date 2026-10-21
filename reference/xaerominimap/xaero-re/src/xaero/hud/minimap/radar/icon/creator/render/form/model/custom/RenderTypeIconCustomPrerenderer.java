/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.custom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RadarIconCustomPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.XaeroBufferProvider;

public abstract class RenderTypeIconCustomPrerenderer
extends RadarIconCustomPrerenderer {
    @Override
    public <S extends EntityRenderState> ModelPart render(PoseStack matrixStack, XaeroBufferProvider bufferSource, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, Entity e, EntityModel<S> defaultModel, RadarIconModelPartPrerenderer partPrerenderer, List<ModelPart> rendered, ModelPart mainPart, RadarIconModelConfig config, ModelRenderTrace mrt) {
        RenderType renderType = this.getRenderType(entityRenderer, e);
        if (renderType == null) {
            return mainPart;
        }
        BufferBuilder vertexBuilder = bufferSource.getBuffer(renderType);
        Iterable<ModelPart> modelParts = this.getModelParts(partPrerenderer, rendered, e, defaultModel);
        List<ModelPart> renderedDest = this.getRenderedDest(rendered);
        RadarIconModelPartPrerenderer.Parameters parameters = new RadarIconModelPartPrerenderer.Parameters(config, mrt, renderedDest);
        mainPart = partPrerenderer.renderPartsIterable(modelParts, matrixStack, (VertexConsumer)vertexBuilder, mainPart, parameters);
        bufferSource.endBatch();
        return mainPart;
    }

    protected <S extends EntityRenderState> Iterable<ModelPart> getModelParts(RadarIconModelPartPrerenderer partPrerenderer, List<ModelPart> rendered, Entity entity, EntityModel<S> defaultModel) {
        return rendered;
    }

    protected List<ModelPart> getRenderedDest(List<ModelPart> rendered) {
        return new ArrayList<ModelPart>();
    }

    protected abstract <S extends EntityRenderState> RenderType getRenderType(EntityRenderer<?, ? super S> var1, Entity var2);
}

