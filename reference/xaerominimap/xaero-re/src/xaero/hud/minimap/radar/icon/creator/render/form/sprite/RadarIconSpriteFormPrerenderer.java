/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.sprite;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.sprite.RadarIconSpriteForm;
import xaero.lib.client.graphics.XaeroRenderType;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class RadarIconSpriteFormPrerenderer
implements IRadarIconFormPrerenderer {
    private final boolean flipped;
    private final boolean outlined;

    public RadarIconSpriteFormPrerenderer(boolean flipped, boolean outlined) {
        this.flipped = flipped;
        this.outlined = outlined;
    }

    @Override
    public boolean requiresEntityModel() {
        return false;
    }

    @Override
    public boolean isFlipped() {
        return this.flipped;
    }

    @Override
    public boolean isOutlined() {
        return this.outlined;
    }

    @Override
    public <S extends EntityRenderState> boolean prerender(MinimapElementGraphics guiGraphics, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, @Nullable EntityModel<S> entityModel, Entity entity, @Nullable List<ModelRenderTrace> traceResult, RadarIconCreator.Parameters parameters, RenderTarget renderTarget) {
        PoseStack matrixStack = guiGraphics.pose();
        RadarIconSpriteForm spriteForm = (RadarIconSpriteForm)parameters.form;
        Identifier sprite = spriteForm.getSpriteLocation();
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(sprite);
        GpuTextureView gpuTextureView = texture.getTextureView();
        int halfIcon = 32;
        matrixStack.translate((float)halfIcon, (float)halfIcon, 1.0f);
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.scale(scale, scale, 1.0f);
        }
        GpuSampler sampler = XaeroRenderType.getSimpleSampler((FilterMode)FilterMode.LINEAR, (FilterMode)FilterMode.NEAREST);
        PreparedRenderType.Texture tas = new PreparedRenderType.Texture("Sampler0", gpuTextureView, sampler);
        ImmediateRenderUtil.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        ImmediateRenderUtil.texturedRect((PoseStack)matrixStack, (float)(-halfIcon), (float)(-halfIcon), (int)0, (int)64, (float)64.0f, (float)64.0f, (float)-64.0f, (float)64.0f, (RenderPipeline)XaeroRenderType.RP_POSITION_TEX_ALPHA_NO_CULL, (PreparedRenderType.Texture)tas);
        return true;
    }
}

