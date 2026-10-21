/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.item;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.item.RadarIconItemForm;

public class RadarIconItemFormPrerenderer
implements IRadarIconFormPrerenderer {
    private final SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();

    @Override
    public boolean requiresEntityModel() {
        return false;
    }

    @Override
    public boolean isFlipped() {
        return false;
    }

    @Override
    public boolean isOutlined() {
        return true;
    }

    @Override
    public <S extends EntityRenderState> boolean prerender(MinimapElementGraphics guiGraphics, EntityRenderer<?, ? super S> entityRenderer, S entityRenderState, @Nullable EntityModel<S> entityModel, Entity entity, @Nullable List<ModelRenderTrace> traceResult, RadarIconCreator.Parameters parameters, RenderTarget renderTarget) {
        RadarIconItemForm itemForm = (RadarIconItemForm)parameters.form;
        ItemStack itemStack = this.getItemToRender(entity, itemForm);
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }
        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.GUI, null, null, 0);
        Minecraft.getInstance().gameRenderer.lighting().setupFor(itemStackRenderState.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT);
        PoseStack matrixStack = guiGraphics.pose();
        int halfIcon = 32;
        matrixStack.translate((float)halfIcon, (float)halfIcon, 1.0f);
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.scale(scale, scale, 1.0f);
        }
        matrixStack.translate(0.0f, 0.0f, -300.0f);
        matrixStack.scale(16.0f, -16.0f, 16.0f);
        FeatureRenderDispatcher renderDispatcher = Minecraft.getInstance().gameRenderer.featureRenderDispatcher();
        itemStackRenderState.submit(matrixStack, (SubmitNodeCollector)this.submitNodeStorage, 0xF000F0, OverlayTexture.NO_OVERLAY, 0);
        try (FeatureRenderDispatcher.PreparedFrame frame = renderDispatcher.prepareFrame(this.submitNodeStorage);
             RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Item to GUI item atlas", renderTarget.getColorTextureView(), Optional.empty(), renderTarget.hasDepth() ? renderTarget.getDepthTextureView() : null, OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            FeatureRenderDispatcher.renderAllFeatures((RenderPass)renderPass, (FeatureRenderDispatcher.PreparedFrame)frame);
        }
        guiGraphics.flush();
        return true;
    }

    private ItemStack getItemToRender(Entity entity, RadarIconItemForm itemForm) {
        Identifier itemKey = itemForm.getItemKey();
        if (itemKey != null) {
            Item item;
            Holder.Reference itemReference = BuiltInRegistries.ITEM.get(itemKey).orElse(null);
            Item item2 = item = itemReference == null || !itemReference.isBound() ? null : (Item)itemReference.value();
            if (item == null) {
                return null;
            }
            return new ItemStack((ItemLike)item);
        }
        ItemStack selfStack = this.getSelfItem(entity);
        if (selfStack == null) {
            return null;
        }
        return new ItemStack((ItemLike)selfStack.getItem());
    }

    private ItemStack getSelfItem(Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            return itemEntity.getItem();
        }
        if (entity instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame)entity;
            return itemFrame.getItem();
        }
        return entity.getPickResult();
    }
}

