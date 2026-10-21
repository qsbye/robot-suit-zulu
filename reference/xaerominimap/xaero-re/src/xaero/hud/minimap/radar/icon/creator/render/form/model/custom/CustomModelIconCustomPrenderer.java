/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.custom;

import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RenderTypeIconCustomPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;

public abstract class CustomModelIconCustomPrenderer
extends RenderTypeIconCustomPrerenderer {
    @Override
    protected List<ModelPart> getRenderedDest(List<ModelPart> rendered) {
        return rendered;
    }

    @Override
    protected abstract <S extends EntityRenderState> Iterable<ModelPart> getModelParts(RadarIconModelPartPrerenderer var1, List<ModelPart> var2, Entity var3, EntityModel<S> var4);
}

