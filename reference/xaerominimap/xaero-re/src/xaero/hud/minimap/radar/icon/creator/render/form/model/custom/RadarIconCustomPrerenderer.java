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
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.lib.client.graphics.XaeroBufferProvider;

public abstract class RadarIconCustomPrerenderer {
    public abstract <S extends EntityRenderState> ModelPart render(PoseStack var1, XaeroBufferProvider var2, EntityRenderer<?, ? super S> var3, S var4, Entity var5, EntityModel<S> var6, RadarIconModelPartPrerenderer var7, List<ModelPart> var8, ModelPart var9, RadarIconModelConfig var10, ModelRenderTrace var11);
}

