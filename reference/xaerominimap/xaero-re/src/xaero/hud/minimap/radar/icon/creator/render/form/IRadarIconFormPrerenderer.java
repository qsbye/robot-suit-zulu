/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;

public interface IRadarIconFormPrerenderer {
    public boolean requiresEntityModel();

    public boolean isFlipped();

    public boolean isOutlined();

    public <S extends EntityRenderState> boolean prerender(MinimapElementGraphics var1, EntityRenderer<?, ? super S> var2, S var3, @Nullable EntityModel<S> var4, Entity var5, @Nullable List<ModelRenderTrace> var6, RadarIconCreator.Parameters var7, RenderTarget var8);
}

